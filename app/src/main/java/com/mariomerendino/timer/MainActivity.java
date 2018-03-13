package com.mariomerendino.timer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.*;
import android.widget.SeekBar;
import android.text.*;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.os.CountDownTimer;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
public class MainActivity extends AppCompatActivity {

    //declare UI Elements
    TextView SecondTextView;
    TextView MinuteTextView;
    TextView PTextView;
    TextView Status;
    TextView VolumeTextView;

    EditText SecondEditText;
    EditText MinuteEditText;

    SeekBar SecSeekBar;
    SeekBar MinSeekBar;
    SeekBar PSeekBar;
    SeekBar VSeekBar;

    //Variables for Edit Text Values
    int inputMin = 0;
    int inputSec = 0;

    //checks if enter key has been hit
    //for second EditText
    boolean secEnter = false;

    //Seekbar Values
    int seekBarSec = 0;
    int seekBarMin = 0;
    int PSeekbarVal = 0;
    double volume = 0.100;

    //Sound Effect
    MediaPlayer tick;
    MediaPlayer song;

    //Song timer in seconds
    int songTimeSec = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Link TextViews
        SecondTextView = (TextView) findViewById(R.id.SecondTextView);
        MinuteTextView = (TextView) findViewById(R.id.MinuteTextView);
        PTextView = (TextView) findViewById(R.id.percent);
        Status = (TextView) findViewById(R.id.status);
        VolumeTextView = (TextView)findViewById(R.id.volumeTextView);


        //Link EditTexts
        SecondEditText = (EditText) findViewById(R.id.secondsEdit);
        MinuteEditText = (EditText) findViewById(R.id.minutesEdit);
        //TextChangedListeners For Edit Texts
        SecondEditText.addTextChangedListener(editTextWatch);
        MinuteEditText.addTextChangedListener(editTextWatch);

        //Link Seekbar
        SecSeekBar = (SeekBar)findViewById(R.id.SecondSeekBar);
        MinSeekBar = (SeekBar)findViewById(R.id.MinuteSeekBar);
        PSeekBar = (SeekBar)findViewById(R.id.percentBar);
        VSeekBar = (SeekBar)findViewById(R.id.volumeSeekBar);
        //SeekbarListeners
        SecSeekBar.setOnSeekBarChangeListener(seekBarListener);
        MinSeekBar.setOnSeekBarChangeListener(seekBarListener);
        PSeekBar.setOnSeekBarChangeListener(seekBarListener);

