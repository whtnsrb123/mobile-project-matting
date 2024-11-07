package com.example.Matting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedSearchAdapter extends RecyclerView.Adapter<FeedSearchAdapter.ViewHolder> {

    private List<String> searchResults;

    public FeedSearchAdapter(List<String> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public FeedSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FeedSearchAdapter.ViewHolder holder, int position) {
        holder.resultTextView.setText(searchResults.get(position));
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView resultTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            resultTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
