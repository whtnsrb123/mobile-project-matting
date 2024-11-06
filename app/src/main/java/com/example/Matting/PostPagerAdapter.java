package com.example.Matting;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class PostPagerAdapter extends FragmentStateAdapter {
    private List<Post> postList;

    public PostPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Post> postList) {
        super(fragmentActivity);
        this.postList = postList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new PostFragment(postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }
}
