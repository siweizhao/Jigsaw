package com.szhao.jigsaw.Global;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.szhao.jigsaw.R;

import java.util.Random;

/**
 * Created by Owner on 8/15/2017.
 */

public class GameSettings {
    Context context;
    private static int[] CLICK_SOUNDS = new int[]{
            R.raw.click_1,
            R.raw.click_2,
            R.raw.click_3
    };

    private MediaPlayer bgmPlayer;
    private static SoundPool soundPool;

    public static int soundVolume;
    public static int bgmVolume;
    MaterialDialog settingsDialog;

    public GameSettings(Context context){
        this.context = context;
        loadSavedVolumes();
        initMediaPlayers();
        initSettingsDialog();
    }

    public void startBGM(){
        float finalVolume = calculateVolume(context, bgmVolume);
        bgmPlayer.setVolume(finalVolume, finalVolume);
        bgmPlayer.start();

    }

    public static float calculateVolume(Context context, int vol){
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int currentDeviceVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxDeviceVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return ((float)currentDeviceVolume/maxDeviceVolume) * ((float)(1 - Math.log(Utility.MAX_VOLUME - vol)/ Math.log(Utility.MAX_VOLUME)));
    }

    public void initMediaPlayers() {
        bgmPlayer = MediaPlayer.create(context, R.raw.bg_music);
        bgmPlayer.setLooping(true);
    }

    //Soundpool too unreliable(sometimes does not play sounds and reports no errors) so I used Mediaplayer here
    public static void playClick(Context context){
        Random random = new Random();
        int randomPosition = random.nextInt(CLICK_SOUNDS.length);

        MediaPlayer clickPlayer = MediaPlayer.create(context, CLICK_SOUNDS[randomPosition]);
        clickPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        clickPlayer.start();
    }

    public void loadSavedVolumes(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        soundVolume = sharedPref.getInt(context.getString(R.string.sound_volume), Utility.MAX_VOLUME);
        bgmVolume = sharedPref.getInt(context.getString(R.string.music_volume), Utility.MAX_VOLUME);
    }

    public void initSettingsDialog(){
        settingsDialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_settings, false)
                .titleGravity(GravityEnum.CENTER)
                .title("Settings")
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utility.startImmersiveMode(context);
                    }
                })
                .build();
        View v = settingsDialog.getCustomView();

        SeekBar soundSeekbar = (SeekBar)v.findViewById(R.id.soundSeekBar);
        soundSeekbar.setMax(Utility.MAX_VOLUME);
        soundSeekbar.setProgress(soundVolume);
        soundSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                soundVolume = progress;
                Utility.setSharedPrefValues(context, context.getString(R.string.sound_volume), progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar bgmSeekbar = (SeekBar)v.findViewById(R.id.bgmSeekBar);
        bgmSeekbar.setMax(Utility.MAX_VOLUME);
        bgmSeekbar.setProgress(bgmVolume);
        bgmSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                bgmVolume = progress;
                Utility.setSharedPrefValues(context, context.getString(R.string.music_volume), progress);

                float finalVolume = calculateVolume(context, bgmVolume);
                bgmPlayer.setVolume(finalVolume, finalVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void show(){
        settingsDialog.show();
    }
}
