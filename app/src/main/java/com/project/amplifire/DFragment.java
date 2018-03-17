package com.project.amplifire;

import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by chait on 17-03-2018.
 */

public class DFragment extends DialogFragment {

    private static Song mSong;
    public DFragment() {
        super();
    }

    public static void setMSong(Song song){
        mSong = song;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.dialogfragment, container,
                false);
        TextView artistView = rootView.findViewById(R.id.f_song_artist);
        artistView.setSelected(true);
        TextView albumView = rootView.findViewById(R.id.f_song_album);
        albumView.setSelected(true);
        TextView durationView = rootView.findViewById(R.id.f_song_duration);
        durationView.setSelected(true);
        TextView titleView = rootView.findViewById(R.id.f_song_title);
        titleView.setSelected(true);
        ImageView albumImage = rootView.findViewById(R.id.album_art_view);
        FloatingActionButton floatingActionButton = getActivity().findViewById(R.id.fab);
        String artist = mSong.getMArtist();
        artistView.setText(artist);
        albumView.setText(mSong.getMAlbum());
        durationView.setText(mSong.getMDuration());
        titleView.setText(mSong.getMTitle());
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri uri = ContentUris.withAppendedId(sArtworkUri, mSong.getMAlbumId());
        ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();
        InputStream in = null;
        try {
            in = contentResolver.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap artwork = BitmapFactory.decodeStream(in);
        if(artwork != null) {
            albumImage.setImageBitmap(artwork);
        }
//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                play(mSong, rootView.getContext());
//            }
//        });

        return rootView;
    }
    public void play(Song currentSong, Context context){

        Intent intent = new Intent(context, Player.class);
        String artist = currentSong.getMArtist();
        String album = currentSong.getMAlbum();
        if(artist.equals("<unknown>")){
            artist = "";
        }
        if(album.equals("<unknown>")){
            album = "";
        }
        intent.putExtra("songID", currentSong.getMId());
        intent.putExtra("track",currentSong.getMTitle());
        intent.putExtra("album",album);
        intent.putExtra("artist",artist);
        intent.putExtra("albumID", currentSong.getMAlbumId());
        context.startActivity(intent);
    }

}