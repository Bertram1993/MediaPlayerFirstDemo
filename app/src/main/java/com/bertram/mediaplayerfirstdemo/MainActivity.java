package com.bertram.mediaplayerfirstdemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.media.MediaPlayer.SEEK_CLOSEST;


public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private final String TAG = "MyMainActivity";
    private final int TIME_DURATION = 3000;//3s  快进快退时间间隔

    private Button btnStart;
    private Button btnPause;
    private Button btnStop;
    private Button btnFastForward;
    private Button btnRewind;
    private ProgressBar videoProgressBar;
    private Spinner spinnerPlaySpeed;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private Context context;
    private boolean hasPrepared = false;
    private int duration = 0;
    List<String> playSpeedList;
    private ArrayAdapter<String> playSpeedListAdapter;

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initMediaPlayer();
        Log.i(TAG, "onCreate");
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlay();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
        Log.i(TAG, "onDestroy");
    }

/*    @Override
    protected void onStop() {
        super.onStop();
        stopPlay();
        Log.i(TAG, "onStop");
    }*/

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPlay();
        Log.i(TAG, "onResume");
    }

    private void initView(){
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById((R.id.btnStop));
        btnFastForward = findViewById(R.id.btnFastForward);
        btnRewind = findViewById(R.id.btnRewind);
        surfaceView = findViewById(R.id.videoView);
        videoProgressBar = findViewById(R.id.videoProgressBar);
        spinnerPlaySpeed = findViewById(R.id.SpinnerPlaySpeed);
        playSpeedList = new ArrayList<String>();
        playSpeedList.add("0.2倍速");
        playSpeedList.add("0.5倍速");
        playSpeedList.add("0.8倍速");
        playSpeedList.add("1.0倍速");
        playSpeedList.add("1.2倍速");
        playSpeedList.add("1.5倍速");
        playSpeedList.add("2.0倍速");
        playSpeedListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, playSpeedList);
        playSpeedListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlaySpeed.setAdapter(playSpeedListAdapter);

        context = this;
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pausePlay();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });
        btnFastForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seek();
            }
        });
        btnRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewind();
            }
        });
        spinnerPlaySpeed.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, String.valueOf(position));
                setPlayerSpeed(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initMediaPlayer(){
        mediaPlayer = new MediaPlayer();
        try {
            String url = Environment.getExternalStorageDirectory().getPath()+"/Test.mp4";
            Log.d(TAG, url);
            File file = new File(url);
            if (!file.exists()) {
                Log.d(TAG, "文件不存在");
            }
            //url = "https://www.apple.com/105/media/us/iphone-x/2017/01df5b43-28e4-4848-bf20-490c34a926a7/films/feature/iphone-x-feature-tpl-cc-us-20170912_1920x1080h.mp4";
            //url = "http://ksy.fffffive.com/mda-hinp1ik37b0rt1mj/mda-hinp1ik37b0rt1mj.mp4";
            mediaPlayer.setDataSource(context, Uri.parse(url));
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    hasPrepared = true;
                    duration = mp.getDuration();
                    videoProgressBar.setMax(duration);
                }
            });
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "失败");
        }
    }


    private boolean checkMediaPlayerHasPrepared(){
        if(!hasPrepared){
            Toast.makeText(this, "正在加载中。。。", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void startPlay() {
        if (mediaPlayer == null) {
            return;
        }
        if (!checkMediaPlayerHasPrepared()) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            return;
        }
        mediaPlayer.start();
        Log.d(TAG, "Start player");
    }

    private void stopPlay() {
        if (mediaPlayer == null) {
            return;
        }
        if (!checkMediaPlayerHasPrepared()) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }

    private void pausePlay() {
        if (mediaPlayer == null) {
            return;
        }
        if (!checkMediaPlayerHasPrepared()) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    private void setPlayerSpeed(int pos) {
        float speed = 1.0f;
        switch (pos) {
            case 0:
                speed = 0.2f;
                break;
            case 1:
                speed = 0.5f;
                break;
            case 2:
                speed = 0.8f;
                break;
            case 3:
                speed = 1.0f;
                break;
            case 4:
                speed = 1.2f;
                break;
            case 5:
                speed = 1.5f;
                break;
            case 6:
                speed = 2.0f;
                break;
                default:
        }
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
        } else {
            //mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
            //mediaPlayer.pause();
        }
    }

    private void seek() {
        if (mediaPlayer == null) {
            return;
        }
        if (!checkMediaPlayerHasPrepared()) {
            return;
        }

        int currentPos = mediaPlayer.getCurrentPosition();
        currentPos += TIME_DURATION;
        //int duration = mediaPlayer.getDuration();
        if (currentPos >= duration) {
            currentPos = duration;
        }

        if (Build.VERSION.SDK_INT >= 26) {
            mediaPlayer.seekTo(currentPos, SEEK_CLOSEST);//SEEK_CLOSEST 模式下快进快退定位比较准确
        } else {
            mediaPlayer.seekTo(currentPos);
        }
    }

    private void rewind() {
        if (mediaPlayer == null) {
            return;
        }
        if (!checkMediaPlayerHasPrepared()) {
            return;
        }

        int currentPos = mediaPlayer.getCurrentPosition();
        currentPos -= TIME_DURATION;
        // int duration = mediaPlayer.getDuration();
        if (currentPos <= 0) {
            currentPos = 0;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            mediaPlayer.seekTo(currentPos, SEEK_CLOSEST);
        } else {
            mediaPlayer.seekTo(currentPos);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated enter");

        if (mediaPlayer != null) {
            Log.d(TAG, "surfaceCreated");
            mediaPlayer.setDisplay(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
