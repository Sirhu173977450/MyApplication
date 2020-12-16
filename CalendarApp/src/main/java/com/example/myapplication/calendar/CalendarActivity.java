package com.example.myapplication.calendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.librarycalendar.adapter.CalendarViewPagerAdapter;
import com.example.myapplication.librarycalendar.data.CalendarDate;
import com.example.myapplication.librarycalendar.fragment.CalendarViewFragment;
import com.example.myapplication.librarycalendar.fragment.CalendarViewPagerFragment;
import com.example.myapplication.librarycalendar.utils.CalendarEvent2;
import com.example.myapplication.librarycalendar.utils.CalendarsResolver;


public class CalendarActivity extends FragmentActivity implements
		CalendarViewPagerFragment.OnPageChangeListener,
		CalendarViewFragment.OnDateClickListener,
		CalendarViewFragment.OnDateCancelListener{
	private TextView tv_date;

	private TextView today;

	private TextView list;

	private TextView create;

	private LinearLayout back;
	
	private CalendarViewPagerAdapter adapter;
	
	/**
	 * 日历fragment
	 */
	CalendarViewPagerFragment fragment;
	
	private boolean FirstIn=true;

	private List<CalendarEvent2> events;
	private List<CalendarDate> mListDate = new ArrayList<>();
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar_layout);
		tv_date = (TextView) findViewById(R.id.tv_date);
		back = (LinearLayout) findViewById(R.id.back);
		today = (TextView) findViewById(R.id.today);
		list = (TextView) findViewById(R.id.list);
		create = (TextView) findViewById(R.id.create);
		back.setOnClickListener(clickListener);
		tv_date.setOnClickListener(clickListener);
		today.setOnClickListener(clickListener);
		list.setOnClickListener(clickListener);
		create.setOnClickListener(clickListener);
		initFragment();
	}
	
	OnClickListener clickListener=new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (R.id.back==v.getId()) {
				finish();
			}
			else if (R.id.tv_date==v.getId()) {
				getListViewData(1,  getOneDayStartTime(), 0, 0, 7, false);
			}
			else if (R.id.today==v.getId()) {
				FirstIn=true;
				fragment.chooseToday();
			}
			else if (R.id.list==v.getId()) {
				Intent intent=new Intent(CalendarActivity.this,EventsListActivity.class);
				intent.putExtra("flag", 1);//flag=0某天，flag=1全部
				startActivityForResult(intent,999);
			}
			else if (R.id.create==v.getId()) {
//				Intent intent=new Intent(CalendarActivity.this,
//						EditCalendarActivity.class);
				Intent intent=new Intent(CalendarActivity.this,
						EditCalendarActivity2.class);
				intent.putExtra("type", 0);
				startActivityForResult(intent,888);
			}
		}
	};

	/**
	 * 得到一天当中开始的时间
	 *
	 * @return
	 */
	private GregorianCalendar getOneDayStartTime() {
		GregorianCalendar calendar = new GregorianCalendar();
		int year, month, day;
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar = new GregorianCalendar(year, month, day);
		return calendar;
	}

	/**
	 * @param flag     0为过去，1为将来
	 * @param calendar 现在的时间
	 * @param year     将来或过去多少年
	 * @param month    将来或过去多少月
	 * @param day      将来或过去多少天
	 */
	private void getListViewData(int flag, GregorianCalendar calendar, int year, int month, int day, boolean twoYears) {
		if (calendar == null) {
			events = CalendarsResolver.getInstance().queryData(CalendarActivity.this);
		} else {
			if (twoYears) {
				events = CalendarsResolver.getInstance().queryTwoYearsData(CalendarActivity.this, calendar);
			} else {
				events = CalendarsResolver.getInstance().querySomeDaysData(CalendarActivity.this, flag, calendar, year, month, day);
			}
		}
		Log.e("local event :",events.toString());
	}

	private void initFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		fragment = CalendarViewPagerFragment.newInstance(true);
		tx.replace(R.id.fl_content, fragment);
		tx.commit();
	}

	@Override
	public void onDateClick(CalendarDate calendarDate,int position) {
		this.position=position;
		int year = calendarDate.getSolar().solarYear;
		int month = calendarDate.getSolar().solarMonth;
		int day = calendarDate.getSolar().solarDay;
		// 单选模式
		tv_date.setText(year + "-" + month + "-" + day);
		if (FirstIn) {
			FirstIn=false;
			return;
		}
		GregorianCalendar calendar =new GregorianCalendar(year,month-1,day);
		events= CalendarsResolver.getInstance().
				querySomeDaysData(CalendarActivity.this, 1, calendar, 0, 0, 1);
		if (events.size()>0) {
			Intent intent=new Intent(CalendarActivity.this,EventsListActivity.class);
			intent.putExtra("flag", 0);//flag=0某天，flag=1全部
			intent.putExtra("year", year);
			intent.putExtra("month", month);
			intent.putExtra("day", day);
			startActivityForResult(intent,999);
		}
		else {
//			Intent intent=new Intent(CalendarActivity.this,
//					EditCalendarActivity.class);
			Intent intent=new Intent(CalendarActivity.this,
					EditCalendarActivity2.class);
			intent.putExtra("type", 0);
			intent.putExtra("year", year);//当前点击时间年
			intent.putExtra("month", month-1);//当前点击时间月
			intent.putExtra("day", day);//当前点击时间日
			startActivityForResult(intent,888);
		}
	}

	@Override
	public void onDateCancel(CalendarDate calendarDate) {
		int count = mListDate.size();
		for (int i = 0; i < count; i++) {
			CalendarDate date = mListDate.get(i);
			if (date.getSolar().solarDay == calendarDate.getSolar().solarDay) {
				mListDate.remove(i);
				break;
			}                          
		}
		tv_date.setText(listToString(mListDate));
	}

	@Override
	public void onPageChange(int year, int month) {
		tv_date.setText(year + "-" + month);
		mListDate.clear();
	}

	private static String listToString(List<CalendarDate> list) {
		StringBuffer stringBuffer = new StringBuffer();
		for (CalendarDate date : list) {
			stringBuffer.append(
					date.getSolar().solarYear + "-"
							+ date.getSolar().solarMonth + "-"
							+ date.getSolar().solarDay).append(" ");
		}
		return stringBuffer.toString();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (resultCode==Activity.RESULT_OK) {
//			if (requestCode==888) {//添加事件成功回调函数

		if ( null != adapter )  {
			CalendarViewFragment fragment = adapter.getCurrentFragment();
			if(null != fragment){
				fragment.setGridviewData();
				fragment.mGridView.setItemChecked(position, true);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
//			}
//		}
	}

	@Override
	public void getViewPager(CalendarViewPagerAdapter adapter) {
		this.adapter=adapter;
	}
}
