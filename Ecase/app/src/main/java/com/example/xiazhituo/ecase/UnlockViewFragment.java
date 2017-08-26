package com.example.xiazhituo.ecase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xiazhituo on 2017/8/23.
 */

public class UnlockViewFragment extends Fragment {

    CircleProgressView mUnlockCircleView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_unlock, container, false);

        mUnlockCircleView = (CircleProgressView) rootView.findViewById(R.id.unlockCircleBtn);
        mUnlockCircleView.setProgress(100);
        mUnlockCircleView.setOnClickListener(new View.OnClickListener() {
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
