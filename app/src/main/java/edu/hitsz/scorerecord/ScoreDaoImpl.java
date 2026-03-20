package edu.hitsz.scorerecord;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreDaoImpl implements ScoreDao {
    private final List<ScoreRecord> records;
    private final String filePath;
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

    public ScoreDaoImpl(String difficulty) {
        this.records = new ArrayList<>();
        // 根据难度选择不同的文件
        switch(difficulty) {
            case "Easy":
                this.filePath = "score_easy.csv";
                break;
            case "Normal":
                this.filePath = "score_normal.csv";
                break;
            case "Hard":
                this.filePath = "score_hard.csv";
                break;
            default:
                this.filePath = "score_records.csv";
        }
        loadFromFile();
    }

    @Override
    public List<ScoreRecord> getAllRecords() {
        return new ArrayList<>(records);
    }

    @Override
    public void addRecord(ScoreRecord record) {
        records.add(record);
        // 按分数降序排序
        records.sort((r1, r2) -> r2.getScore() - r1.getScore());
        saveToFile();
    }

    @Override
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            System.out.println("正在保存分数到文件：" + filePath);
            for (ScoreRecord record : records) {
                String line = record.toString();
                writer.write(line);
                writer.newLine();
                System.out.println("写入记录：" + line);
            }
            System.out.println("保存完成，共写入 " + records.size() + " 条记录");
        } catch (IOException e) {
            System.out.println("Error saving score records: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void loadFromFile() {
        records.clear();
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    ScoreRecord record = new ScoreRecord(
                        parts[0],
                        Integer.parseInt(parts[1]),
                        sdf.parse(parts[2])
                    );
                    records.add(record);
                }
            }
        } catch (IOException | ParseException e) {
            System.out.println("Error loading score records: " + e.getMessage());
        }
    }

    @Override
    public void printRankList() {
        System.out.println("***************");
        System.out.println("得分排行榜");
        System.out.println("***************");
        
        for (int i = 0; i < records.size(); i++) {
            ScoreRecord record = records.get(i);
            System.out.printf("第%d名：%s\n", i + 1, record.toString());
        }
    }
}