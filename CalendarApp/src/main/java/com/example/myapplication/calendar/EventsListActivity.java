package com.example.myapplication.calendar;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.dao.DataBaseDaoUtils;
import com.example.myapplication.librarycalendar.adapter.CalendarEventAdapter;
import com.example.myapplication.librarycalendar.utils.CalendarEvent2;
import com.example.myapplication.librarycalendar.utils.CalendarsResolver;
import com.example.myapplication.librarycalendar.utils.Md5Util;
import com.example.myapplication.librarycalendar.utils.Utils;

public class EventsListActivity extends Activity {
	public static final String EVENT_ADDED = "com.kylin.eventAdd";
	public static final String CALENDAR_EVENT = "calendar_event";

	private TextView tv_date;

	private TextView today;

	private TextView list;

	private TextView create;

	private LinearLayout back;
	private ListView listview;

	private TextView no_data;

	private int updatePosition;

	/**
	 * 多选数量
	 */
	private int eventChoiceCount;
	private TextView tvEventChoiceCount;
	private Button btnEventChoice;

	/**
	 * 多选操作布局
	 */
	private RelativeLayout rlMultiChoice;

	/**
	 * 多选删除按钮
	 */
	private Button btnDeleteEvent;

	/**
	 * 日程事件
	 */
	private CalendarEvent2 calendarEvent;

	private List<CalendarEvent2> events;
	private CalendarEventAdapter adapter;
	int flag = 0;
	int year, month, day;

	/**
	 * 弹出框
	 */
	PopupWindow pop;

	private BroadcastReceiver receiver;

	DataBaseDaoUtils mDaoUtils = new DataBaseDaoUtils(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eventslist);
		initView();
		flag = getIntent().getIntExtra("flag", 1);
		year = getIntent().getIntExtra("year", 0);
		month = getIntent().getIntExtra("month", 0);
		day = getIntent().getIntExtra("day", 1);

