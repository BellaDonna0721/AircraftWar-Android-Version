package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

/**
 * 英雄机单例管理类（饿汉式）
 */
public class HeroAircraftSingleton {

    /** 唯一实例（类加载时就创建） */
    private static final HeroAircraft instance =
            new HeroAircraft(
                    Main.WINDOW_WIDTH / 2,
                    Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                    0, 0, 700);

    /** 私有化构造，防止外部 new */
    private HeroAircraftSingleton() {}

    /** 全局唯一访问点 */
    public static HeroAircraft getInstance() {
        return instance;
    }
}
