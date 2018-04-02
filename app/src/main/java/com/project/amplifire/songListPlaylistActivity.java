package com.project.amplifire;

import android.app.Activity;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.project.amplifire.Adapters.PlaylistSongAdapter;
import com.project.amplifire.DataModels.Playlist;
import com.project.amplifire.DataModels.Song;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class songListPlaylistActivity extends AppCompatActivity {

    private static final String SAVED_LAYOUT_MANAGER ="layoutmanager" ;
    public ArrayList<Song> mPlaylistSongArrayList;
    private RecyclerView mPlaylistSongView;
//    private FastScroller mFastScroller;
    //private TextView nameView;
//    private MaterialSearchView mSearchView;
//    private TabLayout libraryTabLayout;
    private long playlistID;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_list_playlist_activity);
        Bundle extras = getIntent().getExtras();
        playlistID = (long)extras.get("playlistID");
        mPlaylistSongArrayList = new ArrayList<Song>();
        mPlaylistSongView = findViewById(R.id.song_list_playlist_recyclerView);
      //  mFastScroller = findViewById(R.id.song_list_playlist_fastscroll);
      //  nameView = findViewById(R.id.song_list_playlist_name);

        CollapsingToolbarLayout playlistToolbar = findViewById(R.id.playlist_collapsing_toolbar);

        Playlist playlist = Playlist.getPlaylistByID(getContentResolver(), playlistID);
        ImageView albumArt = findViewById(R.id.playlist_app_bar_image);
        String title = playlist.getPlaylistName();

        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        if((playlist.getSongs(getApplicationContext().getContentResolver()).size() != 0)){
            Uri uri = ContentUris.withAppendedId(sArtworkUri, playlist.getSongs(getApplicationContext().getContentResolver()).get(0).getMAlbumId());
            InputStream in = null;
            try {
                in = getApplicationContext().getContentResolver().openInputStream(uri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Bitmap artwork = BitmapFactory.decodeStream(in);
            if (artwork != null) {
                Glide.with(getApplicationContext()).load(uri).into(albumArt);
            } else {
                albumArt.setImageResource(R.drawable.ic_album_art_template);
            }
        }
        else{
            albumArt.setImageResource(R.drawable.ic_album_art_template);
        }

        playlistToolbar.setTitle(title);
        playlistToolbar.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
        playlistToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));
        //playlistToolbar.setBackgroundColor(getResources().getColor(R.color.colorTransparentBG));

        getPlaylistSongList();
        setPlaylistList();
    }

//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
//    {
//        View rootView = inflater.inflate(R.layout.playlist_recycler_view_fragment, container, false);
//        rootView.setTag(TAG);
//        mPlaylistSongArrayList = new ArrayList<Song>();
//        getPlaylistSongList();
//        mPlaylistSongView = rootView.findViewById(R.id.playlist_songs_recycler_view);
//        mFastScroller = rootView.findViewById(R.id.playlist_songs_fastScroller);
//        setPlaylistList();
//        setHasOptionsMenu(true);
//        Toolbar libraryToolbar = getActivity().findViewById(R.id.libraryToolbar);
//        libraryToolbar.inflateMenu(R.menu.search_menu);
//        mSearchView = getActivity().findViewById(R.id.search_view);
//        libraryTabLayout = getActivity().findViewById(R.id.library_tab_layout);
//        return rootView;
//    }

    @Override
    public void onStart() {
        super.onStart();
        mPlaylistSongArrayList = new ArrayList<Song>();
        getPlaylistSongList();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!mPlaylistSongArrayList.isEmpty()){
            mPlaylistSongArrayList.clear();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mPlaylistSongView.getLayoutManager().onSaveInstanceState());
    }
    public void getPlaylistSongList()
    {
        Playlist playlist = Playlist.getPlaylistByID(getContentResolver(), playlistID);
       // nameView.setText(playlist.getPlaylistName());
        mPlaylistSongArrayList = playlist.getSongs(getContentResolver());
    }
    public void setPlaylistList()
    {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        PlaylistSongAdapter playlistSongAdt = new PlaylistSongAdapter(mPlaylistSongArrayList);
        mPlaylistSongView.setLayoutManager(mLinearLayoutManager);
        Collections.sort(mPlaylistSongArrayList, new Comparator<Song>()
        {
            @Override
            public int compare(Song o1, Song o2)
            {
                return o1.getMTitle().toLowerCase().compareTo(o2.getMTitle().toLowerCase());
            }
        });

        mPlaylistSongView.setAdapter(playlistSongAdt);
        //mFastScroller.setRecyclerView(mPlaylistSongView);
    }

//    public void filter(String text)
//    {
//        ArrayList<Song> temp = new ArrayList<Song>();
//        for (Song d : mPlaylistSongArrayList)
//        {
//            if (d.getMTitle().toLowerCase().contains(text.toLowerCase()))
//            {
//                temp.add(d);
//            }
//        }
//        Collections.sort(temp, new Comparator<Song>()
//        {
//            @Override
//            public int compare(Song o1, Song o2)
//            {
//                return o1.getMTitle().compareTo(o2.getMTitle());
//            }
//        });
//        playlistSongAdt.updateList(temp);
//    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        getMenuInflater().inflate(R.menu.search_menu, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        mSearchView.setMenuItem(item);
//        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filter(newText);
//                return false;
//            }
//        });
//
//        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
//            @Override
//            public void onSearchViewShown() {
//            }
//
//            @Override
//            public void onSearchViewClosed() {
//            }
//        });
//
//        return true;
//    }
}