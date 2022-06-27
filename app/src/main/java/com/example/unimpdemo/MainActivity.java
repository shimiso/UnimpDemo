package com.example.unimpdemo;

import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.MenuItem;

import com.example.unimpdemo.base.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    // collections
    private SparseIntArray items;// used for change ViewPager selected item
    private MessageTabFragment messageTabFragment;
    private MyCenterFragment  myCenterFragment;
    private WorkFragment  workFragment;
    private List<Fragment> fragments;// used for ViewPager adapter
    private VpAdapter adapter;
    @BindView(R.id.nav_view)
    public BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            messageTabFragment = MessageTabFragment.newInstance();
            workFragment = WorkFragment.newInstance();
            myCenterFragment = MyCenterFragment.newInstance();
        } else {
            messageTabFragment = (MessageTabFragment) getSupportFragmentManager().getFragment(savedInstanceState, MessageTabFragment.class.getName());
            workFragment = (WorkFragment) getSupportFragmentManager().getFragment(savedInstanceState, WorkFragment.class.getName());
            myCenterFragment = (MyCenterFragment) getSupportFragmentManager().getFragment(savedInstanceState, MyCenterFragment.class.getName());
        }

        fragments = new ArrayList<>(3);
        items = new SparseIntArray(3);
        fragments.add(messageTabFragment);
        fragments.add(workFragment);
        fragments.add(myCenterFragment);

        items.put(R.id.navigation_message, 0);
        items.put(R.id.navigation_work, 1);
        items.put(R.id.navigation_mycenter, 2);

        // set adapter
        adapter = new VpAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);//默认预加载几个页面

        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            private int previousPosition = -1;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int position = items.get(item.getItemId());
                if (previousPosition != position) {
                    previousPosition = position;
                    viewPager.setCurrentItem(position);
                }
                return true;
            }
        });
        // set listener to change the current checked item of bottom nav when scroll view pager
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                navigation.setSelectedItemId(navigation.getMenu().getItem(position).getItemId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            getSupportFragmentManager().putFragment(outState, MessageTabFragment.class.getName(), messageTabFragment);
            getSupportFragmentManager().putFragment(outState, MyCenterFragment.class.getName(), myCenterFragment);
            getSupportFragmentManager().putFragment(outState, WorkFragment.class.getName(), workFragment);
        } catch (Exception e) {
        }
    }


    /**
     * view pager adapter
     */
    private static class VpAdapter extends FragmentPagerAdapter {
        private List<Fragment> data;

        public VpAdapter(FragmentManager fm, List<Fragment> data) {
            super(fm);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Fragment getItem(int position) {
            return data.get(position);
        }
    }
}