package com.jsbl.genix.views.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jsbl.genix.R;
import com.jsbl.genix.model.profileManagement.InterestSubInterest;
import com.jsbl.genix.model.profileManagement.Purpose;
import com.jsbl.genix.viewModel.AreaOfInterestViewModel;
import com.jsbl.genix.views.activities.SubAreaOfInterestActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AreaOfInterestAdapter extends RecyclerView.Adapter<AreaOfInterestAdapter.MyViewHolder>{
    private List<InterestSubInterest> areaOfInterestViewModelList;
    private Context context;

    public AreaOfInterestAdapter(List<InterestSubInterest> areaOfInterestViewModelList, Context context) {
        this.areaOfInterestViewModelList = areaOfInterestViewModelList;
        this.context = context;
    }

    @NotNull
    @Override
    public AreaOfInterestAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_of_interest, null);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AreaOfInterestAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.areaOfInterestTV.setText(areaOfInterestViewModelList.get(position).getInterestTitle());
        if (areaOfInterestViewModelList.get(position).isSelected()){
            holder.innerCard.setBackgroundResource(R.drawable.new_ic_btn_bg_selected);
        }else {
            holder.innerCard.setBackgroundResource(R.drawable.new_ic_btn_bg_unselected);
        }
        Glide.with(context)
                .load("http://genix.ermispk.com/Icons/Interest/"+areaOfInterestViewModelList.get(position).getInterestFilePath())
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.place_holder)
                .into(holder.areaOfInterestIV);


        holder.innerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context, SubAreaOfInterestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id",""+areaOfInterestViewModelList.get(position).getInterestID());
                bundle.putParcelableArrayList("subInterestList", (ArrayList<? extends Parcelable>) areaOfInterestViewModelList.get(position).getSubInterestList());
                intent.putExtras(bundle);
                /*intent.putExtra("id",""+areaOfInterestViewModelList.get(position).getInterestID());
                intent.putExtra("position",""+areaOfInterestViewModelList.get(position));
                intent.putExtra("interestName",""+areaOfInterestViewModelList.get(position).getInterestTitle());
                intent.putExtra("subInterestList", "" +  areaOfInterestViewModelList.get(position).getSubInterestList());*/
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return areaOfInterestViewModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView areaOfInterestIV;
        TextView areaOfInterestTV;
        LinearLayout innerCard;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            areaOfInterestTV=itemView.findViewById(R.id.areaOfInterestTV);
            areaOfInterestIV=itemView.findViewById(R.id.areaOfInterestIV);
            innerCard=itemView.findViewById(R.id.innerConstraint);
        }
    }
}
