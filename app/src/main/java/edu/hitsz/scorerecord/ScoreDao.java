package edu.hitsz.scorerecord;

import java.util.List;

public interface ScoreDao {
    /**
     * 获取所有分数记录
     */
    List<ScoreRecord> getAllRecords();

    /**
     * 添加新的分数记录
     */
    void addRecord(ScoreRecord record);

    /**
     * 保存记录到文件
     */
    void saveToFile();

    /**
     * 从文件加载记录
     */
    void loadFromFile();

    /**
     * 打印排行榜
     */
    void printRankList();
}