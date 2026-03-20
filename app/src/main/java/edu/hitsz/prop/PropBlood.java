package edu.hitsz.prop;

import edu.hitsz.aircraft.HeroAircraft;

public class PropBlood extends AbstractProp {

    private int bloodValue; // 恢复血量

    public PropBlood(int locationX, int locationY, int speedY, int bloodValue) {
        super(locationX, locationY, speedY);
        this.bloodValue = bloodValue;
    }

    @Override
    public void effect(HeroAircraft heroAircraft) {
        int newHp = Math.min(heroAircraft.getHp() + bloodValue, heroAircraft.getMaxHp());
        heroAircraft.setHp(newHp);
        System.out.println("BloodSupply active! HP = " + newHp);
        vanish(); // 道具生效后消失
    }
}
