package com.divan.liftapp.Fragments;

import android.animation.StateListAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.divan.liftapp.R;

import java.io.IOException;
import java.util.List;


public class FragmentImage extends MyFragment {

     Animation animationFlipOut,animationFlipIn;

    ImageView image;
    List<String> images,musics;
    int nImage=0,nMusic=0;
    int lastSignal=0;

    public void setLists(List<String> images,List<String> musics) {
        this.images = images;
        this.musics=musics;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_image, null);

        animationFlipOut = AnimationUtils.loadAnimation(container.getContext(),
                android.R.anim.slide_out_right);
        animationFlipIn = AnimationUtils.loadAnimation(container.getContext(),
                android.R.anim.slide_in_left);

        image=((ImageView) v.findViewById(R.id.imageFragment));


        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onUpdate(0,lastSignal);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(images.size()!=0) {
            Bitmap bm = BitmapFactory.decodeFile(images.get(nImage++ % images.size()));
            image.setImageBitmap(bm);
        }
    }

    @Override
    public void onUpdate(int floor, int signal) {
        lastSignal=signal;
        if(images.size()!=0&&signal==1) {
            final Bitmap bm = BitmapFactory.decodeFile(images.get(nImage++ % images.size()));

            animationFlipOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    image.setImageBitmap(bm);
                    image.startAnimation(animationFlipIn);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            image.startAnimation(animationFlipOut);

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
}
