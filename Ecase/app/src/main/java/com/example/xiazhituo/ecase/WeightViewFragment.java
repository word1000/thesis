package com.example.xiazhituo.ecase;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xiazhituo on 2017/8/23.
 */

public class WeightViewFragment extends Fragment {
    CircleProgressView mWeightCircleView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weight, container, false);

        mWeightCircleView = (CircleProgressView) rootView.findViewById(R.id.weightCircleBtn);
        String weightHint = "X " + " Kg";
        mWeightCircleView.setmTxtHint1(weightHint);
        mWeightCircleView.setProgress(0);

        mWeightCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
