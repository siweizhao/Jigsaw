package com.szhao.jigsaw.activities.dashboard;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.adapter.CategoryRecyclerViewAdapter;
import com.szhao.jigsaw.activities.dashboard.adapter.ItemSelectListener;
import com.szhao.jigsaw.activities.dashboard.fragment.CustomPuzzlesFragment;
import com.szhao.jigsaw.activities.dashboard.fragment.DifficultyFragment;
import com.szhao.jigsaw.activities.dashboard.fragment.NavigationFragment;
import com.szhao.jigsaw.global.Constants;
import com.szhao.jigsaw.global.DisplayDimensions;
import com.szhao.jigsaw.global.PointSystem;
import com.szhao.jigsaw.global.SoundSettings;
import com.szhao.jigsaw.global.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DashboardActivity extends AppCompatActivity implements ItemSelectListener, PointSystem.PointChangeListener {
    SoundSettings soundSettings;
    CategoryRecyclerViewAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        DisplayDimensions.getInstance().initDimensions(this);
        soundSettings = new SoundSettings(this);
        initNavigationFragment();
        initPointSystem();
        Utility.startImmersiveMode(this);
    }

    private void initPointSystem() {
        PointSystem.getInstance().setListener(this);
        PointSystem.getInstance().loadPoints(this);
        PointSystem.getInstance().loadAvailablePuzzles(this);
    }

    private void initNavigationFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new NavigationFragment(), "Navigation")
                .commit();
        findViewById(R.id.dashboardBackBtn).setVisibility(View.INVISIBLE);
    }

    public void goBack(View view){
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            findViewById(R.id.dashboardBackBtn).setVisibility(View.INVISIBLE);
    }

    public void openSettings(View view){
        soundSettings.show();
    }

    public void createCustomPuzzle(View view){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new CustomPuzzlesFragment(), "Custom")
                .addToBackStack("Custom")
                .commit();
        findViewById(R.id.dashboardBackBtn).setVisibility(View.VISIBLE);
    }

    public void dlPuzzles(View view) {
        new MaterialDialog.Builder(this)
                .titleGravity(GravityEnum.CENTER)
                .title(getString(R.string.download_puzzles))
                .content(R.string.check_new_puzzles)
                .positiveText(getString(R.string.yes))
                .negativeText(getString(R.string.no))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        checkNewPuzzles();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utility.startImmersiveMode(DashboardActivity.this);
                    }
                })
                .show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void checkNewPuzzles() {
        if (!isOnline()) {
            new MaterialDialog.Builder(this)
                    .titleGravity(GravityEnum.CENTER)
                    .title(getString(R.string.no_connection))
                    .content(getString(R.string.try_again_later))
                    .positiveText(getString(R.string.ok))
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            Utility.startImmersiveMode(DashboardActivity.this);
                        }
                    })
                    .show();
            return;
        }
        DatabaseReference firebaseDB = FirebaseDatabase.getInstance().getReference().child("categories");
        firebaseDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String category = dataSnapshot.child("category").getValue(String.class);
                DataSnapshot puzzles = dataSnapshot.child("puzzles");
                downloadPuzzles(category, puzzles);

                if (categoryAdapter != null)
                    categoryAdapter.setDLPuzzle();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void downloadPuzzles(String category, DataSnapshot puzzles) {
        for (DataSnapshot p : puzzles.getChildren()) {
            String title = p.child("title").getValue(String.class);
            String b64img = p.child("img").getValue(String.class);
            byte[] decodedBytes = Base64.decode(b64img, Base64.DEFAULT);
            storeDownloadedImage(category, title, decodedBytes);
        }
    }

    @Override
    public void onClick(String item, int difficulty) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, DifficultyFragment.newInstance(item, difficulty), "Difficulty")
                .addToBackStack("Difficulty")
                .commit();
        findViewById(R.id.dashboardBackBtn).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        soundSettings.toggleBGM();
        Utility.startImmersiveMode(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        soundSettings.toggleBGM();
    }

    @Override
    public void pointChanged(int points) {
        ((TextView) findViewById(R.id.dashboardPointsTxt)).setText(String.valueOf(points));
    }

    private void deleteDL() {
        File dir = this.getDir(Constants.DOWNLOADED_PUZZLES_DIR, Context.MODE_PRIVATE);
        if (dir == null)
            return;
        File[] categories = dir.listFiles();
        for (File c : categories) {
            File[] puzzles = c.listFiles();
            for (File p : puzzles) {
                p.delete();
            }
            c.delete();
        }
        dir.delete();
    }

    private void storeDownloadedImage(String category, String title, byte[] image) {
        File dir = this.getDir(Constants.DOWNLOADED_PUZZLES_DIR, Context.MODE_PRIVATE);
        if (!dir.exists())
            dir.mkdir();
        File categoryDir = new File(dir, category);

        //Create dir if it does not exist
        if (categoryDir.exists() || categoryDir.mkdir()) {
            File imageFile = new File(categoryDir, title);
            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                out.write(image);
                out.close();
            } catch (IOException e) {
                Log.d("Save DL file", e.getMessage());
            }
        }
    }

    public void setCategoryAdapter(CategoryRecyclerViewAdapter categoryAdapter) {
        this.categoryAdapter = categoryAdapter;
    }
}
