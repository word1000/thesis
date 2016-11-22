package com.example.xiazhituo.ecase;

import com.dd.CircularProgressButton;
import com.example.xiazhituo.ecase.CircleProgressView;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.view.WindowManager;
import android.widget.Button;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.ValueAnimator;

public class MainActivity extends AppCompatActivity {
    CircularProgressButton getWeightBut;
    CircleProgressView unlockBtn;
    CircleProgressView mCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Loading Map", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, LocateMapActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton ble_fab = (FloatingActionButton) findViewById(R.id.ble_fab);
        ble_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Loading Map", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();


            }
        });

        unlockBtn = (CircleProgressView) findViewById(R.id.unlockBtn);
        unlockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //unlockBtn.setProgress(0);
                //Snackbar.make(v, "Unlock command sent!", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });

        mCircleView = (CircleProgressView) findViewById(R.id.weightCircleProgress);
        mCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = 20;
                String weightHint = progress + " Kg";
                mCircleView.setmTxtHint1(weightHint);
                mCircleView.setProgress(progress);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void simulateSuccessProgress(final CircularProgressButton button, int progress) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, progress);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }
}
