package com.ei.kalavarafoods.ui.select_location;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.select_location.adapter.LocationsAdapter;
import com.ei.kalavarafoods.ui.select_location.model.Location;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectLocationDialogFragment extends DialogFragment implements LocationsAdapter.SelectLocation {

    private LocationsAdapter.SelectLocation selectLocation;
    private SelectLocationViewModel selectLocationViewModel;

    @BindView(R.id.rvLocations)
    RecyclerView rvLocations;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    public static SelectLocationDialogFragment newInstance() {
        Bundle args = new Bundle();
        SelectLocationDialogFragment fragment = new SelectLocationDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_location_dialog, container, false);
        ButterKnife.bind(this, view);

        rvLocations.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLocations.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        if (getContext() instanceof LocationsAdapter.SelectLocation){
            selectLocation = (LocationsAdapter.SelectLocation) getContext();
        }

        selectLocationViewModel = ViewModelProviders.of(this).get(SelectLocationViewModel.class);
        selectLocationViewModel.getDeliveryLocations().observe(this, locationResponse -> {
            rvLocations.setAdapter(new LocationsAdapter(this, locationResponse.getLocations()));
        });

        selectLocationViewModel.liveDataIsLoading.observe(this, aBoolean -> {
            if (aBoolean){
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    @Override
    public void onLocationSelected(Location location) {
        selectLocation.onLocationSelected(location);
        dismiss();
    }
}
