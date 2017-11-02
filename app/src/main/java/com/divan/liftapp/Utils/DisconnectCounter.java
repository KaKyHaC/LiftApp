package com.divan.liftapp.Utils;

/**
 * Created by Dima on 02.11.2017.
 */

public class DisconnectCounter {
    final int max;
    int curCount;

    public DisconnectCounter(int max) {
        this.max = max;
    }
    public boolean isDisconnected(){
        return ++curCount>max;
    }
    public void reSet(){
        curCount=0;
    }
}
