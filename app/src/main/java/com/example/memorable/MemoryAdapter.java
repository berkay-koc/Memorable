package com.example.memorable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder>{

    private List<Memory> memoryList;

    public MemoryAdapter(List<Memory> list){
        memoryList = list;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @NonNull
    @Override
    public MemoryAdapter.MemoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(MemoryAdapter.MemoryViewHolder holder, int position) {
        holder.title.setText(memoryList.get(position).getTitle());
        holder.number.setText(getItemCount() + 1);
        holder.todayDate.setText(memoryList.get(position).getDate().toString());
        holder.imageView.setImageResource(R.drawable.diary_img);
    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }

    public static class MemoryViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView number, todayDate, title;
        public MemoryViewHolder(View itemView) {
            super(itemView);

            this.imageView = itemView.findViewById(R.id.diary_cover);
            this.title = itemView.findViewById(R.id.title);
            this.number = itemView.findViewById(R.id.number);
            this.todayDate = itemView.findViewById(R.id.todayDate);
        }
    }
}
