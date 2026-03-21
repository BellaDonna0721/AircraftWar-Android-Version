package edu.hitsz.application;

import java.util.HashMap;
import java.util.Map;

/**
 * Android 版本音乐资源管理器 Stub
 * 原始 Java Desktop 版本使用 javax.sound.sampled.Clip
 * Android 版本需要使用 MediaPlayer/SoundPool 实现
 */
public class MusicManager {

    /**
     * 存放音频名称的映射（暂时为空）
     */
    private static final Map<String, String> MUSIC_MAP = new HashMap<>();

    // 音乐资源标识常量
    public static final String BGM = "bgm";
    public static final String BOSS_BGM = "boss_bgm";
    public static final String EXPLOSION_BOOM = "boom";
    public static final String BULLET_SHOOT = "shoot";
    public static final String BULLET_HIT = "hit";
    public static final String GAME_OVER = "game_over";
    public static final String GET_SUPPLY = "get_supply";

    static {
        // Android 版本的实际音频加载应在 Activity 中通过 MediaPlayer 进行
        MUSIC_MAP.put("bgm", "bgm");
        MUSIC_MAP.put("boss_bgm", "boss_bgm");
        MUSIC_MAP.put("boom", "explosion_boom");
        MUSIC_MAP.put("shoot", "bullet_shoot");
        MUSIC_MAP.put("hit", "bullet_hit");
        MUSIC_MAP.put("game_over", "game_over");
        MUSIC_MAP.put("get_supply", "get_supply");
    }

    /**
     * 播放指定名称的音乐
     */
    public static void play(String name) {
        System.out.println("播放音乐: " + name);
        // Android 实现需要通过 SoundPool 或 MediaPlayer
    }

    /**
     * 循环播放指定音乐（如BGM）
     */
    public static void loop(String name) {
        System.out.println("循环播放音乐: " + name);
        // Android 实现需要通过 MediaPlayer.setLooping(true)
    }

    /**
     * 停止播放
     */
    public static void stop(String name) {
        System.out.println("停止播放: " + name);
        // Android 实现需要通过 MediaPlayer.stop()
    }

    /**
     * 关闭所有音频资源
     */
    public static void closeAll() {
        System.out.println("关闭所有音频资源");
        // Android 实现需要通过 MediaPlayer.release()
    }
}
