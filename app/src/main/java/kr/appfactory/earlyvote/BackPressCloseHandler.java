package kr.appfactory.earlyvote;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class BackPressCloseHandler {
    private Activity activity;
    private long backKeyPressedTime;
    private Toast toast;

    public BackPressCloseHandler(Activity context) {
        this.backKeyPressedTime = 0;
        this.activity = context;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > this.backKeyPressedTime + 1000) {
            this.backKeyPressedTime = System.currentTimeMillis();
            showGuide();
        } else if (System.currentTimeMillis() <= this.backKeyPressedTime + 1000) {
            this.toast.cancel();

            AdsFull.getInstance(activity.getApplicationContext()).setAdsFull();
            Timer timer = new Timer();
            timer.schedule( new TimerTask()
                            {
                                public void run()
                                {
                                    activity.finish();
                                }
                            }
                    , 1000);

        }
    }

    public void showGuide() {
        this.toast = Toast.makeText(this.activity, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        this.toast.show();
    }
}
