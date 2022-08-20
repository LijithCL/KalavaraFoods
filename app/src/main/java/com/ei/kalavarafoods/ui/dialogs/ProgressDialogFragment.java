package com.ei.kalavarafoods.ui.dialogs;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.base.BaseDialogFragment;


public class ProgressDialogFragment extends BaseDialogFragment {

    public static ProgressDialogFragment newInstance() {
        Bundle args = new Bundle();
        ProgressDialogFragment fragment = new ProgressDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View setView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_dialog, container, false);
        return view;
    }

}
