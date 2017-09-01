package com.szhao.jigsaw.Old;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.db.PuzzleContentProvider;
import com.szhao.jigsaw.global.Utility;
import com.szhao.jigsaw.Old.fragments.DifficultyDialog;
import com.szhao.jigsaw.Old.fragments.PuzzleSelectFragment;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class PuzzleSelector extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    public static final String FRAG_PROVIDED_PUZZLES = "PROVIDED";
    public static final String FRAG_CUSTOM_PUZZLES = "CUSTOM";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private Toolbar toolbar;
    private boolean removePuzzles = false;
    private Menu menuOptions;
    DifficultyDialog difficultyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_selector);
        difficultyDialog = new DifficultyDialog();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        FloatingActionButton customPuzzleButton = (FloatingActionButton) findViewById(R.id.customPuzzleButton);
        customPuzzleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT > 23 && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_puzzle_selector, menu);
        menuOptions = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_delete:
                toggleRemovePuzzles();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Remove user added puzzles
    public void toggleRemovePuzzles(){
        if (!removePuzzles) {
            menuOptions.findItem(R.id.action_delete).setIcon(android.R.drawable.ic_delete);
        } else {
            menuOptions.findItem(R.id.action_delete).setIcon(android.R.drawable.ic_menu_delete);
        }
        removePuzzles = !removePuzzles;
    }

    public boolean isRemovePuzzles(){
        return removePuzzles;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return PuzzleSelectFragment.newInstance(FRAG_PROVIDED_PUZZLES);
                case 1:
                    return PuzzleSelectFragment.newInstance(FRAG_CUSTOM_PUZZLES);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,requestCode,data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setRequestedSize(Utility.IMAGE_DIMENSIONS, Utility.IMAGE_DIMENSIONS, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                    .setAspectRatio(50,50)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result.getUri() != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                    insertCustomPuzzles(bitmap);
                    openDifficultySelector(bitmap);
                } catch (IOException e) {
                    Toast.makeText(this, "Photos inaccessible", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.d("Upload image error", error.getMessage());
            }
        }
    }

    private void openDifficultySelector(Bitmap bitmap){
        FragmentManager fm = getSupportFragmentManager();
        Utility.storeImage(this, bitmap);
        difficultyDialog.show(fm, "Difficulty");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
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

    public void insertCustomPuzzles(Bitmap bitmap){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DESCRIPTION", "caption");
        contentValues.put("PUZZLE", Utility.getBytes(bitmap));
        //getContentResolver().insert(PuzzleContentProvider.CONTENT_URI_CUSTOM, contentValues);
        //getContentResolver().notifyChange(PuzzleContentProvider.CONTENT_URI_CUSTOM, null);
    }
}
