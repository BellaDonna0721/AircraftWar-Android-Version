package edu.hitsz.application;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Table extends JFrame {
    private JPanel mainPanel;
    private JLabel label2;
    private JLabel label3;
    private JTable Table1;
    private JButton DeleteButton;
    private DefaultTableModel model;
    private JScrollPane scrollPane; // 添加滚动面板
    
    // 定义不同难度的文件名
    private static final String EASY_FILE = "./score_easy.csv";
    private static final String COMMON_FILE = "./score_normal.csv";
    private static final String HARD_FILE = "./score_hard.csv";
    
    private String currentFile; // 当前使用的文件

    public Table(String difficulty) {
        // 设置窗口标题
        super("游戏结束 - 排行榜");
        
        // 初始化主面板
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // 创建标签面板
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        
        // 创建难度标签面板（左对齐）
        JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        label2 = new JLabel();
        label2.setFont(new Font("宋体", Font.PLAIN, 16));
        difficultyPanel.add(label2);

        // 创建标题标签面板（居中）
        JPanel titlePanel = new JPanel(new BorderLayout());
        label3 = new JLabel("排行榜", SwingConstants.CENTER); // 居中对齐
        label3.setFont(new Font("宋体", Font.BOLD, 20));
        titlePanel.add(label3, BorderLayout.CENTER);


        // 将两个面板添加到标签面板
        labelPanel.add(difficultyPanel);
        labelPanel.add(titlePanel);
        
        // 根据难度设置标签和文件
        switch (difficulty) {
            case "Easy":
                label2.setText("难度：Easy");
                currentFile = EASY_FILE;
                break;
            case "Normal":
                label2.setText("难度：Normal");
                currentFile = COMMON_FILE;
                break;
            case "Hard":
                label2.setText("难度：Hard");
                currentFile = HARD_FILE;
                break;
            default:
                label2.setText("难度：未知");
                currentFile = "score_records.csv";
        }
        
        // 设置标题
        label3.setText("排行榜");

        // 初始化表格
        String[] columnNames = {"排名", "玩家名", "分数", "时间"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        // 创建表格和设置模型
        Table1 = new JTable();
        Table1.setModel(model);
        Table1.setFillsViewportHeight(true);
        
        // 设置表格列宽
        Table1.getColumnModel().getColumn(0).setPreferredWidth(50);
        Table1.getColumnModel().getColumn(1).setPreferredWidth(150);
        Table1.getColumnModel().getColumn(2).setPreferredWidth(100);
        Table1.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        // 创建滚动面板并添加表格
        scrollPane = new JScrollPane(Table1);
        
        // 添加组件到面板
        labelPanel.add(label2);
        labelPanel.add(label3);
        mainPanel.add(labelPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel();
        DeleteButton = new JButton("删除选中记录");
        buttonPanel.add(DeleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 加载历史记录
        loadScores();
        
        // 删除按钮事件
        DeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = Table1.getSelectedRow();
                if (selectedRow != -1) {
                    int result = JOptionPane.showConfirmDialog(
                        Table.this,
                        "确定要删除这条记录吗？",
                        "确认删除",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (result == JOptionPane.YES_OPTION) {
                        model.removeRow(selectedRow);
                        saveScores(); // 保存更改
                    }
                } else {
                    JOptionPane.showMessageDialog(Table.this, "请先选择要删除的记录");
                }
            }
        });

        // 设置窗口
        this.setContentPane(mainPanel);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void loadScores() {
        System.out.println("尝试加载分数文件：" + currentFile);
        File file = new File(currentFile);
        if (!file.exists()) {
            System.out.println("文件不存在，创建新的记录文件：" + file.getAbsolutePath());
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("创建文件失败：" + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        System.out.println("开始读取文件：" + file.getAbsolutePath());
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            model.setRowCount(0); // 清空现有数据
            
            // 读取所有记录到列表中
            List<Object[]> records = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        String playerName = parts[0];
                        int score = Integer.parseInt(parts[1].trim());
                        String time = parts[2];
                        records.add(new Object[]{0, playerName, score, time});
                    } catch (NumberFormatException e) {
                        System.out.println("无效的分数格式：" + parts[1]);
                    }
                }
            }
            
            // 按分数降序排序
            records.sort((a, b) -> Integer.compare((Integer)b[2], (Integer)a[2]));
            
            // 添加到表格中
            int rank = 1;
            for (Object[] record : records) {
                record[0] = rank++;
                model.addRow(record);
            }
            
            System.out.println("加载完成，共读取 " + records.size() + " 条记录");
        } catch (IOException e) {
            System.out.println("无法读取记录文件：" + currentFile);
            e.printStackTrace();
        }
    }

    private void addNewScore(String playerName, int score) {
        // 获取当前时间
        String time = new java.text.SimpleDateFormat("MM-dd HH:mm")
            .format(new java.util.Date());
        
        // 添加新记录
        model.addRow(new Object[]{
            model.getRowCount() + 1,
            playerName,
            score,
            time
        });
        
        // 保存到文件
        saveScores();
        
        // 重新加载并排序数据
        loadScores();
    }

    private void saveScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentFile))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                writer.printf("%s,%d,%s\n",
                    model.getValueAt(i, 1), // 玩家名
                    Integer.parseInt(model.getValueAt(i, 2).toString()), // 分数
                    model.getValueAt(i, 3)  // 时间
                );
            }
        } catch (IOException e) {
            System.out.println("无法保存记录到文件：" + currentFile);
            e.printStackTrace();
        }
    }


}
