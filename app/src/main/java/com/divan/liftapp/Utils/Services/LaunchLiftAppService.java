package com.divan.liftapp.Utils.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LaunchLiftAppService extends Service {
    private static final long sleepTime=5000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(sleepTime);
                    Intent intent1=new Intent();
                    intent1.setAction("android.intent.action.LiftApp");
                    intent1.addCategory(Intent.CATEGORY_DEFAULT);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }catch (Exception e){}
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
