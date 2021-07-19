package com.example.nothingbutthirty;
//참고 앱 - TextApp, VoteApp, splash, optionMenuApp

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    Button study, test, community, btnLogin, btnJoin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent1=new Intent(getApplicationContext(), Splash.class);
        startActivity(intent1);

        ActionBar bar1=getSupportActionBar();
        bar1.setTitle("겨우서른");
        bar1.setIcon(R.drawable.icon1);
        bar1.setDisplayShowHomeEnabled(true);

        study = findViewById(R.id.study);
        test = findViewById(R.id.test);
        community = findViewById(R.id.community);
        btnLogin = findViewById(R.id.btnLogin);
        btnJoin = findViewById(R.id.btnJoin);



        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), ListViewMain.class);
                startActivity(intent);
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), RandomTest.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }
        });




    }//onCreate 메소드 끝




}