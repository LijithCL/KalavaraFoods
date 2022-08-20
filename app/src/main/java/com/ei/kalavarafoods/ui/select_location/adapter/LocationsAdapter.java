package com.ei.kalavarafoods.ui.select_location.adapter;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ei.kalavarafoods.R;
import com.ei.kalavarafoods.ui.select_location.model.Location;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.ViewHold> {

    private List<Location> locationList;
    private SelectLocation selectLocation;

    public LocationsAdapter(DialogFragment fragment, List<Location> locationList) {
        this.locationList = locationList;
        if (fragment instanceof SelectLocation){
            selectLocation = (SelectLocation) fragment;
        }
    }

    @NonNull
    @Override
    public ViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_location_item, parent, false);
        return new ViewHold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHold holder, int position) {
        Location location = locationList.get(position);
        holder.tvLocationText.setText(location.getLocationName());
        holder.clLocation.setOnClickListener(view -> {
            selectLocation.onLocationSelected(location);
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHold extends RecyclerView.ViewHolder {
        @BindView(R.id.clLocation)
        ConstraintLayout clLocation;
        @BindView(R.id.tvLocationText)
        TextView tvLocationText;

        public ViewHold(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface SelectLocation{
        void onLocationSelected(Location location);
    }
}
