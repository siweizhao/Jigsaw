package com.szhao.jigsaw.activities.Dashboard;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.Dashboard.Fragments.NavigationFragment;
import com.szhao.jigsaw.Global.Utility;
import com.szhao.jigsaw.Global.GameSettings;
import com.szhao.jigsaw.Global.GlobalGameData;

public class DashboardActivity extends AppCompatActivity implements NavigationFragment.OnFragmentInteractionListener{
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
                .commit();
    }

    public void openSettings(View view){
        gameSettings.show();
    }

    public void createCustomPuzzle(View v){
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
