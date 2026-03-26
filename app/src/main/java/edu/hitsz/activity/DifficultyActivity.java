package edu.hitsz.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.R;
import edu.hitsz.application.DifficultySelection;

public class DifficultyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);

        findViewById(R.id.btn_easy).setOnClickListener(v -> {
            DifficultySelection.setDifficulty(DifficultySelection.SIMPLE);
            startGame();
        });

        findViewById(R.id.btn_normal).setOnClickListener(v -> {
            DifficultySelection.setDifficulty(DifficultySelection.COMMON);
            startGame();
        });

        findViewById(R.id.btn_hard).setOnClickListener(v -> {
            DifficultySelection.setDifficulty(DifficultySelection.HARD);
            startGame();
        });
    }

    private void startGame() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}