package com.example.nothingbutthirty;
//참고: ListView
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ListViewMain extends AppCompatActivity {


    ListView listView; //리스트뷰
    ArrayAdapter<String> adapter;

    String[] epNumIDs = {"1화", "2화", "3화", "4화", "5화", "6화", "7화", "8화", "9화", "10화", "11화", "12화", "13화", "14화", "15화", "16화", "17화", "18화", "19화", "20화", "21화",
            "22화", "23화", "24화", "25화", "26화", "27화", "28화", "29화", "30화", "31화", "32화", "33화", "34화", "35화", "36화", "37화", "38화", "39화", "40화", "41화", "42화", "43화"};
    String[] epTitleIDs = {"第 1 季：第 1 第 1 集", "第 1 季：第 2 第 2 集", "第 1 季：第 3 第 3 集", "第 1 季：第 4 第 4 集", "第 1 季：第 5 第 5 集", "第 1 季：第 6 第 6 集",
            "第 1 季：第 7 第 7 集", "第 1 季：第 8 第 8 集", "第 1 季：第 9 第 9 集", "第 1 季：第 10 第 10 集", "第 1 季：第 11 第 11 集", "第 1 季：第 12 第 12 集", "第 1 季：第 13 第 13 集",
            "第 1 季：第 14 第 14 集", "第 1 季：第 15 第 15 集", " 第 1 季：第 16 第 16 集", "第 1 季：第 17 第 17 集", "第 1 季：第 18 第 18 集", "第 1 季：第 19 第 19 集", "第 1 季：第 20 第 20 集",
            "第 1 季：第 21 第 21 集", "第 1 季：第 22 第 22 集", "第 1 季：第 23 第 23 集", "第 1 季：第 24 第 24 集", "第 1 季：第 25 第 25 集", "第 1 季：第 26 第 26 集", "第 1 季：第 27 第 27 集",
            "第 1 季：第 28 第 28 集", "第 1 季：第 29 第 29 集", "第 1 季：第 30 第 30 集", "第 1 季：第 31 第 31 集", "第 1 季：第 32 第 32 集", "第 1 季：第 33 第 33 集", "第 1 季：第 34 第 34 集",
            "第 1 季：第 35 第 35 集", "第 1 季：第 36 第 36 集", "第 1 季：第 37 第 37 集", "第 1 季：第 38 第 38 集", "第 1 季：第 39 第 39 集", "第 1 季：第 40 第 40 集", "第 1 季：第 41 第 41 集",
            "第 1 季：第 42 第 42 集", "第 1 季：第 43 第 43 集"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episodelistview);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("리스트");
        bar.setIcon(R.drawable.party);
        bar.setDisplayShowHomeEnabled(true); //액션바 아이콘을 보여주는 것
        bar.setDisplayHomeAsUpEnabled(true);//뒤로 가기 화살표


        listView = findViewById(R.id.listView);


        adapter = new ArrayAdapter<String>(ListViewMain.this, android.R.layout.simple_list_item_1,epNumIDs);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getApplicationContext(), EpisodeMain.class);
                intent.putExtra("page", epNumIDs[position]);
                startActivity(intent);

            }
        });
    }//onCreate 메소드 끝

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //뒤로가기 버튼
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}