package com.blackfilter;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class FilterService extends Service{
    LinearLayout mView;
    com.blackfilter.SharedMemory shared;

    public static int STATE;

    public static final int INACTIVE=0;
    public static final int ACTIVE=0;

    static{
        STATE=INACTIVE;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        shared= new SharedMemory(this);
        mView = new LinearLayout(this);
        mView.setBackgroundColor(shared.getColor());
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0 | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mView!=null){
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(mView);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mView.setBackgroundColor(shared.getColor());
        return super.onStartCommand(intent, flags, startId);
    }
}
