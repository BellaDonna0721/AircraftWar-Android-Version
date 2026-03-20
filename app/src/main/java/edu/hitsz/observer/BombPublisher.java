package edu.hitsz.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 炸弹发布者：负责通知所有观察者（敌机、子弹等）
 */
public class BombPublisher {

    private List<BombSubscriber> subscribers = new ArrayList<>();

    // 注册观察者
    public void addSubscriber(BombSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    // 移除观察者
    public void removeSubscriber(BombSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    // 通知所有观察者
    public void notifyAllSubscribers() {
        for (BombSubscriber subscriber : subscribers) {
            subscriber.update();
        }
    }

    // 炸弹生效
    public void bombEffect() {
        notifyAllSubscribers();
    }
}