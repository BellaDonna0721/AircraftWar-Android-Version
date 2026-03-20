package edu.hitsz.application;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class DifficultySelection {
    private JPanel Main;
    private JButton SimpleModeButton;
    private JButton CommonModeButton;
    private JButton HardModeButton;
    private JComboBox comboBox1;
    private JLabel Label1;
    private JFrame frame;
    
    // 定义难度常量
    public static final int SIMPLE = 0;
    public static final int COMMON = 1;
    public static final int HARD = 2;
    
    private static int difficulty = SIMPLE; // 默认简单模式
    private static boolean musicOn = true;  // 默认音乐开启

    public DifficultySelection(JFrame frame) {
        this.frame = frame;

        SimpleModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = SIMPLE;
                startGame();
            }
        });
        CommonModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = COMMON;
                startGame();
            }
        });
        HardModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                difficulty = HARD;
                startGame();
            }
        });
        comboBox1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    musicOn = e.getItem().toString().equals("开");
                }
            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    private void startGame() {
        // 根据难度选择设置背景
        switch (difficulty) {
            case SIMPLE:
                ImageManager.BACKGROUND_IMAGE = ImageManager.BACKGROUND_IMAGE;
                break;
            case COMMON:
                ImageManager.BACKGROUND_IMAGE = ImageManager.BACKGROUND_IMAGE2;
                break;
            case HARD:
                ImageManager.BACKGROUND_IMAGE = ImageManager.BACKGROUND_IMAGE3;
                break;
        }
        frame.dispose(); // 关闭难度选择窗口
    }

    public static int getSelectedDifficulty() {
        return difficulty;
    }

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

    public static boolean isMusicOn() {
        return musicOn;
    }

    public JPanel getMainPanel() {
        return Main;
    }
}