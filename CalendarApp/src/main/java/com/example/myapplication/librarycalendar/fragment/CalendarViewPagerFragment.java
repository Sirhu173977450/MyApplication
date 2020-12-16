package com.example.myapplication.librarycalendar.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.calendar.R;
import com.example.myapplication.librarycalendar.adapter.CalendarViewPagerAdapter;


/**
 * updated by kylin on 2017年4月19日09:25:03 王仲璘
 */
public class CalendarViewPagerFragment extends Fragment {

    private static final String CHOICE_MODE_SINGLE="choice_mode_single";
    private boolean isChoiceModelSingle;
    public ViewPager viewPager;
    private OnPageChangeListener onPageChangeListener;
	private CalendarViewPagerAdapter adapter;

    public CalendarViewPagerFragment() {
    }

    public static CalendarViewPagerFragment newInstance(boolean isChoiceModelSingle) {
        CalendarViewPagerFragment fragment = new CalendarViewPagerFragment();
        Bundle args = new Bundle();
        args.putBoolean(CHOICE_MODE_SINGLE, isChoiceModelSingle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onPageChangeListener = (OnPageChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement OnDateClickListener");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isChoiceModelSingle = getArguments().getBoolean(CHOICE_MODE_SINGLE,false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar_viewpager, container, false);
        initViewPager(view);
        return view;
    }
    private void initViewPager(View view){
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        adapter=new CalendarViewPagerAdapter(getChildFragmentManager(),isChoiceModelSingle);
        onPageChangeListener.getViewPager(adapter);
        resetData(adapter);
    }

	private void resetData(final CalendarViewPagerAdapter myAdapter) {
		viewPager.setAdapter(myAdapter);
        viewPager.setCurrentItem(CalendarViewPagerAdapter.NUM_ITEMS_CURRENT);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                int year = myAdapter.getYearByPosition(position);
                int month = myAdapter.getMonthByPosition(position);
               // tv_date.setText(year+"-"+month+"");
                onPageChangeListener.onPageChange(year,month);
            }
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
	}

    public interface OnPageChangeListener {
         void onPageChange(int year, int month);
         void getViewPager(CalendarViewPagerAdapter adapter);
    }
    
    /**
     * 重置到今天
     */
    public void chooseToday() {
    	resetData(adapter);
	}
}
