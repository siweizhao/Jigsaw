package com.szhao.jigsaw.Old.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.szhao.jigsaw.R;

/**
 * Created by Owner on 7/19/2017.
 */

public class GameMenuDialog extends AppCompatDialogFragment {
    private Button showSolutionBtn;
    private Button resetPuzzleBtn;
    private Button goPuzzleSelectorBtn;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.dialog_light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View view = inflater.inflate(R.layout.dialog_game_menu, container, false);
        return view;
    }

}
