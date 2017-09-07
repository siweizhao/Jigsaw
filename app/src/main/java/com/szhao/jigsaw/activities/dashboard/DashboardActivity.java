package com.szhao.jigsaw.activities.dashboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.adapter.ItemSelectListener;
import com.szhao.jigsaw.activities.dashboard.fragment.CustomPuzzlesFragment;
import com.szhao.jigsaw.activities.dashboard.fragment.DifficultyFragment;
import com.szhao.jigsaw.activities.dashboard.fragment.NavigationFragment;
import com.szhao.jigsaw.global.GameSettings;
import com.szhao.jigsaw.global.PointSystem;
import com.szhao.jigsaw.global.Utility;

public class DashboardActivity extends AppCompatActivity implements ItemSelectListener, PointSystem.PointChangeListener {
    GameSettings gameSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        gameSettings = new GameSettings(this);
        initNavigationFragment();
        initPoints();
        Utility.startImmersiveMode(this);
    }


    private void initPoints() {
        PointSystem.getInstance().setListener(this);
        PointSystem.getInstance().loadPoints(this);
        PointSystem.getInstance().loadAvailablePuzzles(this);
    }

    private void initNavigationFragment(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, new NavigationFragment())
                .addToBackStack("Navigation")
                .commit();
        findViewById(R.id.dashboardBackBtn).setVisibility(View.INVISIBLE);
    }

    public void goBack(View view){
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            findViewById(R.id.dashboardBackBtn).setVisibility(View.INVISIBLE);
            getSupportFragmentManager().popBackStack();
        }

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
        findViewById(R.id.dashboardBackBtn).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String item, int difficulty) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, DifficultyFragment.newInstance(item, difficulty))
                .addToBackStack("Difficulty")
                .commit();
        findViewById(R.id.dashboardBackBtn).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utility.startImmersiveMode(this);
    }

    @Override
    public void pointChanged(int points) {
        ((TextView) findViewById(R.id.dashboardPointsTxt)).setText(String.valueOf(points));
    }
}
