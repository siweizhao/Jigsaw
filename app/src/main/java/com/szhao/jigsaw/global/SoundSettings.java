package com.szhao.jigsaw.global;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
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

public class SoundSettings {
    private static final int MAX_VOLUME = 8;
    private static int[] CLICK_SOUNDS = new int[]{
            R.raw.click_1,
            R.raw.click_2,
            R.raw.click_3
    };
    private static int[] BGM = new int[]{
            R.raw.bg_1,
            R.raw.bg_2,
            R.raw.bg_3,
            R.raw.bg_4
    };

    private static int soundVolume;
    private static int bgmVolume;
    private Context context;
    private MediaPlayer bgmPlayer;
    private MaterialDialog settingsDialog;

    public SoundSettings(Context context) {
        this.context = context;
        loadSavedVolumes();
        initSettingsDialog();
        initBGM();
    }

    private static float calculateVolume(Context context, int vol) {
        AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int currentDeviceVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxDeviceVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return ((float) currentDeviceVolume / maxDeviceVolume) * ((float) (1 - Math.log(MAX_VOLUME - vol) / Math.log(MAX_VOLUME)));
    }

    //Soundpool too unreliable(sometimes does not play sounds and reports no errors) so I used Mediaplayer here
    public void playClick() {
        Random random = new Random();
        int randomPosition = random.nextInt(CLICK_SOUNDS.length);

        MediaPlayer clickPlayer = MediaPlayer.create(context, CLICK_SOUNDS[randomPosition]);
        float finalVolume = calculateVolume(context, soundVolume);
        clickPlayer.setVolume(finalVolume, finalVolume);
        clickPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        clickPlayer.start();
    }

    public void playCheer() {
        MediaPlayer cheerPlayer = MediaPlayer.create(context, R.raw.cheer);
        float finalVolume = calculateVolume(context, soundVolume);
        cheerPlayer.setVolume(finalVolume, finalVolume);
        cheerPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        cheerPlayer.start();
    }

    private void initBGM() {
        Random random = new Random();
        int randomPosition = random.nextInt(BGM.length);
        bgmPlayer = MediaPlayer.create(context, BGM[randomPosition]);
        float finalVolume = calculateVolume(context, bgmVolume);
        bgmPlayer.setVolume(finalVolume, finalVolume);
        bgmPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                initBGM();
            }
        });
    }

    public void toggleBGM() {
        if (bgmPlayer.isPlaying())
            bgmPlayer.pause();
        else
            bgmPlayer.start();
    }

    private void loadSavedVolumes() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        soundVolume = sharedPref.getInt(context.getString(R.string.sound_volume), MAX_VOLUME);
        bgmVolume = sharedPref.getInt(context.getString(R.string.music_volume), MAX_VOLUME);
    }

    private void initSettingsDialog() {
        settingsDialog = new MaterialDialog.Builder(context)
                .customView(R.layout.dialog_settings, false)
                .titleGravity(GravityEnum.CENTER)
                .title("Sound Settings")
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Utility.startImmersiveMode(context);
                    }
                })
                .build();
        View v = settingsDialog.getCustomView();

        SeekBar soundSeekbar = (SeekBar)v.findViewById(R.id.soundSeekBar);
        soundSeekbar.setMax(MAX_VOLUME);
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
        bgmSeekbar.setMax(MAX_VOLUME);
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
