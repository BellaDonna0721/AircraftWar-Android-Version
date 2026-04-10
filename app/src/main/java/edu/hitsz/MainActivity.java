package edu.hitsz;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.activity.DifficultyActivity;
import edu.hitsz.activity.RankActivity;
import edu.hitsz.application.DifficultySelection;
import android.widget.Switch;
import android.media.MediaPlayer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化音乐开关状态
        Switch switchMusic = findViewById(R.id.switch_music);
        if (switchMusic != null) {
            // 反转逻辑：确保 Switch 勾选状态对应“有声音”这一直觉行为
            switchMusic.setChecked(DifficultySelection.isMusicOn());
            switchMusic.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // isChecked = 用户界面上的状态（勾选表示希望有声音）
                boolean newMusicOn = isChecked;
                DifficultySelection.setMusicOn(newMusicOn);
                // 当实际开启音效时，播放短音效作为反馈；关闭则不播放
                try {
                    if (newMusicOn) {
                        MediaPlayer mp = MediaPlayer.create(this, R.raw.bullet_shoot);
                        if (mp != null) {
                            mp.setOnCompletionListener(MediaPlayer::release);
                            mp.start();
                        }
                    }
                } catch (Exception ignored) {
                }
            });
        }

        findViewById(R.id.btn_single).setOnClickListener(v -> {
            Intent intent = new Intent(this, DifficultyActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.btn_rank).setOnClickListener(v -> {
            Intent intent = new Intent(this, RankActivity.class);
            startActivity(intent);
        });
    }


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        try {
//            System.out.println("MainActivity onCreate 开始...");
//
//            // 初始化图片资源
//            System.out.println("初始化图片资源...");
//            ImageManager.init(this);
//            System.out.println("图片资源初始化完成");
//
//            // 根据 DifficultySelection 中的默认值创建对应游戏视图
//            int difficulty = DifficultySelection.getSelectedDifficulty();
//            System.out.println("当前选择难度: " + difficulty);
//
//            switch (difficulty) {
//                case DifficultySelection.SIMPLE:
//                    mBaseGame = new EasyGame(this);
//                    break;
//                case DifficultySelection.COMMON:
//                    mBaseGame = new NormalGame(this);
//                    break;
//                case DifficultySelection.HARD:
//                    mBaseGame = new HardGame(this);
//                    break;
//                default:
//                    mBaseGame = new NormalGame(this);
//            }
//            System.out.println("游戏视图创建完成: " + mBaseGame.getClass().getSimpleName());
//
//            setContentView(mBaseGame);
//            System.out.println("ContentView 设置完成");
//
//            // 注册返回键回调
//            mBackPressedCallback = new OnBackPressedCallback(true) {
//                @Override
//                public void handleOnBackPressed() {
//                    // 关闭当前Activity
//                    finish();
//                }
//            };
//            // 将回调注册到Activity的返回键分发器
//            getOnBackPressedDispatcher().addCallback(this, mBackPressedCallback);
//
//            System.out.println("MainActivity onCreate 完成");
//
//        } catch (Exception e) {
//            System.err.println("MainActivity 初始化异常: " + e.getMessage());
//            e.printStackTrace();
//            Toast.makeText(this, "应用初始化失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    }

    // 注意：触屏事件已在BaseGame中通过setOnTouchListener处理
    // 不需要在Activity中再次转发
}