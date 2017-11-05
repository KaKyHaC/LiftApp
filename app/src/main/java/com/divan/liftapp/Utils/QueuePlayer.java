package com.divan.liftapp.Utils;

import android.media.MediaPlayer;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Димка on 20.01.2017.
 */

public class QueuePlayer {
    static int isPlays=0;
    private MediaPlayer mediaPlayer;
    private ArrayDeque<String> pathToFiles=new ArrayDeque<>();

    public QueuePlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    if(isPlays>0)
                        isPlays--;
                    mp.stop();
                    mp.reset();
                    String path=pathToFiles.poll();
                    if(path!=null) {
                        mp.setDataSource(path);
                        mp.prepare();
                        mp.start();
                        isPlays++;
                    }
                        } catch (IOException e) {
                        }
            }
        });
    }

    public void add(String file){
        pathToFiles.offer(file);
        if(!mediaPlayer.isPlaying()) {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                String path = pathToFiles.poll();
                if (path != null) {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    isPlays++;
                }
            } catch (IOException e) {
            }
        }
    }
    public static boolean isPlaying(){
        return isPlays!=0;
    }
}
