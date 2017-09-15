package com.szhao.jigsaw.activities.dashboard.fragment;

import android.content.Context;
import android.database.Cursor;
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
import com.szhao.jigsaw.activities.dashboard.DashboardActivity;
import com.szhao.jigsaw.activities.dashboard.adapter.CategoryRecyclerViewAdapter;
import com.szhao.jigsaw.activities.dashboard.adapter.ContentRecyclerViewAdapter;
import com.szhao.jigsaw.activities.dashboard.adapter.ItemSelectListener;
import com.szhao.jigsaw.db.PuzzleContentProvider;
import com.szhao.jigsaw.global.DisplayDimensions;

public class NavigationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    View masterLayout;
    CategoryRecyclerViewAdapter categoryAdapter;
    private ItemSelectListener mListener;
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
        RecyclerView categoryRecycler = (RecyclerView) masterLayout.findViewById(R.id.categorySelectRecycler);

        ViewGroup.LayoutParams recyclerParams = categoryRecycler.getLayoutParams();
        recyclerParams.height = (int) (DisplayDimensions.getInstance().getHeight() * 0.3);
        categoryRecycler.setLayoutParams(recyclerParams);

        final RecyclerView contentRecycler = (RecyclerView) masterLayout.findViewById(R.id.contentSelectRecycler);
        contentRecycler.post(new Runnable() {
            @Override
            public void run() {
                DisplayDimensions.getInstance().initContentRecyclerHeight(contentRecycler.getHeight());
            }
        });
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
        final RecyclerView contentRecycler = (RecyclerView) masterLayout.findViewById(R.id.contentSelectRecycler);
        final ContentRecyclerViewAdapter contentAdapter = new ContentRecyclerViewAdapter(getContext(), data);
        contentAdapter.setListener(mListener);
        contentRecycler.setAdapter(contentAdapter);

        String categoryImage = null;
        if (data.getCount() > 0){
            data.moveToLast();
            categoryImage = data.getString(data.getColumnIndex("PUZZLE"));
        }

        RecyclerView categoryRecycler = (RecyclerView)masterLayout.findViewById(R.id.categorySelectRecycler);
        categoryAdapter = new CategoryRecyclerViewAdapter(getContext(), categoryImage, data.getCount());
        categoryAdapter = new CategoryRecyclerViewAdapter(getContext(), categoryImage, data.getCount());
        categoryAdapter.setListener(new ItemSelectListener() {
            @Override
            public void onClick(String category, int difficulty) {
                contentAdapter.setPuzzles(category);
            }
        });
        categoryAdapter.setDLPuzzle();
        categoryRecycler.setAdapter(categoryAdapter);
        ((DashboardActivity) getActivity()).setCategoryAdapter(categoryAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
