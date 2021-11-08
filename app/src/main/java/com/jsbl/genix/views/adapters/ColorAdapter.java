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
import com.jsbl.genix.model.profileManagement.Color;
import com.jsbl.genix.model.profileManagement.Manufacturer;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.MyViewHolder> {

    private List<Color> colorList;

    private Context context;
    private int pos;
//    private String userID;
    public static ColorsClicks clicks;

    public ColorAdapter(List<Color> colorList, ColorsClicks clicks, Context context) {
        this.colorList = colorList;
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
        holder.textHolder.setText(colorList.get(position).getName());

        holder.textHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicks.colorPicker(position,  colorList.get(position).getName());
            }
        });


    }

    @Override
    public int getItemCount() {
        Log.d("brandSize", ""+ colorList.size());
        return colorList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textHolder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textHolder=itemView.findViewById(R.id.textHolder);

        }
    }

    public void setProductList(List<Color> colorList) {
        this.colorList = colorList;
        notifyDataSetChanged();
    }



    public interface ColorsClicks {

        void colorPicker( int position, String colorName);
    }

}
