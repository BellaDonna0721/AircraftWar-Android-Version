package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.strategy.HeroFanShoot;
import edu.hitsz.aircraft.strategy.Strategy;
import edu.hitsz.application.PowerTimeManager;

public class PropBullet extends AbstractProp {

    public PropBullet(int locationX, int locationY, int speedY) {
        super(locationX, locationY, speedY);
    }

    @Override
    public void effect(HeroAircraft heroAircraft) {
        System.out.println("FireSupply active! Switch to fan shooting mode!");
        // 保存原始策略
        Strategy originalStrategy = heroAircraft.getShootStrategy();
        // 设置为散射模式
        heroAircraft.setShootNum(3);  // 设置为3连发
        heroAircraft.setShootStrategy(new HeroFanShoot());
        // 启动定时器，到时恢复原始射击策略
        PowerTimeManager.startPowerTimer(heroAircraft, originalStrategy);
        vanish();
    }
}
