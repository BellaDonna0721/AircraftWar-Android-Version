package edu.hitsz.application;

/**
 * Android 版本音乐线程 Stub
 * 原始 Java Desktop 版本使用 javax.sound.sampled
 * Android 版本需要使用 MediaPlayer 实现
 */
public class MusicThread extends Thread {

    private String filename;
    private volatile boolean running = true;
    private boolean loop = false;

    public MusicThread(String filename, boolean loop) {
        this.filename = filename;
        this.loop = loop;
    }

    public MusicThread(String filename) {
        this(filename, false);
    }

    public synchronized void stopAndClose() {
        running = false;
        interrupt();
    }

    public void stopMusic() {
        running = false;
    }

    @Override
    public void run() {
        // Android 音频播放应该通过 MediaPlayer 实现
        // 这是一个 stub 实现，用于 Android 编译兼容性
        System.out.println("音频播放功能（文件：" + filename + "）需要在 Android Activity 中实现");
    }
}


