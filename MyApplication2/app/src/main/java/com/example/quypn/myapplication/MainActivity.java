package com.example.quypn.myapplication;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;

import static com.example.quypn.myapplication.R.id.txtCurrentTime;
import static com.example.quypn.myapplication.R.id.txtCurrentTime2;

public class MainActivity extends AppCompatActivity {
    private SeekBar seekBar;
    private Button button;
    private TextView textView, textView2;
    private MediaPlayer mediaPlayer;
    private String Audio_path = "http://s1mp3.r17s101.vcdn.vn/228ebf35e5710c2f5560/4772889656698323819?key=jY_WTCQoIj7XZK0rkX_0Jg&expires=1490995686&filename=Where-U-At-Teayang.mp3";
    private int total_time = 0, counter = 0;
    private Boolean audio_Available = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init() {
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        button = (Button) findViewById(R.id.sbAudio);
        textView = (TextView) findViewById(txtCurrentTime);
        textView2 = (TextView) findViewById(txtCurrentTime2);
        handler = new Handler();

        button.setOnClickListener(onPlay_Click);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(OnChange_seekbar);

        try {


            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
            mediaPlayer.setLooping(true);
            mediaPlayer.setDataSource(Audio_path);
            mediaPlayer.setOnPreparedListener(OnPrepare_Audio);
            mediaPlayer.setOnBufferingUpdateListener(OnBuffering);
            mediaPlayer.prepareAsync();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private MediaPlayer.OnBufferingUpdateListener OnBuffering = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

            /*
             percent: % audio đã load được
             */
            seekBar.setSecondaryProgress(percent);

        }
    };
    private MediaPlayer.OnPreparedListener OnPrepare_Audio = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {

            total_time = mediaPlayer.getDuration();

            int minute = total_time / 1000;


            int second_number = minute % 60;
            int minute_number = ( minute - second_number ) / 60;
            Log.w("aaa", String.valueOf(minute_number));
            textView2.setText(minute_number + ":" + second_number);
            audio_Available = true;

        }
    };

    private SeekBar.OnSeekBarChangeListener OnChange_seekbar = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            int number = (progress*total_time/1000)/100 ;
            int second_number = number % 60;
            int minute_number = ( number - second_number ) / 60;

            textView.setText(minute_number + ":" + second_number);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            /*
                Lấy ra % seekBar đã kéo
             */
            int percent = seekBar.getProgress();


            /*
                Số giấy mà seekbar dịch đến
             */
            int currentTime_SeekTo = (percent * total_time) / 100;
            counter = currentTime_SeekTo / 1000;

            int second_number = counter % 60;
            int minute_number = ( counter - second_number ) / 60;

            mediaPlayer.seekTo(currentTime_SeekTo);
            textView.setText(minute_number + ":" + second_number);
        }
    };


    private View.OnClickListener onPlay_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (audio_Available) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();

                    /*
                        đếm thời gian phát nhạc
                     */
                    countTimer();
                    button.setText("Pause");
                } else {
                    handler.removeCallbacks(Timer_Counter);
                    mediaPlayer.pause();
                    button.setText("Play");
                }
            }
        }
    };

    private void countTimer() {
        handler.postDelayed(Timer_Counter, 1000);
    }

    private Runnable Timer_Counter = new Runnable() {
        @Override
        public void run() {

            counter++;

            int second_number = counter % 60;
            int minute_number = ( counter - second_number ) / 60;

            textView.setText(minute_number + ":" + second_number);


            int currentPercent = 100 * mediaPlayer.getCurrentPosition() / total_time;
            seekBar.setProgress(currentPercent);
            if (counter >= (total_time/1000)) counter = 0;
            countTimer();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        handler.removeCallbacks(Timer_Counter);
    }
}
