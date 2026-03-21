package edu.hitsz.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Android 版本排行榜管理器
 * 原始 Swing 版本已改为 Android 兼容版本
 */
public class Table {
    
    // 定义不同难度的文件名
    private static final String EASY_FILE = "./score_easy.csv";
    private static final String COMMON_FILE = "./score_normal.csv";
    private static final String HARD_FILE = "./score_hard.csv";
    
    private String currentFile; // 当前使用的文件
    private List<ScoreRecord> records; // 分数记录列表

    public Table(String difficulty) {
        // 根据难度设置文件
        switch (difficulty) {
            case "Easy":
                currentFile = EASY_FILE;
                break;
            case "Normal":
                currentFile = COMMON_FILE;
                break;
            case "Hard":
                currentFile = HARD_FILE;
                break;
            default:
                currentFile = "score_records.csv";
        }
        
        records = new ArrayList<>();
        loadScores();
    }

    /**
     * 加载分数文件
     */
    private void loadScores() {
        System.out.println("尝试加载分数文件：" + currentFile);
        File file = new File(currentFile);
        
        if (!file.exists()) {
            System.out.println("文件不存在，创建新的记录文件：" + file.getAbsolutePath());
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("创建文件失败：" + e.getMessage());
            }
            return;
        }

        System.out.println("开始读取文件：" + file.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            records.clear();
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        String playerName = parts[0];
                        int score = Integer.parseInt(parts[1].trim());
                        String time = parts[2];
                        records.add(new ScoreRecord(playerName, score, time));
                    } catch (NumberFormatException e) {
                        System.out.println("无效的分数格式：" + parts[1]);
                    }
                }
            }
            
            // 按分数降序排序
            records.sort((a, b) -> Integer.compare(b.score, a.score));
            System.out.println("加载完成，共读取 " + records.size() + " 条记录");
        } catch (IOException e) {
            System.out.println("无法读取记录文件：" + currentFile);
        }
    }

    /**
     * 添加新分数
     */
    public void addNewScore(String playerName, int score) {
        String time = new SimpleDateFormat("MM-dd HH:mm").format(new Date());
        records.add(new ScoreRecord(playerName, score, time));
        
        // 按分数降序排序
        records.sort((a, b) -> Integer.compare(b.score, a.score));
        
        // 保存到文件
        saveScores();
    }

    /**
     * 保存分数到文件
     */
    private void saveScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentFile))) {
            for (ScoreRecord record : records) {
                writer.printf("%s,%d,%s\n", record.playerName, record.score, record.time);
            }
        } catch (IOException e) {
            System.out.println("无法保存记录到文件：" + currentFile);
        }
    }

    /**
     * 获取分数记录列表
     */
    public List<ScoreRecord> getRecords() {
        return new ArrayList<>(records);
    }

    /**
     * 打印排行榜到控制台
     */
    public void printRankList() {
        System.out.println("=== 排行榜 ===");
        int rank = 1;
        for (ScoreRecord record : records) {
            System.out.printf("%d. %s - %d分 - %s\n", rank++, record.playerName, record.score, record.time);
        }
    }

    /**
     * 内部类：分数记录
     */
    public static class ScoreRecord {
        public String playerName;
        public int score;
        public String time;

        public ScoreRecord(String playerName, int score, String time) {
            this.playerName = playerName;
            this.score = score;
            this.time = time;
        }
    }
}
