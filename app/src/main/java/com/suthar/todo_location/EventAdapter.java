package com.suthar.todo_location;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {


    ItemClicked activity;
    Context cnt;
    private ArrayList<Event> EventRemain;

    public EventAdapter(@NonNull Context context, ArrayList<Event> list) {
        EventRemain = list;
        cnt = context;
        activity = (ItemClicked) context;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(EventRemain.get(position));

        if (EventRemain.get(position).getType().compareToIgnoreCase("TIME") == 0) {
            holder.ivType.setImageResource(R.drawable.ic_watch);
            holder.tvTitle.setText(EventRemain.get(position).getTitle());
            holder.tvDescription.setText(EventRemain.get(position).getDescription());
            holder.tvDate.setText(" " + EventRemain.get(position).getDate());
            holder.tvTime.setText(EventRemain.get(position).getTime() + " ");
            holder.tvLocation.setVisibility(View.GONE);
            holder.tvAccuracy.setVisibility(View.GONE);
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvTime.setVisibility(View.VISIBLE);
            boolean state = true;
            if (EventRemain.get(position).getisDone() == 0) state = false;
            holder.cbDone.setChecked(state);

        } else {
            holder.ivType.setImageResource(R.drawable.ic_location);
            holder.tvTitle.setText(EventRemain.get(position).getTitle());
            holder.tvDescription.setText(EventRemain.get(position).getDescription());
            holder.tvDate.setVisibility(View.GONE);
            holder.tvTime.setVisibility(View.GONE);
            holder.tvLocation.setText("Address: " + EventRemain.get(position).getAddress());
            holder.tvAccuracy.setText("Accuracy: " + EventRemain.get(position).getAccuracy() + " m");
            holder.tvLocation.setVisibility(View.VISIBLE);
            holder.tvAccuracy.setVisibility(View.VISIBLE);
            boolean state = true;
            if (EventRemain.get(position).getisDone() == 0) state = false;
            holder.cbDone.setChecked(state);

        }

    }

    @Override
    public int getItemCount() {
        return EventRemain.size();
    }

    public interface ItemClicked {
        void OnItemClicked(int index);

        void OnCheckBoxClicked(int index, boolean newState);

        void OnItemLongClicked(int index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivType;
        TextView tvTitle, tvDescription, tvDate, tvTime, tvLocation, tvAccuracy;
        CheckBox cbDone;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            ivType = itemView.findViewById(R.id.ivType);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvAccuracy = itemView.findViewById(R.id.tvAccuracy);
            cbDone = itemView.findViewById(R.id.cbDone);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.OnItemClicked(EventRemain.indexOf((Event) view.getTag()));
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    activity.OnItemLongClicked(EventRemain.indexOf((Event) view.getTag()));
                    return true;
                }
            });
            cbDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean state = cbDone.isChecked();
                    activity.OnCheckBoxClicked(EventRemain.indexOf((Event) itemView.getTag()), state);

                }
            });

        }
    }
}
