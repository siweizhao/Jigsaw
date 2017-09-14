package com.szhao.jigsaw.activities.dashboard;

import android.content.Context;
import android.content.DialogInterface;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.szhao.jigsaw.R;
import com.szhao.jigsaw.activities.dashboard.adapter.ItemSelectListener;
import com.szhao.jigsaw.activities.dashboard.fragment.CustomPuzzlesFragment;
import com.szhao.jigsaw.activities.dashboard.fragment.DifficultyFragment;
import com.szhao.jigsaw.activities.dashboard.fragment.NavigationFragment;
import com.szhao.jigsaw.global.DisplayDimensions;
import com.szhao.jigsaw.global.PointSystem;
import com.szhao.jigsaw.global.SoundSettings;
import com.szhao.jigsaw.global.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DashboardActivity extends AppCompatActivity implements ItemSelectListener, PointSystem.PointChangeListener {
    SoundSettings soundSettings;
    RequestQueue requestQueue;
    String serverUrl = "http://10.215.5.203:8000/puzzles";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        DisplayDimensions.getInstance().initDimensions(this);
        soundSettings = new SoundSettings(this);
        initNavigationFragment();
        initPointSystem();
        Utility.startImmersiveMode(this);
        requestQueue = Volley.newRequestQueue(this);
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
                .title("Download Puzzles")
                .content("Would you like to check for new available puzzles?")
                .positiveText("Yes")
                .negativeText("No")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        connectToServer();
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

    public void connectToServer() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(serverUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray categories = response.getJSONArray("categories");
                            for (int i = 0; i < categories.length(); i++) {
                                String category = categories.getJSONObject(i).getString("title");
                                downloadPuzzleByCategory(category);
                            }
                        } catch (JSONException e) {
                            Log.d("jsonexception", Log.getStackTraceString(e));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("json", "Server connection failed " + Log.getStackTraceString(error));
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void downloadPuzzleByCategory(String category) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(serverUrl + "/" + category + "/", null,
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
                            }
                        } catch (JSONException e) {
                            Log.d("jsonexception", Log.getStackTraceString(e));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("json", "Server connection failed " + Log.getStackTraceString(error));
            }
        });
        requestQueue.add(jsonObjectRequest);
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

    private void storeDownloadedImage(String category, String title, byte[] image) {
        File dir = this.getDir("DL", Context.MODE_PRIVATE);
        File categoryDir = new File(dir, category);

        //Create dir if it does not exist
        if (!categoryDir.exists() || categoryDir.mkdir()) {
            File imageFile = new File(categoryDir, title);
            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                out.write(image);
                out.close();
            } catch (IOException e) {
                Log.d("Save DL file", e.getMessage());
            }
        }
    }
}
