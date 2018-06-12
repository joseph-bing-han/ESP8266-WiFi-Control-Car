package net.jsoeph_han.carcontroller;

import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private OkHttpClient client = new OkHttpClient();

    private static final String baseURI = "http://192.168.4.1/controll?direction=";

    private int direction = 0;

    // effect when pressed
    // 反色+蓝
    private final static float[] BUTTON_PRESSED = new float[]{
            -1, 0, 0, 0, 255,
            0, -1, 0, 0, 255,
            0, 0, 1, 0, 255,
            0, 0, 0, 1, 0};

    // effect when release
    private final static float[] BUTTON_RELEASED = new float[]{
            1, 0, 0, 0, 0,
            0, 1, 0, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 0, 1, 0};

    private static final int MSG = 1984;
    // new handler for multi threads
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        controlMotor();
                        if (direction != 0) {
                            mHandler.postDelayed(this, 100);
                        }
                    }
                });
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        ImageButton btnFront = (ImageButton) findViewById(R.id.btn_front);
        btnFront.setOnTouchListener(buttonTouch);

        ImageButton btnRight = (ImageButton) findViewById(R.id.btn_right);
        btnRight.setOnTouchListener(buttonTouch);

        ImageButton btnBack = (ImageButton) findViewById(R.id.btn_back);
        btnBack.setOnTouchListener(buttonTouch);

        ImageButton btnLeft = (ImageButton) findViewById(R.id.btn_left);
        btnLeft.setOnTouchListener(buttonTouch);

        ImageButton btnStop = (ImageButton) findViewById(R.id.btn_stop);
        btnStop.setOnTouchListener(buttonTouch);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    private View.OnTouchListener buttonTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(BUTTON_PRESSED));
                v.setBackgroundDrawable(v.getBackground());
                switch (v.getId()) {
                    case R.id.btn_front:
                        direction = 1;
                        break;
                    case R.id.btn_right:
                        direction = 2;
                        break;
                    case R.id.btn_back:
                        direction = 3;
                        break;
                    case R.id.btn_left:
                        direction = 4;
                        break;
                    case R.id.btn_stop:
                    default:
                        direction = 0;
                        break;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(BUTTON_RELEASED));
                v.setBackgroundDrawable(v.getBackground());
                direction = 0;
            }
            Message msg = mHandler.obtainMessage();
            msg.what = MSG;
            msg.sendToTarget();
            return false;
        }
    };

    private void controlMotor() {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                String url = baseURI + direction;
                Log.d("JSON", "URL: " + url);
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Response response = client.newCall(request).execute();
                Log.d("JSON", response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
