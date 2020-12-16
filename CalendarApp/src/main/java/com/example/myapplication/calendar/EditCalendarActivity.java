package com.example.myapplication.calendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;

import com.example.myapplication.dao.DataBaseDaoUtils;
import com.example.myapplication.librarycalendar.utils.CalendarEvent2;
import com.example.myapplication.librarycalendar.utils.CalendarsResolver;
import com.example.myapplication.librarycalendar.utils.DateUtils;
import com.example.myapplication.librarycalendar.utils.Utils;
import com.example.myapplication.spinner.NiceSpinner;

public class EditCalendarActivity extends FragmentActivity {
	private EditText etTitle;
	private EditText etDescription;
	private TextView title;
	// private Spinner spinnerRemind;
	private NiceSpinner spinnerRemind;
	private NiceSpinner spinnerRepeat;
//	private Spinner spinnerRepeat;
	private LinearLayout llStartTime;
	TextView tvStartTime;
	private LinearLayout llEndTime;
	TextView tvEndTime;
	private Button save;
	private Button cancel;

	private int type = 0;// 类型：0为添加，1为更新
	private String text = "";// 聊天界面传过来的值
	private LayoutInflater inflater;
	/** 日程事件 */
	private CalendarEvent2 calendarEvent;

	int year = 0;// 日历点击时间年
	int month = 0;// 日历点击时间月
	int day = 0;// 日历点击时间日

    DataBaseDaoUtils mDaoUtils = new DataBaseDaoUtils(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agenda_setting);
		inflater = LayoutInflater.from(this);
		boolean has_permission = (PackageManager.PERMISSION_GRANTED == getPackageManager()
				.checkPermission("android.permission.WRITE_CALENDAR", getPackageName()));
		if (!has_permission) {
			Toast.makeText(this, "权限被禁止，请前往app详情设置权限", Toast.LENGTH_SHORT).show();
			return;
		}
		if (null != getIntent().getSerializableExtra("objectEntity")) {
			calendarEvent = (CalendarEvent2) getIntent().getSerializableExtra("objectEntity");
		} else {
			calendarEvent = new CalendarEvent2();
		}
		type = getIntent().getIntExtra("type", 0);
		title = (TextView) findViewById(R.id.title);
		if (type == 0) {
			title.setText(R.string.event_add);
			year = getIntent().getIntExtra("year", 0);
			month = getIntent().getIntExtra("month", -1);
			day = getIntent().getIntExtra("day", 0);
		} else if (type == 1) {
			title.setText(R.string.event_update);
		}

		llStartTime = (LinearLayout) findViewById(R.id.ll_start_time);
		tvStartTime = (TextView) findViewById(R.id.tv_start_time);
		setAgendaTime(0, type, year, month, day); // 设置日程事件时间

		llEndTime = (LinearLayout) findViewById(R.id.ll_end_time);
		tvEndTime = (TextView) findViewById(R.id.tv_end_time);
		setAgendaTime(1, type, year, month, day); // 设置日程事件时间

		etTitle = (EditText) findViewById(R.id.et_agenda_title);
		etDescription = (EditText) findViewById(R.id.et_agenda_description);

		text = getIntent().getStringExtra("text");
		if (type == 0 && null != text && !text.equals("")) {
			etTitle.setText(text);
			etDescription.setText(text);
		}
		spinnerRemind = (NiceSpinner) findViewById(R.id.spin_agenda_remind);
		spinnerRepeat = (NiceSpinner) findViewById(R.id.spin_agenda_repeat);
		if (type == 1) {
			etTitle.setText(calendarEvent.getTitle());
			etDescription.setText(calendarEvent.getDescription());
			etTitle.setSelection(calendarEvent.getTitle().length());
			etDescription.setSelection(calendarEvent.getDescription().length());
		}

		setAgendaRemind(); // 设置提醒时间
		setAgendaRepeat(); // 设置重复

		save = (Button) findViewById(R.id.btn_save);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String eventTitle = etTitle.getText().toString();
				String description = etDescription.getText().toString();

