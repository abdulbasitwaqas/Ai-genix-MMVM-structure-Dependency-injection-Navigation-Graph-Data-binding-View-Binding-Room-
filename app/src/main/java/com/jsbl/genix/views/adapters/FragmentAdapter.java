package com.jsbl.genix.views.adapters;
/**
 * all required libraries imported here
 */

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ibrahim Khalil on 25-Sep-17.
 */

public class FragmentAdapter extends FragmentStatePagerAdapter {
    /**
     * field instances array lists  of fragments and fragment titles
     */
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public FragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    /**
     * constructor for getting the current fragment manager
     *
     * @param manager
     */

    /**
     * returning the fragment of each position
     *
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    /**
     * counting the total fragment from arratlist size and return
     *
     * @return
     */

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * adding fragment and fragment to the array lists
     *
     * @param fragment
     * @param title
     */

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }


    public void addFragmentAtFirst(Fragment fragment, String title) {
        mFragmentList.add(0,fragment);
        mFragmentTitleList.add(0,title);
    }

    /**
     * returning the title of each position
     *
     * @param position
     * @return
     */

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public int getItemPosition(@NotNull Object item)
    {
        return POSITION_NONE;
    }
    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {

    }
}
