package edu.hitsz.application;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.strategy.Strategy;
import edu.hitsz.aircraft.strategy.HeroStraightShoot;

/**
 * 火力道具时间管理器
 * 使用Runnable接口实现多线程
 */
public class PowerTimeManager {
    private static final int POWER_TIME = 10000; // 火力道具持续时间，单位毫秒

    /**
     * 当前正在运行的计时线程（保证同一时间只有一个火力倒计时）
     */
    private static Thread powerThread;

    /**
     * 启动火力道具计时器
     * 连续拾取不同火力道具时，以最后一次的效果为准，之前的计时器会被打断。
     * 计时结束后统一恢复为默认直线单发模式。
     *
     * @param heroAircraft 英雄机实例
     * @param originStrategy 原始射击策略（为了兼容旧接口，这里不再使用）
     */
    public static synchronized void startPowerTimer(HeroAircraft heroAircraft, Strategy originStrategy) {
        // 如果之前已有计时线程在跑，先中断它，保证以最后一次道具为准
        if (powerThread != null && powerThread.isAlive()) {
            powerThread.interrupt();
        }

        Runnable powerTimer = () -> {
            try {
                // 等待指定时间
                Thread.sleep(POWER_TIME);
            } catch (InterruptedException e) {
                System.out.println("火力道具计时器被中断");
                // 被中断说明有新的火力道具生效，直接结束当前线程即可
                return;
            }

            // 计时结束：统一恢复为默认直线单发模式
            heroAircraft.resetShootStrategy();
            System.out.println("火力增强效果结束，恢复直线射击！");
        };

        powerThread = new Thread(powerTimer, "PowerTimer");
        powerThread.start();
    }
}