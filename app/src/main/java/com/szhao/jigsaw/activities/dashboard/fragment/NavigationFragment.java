package com.szhao.jigsaw.activities.dashboard.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.adapter.CategoryRecyclerViewAdapter;
import com.szhao.jigsaw.activities.dashboard.adapter.ContentRecyclerViewAdapter;
import com.szhao.jigsaw.activities.dashboard.adapter.ItemSelectListener;
import com.szhao.jigsaw.db.PuzzleContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NavigationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    View masterLayout;
    RequestQueue requestQueue;
    CategoryRecyclerViewAdapter categoryAdapter;
    String serverUrl = "http://10.215.5.203:8000/puzzles/StarWars/";
    private ItemSelectListener mListener;
    public NavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
        requestQueue = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(serverUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String category = response.getString("category");
                            JSONArray puzzles = response.getJSONArray("puzzles");
                            for (int i = 0; i < puzzles.length(); i++) {
                                String title = puzzles.getJSONObject(i).getString("title");
                                String b64Image = puzzles.getJSONObject(i).getString("img");
                                final byte[] decodedBytes = Base64.decode(b64Image, Base64.DEFAULT);
                                storeDownloadedImage(category, title, decodedBytes);
                                Log.d("dl", title);
                            }
                        } catch (JSONException e) {
                            Log.d("jsonexception", e.getMessage());
                        }
                        categoryAdapter.setDLPuzzle();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("json", "Server connection failed " + Log.getStackTraceString(error));
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public void storeDownloadedImage(String category, String title, byte[] image) {
        File dir = getActivity().getDir("DL", Context.MODE_PRIVATE);
        File categoryDir = new File(dir, category);
        if (!categoryDir.exists())
            categoryDir.mkdir();
        File imageFile = new File(categoryDir, title);
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            out.write(image);
            out.close();
        } catch (IOException e) {
            Log.d("Save DL file", e.getMessage());
        }
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
        categoryAdapter.setListener(new ItemSelectListener() {
            @Override
            public void onClick(String category, int difficulty) {
                contentAdapter.setPuzzles(category);
            }
        });
        categoryAdapter.setDLPuzzle();
        categoryRecycler.setAdapter(categoryAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
