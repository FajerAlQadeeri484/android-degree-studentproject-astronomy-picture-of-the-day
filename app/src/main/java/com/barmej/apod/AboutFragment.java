package com.barmej.apod;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

public class AboutFragment extends DialogFragment {

    private TextView mTitle;
    private View mLine;
    private TextView mAboutText;
    private ImageView mNasaLogo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View mainView = getView();

        mTitle = mainView.findViewById(R.id.txt_about_title);
        mLine = mainView.findViewById(R.id.lineA);
        mAboutText = mainView.findViewById(R.id.txt_about);
        mNasaLogo = mainView.findViewById(R.id.nasaLogo);
    }

}