		Log.e("local queur : ","flag :"+flag +" year :" +year +" month : "+ month +" day: "+ day);
		initListView();
		initReceiver();
		//TODO: 本地数据库查询
//		List<CalendarEvent2> notificationDB = mDaoUtils.queryAll();
//		notificationDB.size();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void initListView() {
		GregorianCalendar calendar = new GregorianCalendar();
		if (flag == 0) {//查某天
			tv_date.setText(year + "-" + month + "-" + day);
			calendar = new GregorianCalendar(year, month - 1, day);
		} else {//查全部
			calendar = null;
			tv_date.setText("全部日程");
		}
		getListViewData(1, calendar, 0, 0, 1, false);
		hideMultiChoice();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		calendarEvent = new CalendarEvent2();
		tv_date = (TextView) findViewById(R.id.tv_date);
		back = (LinearLayout) findViewById(R.id.back);
		today = (TextView) findViewById(R.id.today);
		list = (TextView) findViewById(R.id.list);
		create = (TextView) findViewById(R.id.create);
		listview = (ListView) findViewById(R.id.listview);
		no_data = (TextView) findViewById(R.id.no_data);
		back.setOnClickListener(clickListener);
		tv_date.setOnClickListener(clickListener);
		today.setOnClickListener(clickListener);
		list.setOnClickListener(clickListener);
		create.setOnClickListener(clickListener);
		rlMultiChoice = (RelativeLayout) findViewById(R.id.rl_multi_choice);
	}

	private void initReceiver() {
		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(EVENT_ADDED)) {
					CalendarEvent2 event = (CalendarEvent2) intent.getSerializableExtra(CALENDAR_EVENT);
					events.add(event);
					refreshEventListView();
				}
			}
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(EVENT_ADDED);
		registerReceiver(receiver, filter);
	}

	OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (R.id.back == v.getId()) {
				finish();
			} else if (R.id.tv_date == v.getId()) {

				try {
					String str1 = Md5Util.getMD5("123");
					String str2 = Md5Util.getMD5("123");
					Log.e("local Md5 : ", "str1 : "+str1 +" str2: "+str2);
					Toast.makeText(EventsListActivity.this,"对比结果：" + (str1 .equals(str2)), Toast.LENGTH_SHORT).show();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}else if (R.id.today == v.getId()) {
				initListView();
			} else if (R.id.list == v.getId()) {
				tv_date.setText("全部日程");
				getListViewData(1, null, 0, 0, 0, false);
				hideMultiChoice();
			} else if (R.id.create == v.getId()) {
				showPop(v);
			}
		}
	};

	/**
	 * @param flag     0为过去，1为将来
	 * @param calendar 现在的时间
	 * @param year     将来或过去多少年
	 * @param month    将来或过去多少月
	 * @param day      将来或过去多少天
	 */
	private void getListViewData(int flag, GregorianCalendar calendar, int year, int month, int day, boolean twoYears) {
		Log.e("local queryParam : ","flag : "+ flag +" calendar : "+ ((null == calendar)? "" : calendar.toString()) +" year: "+year +" month: "+month +" day: "+day +" twoYears: "+twoYears);
		if (calendar == null) {
			events = CalendarsResolver.getInstance().queryData(EventsListActivity.this);
		} else {
			if (twoYears) {
				events = CalendarsResolver.getInstance().queryTwoYearsData(EventsListActivity.this, calendar);
			} else {
				events = CalendarsResolver.getInstance().querySomeDaysData(EventsListActivity.this, flag, calendar, year, month, day);
			}
		}
		calendarEventSort(events);
		refreshListView();
	}

	private void refreshListView() {
		Log.e("local : events :",events.toString());
		if (events.size() > 0) {
			no_data.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
			adapter = new CalendarEventAdapter(EventsListActivity.this, events);
			listview.setAdapter(adapter);

			listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if (adapter.isMultiChoice()) {
						boolean choice = events.get(position).isChoose();
						events.get(position).setChoose(!choice);
						adapter.notifyDataSetChanged();

						// 设置选择数量
						if (choice) {
							setEventChoiceCount(eventChoiceCount - 1);
						} else {
							setEventChoiceCount(eventChoiceCount + 1);
						}

						if (eventChoiceCount == events.size()) {
							btnEventChoice.setText(R.string.choice_none);
						} else {
							btnEventChoice.setText(R.string.choice_all);
						}
					} else {
						updatePosition = position;
						calendarEvent.copy(events.get(position));
//						Intent intent = new Intent(EventsListActivity.this,
//								EditCalendarActivity.class);
						Intent intent = new Intent(EventsListActivity.this,
								EditCalendarActivity2.class);
						intent.putExtra("type", 1);
						intent.putExtra("objectEntity", calendarEvent);
						startActivityForResult(intent, 88);
					}
				}
			});

			listview.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					showMultiChoice();
					return true;
				}
			});
			initMultiChoice();
		} else {
			listview.setVisibility(View.GONE);
			no_data.setVisibility(View.VISIBLE);
		}
	}

	private void initMultiChoice() {
		tvEventChoiceCount = (TextView) findViewById(R.id.tv_multi_choice_count);
		setEventChoiceCount(0);

		Button cancel = (Button) findViewById(R.id.btn_multi_choice_cancel);
		btnEventChoice = (Button) findViewById(R.id.btn_multi_choice);
		btnDeleteEvent = (Button) findViewById(R.id.btn_delete_event);

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideMultiChoice();
			}
		});

		btnEventChoice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = btnEventChoice.getText().toString();
				if (text.equals("全选")) {
					btnEventChoice.setText(R.string.choice_none);

					setAllEventsChoice(true);
					setEventChoiceCount(events.size());
					adapter.notifyDataSetChanged();
				} else if (text.equals("反选")) {
					btnEventChoice.setText(R.string.choice_all);

					setAllEventsChoice(false);
					setEventChoiceCount(0);
					adapter.notifyDataSetChanged();
				}
			}
		});

		btnDeleteEvent.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delCalendar();
			}
		});
	}

	/**
	 * 设置日程事件选择数量
	 *
	 * @param count
	 */
	private void setEventChoiceCount(int count) {
		eventChoiceCount = count >= 0 ? count : 0;
		tvEventChoiceCount.setText(String.valueOf(eventChoiceCount));
	}

	/**
	 * 显示多选
	 */
	private void showMultiChoice() {
		adapter.setMultiChoice(true);
		adapter.notifyDataSetChanged();
		rlMultiChoice.setVisibility(View.VISIBLE);
		btnDeleteEvent.setVisibility(View.VISIBLE);

		setEventChoiceCount(0);
	}

	/**
	 * 隐藏多选
	 */
	private void hideMultiChoice() {
		if (adapter != null) {
			adapter.setMultiChoice(false);
			adapter.notifyDataSetChanged();
		}
		if (rlMultiChoice != null)
			rlMultiChoice.setVisibility(View.GONE);
		if (btnDeleteEvent != null)
			btnDeleteEvent.setVisibility(View.GONE);
		if (btnEventChoice != null)
			btnEventChoice.setText(R.string.choice_all);
		setAllEventsChoice(false);
	}

	/**
	 * 设置所有日程事件的选择状态
	 *
	 * @param choice
	 */
	private void setAllEventsChoice(boolean choice) {
		for (CalendarEvent2 event : events) {
			event.setChoose(choice);
		}
	}

	/**
	 * 刷新日程事件列表
	 */
	private void refreshEventListView() {
		calendarEventSort(events);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 按日程事件的时间升序排序
	 *
	 * @param events
	 */
	private void calendarEventSort(List<CalendarEvent2> events) {
		Comparator<CalendarEvent2> comparator = new Comparator<CalendarEvent2>() {
			@Override
			public int compare(CalendarEvent2 lhs, CalendarEvent2 rhs) {
				if (lhs.getBeginTime() > rhs.getBeginTime())
					return 1;
				else if (lhs.getBeginTime() < rhs.getBeginTime())
					return -1;
				else
					return 0;
			}
		};
		Collections.sort(events, comparator);
	}

	private void showPop(View v) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		View view = LayoutInflater.from(EventsListActivity.this).inflate(R.layout.popupwindow_layout, null);
		pop = new PopupWindow(view, (int) (screenWidth * 0.4),
				LayoutParams.WRAP_CONTENT, false);
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (pop.isShowing()) {
					pop.dismiss();
				}
				return false;
			}
		});
		View btn_add_event = view.findViewById(R.id.btn_add_event);//添加事件
		View btn_query_gone7 = view.findViewById(R.id.btn_query_gone7);//之前7天事件
		View btn_query_futrue7 = view.findViewById(R.id.btn_query_futrue7);//未来7天事件
		View btn_query_gone30 = view.findViewById(R.id.btn_query_gone30);//之前30天事件
		View btn_query_futrue30 = view.findViewById(R.id.btn_query_futrue30);//未来30天事件
		View btn_query_two_years = view.findViewById(R.id.btn_query_two_years);//前后两年内事件
		//添加事件
		btn_add_event.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent(EventsListActivity.this,
//						EditCalendarActivity.class);
				Intent intent = new Intent(EventsListActivity.this,
						EditCalendarActivity2.class);
				intent.putExtra("type", 0);
				startActivityForResult(intent, 888);
				pop.dismiss();
			}
		});
		//之前7天事件
		btn_query_gone7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GregorianCalendar calendar = getOneDayStartTime();
				tv_date.setText("之前7天事件");
				getListViewData(0, calendar, 0, 0, 7, false);
				hideMultiChoice();
				pop.dismiss();
			}
		});
		//未来7天事件
		btn_query_futrue7.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GregorianCalendar calendar = getOneDayStartTime();
				tv_date.setText("未来7天事件");
				getListViewData(1, calendar, 0, 0, 7, false);
				hideMultiChoice();
				pop.dismiss();
			}
		});
		//之前30天事件
		btn_query_gone30.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GregorianCalendar calendar = getOneDayStartTime();
				tv_date.setText("之前30天事件");
				getListViewData(0, calendar, 0, 0, 30, false);
				hideMultiChoice();
				pop.dismiss();
			}
		});
		//未来30天事件
		btn_query_futrue30.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GregorianCalendar calendar = getOneDayStartTime();
				tv_date.setText("未来7天事件");
				getListViewData(1, calendar, 0, 0, 30, false);
				hideMultiChoice();
				pop.dismiss();
			}
		});
		//前后两年内事件
		btn_query_two_years.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				GregorianCalendar calendar = getOneDayStartTime();
				tv_date.setText("前后两年内事件");
				getListViewData(1, calendar, 0, 0, 0, true);
				hideMultiChoice();
				pop.dismiss();
			}
		});
		pop.showAsDropDown(v);
	}

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 888) {//添加事件成功回调函数
				if (events == null) {
					events = new ArrayList<CalendarEvent2>();
				}
				if (data.getSerializableExtra("objectEntity") != null) {
					events.add((CalendarEvent2) data.getSerializableExtra("objectEntity"));
				}
				refreshListView();
			} else if (requestCode == 88) {//更新成功回调操作
				if (data.getSerializableExtra("objectEntity") != null) {
					events.get(updatePosition).copy((CalendarEvent2) data.getSerializableExtra("objectEntity"));
				}
				updatePosition = 0;
				adapter.notifyDataSetChanged();
			}
			hideMultiChoice();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (adapter.isMultiChoice()) {
				hideMultiChoice();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void delCalendar() {
		final List<CalendarEvent2> eventsTemp = new ArrayList<CalendarEvent2>();
		for (CalendarEvent2 event : events) {
			if (event.isChoose())
				eventsTemp.add(event);
		}

		if (eventsTemp.size() > 0) {
			Builder builder = new Builder(EventsListActivity.this);
			builder.setTitle("提醒");
			builder.setMessage("是否确认删除" + (eventsTemp.size()) + "个日程");
			builder.setPositiveButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
											int which) {
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
											int which) {
							boolean result = CalendarsResolver.getInstance()
									.delDatas(EventsListActivity.this, eventsTemp);
							if (result) {
								events.removeAll(eventsTemp);
								refreshEventListView();

								hideMultiChoice();
								if (events.size() <= 0) {
									listview.setVisibility(View.GONE);
									no_data.setVisibility(View.VISIBLE);
								}

								//TODO: 删除本地数据EventId
								long eventId = eventsTemp.get(0).getEventId();
								if(eventId >0){
									List<CalendarEvent2>  eventList = mDaoUtils.queryByEventId(eventId);
									if (null != eventList && eventList.size() > 0) {
										boolean b = mDaoUtils.deleteForEventId(eventId);
										if(b)Log.e("local delete ","删除数据库记录成功！");
									}
								}

								Utils.toast(EventsListActivity.this, "删除成功！");
							}
							dialog.dismiss();
						}
					});
			builder.create().show();
		} else {
			Utils.toast(EventsListActivity.this, "请选择要删除的项");
		}
	}
}
