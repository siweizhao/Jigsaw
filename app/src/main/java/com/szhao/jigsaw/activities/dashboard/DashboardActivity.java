package com.szhao.jigsaw.activities.dashboard;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.adapter.ItemSelectListener;
import com.szhao.jigsaw.activities.dashboard.fragment.CustomPuzzlesFragment;
import com.szhao.jigsaw.activities.dashboard.fragment.DifficultyFragment;
import com.szhao.jigsaw.activities.dashboard.fragment.NavigationFragment;
import com.szhao.jigsaw.global.Utility;
import com.szhao.jigsaw.global.GameSettings;
import com.szhao.jigsaw.global.GlobalGameData;

public class DashboardActivity extends AppCompatActivity implements ItemSelectListener{
    GameSettings gameSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        GlobalGameData.getInstance().setContext(this);
        gameSettings = new GameSettings(this);
        initNavigationFragment();
        Utility.startImmersiveMode(this);
    }

    private void initNavigationFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new NavigationFragment())
                .addToBackStack("Navigation")
                .commit();
    }

    public void goBack(View view){
        if (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStack();
    }

    public void openSettings(View view){
        gameSettings.show();
    }

    public void createCustomPuzzle(View view){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new CustomPuzzlesFragment())
                .addToBackStack("Custom")
                .commit();
    }

    @Override
    public void onClick(String item, int progress, String positions) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, DifficultyFragment.newInstance(item, progress, positions))
                .addToBackStack("Difficulty")
                .commit();
    }
}
