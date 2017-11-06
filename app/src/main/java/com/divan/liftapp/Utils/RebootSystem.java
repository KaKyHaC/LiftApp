package com.divan.liftapp.Utils;

import android.content.Intent;
import android.os.PowerManager;

import com.divan.liftapp.Activitys.FullscreenActivity;
import com.divan.liftapp.Utils.Services.LaunchLiftAppService;

/**
 * Created by Dima on 05.11.2017.
 */

public class RebootSystem {
    FullscreenActivity app;
    private boolean isRebooting=false;

    public RebootSystem(FullscreenActivity app) {
        this.app = app;
    }

    public void startLaunchService(){
        if(!isRebooting) {
            isRebooting = true;

            app.startService(new Intent(app, LaunchLiftAppService.class));
            app.finish();
        }
    }
    public void startAsyncPauseResume(final long delay){
        if(!isRebooting) {
            isRebooting = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    app.onPause();
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    app.onResume();
                    isRebooting = false;
                }
            }).start();
        }
    }
    public void startReboot(){
        if(!isRebooting) {
            isRebooting = true;
            try {
//                PowerManager pm = (PowerManager) app.getSystemService(app.POWER_SERVICE);
//                pm.reboot(null);
                Runtime.getRuntime().exec("su -c reboot");
            }catch (Exception r){
                r.printStackTrace();
            }
        }
    }
}
