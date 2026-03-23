package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;

/**
 * 英雄机单例管理类（懒汉式 - 线程安全）
 * 延迟初始化确保 ImageManager 已加载
 */
public class HeroAircraftSingleton {

    /** 唯一实例（延迟初始化） */
    private static HeroAircraft instance;

    /** 私有化构造，防止外部 new */
    private HeroAircraftSingleton() {}

    /** 全局唯一访问点（线程安全的懒汉式） */
    public static synchronized HeroAircraft getInstance() {
        if (instance == null) {
            try {
                // 获取图片高度，如果图片为null则使用默认值
                int heroImageHeight = 0;
                if (ImageManager.HERO_IMAGE != null) {
                    heroImageHeight = ImageManager.HERO_IMAGE.getHeight();
                }
                
                instance = new HeroAircraft(
                        edu.hitsz.application.Game.SCREEN_WIDTH / 2,
                        edu.hitsz.application.Game.SCREEN_HEIGHT - heroImageHeight,
                        0, 0, 700);
                
                System.out.println("HeroAircraftSingleton 初始化成功");
            } catch (Exception e) {
                System.err.println("HeroAircraftSingleton 初始化异常: " + e.getMessage());
                e.printStackTrace();
                // 创建一个即使失败也能继续执行的默认实例
                if (instance == null) {
                    instance = new HeroAircraft(256, 650, 0, 0, 700);
                }
            }
        }
        return instance;
    }

    /**
     * 重置英雄机位置（用于屏幕尺寸变化后）
     */
    public static void resetInstancePosition(int screenWidth, int screenHeight) {
        if (instance != null) {
            int heroImageHeight = 0;
            if (ImageManager.HERO_IMAGE != null) {
                heroImageHeight = ImageManager.HERO_IMAGE.getHeight();
            }
            instance.setLocation(screenWidth / 2, screenHeight - heroImageHeight);
        }
    }
}
