package com.divan.liftapp.Utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.divan.liftapp.FullscreenActivity;

/**
 * Created by Dima on 05.11.2017.
 */

public class RebootSystem {
    FullscreenActivity app;
    private boolean isRebooting=false;

    public RebootSystem(FullscreenActivity app) {
        this.app = app;
    }

    public void start(){

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
}
