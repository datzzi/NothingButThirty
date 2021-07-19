package com.example.nothingbutthirty;
//1화 대사가 나오는 콘텐츠 부분, 참고: DataBaseEx, PetHospitalManager

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class EpisodeMain extends AppCompatActivity  implements TextPlayer, View.OnClickListener{

      boolean check;
      boolean tvKoCheck=false;
      View dialogueView;

      Bundle params = new Bundle();
      BackgroundColorSpan colorSpan = new BackgroundColorSpan(Color.YELLOW);

      ImageView imgDialogue;
      TextToSpeech tts;
      Button btnPlay, btnPause, btnStop, btnPrev, btnNext, btnBulb;
      TextView tvCh, tvLiteration, tvKo, tvHelper;
      EditText inputText;

      SQLiteDatabase sqlDB;
      Cursor cursor;

      PlayState playstate = PlayState.STOP;
      Spannable spannable;
      int standbyIndex = 0;
      int lastPlayIndex = 0;

      ArrayList<String> ch, literation, ko; //동적배열에 데이터 담음

      int dataPos=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodemain);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true); //액션바 아이콘을 보여주는 것
        bar.setDisplayHomeAsUpEnabled(true);//뒤로 가기 화살표

        Intent intent = getIntent();
//        String[] epNumIDs = intent.getStringArrayListExtra().get()


        ch = new ArrayList<String>();
        literation = new ArrayList<String>();
        ko = new ArrayList<String>();

        initTTS();
        initView();

        check = isCheckDB(this);
        if (!check) {
            copyDB(this); //return false면 디비가 없으므로 copy시작
        }
        sqlDB = SQLiteDatabase.openDatabase("/data/data/com.example.nothingbutthirty/databases/one.db", null, SQLiteDatabase.OPEN_READONLY);
        cursor = sqlDB.rawQuery("select Subtitle, Transliteration, Translation from one1 order by Number asc; " , null);
        while(cursor.moveToNext()){
//           a=a+1; //총 데이터 값 알아보기
           /* tvCh.setText(cursor.getString(0));
            tvLiteration.setText(cursor.getString(1));
            tvKo.setText(cursor.getString(2));*/
            ch.add(cursor.getString(0));
            literation.add(cursor.getString(1));
            ko.add(cursor.getString(2));
            }
        tvCh.setText(ch.get(0));
        tvLiteration.setText(literation.get(0));
        tvKo.setText(ko.get(0));

//        Toast.makeText(getApplicationContext(),"반복값:"+ a, Toast.LENGTH_SHORT).show(); //총 데이터 값 알아보기

        btnPrev = findViewById(R.id.btnPrev);
        btnNext = findViewById(R.id.btnNext);
        btnBulb = findViewById(R.id.btnBulb);




//도움말 다이알로그 띄우기
        btnBulb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogueView = View.inflate(EpisodeMain.this, R.layout.dialogue, null);
                tvHelper= dialogueView.findViewById(R.id.tvHelper);
                AlertDialog.Builder builder = new AlertDialog.Builder(EpisodeMain.this);
                builder.setTitle("도움말");
                builder.setIcon(R.drawable.lightbulb);
                builder.setView(dialogueView);

                builder.setNegativeButton("닫기", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

//텍스트뷰에 뿌리기






 //이전 대사 나타내기
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPos--; //위치값 하나씩 빼기
                if(dataPos<0){ //위치값이 맨 처음에서 이전버튼 누른다면
                    dataPos=ch.size()-1; //위치값이 필드(중국어) 크기가 같다면 가장 마지막 데이터값으로 간다. 인덱스는 0부터이기때문에  -1 해줘야한다.
                }
                tvCh.setText(ch.get(dataPos)); //ch동적배열에 들어가 있는 위치값을 들고와서 텍스트뷰 tvCH에 뿌린다.
                tvLiteration.setText(literation.get(dataPos));
                tvKo.setText(ko.get(dataPos));

            }
        });

//다음 대사 나타내기
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataPos++;
                if(dataPos>ch.size()-1){ //위치값이 ch동적배열 크기보다 크다면 = 맨 마지막 데이터에서 다음 버튼 눌렀을 때
                    dataPos=0; //데이터 맨 처음 값으로 간다.
                }
                tvCh.setText(ch.get(dataPos));
                tvLiteration.setText(literation.get(dataPos));
                tvKo.setText(ko.get(dataPos));

            }
        });




    } //onCreate 메소드 끝

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
            if (!folder.exists()) { //folder변수에 /data/data/com.example.pethospitalmanager/databases/이 들어있지 않다면
                folder.mkdir(); //folder 변수에 mkdir()가 폴더를 만드는 변수, 즉 폴더를 만들어라
            }
            if (file.exists()) { //file 변수에 /data/data/com.example.pethospitalmanager/databases/petHDB.db가 존재하냐?
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














     void initView() {
        btnPlay = findViewById(R.id.btnPlay);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);
        inputText = findViewById(R.id.inputText);
        tvCh = findViewById(R.id.tvCh);
        tvKo = findViewById(R.id.tvKo);
        tvLiteration = findViewById(R.id.tvLiteration);

        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }



    void initTTS() {
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int state) {
                if(state == TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.CHINESE);
                }else{
                    showToast("TTS 객체 초기화 중 문제가 발생했습니다.");
                }
            }
        });

       tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
           @Override
           public void onStart(String s) {

           }

           @Override
           public void onDone(String s) {
               clearAll();
           }

           @Override
           public void onError(String s) {
               showToast("재생 중 에러가 발생했습니다.");
           }

           @Override
           public void onRangeStart(String utteranceId, int start, int end, int frame) {
               changeHighlight(standbyIndex + start, standbyIndex + end);
               lastPlayIndex = start;
           }
       });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnPlay:
                startPlay();
                break;
            case R.id.btnPause:
                pausePlay();
                break;
            case R.id.btnStop:
                stopPlay();
                break;
        }
        showToast(playstate.getState());
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startPlay() {
        String content = tvCh.getText().toString();
        if(playstate.isStopping() && !tts.isSpeaking()){
     //       setContentFromTvCh(content);
            startSpeak(content);
        }else if(playstate.isWaiting()){
            standbyIndex += lastPlayIndex;
            startSpeak(content.substring(standbyIndex));
        }
        tvCh.setText(content, TextView.BufferType.SPANNABLE);
        spannable = (SpannableString) tvCh.getText();
    }

    @Override
    public void pausePlay() {
        if(playstate.isPlaying()){
            playstate = playstate.WAIT;
            tts.stop();
        }
    }

    @Override
    public void stopPlay() {
        tts.stop();
        clearAll();
    }


//하이라이트 메소드 - 직접
     void changeHighlight(final int start, final int end){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spannable.setSpan(colorSpan, start, end, spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        });
    }


 //텍스트 변경- 할 필요없음.
     void setContentFromTvCh(String content){

     }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
     void startSpeak(String text){
        tts.speak(text, TextToSpeech.QUEUE_ADD, params, text);
    }

     void clearAll(){
        playstate = playstate.STOP;
        standbyIndex = 0;
        lastPlayIndex = 0;

        if(spannable != null){
            changeHighlight(0,0); //하이라이트 제거
        }
    }


    //토스트
     void showToast(final String msg){
        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_LONG).show();
    }



    @Override
    protected void onPause() {
        if(playstate.isPlaying()){
            pausePlay();
        }
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        if(playstate.isWaiting()){
            startPlay();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        tts.stop();
        tts.shutdown();
        super.onDestroy();
    }
}