package com.example.myapplication.smallgame;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.List;

public class SmallGameActivity extends AppCompatActivity {

    private static final String TAG = "SmallGameActivity";
    private Context mContext;
    private ViewPager mViewVp;
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_game);
        initView();
        mContext = this/*.getApplicationContext()*/;


    }

    private void initView() {
        mViewVp = (ViewPager) findViewById(R.id.vp_view);
        for (int i = 0; i < 5; i++) {
            mFragments.add(new SmallGameFragment());
        }
        mViewVp.setAdapter(new SmallGameAdapter(getSupportFragmentManager(),mFragments));
    }

    class SmallGameAdapter extends FragmentPagerAdapter{
        private List<Fragment> mFragments;
        public SmallGameAdapter(FragmentManager fm,List<Fragment> mFragments) {
            super(fm);
            this.mFragments = mFragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
