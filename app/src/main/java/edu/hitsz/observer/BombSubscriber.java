package edu.hitsz.observer;

/**
 * 炸弹观察者接口：所有被炸弹影响的对象都要实现它
 */
public interface BombSubscriber {
    /**
     * 收到炸弹通知时的响应
     */
    void update();
}
