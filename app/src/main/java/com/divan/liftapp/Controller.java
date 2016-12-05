package com.divan.liftapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by Димка on 03.12.2016.
 */

public class Controller extends SerialPortActivity {

    private TextView date,info,massage,number;
    private ImageView up,down,man,women,image;
    private FrameLayout frameLayout;

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        runOnUiThread(new Runnable() {
            public void run() {
            //TODO your logic
               /* if(up.getVisibility()==View.VISIBLE)
                    up.setVisibility(View.INVISIBLE);
                else
                    up.setVisibility(View.VISIBLE);*/
            }
        });
    }

    public Controller(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       // new CatTask().execute();

    }
    class CatTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            date=(TextView)findViewById(R.id.date);
            info=(TextView)findViewById(R.id.information);
            massage=(TextView)findViewById(R.id.massage);
            number=(TextView)findViewById(R.id.number);

            up=(ImageView)findViewById(R.id.imageUp);
            down=(ImageView)findViewById(R.id.imageDown);
            man=(ImageView)findViewById(R.id.man);
            women=(ImageView)findViewById(R.id.women);
            image=(ImageView)findViewById(R.id.imageMain);

            frameLayout=(FrameLayout)findViewById(R.id.mainLayout);
        }

        @Override
        protected Void doInBackground(Void... params) {
                        return null;
        }

    }

}
