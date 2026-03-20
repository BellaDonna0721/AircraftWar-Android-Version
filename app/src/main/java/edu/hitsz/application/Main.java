package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;
// 导入 DifficultySelection 类
import edu.hitsz.application.DifficultySelection;
import edu.hitsz.application.GameExtend.EasyGame;
import edu.hitsz.application.GameExtend.HardGame;
import edu.hitsz.application.GameExtend.NormalGame;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;  //定义窗口宽
    public static final int WINDOW_HEIGHT = 768; //定义窗口高

    public static void main(String[] args) {
        System.out.println("Hello Aircraft War");

        // 先显示难度选择窗口
        JFrame diffFrame = new JFrame("难度选择");
        DifficultySelection diffSelection = new DifficultySelection(diffFrame);
        diffFrame.setContentPane(diffSelection.getMainPanel());
        diffFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        diffFrame.pack();
        diffFrame.setLocationRelativeTo(null);
        diffFrame.setVisible(true);

        // 等待难度选择窗口关闭
        while (diffFrame.isVisible()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

// 根据难度创建 Game 子类实例
        Game game;
        int selectedDifficulty = DifficultySelection.getSelectedDifficulty();
        switch (selectedDifficulty) {
            case DifficultySelection.SIMPLE:
                game = new EasyGame();
                break;
            case DifficultySelection.COMMON:
                game = new NormalGame();
                break;
            case DifficultySelection.HARD:
                game = new HardGame();
                break;
            default:
                game = new EasyGame();
        }

        // 初始化游戏窗口
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        JFrame frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.setVisible(true);
        game.action();
    }
}
