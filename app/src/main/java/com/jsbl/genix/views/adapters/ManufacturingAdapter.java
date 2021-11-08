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
import com.jsbl.genix.model.profileManagement.Manufacturer;

import java.util.List;

public class ManufacturingAdapter extends RecyclerView.Adapter<ManufacturingAdapter.MyViewHolder> {

    private List<Manufacturer> brandModelList;
    private Context context;
    private int pos;
//    private String userID;
    public static Clicks clicks;

    public ManufacturingAdapter(List<Manufacturer> searchModelList, Clicks clicks, Context context) {
        this.brandModelList = searchModelList;
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
        holder.textHolder.setText(brandModelList.get(position).getName());

        holder.textHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicks.manufacturer(position,  brandModelList.get(position).getName(),  brandModelList.get(position).getID());
            }
        });


    }

    @Override
    public int getItemCount() {
        Log.d("brandSize", ""+ brandModelList.size());
        return brandModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textHolder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textHolder=itemView.findViewById(R.id.textHolder);

        }
    }

    public void setProductList(List<Manufacturer> productList) {
        this.brandModelList = productList;
        notifyDataSetChanged();
    }



    public interface Clicks {

        void manufacturer(int position, String manufacturerName, Long manufacturerID);
    }

}
