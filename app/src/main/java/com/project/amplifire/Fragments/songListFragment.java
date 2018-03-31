package com.project.amplifire.Fragments;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.amplifire.Adapters.VerticalAdapter;
import com.project.amplifire.DataModels.Song;
import com.project.amplifire.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by aayush on 14-Mar-18.
 */

public class songListFragment extends Fragment
{
    private static final String TAG = "SongListFragment";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;
    private static final String SAVED_LAYOUT_MANAGER ="layoutmanager" ;
    public ArrayList<Song> mSongArrayList;
    private VerticalAdapter songAdt;
    private RecyclerView mSongView;
    private FastScroller mFastScroller;
    private MaterialSearchView mSearchView;
    private TabLayout libraryTabLayout;

    public songListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.song_list_fragment, container, false);
        rootView.setTag(TAG);
        mSongArrayList = new ArrayList<Song>();
        getSongList();
        mSongView = rootView.findViewById(R.id.song_list);
        mFastScroller = rootView.findViewById(R.id.fastscroll);
        setList();
        setHasOptionsMenu(true);


        Toolbar libraryToolbar = getActivity().findViewById(R.id.libraryToolbar);
        libraryToolbar.inflateMenu(R.menu.search_menu);
        mSearchView = getActivity().findViewById(R.id.search_view);
        libraryTabLayout = getActivity().findViewById(R.id.library_tab_layout);


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mSongArrayList = new ArrayList<Song>();
        getSongList();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!mSongArrayList.isEmpty()){
            mSongArrayList.clear();
        }
    }
//    @Override
//    public void onResume() {
//        super.onResume();
//        songAdt.updateList(mSongArrayList);
//        mSongView.setAdapter(songAdt);
//        mSongView.getLayoutManager().scrollToPosition(VerticalAdapter.topElementPosition);
//    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, mSongView.getLayoutManager().onSaveInstanceState());
    }

    public void getSongList() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = null;
        try {
            musicCursor = musicResolver.query(musicUri, null, null, null, null);
            if (musicCursor != null && musicCursor.moveToFirst()) {
                int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int albumIDColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int fullPathColumn = (musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                do {
                    long thisID = musicCursor.getLong(idColumn);
                    long thisAlbumID = musicCursor.getLong(albumIDColumn);
                    String thisTitle = musicCursor.getString(titleColumn);
                    String thisArtist = musicCursor.getString(artistColumn);
                    String thisAlbum = musicCursor.getString(albumColumn);
                    String thisDuration = musicCursor.getString(durationColumn);
                    String thisFullPath = musicCursor.getString(fullPathColumn);
                    if (thisArtist.equals("<unknown>")) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Audio.Media.ARTIST, "Unknown Artist");
                        String where = "_ID=?";
                        musicResolver.update(musicUri, values, where, new String[]{Long.toString(thisID)});
                    }
                    File file = new File(thisFullPath);
                    String fileParent = file.getParent();
                    File parentFile = new File(fileParent);
                    String folderName = parentFile.getName();
                    if(thisAlbum.equals(folderName)){
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Audio.Media.ALBUM, "Unknown Album");
                        String where = "_ID=?";
                        musicResolver.update(musicUri, values, where, new String[]{Long.toString(thisID)});
                    }
                    if(thisArtist.equals("<unknown>")){
                        thisArtist = "Unknown Artist";
                    }
                    if(thisAlbum.equals("<unknown>")){
                        thisAlbum = "Unknown Album";
                    }
                    mSongArrayList.add(new Song(thisID, thisAlbumID, thisTitle, thisArtist, thisAlbum, thisDuration, thisFullPath));
                } while (musicCursor.moveToNext());
            }
        }
            catch(Exception exception){
                if (musicCursor != null) {
                    musicCursor.close();
                }
            }
        }
    public void setList()
    {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        songAdt = new VerticalAdapter(mSongArrayList);
        mSongView.setLayoutManager(mLinearLayoutManager);
        Collections.sort(mSongArrayList, new Comparator<Song>()
        {
            @Override
            public int compare(Song o1, Song o2)
            {
                return o1.getMTitle().toLowerCase().compareTo(o2.getMTitle().toLowerCase());
            }
        });
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
//                mSongView.getContext(),
//                mLinearLayoutManager.getOrientation()
//        );
//        mSongView.addItemDecoration(dividerItemDecoration);
        mSongView.setAdapter(songAdt);
        mFastScroller.setRecyclerView(mSongView);
    }

    public void filter(String text)
    {
        ArrayList<Song> temp = new ArrayList<Song>();
        for (Song d : mSongArrayList)
        {
            if (d.getMTitle().toLowerCase().contains(text.toLowerCase()))
            {
                temp.add(d);
            }
        }
        Collections.sort(temp, new Comparator<Song>()
        {
            @Override
            public int compare(Song o1, Song o2)
            {
                return o1.getMTitle().compareTo(o2.getMTitle());
            }
        });
        songAdt.updateList(temp);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);

        mSearchView.setMenuItem(item);
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //libraryTabLayout.setEnabled(false);
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //libraryTabLayout.setEnabled(true);
                //Do some magic
            }
        });


    }
}
