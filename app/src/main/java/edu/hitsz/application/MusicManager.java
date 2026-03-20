package edu.hitsz.application;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 音乐资源管理类，模仿 ImageManager
 * 统一加载、访问并播放游戏中的音频文件 (.wav)
 */
public class MusicManager {

    /**
     * 存放 音频名称 -> 音频Clip 的映射
     */
    private static final Map<String, Clip> MUSIC_MAP = new HashMap<>();

    // 定义常用音乐的静态引用（可选）
    public static Clip BGM;
    public static Clip BOSS_BGM;
    public static Clip EXPLOSION_BOOM;
    public static Clip BULLET_SHOOT;
    public static Clip BULLET_HIT;
    public static Clip GAME_OVER;
    public static Clip GET_SUPPLY;

    static {
        try {
            // --- 加载所有 wav 文件 ---
            BGM = loadClip("src/videos/bgm.wav");
            BOSS_BGM = loadClip("src/videos/bgm_boss.wav");
            EXPLOSION_BOOM = loadClip("src/videos/bomb_explosion.wav");
            BULLET_SHOOT = loadClip("src/videos/bullet.wav");
            BULLET_HIT = loadClip("src/videos/bullet_hit.wav");
            GAME_OVER = loadClip("src/videos/game_over.wav");
            GET_SUPPLY = loadClip("src/videos/get_supply.wav");



            // 放入映射表中方便通过名字访问
            MUSIC_MAP.put("bgm", BGM);
            MUSIC_MAP.put("boss_bgm", BOSS_BGM);
            MUSIC_MAP.put("boom", EXPLOSION_BOOM);
            MUSIC_MAP.put("shoot", BULLET_SHOOT);
            MUSIC_MAP.put("hit", BULLET_HIT);
            MUSIC_MAP.put("game_over", GAME_OVER);
            MUSIC_MAP.put("get_supply", GET_SUPPLY);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * 从文件路径加载一个 Clip 对象
     */
    private static Clip loadClip(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path));
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        return clip;
    }

    /**
     * 播放指定名称的音乐
     */
    public static void play(String name) {
        Clip clip = MUSIC_MAP.get(name);
        if (clip != null) {
            clip.stop();       // 先停掉防止重叠
            clip.setFramePosition(0); // 从头开始播放
            clip.start();
        }
    }

    /**
     * 循环播放指定音乐（如BGM）
     */
    public static void loop(String name) {
        Clip clip = MUSIC_MAP.get(name);
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * 停止播放
     */
    public static void stop(String name) {
        Clip clip = MUSIC_MAP.get(name);
        if (clip != null) {
            clip.stop();
        }
    }

    /**
     * 关闭所有音频资源
     */
    public static void closeAll() {
        for (Clip clip : MUSIC_MAP.values()) {
            clip.close();
        }
    }
}
