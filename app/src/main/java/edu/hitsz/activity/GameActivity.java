package edu.hitsz.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.application.BaseGame;
import edu.hitsz.application.DifficultySelection;
import edu.hitsz.application.GameExtend.EasyGame;
import edu.hitsz.application.GameExtend.HardGame;
import edu.hitsz.application.GameExtend.NormalGame;
import edu.hitsz.application.ImageManager;

public class GameActivity extends AppCompatActivity {

    private BaseGame mBaseGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageManager.init(this);

        int difficulty = DifficultySelection.getSelectedDifficulty();

        switch (difficulty) {
            case DifficultySelection.SIMPLE:
                mBaseGame = new EasyGame(this);
                break;
            case DifficultySelection.COMMON:
                mBaseGame = new NormalGame(this);
                break;
            case DifficultySelection.HARD:
                mBaseGame = new HardGame(this);
                break;
            default:
                mBaseGame = new NormalGame(this);
        }

        setContentView(mBaseGame);
    }
}
