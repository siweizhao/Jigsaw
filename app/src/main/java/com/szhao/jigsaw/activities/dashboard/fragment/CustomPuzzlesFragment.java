package com.szhao.jigsaw.activities.dashboard.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.adapter.ContentRecyclerViewAdapter;
import com.szhao.jigsaw.activities.dashboard.adapter.ItemSelectListener;
import com.szhao.jigsaw.db.PuzzleContentProvider;
import com.szhao.jigsaw.global.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomPuzzlesFragment extends Fragment implements ItemSelectListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean isRemoveImage;
    private ItemSelectListener mListener;
    ContentRecyclerViewAdapter contentAdapter;

    public CustomPuzzlesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CustomPuzzlesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CustomPuzzlesFragment newInstance(String param1, String param2) {
        CustomPuzzlesFragment fragment = new CustomPuzzlesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_custom_puzzles, container, false);
        ImageButton addImageBtn = (ImageButton)v.findViewById(R.id.addCustomPuzzleBtn);
        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });
        ImageButton removeImageBtn = (ImageButton)v.findViewById(R.id.removeCustomPuzzleBtn);
        removeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRemoveImage = !isRemoveImage;
                if (isRemoveImage){
                    contentAdapter.setListener(CustomPuzzlesFragment.this);
                } else {
                    contentAdapter.setListener(mListener);
                }
            }
        });

        RecyclerView customPuzzleRecycler = (RecyclerView)v.findViewById(R.id.customPuzzlesRecycler);
        contentAdapter = new ContentRecyclerViewAdapter(getContext(),null);
        contentAdapter.setCustomPuzzles();
        contentAdapter.setListener(mListener);
        customPuzzleRecycler.setAdapter(contentAdapter);
        return v;
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

    public void addImage(){
        if (android.os.Build.VERSION.SDK_INT > 23 && getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, Utility.PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,requestCode,data);
        //Result from pick image
        if(requestCode == Utility.PICK_IMAGE && resultCode == Activity.RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(60,45)
                    .start(getActivity(),CustomPuzzlesFragment.this);
        }
        //Result from image cropper
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK && result.getUri() != null) {
                storeCustomPuzzle(result.getUri());
                contentAdapter.setCustomPuzzles();
                Utility.startImmersiveMode(getContext());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Upload image error", error.getMessage());
            }
        }
    }

    public void storeCustomPuzzle(Uri uri){
        File dir = getActivity().getDir("custom_puzzles", Context.MODE_PRIVATE);
        File newPath = new File(dir, "puzzle_" + String.valueOf(System.currentTimeMillis()));
        try {
            FileInputStream in = new FileInputStream(uri.getPath());
            FileOutputStream out = new FileOutputStream(newPath);
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (IOException e){
            Log.d("Save custom puzzle", e.getMessage());
        }
    }

    @Override
    public void onClick(String item, int difficulty, String positions) {
        new File(item).delete();
        String whereClause = "PUZZLE = ?";
        String[] args = new String[]{item};
        getContext().getContentResolver().delete(PuzzleContentProvider.CONTENT_URI_COMPLETED,whereClause,args);
        contentAdapter.setCustomPuzzles();
    }
}
