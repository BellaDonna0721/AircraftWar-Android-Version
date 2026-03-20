package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.strategy.HeroRingShoot;
import edu.hitsz.aircraft.strategy.Strategy;
import edu.hitsz.application.PowerTimeManager;

public class PropBulletPlus extends AbstractProp {

    public PropBulletPlus(int locationX, int locationY, int speedY) {
        super(locationX, locationY, speedY);
    }

    @Override
    public void effect(HeroAircraft heroAircraft) {
        System.out.println("FireSupplyPlus active! Switch to ring shooting mode!");
        // 保存原始策略
        Strategy originalStrategy = heroAircraft.getShootStrategy();
        // 设置为环形射击模式
        heroAircraft.setShootNum(12);  // 设置为12发环形弹幕
        heroAircraft.setShootStrategy(new HeroRingShoot());
        // 启动定时器，到时恢复原始射击策略
        PowerTimeManager.startPowerTimer(heroAircraft, originalStrategy);
        vanish();
    }
}
