package com.tomhedges.numberdrop;

import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DroppingNumbers extends AppCompatActivity implements View.OnClickListener {

    private Button btnStart;
    private Button btnReset;
    private TextView topSquare01;
    private TextView topSquare02;
    private TextView topSquare03;
    private TextView topSquare04;
    private TextView topSquare05;
    private TextView tempText;
    //private AnimatorSet squareMove1;
    //private AnimatorSet squareMove3;
    private AnimationDrawable animationDrawable;
    private ObjectAnimator topSquareDrop;
    private ObjectAnimator topSquareReplace;
    private Timer timer;
    private TimerTask timerTask;
    private Drawable squareOrange;

    private Boolean gameRunning;
    private boolean boxDropped;
    //we are going to use a handler to be able to run in our TimerTask
    private final Handler handler = new Handler();
    final int min_delay = 4000;
    final int max_delay = 7000;
    final int drop_duration = 2000;
    final int drop_reset_height = 75;
    private float top_margin;
    private Random random;
    private long delay_memory;
    private CountDownTimer cdTimer;
    private TextView boxToDrop = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropping_numbers);

        gameRunning = false;
        boxDropped = false;
        //set a new Timer
        random = new Random();
        delay_memory = -100;

        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(this);

        //timer = new Timer(true);
        //timer.schedule(, 5000);

        //topSquare01 = (TextView)findViewById(R.id.topSquare01);
        //TextView square2 = (TextView)findViewById(R.id.topSquare03);

        //ValueAnimator colorAnim = ObjectAnimator.ofInt(square1, "backgroundColor", 0xffff7700, 0xffffffff);
        //colorAnim.setDuration(1000);
        //colorAnim.setEvaluator(new ArgbEvaluator());
        //colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        //colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        //colorAnim.start();

        //load the sun movement animation
        //squareMove1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.box_shift);
        //squareMove3 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.box_shift);
        //set the view as target
        //squareMove1.setTarget(square1);
        //squareMove3.setTarget(square

        tempText = findViewById(R.id.tempText);
        topSquare01 = findViewById(R.id.topSquare01);
        topSquare01.setBackgroundResource(R.drawable.square_animation_list_blue);
        topSquare02 = findViewById(R.id.topSquare02);
        topSquare02.setBackgroundResource(R.drawable.square_animation_list_blue);
        topSquare03 = findViewById(R.id.topSquare03);
        topSquare03.setBackgroundResource(R.drawable.square_animation_list_blue);
        topSquare04 = findViewById(R.id.topSquare04);
        topSquare04.setBackgroundResource(R.drawable.square_animation_list_blue);
        topSquare05 = findViewById(R.id.topSquare05);
        topSquare05.setBackgroundResource(R.drawable.square_animation_list_blue);

        selectSquare();
    }

    private void selectSquare() {
        pickDropper();
        setUpDropper();
    }

    private void pickDropper() {
        int btd = random.nextInt(5)+1;
        //tempText.setText(String.valueOf(btd));
        switch (btd) {
            case 1:
                boxToDrop = topSquare01;
                break;
            case 2:
                boxToDrop = topSquare02;
                break;
            case 3:
                boxToDrop = topSquare03;
                break;
            case 4:
                boxToDrop = topSquare04;
                break;
            case 5:
                boxToDrop = topSquare05;
                break;
        }
    }

    private void setUpDropper() {
        topSquareDrop = ObjectAnimator.ofFloat(boxToDrop, "y", 1400);
        topSquareDrop.setDuration(drop_duration);
        //topSquareDrop.setRepeatCount(ValueAnimator.INFINITE);
        //topSquareDrop.setRepeatMode(ValueAnimator.RESTART);
        topSquareDrop.setInterpolator(new AnticipateInterpolator());
        //topSquareDrop.start();

        topSquareReplace = ObjectAnimator.ofFloat(boxToDrop, "y", drop_reset_height);
        topSquareReplace.setDuration(1500);
        //topSquareDrop.setRepeatCount(ValueAnimator.INFINITE);
        //topSquareDrop.setRepeatMode(ValueAnimator.RESTART);
        topSquareReplace.setInterpolator(new BounceInterpolator());


        //topSquare02 = findViewById(R.id.topSquare02);

        boxToDrop.setBackgroundResource(R.drawable.square_animation_list_blue);
        animationDrawable = (AnimationDrawable) boxToDrop.getBackground();
        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(500);
        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(500);

        top_margin = boxToDrop.getY();
        tempText.setText(String.valueOf(top_margin));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameRunning) {
            if (animationDrawable != null && !animationDrawable.isRunning()) {
                // start the animation
                animationDrawable.start();
            }
            //onResume we start our timer so it can start when the app comes from the background
            //startTimer();
            //squareMove1.resume();
            //squareMove3.resume();
            topSquareDrop.resume();
            if (gameRunning) {
                //timerStart();
                countdownTimerStart();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseAnimation();
    }

    private void pauseAnimation() {
        if (animationDrawable != null && animationDrawable.isRunning()) {
            // stop the animation
            animationDrawable.stop();
        }
        //squareMove1.pause();
        //squareMove3.pause();
        topSquareDrop.pause();
        stoptimertask();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                top_margin = boxToDrop.getY();
                btnStart.setEnabled(false);
                btnReset.setEnabled(true);
                if (gameRunning) {
                    //nothing to do!
                } else {
                    gameRunning = true;
                    runAnimation();
                }
                break;

            case R.id.btnReset:
                btnReset.setEnabled(false);
                btnStart.setEnabled(true);
                if (gameRunning) {
                    gameRunning = false;
                    resetSquare();
                    delay_memory = -100;
                } else {
                    //nothing to do!
                }
                break;
        }
    }

    private void runAnimation() {
        //start the animation
        //squareMove1.start();
        //squareMove3.start();
        //timerStart();

        //topSquareDrop.start();
        //animationDrawable.start();
        countdownTimerStart();
    }

    private void timerStart() {
        timer = new Timer();
        initializeTimerTask();
        int random_delay = random.nextInt((max_delay - min_delay) + 1) + min_delay;
        boxToDrop.setText(String.valueOf(random_delay/1000));
        timer.schedule(timerTask, random_delay);
    }

    private void countdownTimerStart() {
        stoptimertask();
        int delay;
        if (delay_memory < 0) {
            delay = random.nextInt((max_delay - min_delay) + 1) + min_delay;
            delay += drop_duration;
        } else {
            delay = (int) delay_memory;
        }
        //boxToDrop.setText(String.valueOf(delay/1000));
        cdTimer = new CountDownTimer(delay, 100) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                delay_memory = millisUntilFinished;
                //boxToDrop.setText(String.valueOf(millisUntilFinished/1000));
                if (millisUntilFinished < 7000 && animationDrawable != null && !animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
                if (!boxDropped && millisUntilFinished <= (drop_duration+1000) && !topSquareDrop.isRunning()) {
                    boxDropped = true;
                    topSquareDrop.start();
                }
            }
            public void onFinish() {
                delay_memory = -100;
                resetSquare();
                selectSquare();
                runAnimation();
            }
        }.start();
    }

    private void resetSquare() {
        pauseAnimation();
        boxDropped = false;
        //topSquare02.setHeight(height);
        //topSquare02.setBackgroundResource(R.drawable.square_blue);
        //boxToDrop.setBackgroundResource(R.drawable.square_blue);
        //animationDrawable = (AnimationDrawable) boxToDrop.getBackground();
        // setting enter fade animation duration to 5 seconds
        //animationDrawable.setEnterFadeDuration(500);
        // setting exit fade animation duration to 2 seconds
        //animationDrawable.setExitFadeDuration(500);
        boxToDrop.setText("?");
        if (gameRunning) {
            boxToDrop.setY(top_margin - drop_reset_height);
            topSquareReplace.start();
        } else {
            boxToDrop.setY(top_margin);
            selectSquare();
        }
    }

    /**
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after 5000ms
        timer.schedule(timerTask, 5000);
    }
    */

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (cdTimer != null) {
            cdTimer.cancel();
            cdTimer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                //use a handler to run a toast that shows the current timestamp
                handler.post(new Runnable() {
                    public void run() {
                        runAnimation();
                    }
                });
            }
        };
    }
}
