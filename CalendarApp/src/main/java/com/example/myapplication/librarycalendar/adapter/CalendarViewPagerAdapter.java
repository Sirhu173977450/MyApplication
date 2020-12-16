package com.example.myapplication.librarycalendar.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myapplication.librarycalendar.fragment.CalendarViewFragment;
import com.example.myapplication.librarycalendar.utils.DateUtils;


/**
 * Created by joybar on 4/27/16.
 */
public class CalendarViewPagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_ITEMS = 200;
    public static final int NUM_ITEMS_CURRENT = NUM_ITEMS/2;
    private int mThisMonthPosition = DateUtils.getYear()*12+DateUtils.getMonth()-1;//---100 -position
    private int number = mThisMonthPosition - NUM_ITEMS_CURRENT;
    private boolean isChoiceModelSingle;
	private CalendarViewFragment mCurrentFragment;
    public CalendarViewPagerAdapter(FragmentManager fm, boolean isChoiceModelSingle) {
        super(fm);
        this.isChoiceModelSingle = isChoiceModelSingle;
    }

    @Override
    public Fragment getItem(int position) {
        int year = getYearByPosition(position);
        int month = getMonthByPosition(position);
        Fragment fragment = CalendarViewFragment.newInstance(year,month,isChoiceModelSingle);
        return  fragment;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    public int getYearByPosition(int position){
        int year = (position+number)/12;
        return year;
    }
    public  int getMonthByPosition(int position){
        int month = (position + number) % 12 + 1;
        return month;
    }
    
	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		mCurrentFragment = (CalendarViewFragment) object;
		super.setPrimaryItem(container, position, object);
	}

	public CalendarViewFragment getCurrentFragment() {
		return mCurrentFragment;
	}
}
