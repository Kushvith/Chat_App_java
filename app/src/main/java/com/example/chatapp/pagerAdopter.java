package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class pagerAdopter extends FragmentPagerAdapter {
    int tabcount;
    public pagerAdopter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        tabcount = behavior;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new chatfragment();
            case 1:
                return new statusfragment();
            case 2:
                return new callfragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabcount;
    }
}
