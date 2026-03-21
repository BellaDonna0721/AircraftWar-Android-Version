package edu.hitsz;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.BaseGame;
import edu.hitsz.application.GameExtend.NormalGame;

public class MainActivity extends AppCompatActivity {

    private BaseGame mBaseGame;
    private OnBackPressedCallback mBackPressedCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            System.out.println("MainActivity onCreate 开始...");
            
            // 初始化图片资源
            System.out.println("初始化图片资源...");
            ImageManager.init(this);
            System.out.println("图片资源初始化完成");

            // 创建游戏视图
            System.out.println("创建游戏视图...");
            mBaseGame = new NormalGame(this);
            System.out.println("游戏视图创建完成");
            
            setContentView(mBaseGame);
            System.out.println("ContentView 设置完成");

            // 注册返回键回调
            mBackPressedCallback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    // 关闭当前Activity
                    finish();
                }
            };
            // 将回调注册到Activity的返回键分发器
            getOnBackPressedDispatcher().addCallback(this, mBackPressedCallback);
            
            System.out.println("MainActivity onCreate 完成");
            
        } catch (Exception e) {
            System.err.println("MainActivity 初始化异常: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "应用初始化失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 注意：触屏事件已在BaseGame中通过setOnTouchListener处理
    // 不需要在Activity中再次转发
}