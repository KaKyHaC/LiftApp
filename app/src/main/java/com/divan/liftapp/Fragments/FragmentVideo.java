package com.divan.liftapp.Fragments;


import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;


import com.divan.liftapp.R;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentVideo extends MyFragment {
    VideoView video;
    List<String> videos;
    int nVideo = 0;
    boolean isPause=false;
    boolean isFirst=true;
    int curPos=0;
    int lastSignal=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_video, null);

            video = (VideoView) v.findViewById(R.id.videoFragment);

            if (videos.size() != 0) {
               /* if(isFirst) {
                video.setVideoPath(videos.get(nVideo % videos.size()));
                isFirst = false;
            }*/
                //video.setVideoPath(videos.get(nVideo % videos.size()));
                video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        curPos=0;
                        video.setVideoPath(videos.get(++nVideo % videos.size()));
                        video.start();

                    }
                });
                video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {

                    }
                });

            }

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onUpdate(0,lastSignal);
    }

    @Override
    public void onPause() {
        super.onDetach();
        if(video!=null) {
            video.pause();
            curPos = video.getCurrentPosition();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if(video!=null) {
            video.setVideoPath(videos.get(nVideo % videos.size()));
            video.seekTo(curPos);
            if(lastSignal==1)
                 video.start();

        }

    }


    @Override
    public void onUpdate(int floor, final int signal) {
        lastSignal=signal;
        if (video != null) {
            if (video.isPlaying()&&signal == 0) {
                video.pause();
            }
             if (!video.isPlaying()&&signal==1) {
                video.start();
            }
            if(signal==2){
                curPos=0;
                video.setVideoPath(videos.get(++nVideo % videos.size()));
                video.start();
            }

        }
    }

    public void setVideos(List<String> videos) {
        this.videos = videos;
    }
}
