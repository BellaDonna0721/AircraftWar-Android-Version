package edu.hitsz.prop.factory;

import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropBlood;

public class PropBloodFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY, int speedY) {
        return new PropBlood(locationX, locationY, speedY, 100); // 血量+40
    }
}