				calendarEvent.setTitle(eventTitle);
				calendarEvent.setDescription(description);
				if (calendarEvent.getEndTime() < calendarEvent.getBeginTime()) {
					Utils.toast(EditCalendarActivity.this, "结束日期不能小于开始日期");
					return;
				}
				if (type == 0) {
					int result = CalendarsResolver.getInstance().addData(EditCalendarActivity.this, calendarEvent);
					if (result >= 0) {
						Log.e("addEventId: ",result+"");
						calendarEvent.setEventId(result);

						//TODO:添加事件到本地数据库
                        boolean b = mDaoUtils.insertEvent(calendarEvent);
                        if(b)Log.e("local add ","添加本地数据库记录成功！");

						Utils.toast(EditCalendarActivity.this, "添加事件成功！");
					}
				} else if (type == 1) {
					boolean result = CalendarsResolver.getInstance().updateData(EditCalendarActivity.this,
							calendarEvent);
					if (result) {
						Utils.toast(EditCalendarActivity.this, "更新事件成功！");
					}
				}
				Intent data = new Intent();
				data.putExtra("objectEntity", calendarEvent);
				setResult(Activity.RESULT_OK, data);
				finish();
			}
		});
		cancel = (Button) findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 设置日程事件时间
	 * 
	 * @param
	 */
	private void setAgendaTime(final int flag, final int type, final int year, final int month, final int day) {
		if (type == 0) {
			GregorianCalendar calendar = new GregorianCalendar();
			if (year != 0)
				calendar.add(Calendar.YEAR, (year - calendar.get(Calendar.YEAR)));
			if (month >= 0)
				calendar.add(Calendar.MONTH, (month - calendar.get(Calendar.MONTH)));
			if (day != 0)
				calendar.add(Calendar.DAY_OF_MONTH, (day - calendar.get(Calendar.DAY_OF_MONTH)));
			if (flag != 0)
				calendar.add(Calendar.HOUR_OF_DAY, 1);

			if (flag == 0) {
				calendarEvent.setBeginTime(calendar.getTimeInMillis());
			} else {
				calendarEvent.setEndTime(calendar.getTimeInMillis());
			}
			String agendaTimeString = DateUtils.timeStamp2Date(calendar.getTimeInMillis(), "yyyy-MM-dd HH:mm:ss");
			(flag == 0 ? tvStartTime : tvEndTime).setText(agendaTimeString);
		} else {
			String agendaTimeString = DateUtils.timeStamp2Date(
					flag == 0 ? (calendarEvent.getBeginTime()) : (calendarEvent.getEndTime()), "yyyy-MM-dd HH:mm:ss");
			(flag == 0 ? tvStartTime : tvEndTime).setText(agendaTimeString);
		}
		(flag == 0 ? llStartTime : llEndTime).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTimeText((flag == 0 ? tvStartTime : tvEndTime), flag, year, month, day);
			}
		});
	}

	/**
	 * 设置提醒时间
	 * 
	 * @param
	 */
	private void setAgendaRemind() {
		// 默认选择提前15分钟
		final int[] agendaRemindValues = getResources().getIntArray(R.array.agenda_remind_int);
		int position = 0;
		for (int i = 0; i < agendaRemindValues.length; i++) {
			if (calendarEvent.getRemind() == agendaRemindValues[i])
				position = i;
		}
		
		String[] datasRemind = getResources().getStringArray(R.array.agenda_remind_string);
		final List<String> dataset = new ArrayList<String>(Arrays.asList(datasRemind));
		spinnerRemind.attachDataSource(dataset);
		spinnerRemind.setSelectedIndex(position);
		spinnerRemind.addOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				calendarEvent.setRemind(agendaRemindValues[position]);
			}
		});
	}

	/**
	 * 设置事件重复方式
	 * 
	 * @param
	 */
	private void setAgendaRepeat() {
		final String[] repeat = getResources().getStringArray(R.array.agenda_repeat_string_values);
		final String[] repeat2 = getResources().getStringArray(R.array.agenda_repeat_string_values2);

		int position = 0;
		String repeatValue = calendarEvent.getRepeat();
		for (int i = 0; i < repeat2.length; i++) {
			if (repeatValue != null && repeatValue.contains(repeat2[i])) {
				position = i;
			}
		}
		
		String[] datasRepeat = getResources().getStringArray(R.array.agenda_repeat_string);
		final List<String> dataset = new ArrayList<String>(Arrays.asList(datasRepeat));
		spinnerRepeat.attachDataSource(dataset);
		spinnerRepeat.setSelectedIndex(position);
		spinnerRepeat.addOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				calendarEvent.setRepeat(String.format( repeat[position],"MO", "WE","FR"));
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void setTimeText(final TextView textViewTime, final int flag, int year, int month, int day) {
		View viewDate = inflater.inflate(R.layout.data_time_picker, null);
		final DatePicker datePicker = (DatePicker) viewDate.findViewById(R.id.dp_agenda);
		final TimePicker timePicker = (TimePicker) viewDate.findViewById(R.id.tp_agenda);
		// int year = 0;
		// int month = 0;
		// int day = 0;
		int hour = 0;
		int minute = 0;
		final GregorianCalendar calendar = new GregorianCalendar();
		if (type == 0) {
			if (year == 0)
				year = datePicker.getYear();
			if (month < 0)
				month = datePicker.getMonth();
			if (day == 0)
				day = datePicker.getDayOfMonth();
			hour = timePicker.getCurrentHour();
			if (flag == 1)
				hour = timePicker.getCurrentHour() + 1;
			minute = timePicker.getCurrentMinute();
			calendar.set(year, month, day, hour, minute);
		} else {
			calendar.setTime(new Date(flag == 0 ? (calendarEvent.getBeginTime()) : (calendarEvent.getEndTime())));
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
			hour = calendar.get(Calendar.HOUR_OF_DAY);
			minute = calendar.get(Calendar.MINUTE);
		}
		datePicker.init(year, month, day, null);
		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);

		AlertDialog dialog = new AlertDialog.Builder(EditCalendarActivity.this).setView(viewDate)
				.setNegativeButton("取消", null).setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						GregorianCalendar calendar = new GregorianCalendar();
						int year = datePicker.getYear();
						int month = datePicker.getMonth();
						int day = datePicker.getDayOfMonth();
						int hour = timePicker.getCurrentHour();
						int minute = timePicker.getCurrentMinute();
						calendar.set(year, month, day, hour, minute);

						String time = "";
						if (flag == 0) {
							calendarEvent.setBeginTime(calendar.getTimeInMillis());
							time = DateUtils.timeStamp2Date(calendarEvent.getBeginTime(), "yyyy-MM-dd HH:mm:ss");
						} else {
							calendarEvent.setEndTime(calendar.getTimeInMillis());
							time = DateUtils.timeStamp2Date(calendarEvent.getEndTime(), "yyyy-MM-dd HH:mm:ss");
						}
						textViewTime.setText(time);
					}
				}).create();
		dialog.show();
	}
}
