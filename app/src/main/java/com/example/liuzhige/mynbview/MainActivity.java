package com.example.liuzhige.mynbview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.example.liuzhige.mynbview.CircleProgressBar.onProgressStateListener;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  private int per = 0;
  private CircleProgressBar mCircleProgressBar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mCircleProgressBar = (CircleProgressBar) findViewById(R.id.progress_bar);

    mCircleProgressBar.setmListener(new onProgressStateListener() {
      @Override
      public void onStart(int progressStart) {
        Log.d("TAG", "onStart: " + progressStart);
      }

      @Override
      public void onPause(int progressPause) {
      }

      @Override
      public void onComplete(int progressEnd) {
        Log.d("TAG", "onComplete: " + progressEnd);
      }
    });

  }

  public void haha(View view) {
    per = per + 10;
    if (per > 100) {
      per = 100;
    }
    mCircleProgressBar
        .seekToProgress(per);
  }

  public void mama(View view) {
    per = per - 10;
    if (per < 0) {
      per = 0;
    }
    mCircleProgressBar.seekToProgress(per);
  }
}
