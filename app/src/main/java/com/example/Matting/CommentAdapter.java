package com.example.Matting;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.usernameTextView.setText(comment.getUsername());
        holder.contentTextView.setText(comment.getContent());

        if (comment.getTimestamp() != null) {
            holder.timestampTextView.setText(
                    DateUtils.getRelativeTimeSpanString(comment.getTimestamp().getTime())
            );
        } else {
            holder.timestampTextView.setText("Unknown Time");
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTextView, contentTextView, timestampTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.commentUsername);
            contentTextView = itemView.findViewById(R.id.commentContent);
            timestampTextView = itemView.findViewById(R.id.commentTimestamp);
        }
    }
}
