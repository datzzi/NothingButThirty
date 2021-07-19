package com.example.nothingbutthirty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true); //액션바 아이콘을 보여주는 것
        bar.setDisplayHomeAsUpEnabled(true);//뒤로 가기 화살표


    }//onCreate메소드 끝

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