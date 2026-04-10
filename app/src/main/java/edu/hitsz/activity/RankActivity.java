package edu.hitsz.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.hitsz.R;
import edu.hitsz.application.DifficultySelection;
import edu.hitsz.application.RankDatabase;

public class RankActivity extends AppCompatActivity {

    private Spinner spinnerDifficulty;
    private ListView listRank;

    private RankDatabase rankDatabase;
    private final List<RankDatabase.Record> recordList = new ArrayList<>();
    private RankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        spinnerDifficulty = findViewById(R.id.spinner_difficulty);
        listRank = findViewById(R.id.list_rank);

        rankDatabase = new RankDatabase(this);

        // 初始化难度下拉框
        String[] difficulties = new String[]{"Easy", "Normal", "Hard"};
        ArrayAdapter<String> diffAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                difficulties
        );
        diffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(diffAdapter);

        // 默认选中当前难度
        DifficultySelection diffSelection = new DifficultySelection();
        String currentDiff = diffSelection.getSelectedDifficultyDescription();
        int index = 0;
        for (int i = 0; i < difficulties.length; i++) {
            if (difficulties[i].equals(currentDiff)) {
                index = i;
                break;
            }
        }
        spinnerDifficulty.setSelection(index);

        spinnerDifficulty.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String diff = difficulties[position];
                loadRecords(diff);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // ignore
            }
        });

        adapter = new RankAdapter();
        listRank.setAdapter(adapter);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // 首次加载
        loadRecords(currentDiff);
    }

    private String currentDifficulty;

    private void loadRecords(String difficulty) {
        currentDifficulty = difficulty;
        refreshDisplay();
    }

    private void refreshDisplay() {
        recordList.clear();
        if (currentDifficulty != null) {
            List<RankDatabase.Record> records = rankDatabase.getRecords(currentDifficulty);
            recordList.addAll(records);
        }
        adapter.notifyDataSetChanged();
    }

    /** 自定义适配器：每行显示记录信息和一个删除按钮 */
    private class RankAdapter extends ArrayAdapter<RankDatabase.Record> {

        RankAdapter() {
            super(RankActivity.this, 0, recordList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(RankActivity.this);
                view = inflater.inflate(R.layout.item_rank, parent, false);
            }

            TextView tvInfo = view.findViewById(R.id.tv_rank_info);
            Button btnDelete = view.findViewById(R.id.btn_delete_rank);

            RankDatabase.Record record = getItem(position);
            if (record != null) {
                String text = "第" + (position + 1) + "名  " + record.playerName +
                        "  分数:" + record.score + "  时间:" + record.time;
                tvInfo.setText(text);

                btnDelete.setOnClickListener(v -> {
                    new AlertDialog.Builder(RankActivity.this)
                            .setTitle("删除记录")
                            .setMessage("确定删除第 " + (position + 1) + " 条记录吗？")
                            .setPositiveButton("确定", (DialogInterface dialog, int which) -> {
                                rankDatabase.deleteRecord(record.id);
                                refreshDisplay();
                            })
                            .setNegativeButton("取消", null)
                            .show();
                });
            }

            return view;
        }
    }
}
