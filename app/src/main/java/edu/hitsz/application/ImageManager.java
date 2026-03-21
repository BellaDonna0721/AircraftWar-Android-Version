package edu.hitsz.application;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.ElitePlusEnemy;
import edu.hitsz.aircraft.Boss;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.PropBlood;
import edu.hitsz.prop.PropBomb;
import edu.hitsz.prop.PropBullet;
import edu.hitsz.prop.PropBulletPlus;

import java.util.HashMap;
import java.util.Map;

/**
 * 综合管理图片的加载，访问
 * 提供图片的静态访问方法
 *
 * @author hitsz
 */
public class ImageManager {

    /**
     * 类名-图片 映射，存储各基类的图片 <br>
     * 可使用 CLASSNAME_IMAGE_MAP.get( obj.getClass().getName() ) 获得 obj 所属基类对应的图片
     */
    private static final Map<String, Bitmap> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static Bitmap BACKGROUND_IMAGE;
    public static Bitmap BACKGROUND_IMAGE2;
    public static Bitmap BACKGROUND_IMAGE3;
    public static Bitmap HERO_IMAGE;
    public static Bitmap HERO_BULLET_IMAGE;
    public static Bitmap ENEMY_BULLET_IMAGE;

    public static Bitmap MOB_ENEMY_IMAGE;
    public static Bitmap ELITE_ENEMY_IMAGE;
    public static Bitmap ELITEPLUS_ENEMY_IMAGE;
    public static Bitmap BOSS_IMAGE;

    public static Bitmap PROP_BLOOD_IMAGE;
    public static Bitmap PROP_BOMB_IMAGE;
    public static Bitmap PROP_BULLET_IMAGE;
    public static Bitmap PROP_BULLETPLUS_IMAGE;

    /**
     * 初始化图片资源，必须在应用启动时调用
     * @param context 应用Context
     */
    public static void init(Context context) {
        try {
            // 获取资源ID
            int bgId = context.getResources().getIdentifier("bg", "drawable", context.getPackageName());
            int bg2Id = context.getResources().getIdentifier("bg2", "drawable", context.getPackageName());
            int bg3Id = context.getResources().getIdentifier("bg3", "drawable", context.getPackageName());
            
            int eliteId = context.getResources().getIdentifier("elite", "drawable", context.getPackageName());
            int heroId = context.getResources().getIdentifier("hero", "drawable", context.getPackageName());
            int mobId = context.getResources().getIdentifier("mob", "drawable", context.getPackageName());
            int elitePlusId = context.getResources().getIdentifier("elite_plus", "drawable", context.getPackageName());
            int bossId = context.getResources().getIdentifier("boss", "drawable", context.getPackageName());
            
            int bulletHeroId = context.getResources().getIdentifier("bullet_hero", "drawable", context.getPackageName());
            int bulletEnemyId = context.getResources().getIdentifier("bullet_enemy", "drawable", context.getPackageName());
            
            int propBloodId = context.getResources().getIdentifier("prop_blood", "drawable", context.getPackageName());
            int propBombId = context.getResources().getIdentifier("prop_bomb", "drawable", context.getPackageName());
            int propBulletId = context.getResources().getIdentifier("prop_bullet", "drawable", context.getPackageName());
            int propBulletPlusId = context.getResources().getIdentifier("prop_bullet_plus", "drawable", context.getPackageName());
            
            // 检查资源是否存在，然后加载
            if (bgId != 0) BACKGROUND_IMAGE = BitmapFactory.decodeResource(context.getResources(), bgId);
            if (bg2Id != 0) BACKGROUND_IMAGE2 = BitmapFactory.decodeResource(context.getResources(), bg2Id);
            if (bg3Id != 0) BACKGROUND_IMAGE3 = BitmapFactory.decodeResource(context.getResources(), bg3Id);
            
            if (eliteId != 0) ELITE_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), eliteId);
            if (heroId != 0) HERO_IMAGE = BitmapFactory.decodeResource(context.getResources(), heroId);
            if (mobId != 0) MOB_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), mobId);
            if (elitePlusId != 0) ELITEPLUS_ENEMY_IMAGE = BitmapFactory.decodeResource(context.getResources(), elitePlusId);
            if (bossId != 0) BOSS_IMAGE = BitmapFactory.decodeResource(context.getResources(), bossId);
            
            if (bulletHeroId != 0) HERO_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), bulletHeroId);
            if (bulletEnemyId != 0) ENEMY_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), bulletEnemyId);
            
            if (propBloodId != 0) PROP_BLOOD_IMAGE = BitmapFactory.decodeResource(context.getResources(), propBloodId);
            if (propBombId != 0) PROP_BOMB_IMAGE = BitmapFactory.decodeResource(context.getResources(), propBombId);
            if (propBulletId != 0) PROP_BULLET_IMAGE = BitmapFactory.decodeResource(context.getResources(), propBulletId);
            if (propBulletPlusId != 0) PROP_BULLETPLUS_IMAGE = BitmapFactory.decodeResource(context.getResources(), propBulletPlusId);
            
            // 输出加载结果用于调试
            System.out.println("ImageManager 初始化完成");
            if (HERO_IMAGE == null) {
                System.err.println("警告: HERO_IMAGE 加载失败");
            }
            if (BACKGROUND_IMAGE == null) {
                System.err.println("警告: BACKGROUND_IMAGE 加载失败");
            }

            // 构建类名-图片映射
            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(ElitePlusEnemy.class.getName(), ELITEPLUS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(Boss.class.getName(), BOSS_IMAGE);

            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);

            CLASSNAME_IMAGE_MAP.put(PropBlood.class.getName(), PROP_BLOOD_IMAGE);
            CLASSNAME_IMAGE_MAP.put(PropBomb.class.getName(), PROP_BOMB_IMAGE);
            CLASSNAME_IMAGE_MAP.put(PropBullet.class.getName(), PROP_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(PropBulletPlus.class.getName(), PROP_BULLETPLUS_IMAGE);

        } catch (Exception e) {
            System.err.println("ImageManager 初始化异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Bitmap get(String className){
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static Bitmap get(Object obj){
        if (obj == null){
            return null;
        }
        return get(obj.getClass().getName());
    }

}