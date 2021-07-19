package com.example.nothingbutthirty;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RandomTest extends AppCompatActivity {

    SQLiteOpenHelper helper;
    SQLiteDatabase sqldb;
    TextView tvKo, tvCh, tvLiteration;
    EditText inputText;
    Button btnRandom, btnAnswer;
    boolean check;
    boolean tvChCheck = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_test);

        ActionBar bar = getSupportActionBar();
        bar.setTitle("랜덤테스트");
        bar.setIcon(R.drawable.party);
        bar.setDisplayShowHomeEnabled(true); //액션바 아이콘을 보여주는 것
        bar.setDisplayHomeAsUpEnabled(true);//뒤로 가기 화살표

        tvKo = findViewById(R.id.tvKo); //한국어 표시
        tvCh = findViewById(R.id.tvCh);//중국어 표시
        tvLiteration = findViewById(R.id.tvLiteration); //한어병음 표시
        inputText = findViewById(R.id.inputText); //쓰기
        btnRandom = findViewById(R.id.btnRandom);
        btnAnswer = findViewById(R.id.btnAnswer); //정답 보기

        check = isCheckDB(this);
        if (!check) {
            copyDB(this); //return false면 디비가 없으므로 copy시작
        }
        sqldb = SQLiteDatabase.openDatabase("/data/data/com.example.nothingbutthirty/databases/one.db", null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = sqldb.rawQuery("select Subtitle, Transliteration, Translation from one1 order by random() limit 1" , null);
        while(cursor.moveToNext()){
            tvCh.setText(cursor.getString(0));
            tvLiteration.setText(cursor.getString(1));
            tvKo.setText(cursor.getString(2));

        }


//랜덤으로 대사 바꾸는 버튼
        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Cursor cursor =sqldb.rawQuery("SELECT Subtitle, Transliteration, Translation from one1 order by random() limit 1", null );
                while(cursor.moveToNext()) {
                    tvCh.setText(cursor.getString(0));
                    tvLiteration.setText(cursor.getString(1));
                    tvKo.setText(cursor.getString(2));
                }

                tvCh.setVisibility(View.INVISIBLE);
                tvLiteration.setVisibility(View.INVISIBLE);
            }
        });

        btnAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tvChCheck){
                    tvCh.setVisibility(View.INVISIBLE);
                    tvLiteration.setVisibility(View.INVISIBLE);
                    tvChCheck = true;
                }else{
                    tvCh.setVisibility(View.VISIBLE);
                    tvLiteration.setVisibility(View.VISIBLE);
                    tvChCheck = false;
                }
            }
        });




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

    //DB가 기존에 있는지 체크와 크기가 다른지 체크하는 메소드
    public boolean isCheckDB(Context context) {
        String filePath = "/data/data/com.example.nothingbutthirty/databases/one.db";
        File file = new File(filePath);
        long newdbSize = 0;
        long olddbSize = file.length(); //이전 DB파일 크기
        AssetManager manager = context.getAssets();
        try {
            InputStream inputS = manager.open("one.db");
            newdbSize = inputS.available(); //파일의 크기를 newdbSize에 넣음
        } catch (IOException e) { //파일명이 틀릴 수도 있기때문에 파일명을 다룰 때는 항상 try/catch 써야함.
            showToast("해당 파일을 읽을 수가 없습니다.");
        }

        if (file.exists()) { //파일이 존재하는지 체크
            if (newdbSize != olddbSize) { //
                return false; //oncreate로 올라감
            } else {
                return true; //이미 복사되어있다는 말
            }
        }
        return false;
    }

    //assets폴더에 있는 DB를 내부 앱 DB공간으로 복사(크기가 달라도 복사)
    public void copyDB(Context context) {
        AssetManager manager = context.getAssets();
        String folderPath = "/data/data/com.example.nothingbutthirty/databases";
        String filePath = "/data/data/com.example.nothingbutthirty/databases/one.db";
        File folder = new File(folderPath);
        File file = new File(filePath);
        FileOutputStream fileOS = null;
        BufferedOutputStream bufferOS = null; //처음부터 쌓아야하므로
        try {
            InputStream inputS = manager.open("one.db");
            BufferedInputStream bufferIS = new BufferedInputStream(inputS); //담는게 inputStream 보내는게 outputStream
            if (!folder.exists()) { //folder변수에 /data/data/com.example.nothingbutthirty/databases/이 들어있지 않다면
                folder.mkdir(); //folder 변수에 mkdir()가 폴더를 만드는 변수, 즉 폴더를 만들어라
            }
            if (file.exists()) { //file 변수에 /data/data/com.example.nothingbutthirty/databases/one.db가 존재하냐?
                file.delete(); //기존 db를 지우고
                file.createNewFile(); //복사한 db를 갖다놓는다.
            }
            fileOS = new FileOutputStream(file);// petHDB.db를 말함
            bufferOS = new BufferedOutputStream(fileOS); //오픈한 파일을
            int read = -1;
            int size = bufferIS.available();

            byte buffer[] = new byte[size];
            while ((read = bufferIS.read(buffer, 0, size)) != -1) {
                bufferOS.write(buffer, 0, read);
            } //사이즈만큼 다 읽었다면
            //비우고, 닫기
            bufferOS.flush();
            fileOS.close();
            bufferIS.close();
            inputS.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //토스트메소드
    void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }


}