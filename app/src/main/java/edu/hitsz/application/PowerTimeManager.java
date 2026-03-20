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
     * 启动火力道具计时器
     * @param heroAircraft 英雄机实例
     * @param originStrategy 原始射击策略
     */
    public static void startPowerTimer(HeroAircraft heroAircraft, Strategy originStrategy) {
        Runnable powerTimer = () -> {
            try {
                // 等待指定时间
                Thread.sleep(POWER_TIME);
                // 恢复原始射击策略
                heroAircraft.setShootStrategy(originStrategy);
                heroAircraft.setShootNum(1);  // 恢复默认单发
                System.out.println("火力增强效果结束！");
            } catch (InterruptedException e) {
                System.out.println("火力道具计时器被中断");
                // 确保恢复原始射击策略
                heroAircraft.setShootStrategy(originStrategy);
                heroAircraft.setShootNum(1);  // 恢复默认单发
            }
        };

        // 创建并启动新线程
        Thread powerThread = new Thread(powerTimer, "PowerTimer");
        powerThread.start();
    }
}