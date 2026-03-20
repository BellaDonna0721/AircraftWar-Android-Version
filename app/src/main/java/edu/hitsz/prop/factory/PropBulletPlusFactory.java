package edu.hitsz.prop.factory;

import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropBulletPlus;

public class PropBulletPlusFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY, int speedY) {
        return new PropBulletPlus(locationX, locationY, speedY);
    }
}
