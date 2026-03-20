package edu.hitsz.prop;

import edu.hitsz.basic.AbstractFlyingObject;

public abstract class AbstractProp extends AbstractFlyingObject {

    protected int speedY; // 道具向下飞行的速度

    public AbstractProp(int locationX, int locationY, int speedY) {
        super(locationX, locationY, 0, speedY);
        this.speedY = speedY;
    }

    public abstract void effect(edu.hitsz.aircraft.HeroAircraft heroAircraft);

    @Override
    public void forward() {
        locationY += speedY;
        // 超出屏幕底部消失
        if (locationY >= edu.hitsz.application.Main.WINDOW_HEIGHT) {
            vanish();
        }
    }
}
