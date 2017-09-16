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
import android.widget.LinearLayout;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.adapter.ContentRecyclerViewAdapter;
import com.szhao.jigsaw.activities.dashboard.adapter.ItemSelectListener;
import com.szhao.jigsaw.db.PuzzleContentProvider;
import com.szhao.jigsaw.global.Constants;
import com.szhao.jigsaw.global.DisplayDimensions;
import com.szhao.jigsaw.global.Utility;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomPuzzlesFragment extends Fragment implements ItemSelectListener{

    ContentRecyclerViewAdapter contentAdapter;
    private boolean isRemoveImage;
    private ItemSelectListener mListener;

    public CustomPuzzlesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        final ImageButton removeImageBtn = (ImageButton) v.findViewById(R.id.removeCustomPuzzleBtn);
        removeImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRemoveImage = !isRemoveImage;
                if (isRemoveImage){
                    removeImageBtn.setImageResource(R.drawable.ic_trashcan_open64dp);
                    contentAdapter.setListener(CustomPuzzlesFragment.this);
                } else {
                    removeImageBtn.setImageResource(R.drawable.ic_trashcan_close64dp);
                    contentAdapter.setListener(mListener);
                }
            }
        });

        LinearLayout topWrapper = (LinearLayout) v.findViewById(R.id.customPuzzleFragmentTopWrapper);
        ViewGroup.LayoutParams recyclerParams = topWrapper.getLayoutParams();
        recyclerParams.height = (int) (DisplayDimensions.getInstance().getHeight() * 0.3);
        topWrapper.setLayoutParams(recyclerParams);

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
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(intent, Constants.PICK_IMAGE);
                }
            }
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,requestCode,data);
        //Result from pick image
        if(requestCode == Constants.PICK_IMAGE && resultCode == Activity.RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(Math.round(Constants.GOLDEN_RATIO * 100), 100)
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
        File dir = getActivity().getDir(Constants.CUSTOM_PUZZLES_DIR, Context.MODE_PRIVATE);
        File newPath = new File(dir, "puzzle_" + String.valueOf(System.currentTimeMillis()));
        try (FileInputStream in = new FileInputStream(uri.getPath());
             FileOutputStream out = new FileOutputStream(newPath)) {
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
    public void onClick(String item, int difficulty) {
        if (new File(item).delete()) {
            String whereClause = "PUZZLE = ?";
            String[] args = new String[]{item};
            getContext().getContentResolver().delete(PuzzleContentProvider.CONTENT_URI_COMPLETED, whereClause, args);
            contentAdapter.setCustomPuzzles();
        }
    }
}
