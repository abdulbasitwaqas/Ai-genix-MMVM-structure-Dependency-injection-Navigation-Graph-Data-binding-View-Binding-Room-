package com.jsbl.genix.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jsbl.genix.R;
import com.jsbl.genix.model.profileManagement.SubManufacturerModel;
import com.jsbl.genix.model.profileManagement.SubManufacturerModel;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.MyViewHolder> {

    private List<SubManufacturerModel> makerList;
    private Context context;
    private int pos;
    public static MakerClick clicks;

    public VehicleAdapter(List<SubManufacturerModel> makerList, MakerClick clicks, Context context) {
        this.makerList = makerList;
        this.context = context;
        this.clicks = clicks;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_drop_down, null);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textHolder.setText(makerList.get(position).getName());

        holder.textHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicks.vehicle(position,  makerList.get(position).getName());
            }
        });

    }

    @Override
    public int getItemCount() {
        Log.d("brandSize", ""+ makerList.size());
        return makerList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textHolder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textHolder=itemView.findViewById(R.id.textHolder);

        }
    }

    public void setProductList(List<SubManufacturerModel> makerList) {
        this.makerList = makerList;
        notifyDataSetChanged();
    }



    public interface MakerClick{
        void vehicle(int position, String vehicleName);
    }

}
