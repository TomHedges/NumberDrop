package com.tomhedges.numberdrop;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DroppingNumbers extends AppCompatActivity implements View.OnClickListener {

    private Button button;
    private TextView topSquare02;
    private AnimatorSet squareMove1;
    private AnimatorSet squareMove2;
    private AnimationDrawable animationDrawable;
    private ObjectAnimator topSquareDrop;
    private Timer timer;
    private TimerTask timerTask;

    private Boolean gameRunning;
    //we are going to use a handler to be able to run in our TimerTask
    private final Handler handler = new Handler();
    final int min_delay = 1000;
    final int max_delay = 10000;
    private Random random;
    private long delay_memory;
    private CountDownTimer cdTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dropping_numbers);

        gameRunning = false;
        //set a new Timer
        random = new Random();

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);

        //timer = new Timer(true);
        //timer.schedule(, 5000);

        //get the sun view
        TextView square1 = (TextView)findViewById(R.id.topSquare01);
        TextView square2 = (TextView)findViewById(R.id.topSquare03);

        //ValueAnimator colorAnim = ObjectAnimator.ofInt(square1, "backgroundColor", 0xffff7700, 0xffffffff);
        //colorAnim.setDuration(1000);
        //colorAnim.setEvaluator(new ArgbEvaluator());
        //colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        //colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        //colorAnim.start();

        //load the sun movement animation
        squareMove1 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.box_shift);
        squareMove2 = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.box_shift);
        //set the view as target
        squareMove1.setTarget(square1);
        squareMove2.setTarget(square2);

        topSquare02 = (TextView) findViewById(R.id.topSquare02);
        topSquare02.setTextSize(16);
        topSquareDrop = ObjectAnimator.ofFloat(topSquare02, "y", 800);
        topSquareDrop.setDuration(3000);
        //topSquareDrop.setRepeatCount(ValueAnimator.INFINITE);
        //topSquareDrop.setRepeatMode(ValueAnimator.RESTART);
        topSquareDrop.setInterpolator(new AnticipateInterpolator());
        //topSquareDrop.start();

        square1.setBackgroundResource(R.drawable.square_animation_list);
        // initializing animation drawable by getting background from square_orange
        animationDrawable = (AnimationDrawable) square1.getBackground();
        // setting enter fade animation duration to 5 seconds
        animationDrawable.setEnterFadeDuration(500);
        // setting exit fade animation duration to 2 seconds
        animationDrawable.setExitFadeDuration(500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && gameRunning && !animationDrawable.isRunning()) {
            // start the animation
            animationDrawable.start();
        }
        //onResume we start our timer so it can start when the app comes from the background
        //startTimer();
        squareMove1.resume();
        squareMove2.resume();
        topSquareDrop.resume();
        if (gameRunning) {
            //timerStart();
            countdownTimerStart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            // stop the animation
            animationDrawable.stop();
        }
        squareMove1.pause();
        squareMove2.pause();
        topSquareDrop.pause();
        stoptimertask();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                if (gameRunning) {
                    //nothing to do!
                } else {
                    gameRunning = true;
                    runAnimation();
                }
                break;
        }
    }

    private void runAnimation() {
        //start the animation
        squareMove1.start();
        squareMove2.start();
        topSquareDrop.start();
        animationDrawable.start();
        //timerStart();
        countdownTimerStart();
    }

    private void timerStart() {
        timer = new Timer();
        initializeTimerTask();
        int random_delay = random.nextInt((max_delay - min_delay) + 1) + min_delay;
        topSquare02.setText(String.valueOf(random_delay/1000));
        timer.schedule(timerTask, random_delay);
    }

    private void countdownTimerStart() {
        stoptimertask();
        int delay;
        if (delay_memory < 0) {
            delay = random.nextInt((max_delay - min_delay) + 1) + min_delay;
        } else {
            delay = (int) delay_memory;
        }
        topSquare02.setText(String.valueOf(delay/1000));
        cdTimer = new CountDownTimer(delay, 100) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                delay_memory = millisUntilFinished;
                topSquare02.setText(String.valueOf(millisUntilFinished/1000));
            }
            public void onFinish() {
                delay_memory = -100;
                runAnimation();
            }
        }.start();
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
