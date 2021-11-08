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

import java.util.List;

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.MyViewHolder> {

    private List<String> yearList;

    private Context context;
    private int pos;
//    private String userID;
    public static YearClicks clicks;

    public YearAdapter(List<String> colorList, YearClicks clicks, Context context) {
        this.yearList = colorList;
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
        holder.textHolder.setText(yearList.get(position));

        holder.textHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clicks.yearPicker(position,  yearList.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        Log.d("brandSize", ""+ yearList.size());
        return yearList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textHolder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textHolder=itemView.findViewById(R.id.textHolder);

        }
    }

    public void setProductList(List<String> colorList) {
        this.yearList = colorList;
        notifyDataSetChanged();
    }



    public interface YearClicks {

        void yearPicker(int position, String yearName);
    }

}
