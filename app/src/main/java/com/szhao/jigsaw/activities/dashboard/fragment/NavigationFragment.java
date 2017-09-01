package com.szhao.jigsaw.activities.dashboard.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.adapter.CategoryRecyclerViewAdapter;
import com.szhao.jigsaw.activities.dashboard.adapter.ContentRecyclerViewAdapter;
import com.szhao.jigsaw.activities.dashboard.adapter.ItemSelectListener;
import com.szhao.jigsaw.db.PuzzleContentProvider;

public class NavigationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private ItemSelectListener mListener;
    View masterLayout;

    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        masterLayout = inflater.inflate(R.layout.fragment_navigation, container, false);
        return masterLayout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemSelectListener) {
            mListener = (ItemSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),PuzzleContentProvider.CONTENT_URI_STARTED, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {



        RecyclerView contentRecycler = (RecyclerView)masterLayout.findViewById(R.id.contentSelectRecycler);
        final ContentRecyclerViewAdapter contentAdapter = new ContentRecyclerViewAdapter(getContext(), data);
        contentAdapter.setListener(mListener);
        contentRecycler.setAdapter(contentAdapter);

        String categoryImage = null;
        if (data.getCount() > 0){
            data.moveToFirst();
            categoryImage = data.getString(data.getColumnIndex("PUZZLE"));
        }

        RecyclerView categoryRecycler = (RecyclerView)masterLayout.findViewById(R.id.categorySelectRecycler);
        CategoryRecyclerViewAdapter categoryAdapter = new CategoryRecyclerViewAdapter(getContext(), categoryImage, data.getCount());
        categoryAdapter.setListener(new ItemSelectListener() {
            @Override
            public void onClick(String category, int difficulty, String positions) {
                contentAdapter.setPuzzles(category);
            }
        });
        categoryRecycler.setAdapter(categoryAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
