package edu.hitsz.prop.factory;

import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropBullet;

public class PropBulletFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY, int speedY) {
        return new PropBullet(locationX, locationY, speedY);
    }
}
