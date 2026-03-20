package edu.hitsz.prop.factory;

import edu.hitsz.prop.AbstractProp;

public interface PropFactory {
    /**
     * 创建道具
     * @param locationX 道具初始X位置
     * @param locationY 道具初始Y位置
     * @param speedY    向下飞行速度
     * @return 创建的道具对象
     */
    AbstractProp createProp(int locationX, int locationY, int speedY);
}