        //Media Player for tick
        tick = MediaPlayer.create(MainActivity.this, R.raw.tick);
        song = MediaPlayer.create(MainActivity.this, R.raw.halftime);
        //THE INTIAL SKIP TO 23 SECONDS... PART OF THE LAB
        song.seekTo(songTimeSec*1000);
        song.setVolume((float) volume, (float) volume);

    }
    private final OnSeekBarChangeListener seekBarListener =
            new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {

                    if(seekBar.getId() == (R.id.SecondSeekBar) ){
                        SecSeekBar.setProgress(seekBarSec);
                    }
                    if(seekBar.getId() == (R.id.MinuteSeekBar) ) {
                        MinSeekBar.setProgress(seekBarMin);

                    }
                    if(seekBar.getId() == (R.id.percentBar) ) {
                        PSeekBar.setProgress((int) PSeekbarVal);
                    }
                    if(seekBar.getId() == (R.id.volumeSeekBar) ) {
                        VSeekBar.setProgress((int) (volume*100));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            };

    //Run after enter hit
    public void calculate(){
        int totalmili = ((inputMin * 60 ) + inputSec) * 1000;
        new CountDownTimer(totalmili+2000, 1000){
            double totalSeek;
            int color =  0;
            double totalInput =  ((inputMin * 60 ) + inputSec) *1.000;
            public void onTick(long milisUntilFinished){
                //Total time of seek bar in seconds
                totalSeek = ((seekBarMin * 60) + seekBarSec)*1.000;
                
                Status.setBackgroundColor(Color.argb(255, color, 0, color));
                Status.setTextColor(Color.argb(255,255-color, 0, 255-color));

                //Increment the seconds for the seekbar by 1 every second
                seekBarSec++;
                //if seconds is equal to 60:
                //set seconds back to 0, increment minutes
                if(seekBarSec == 60) {
                    seekBarMin++;
                    seekBarSec = 0;
                }
                //checks if tick is playing
                if(tick.isPlaying()){
                    tick.pause();
                    tick.seekTo(0);
                }
                tick.start();
                
                //Song Toggles...
                //When timer hits 0:12, skip 6 seconds of song.
                if(seekBarSec == 12 && seekBarMin == 0){
                    song.pause();
                    songTimeSec = songTimeSec + seekBarSec + 6;
                    song.seekTo(songTimeSec*1000);
                    song.start();
                }
                //when timer hits 0:24, Stop song
                if(seekBarSec == 24 && seekBarMin == 0){
                    song.pause();
                }

                //Volume changes on a sin curve, with a perocidy of 7 seconds.
                volume = (Math.sin((double)((2*Math.PI)/7 * totalSeek))+1.0)*0.9/2.0+0.1;
                song.setVolume((float) volume, (float) volume);

                //Calculate color & percentage completed
                PSeekbarVal = (int)(totalSeek/totalInput*100);
                color = (int)(((totalSeek/totalInput)*1.000)* 255);

                //SET PROGRESS OF SEEKBARS
                MinSeekBar.setProgress(seekBarMin);
                SecSeekBar.setProgress(seekBarSec);
                PSeekBar.setProgress(PSeekbarVal);
                VSeekBar.setProgress((int) (volume * 100));

                //SET TEXTS OF ALL TEXT VIEWS THAT NEED TO BE UPDATED
                SecondTextView.setText(Integer.toString(seekBarSec));
                MinuteTextView.setText(Integer.toString(seekBarMin));
                VolumeTextView.setText(Integer.toString((int) (volume * 100)));
                PTextView.setText(Integer.toString((int) PSeekbarVal));
                Status.setText("Time: " + Integer.toString(seekBarMin) + ":" +
                        Integer.toString(seekBarSec) + "\nTime Remaining: " +
                        Integer.toString(inputMin - seekBarMin) + ":" +
                        Integer.toString(inputSec - seekBarSec));
            }
            public void onFinish(){
                secEnter = false;
                tick.pause();
                song.pause();
                song.seekTo(23000);
                tick.seekTo(0);
                PSeekBar.setProgress(100);
                MinSeekBar.setProgress(inputMin);
                SecSeekBar.setProgress(inputSec);
                MinuteTextView.setText(Integer.toString(inputMin));
                SecondTextView.setText(Integer.toString(inputSec));
                Status.setText("Finished\nClick Me to Restart");
            }
        }.start();
    }

    private final TextWatcher editTextWatch = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Status.setText("Waiting");
            //Second Edit
            if (SecondEditText.getText().hashCode() == s.hashCode()) {
                try {
                    if (s.charAt(s.length() - 1) == '\n') {
                        SecondEditText.getText().replace(start, start + 1, "");
                        secEnter = true;
                    }
                    else {
                        secEnter = false;
                        inputSec = Integer.valueOf(s.toString());
                    }
                }
                catch (NumberFormatException e) {

                }
            }
            //Minute Edit
            if (MinuteEditText.getText().hashCode() == s.hashCode()) {
                try {
                    inputMin = Integer.valueOf(s.toString());
                }
                catch (NumberFormatException e) {

                }
            }
            //If enter key is hit on second Edit Text
            if(secEnter){
                song.start();
                calculate();
            }
            else{
                PSeekbarVal = 0;
                PSeekBar.setProgress(PSeekbarVal);
                PTextView.setText(Integer.toString(PSeekbarVal));
            }
        }

        @Override
        public void afterTextChanged(Editable s) { }

        @Override
        public void beforeTextChanged(
                CharSequence s, int start, int count, int after) { }
    };

    //RESET FUNCTION
    public void reset(View v){
        secEnter = false;

        //Reset vars
        songTimeSec = 23;
        PSeekbarVal = 0;
        seekBarSec = 0;
        seekBarMin = 0;
        inputMin = 0;
        inputSec = 0;
        volume = 0.1;

        //Reset text Views & Edit
        MinuteTextView.setText("0");
        SecondTextView.setText("0");
        VolumeTextView.setText("10");

        MinuteEditText.setText("0");
        SecondEditText.setText("0");

        Status.setText("Waiting");
        Status.setTextColor(Color.argb(255, 255, 0, 0));
        Status.setBackgroundColor(Color.argb(0, 0, 0, 0));

        //Progress bars reset
        SecSeekBar.setProgress(0);
        MinSeekBar.setProgress(0);
        PSeekBar.setProgress(0);
        VSeekBar.setProgress(10);

        //MediaPlayer Resets
        song.seekTo(23000);
        tick.seekTo(0);
    }

}
