package edu.hitsz.application;

/**
 * Android 版本难度选择管理器
 * 原始 Swing 版本已改为 Android 兼容版本
 */
public class DifficultySelection {
    
    // 定义难度常量
    public static final int SIMPLE = 0;
    public static final int COMMON = 1;
    public static final int HARD = 2;
    
    private static int difficulty = COMMON; // 默认普通模式
    private static boolean musicOn = true;  // 默认音乐开启

    public DifficultySelection() {
        // 无参构造器用于 Android 版本
    }

    /**
     * 设置难度
     */
    public static void setDifficulty(int diff) {
        difficulty = diff;
    }

    /**
     * 获取选中的难度
     */
    public static int getSelectedDifficulty() {
        return difficulty;
    }

    /**
     * 获取难度描述
     */
    public String getSelectedDifficultyDescription() {
        switch (difficulty) {
            case SIMPLE:
                return "Easy";
            case COMMON:
                return "Normal";
            case HARD:
                return "Hard";
            default:
                return "Easy";
        }
    }

    /**
     * 是否开启音乐
     */
    public static boolean isMusicOn() {
        return musicOn;
    }

    /**
     * 设置音乐状态
     */
    public static void setMusicOn(boolean on) {
        musicOn = on;
    }
}