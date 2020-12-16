package com.example.myapplication.librarycalendar.utils;

import static android.provider.CalendarContract.CALLER_IS_SYNCADAPTER;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;

import com.example.myapplication.calendar.R;

/**
 * 日程事件管理
 * 
 * @author 王仲璘
 * 
 */
public class CalendarsResolver {
	private static String uri;
	private static String eventUri;
	private static String reminderUri;
	
	private static String CALENDARS_NAME = "ehome";
	private static String CALENDARS_ACCOUNT_NAME = "ehome@gmail.com";
	private static String CALENDARS_DISPLAY_NAME = "e_home";
	
	private static CalendarsResolver INSTANCE = null;
	
	public static CalendarsResolver getInstance() {
		return getInstance(CALENDARS_ACCOUNT_NAME);
	}
	
	public static CalendarsResolver getInstance(String accountName) {
		if(INSTANCE == null) {
			synchronized(CalendarsResolver.class) {
				if(INSTANCE == null)
					INSTANCE = new CalendarsResolver(accountName);
			}
		}
		else {
			if(!CALENDARS_ACCOUNT_NAME.contains(accountName)) {
				synchronized(CalendarsResolver.class) {
					if(!CALENDARS_ACCOUNT_NAME.contains(accountName))
						INSTANCE = new CalendarsResolver(accountName);
				}
			}
		}
		
		return INSTANCE;
	}
	
	private CalendarsResolver(String accountName) {
		CALENDARS_NAME = "ehome";
		CALENDARS_ACCOUNT_NAME = "ehome@gmail.com";
		CALENDARS_DISPLAY_NAME = "e_home";
		
		if (accountName!=null&&!accountName.equals("")) {
			CALENDARS_NAME+=accountName;
			CALENDARS_ACCOUNT_NAME=accountName+CALENDARS_ACCOUNT_NAME;
			CALENDARS_DISPLAY_NAME+=accountName;
		}
		if (Build.VERSION.SDK_INT >= 8) {
			uri = "content://com.android.calendar/calendars";
			eventUri = "content://com.android.calendar/events";
			reminderUri = "content://com.android.calendar/reminders";
		}
		else {
			uri = "content://calendar/calendars";
			eventUri = "content://calendar/events";
			reminderUri = "content://calendar/reminders";
		}
	}
	
	/**
	 * 添加calendar账号
	 * @param context
	 * @return
	 */
	private long addCalendarAccount(Context context) {
		boolean has_permission = Utils.hasPermission(context, "android.permission.WRITE_CALENDAR");
		if (!has_permission) {
			Utils.toast(context, R.string.calendar_disable_hint);
			return -1;
		}
		TimeZone timeZone = TimeZone.getDefault();
		ContentValues value = new ContentValues();
		value.put(Calendars.NAME, CALENDARS_NAME);

		value.put(Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
		value.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
		value.put(Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
		value.put(Calendars.VISIBLE, 1);
		value.put(Calendars.CALENDAR_COLOR, Color.BLUE);
		value.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
		value.put(Calendars.SYNC_EVENTS, 1);
		value.put(Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
		value.put(Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
		value.put(Calendars.CAN_ORGANIZER_RESPOND, 0);

		Uri calendarUri = Uri.parse(uri);
		calendarUri = calendarUri.buildUpon().appendQueryParameter(CALLER_IS_SYNCADAPTER, "true")
				.appendQueryParameter(Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
				.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL).build();

		Uri result = context.getContentResolver().insert(calendarUri, value);
		long id = result == null ? -1 : ContentUris.parseId(result);
		return id;
	}
	
	/**
	 * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
	 * @param context
	 * @return
	 */
	private int checkAndAddCalendarAccount(Context context) {
		int oldId = checkCalendarAccount(context);
		if (oldId >= 0) {
			return oldId;
		} else {
			long addId = addCalendarAccount(context);
			if (addId >= 0) {
				return checkCalendarAccount(context);
			} else {
				return -1;
			}
		}
	}
	
	/**
	 * 检查是否有calendar账号
	 * 
	 * @param context
	 * @return 如果有，取第一个账号的id返回；如果没有，返回-1。
	 */
	private int checkCalendarAccount(Context context) {
		String selection = Calendars.ACCOUNT_NAME + " = ?";
		String[] selectionArgs = new String[] { CALENDARS_ACCOUNT_NAME };
		int calId=0;
		Cursor userCursor = context.getContentResolver()
				.query(Uri.parse(uri), null, selection, selectionArgs, null);
		try {
			if (userCursor == null)
				return  -1;
			int count = userCursor.getCount();
			if (count > 0) {
				userCursor.moveToFirst();
				calId = userCursor.getInt(userCursor.getColumnIndex(Calendars._ID));
			} else {
				calId = -1;
			}
		} finally {
			if (userCursor != null) {
				userCursor.close();
			}
		}
		return calId;
	}
	
	public int addData(Context context,CalendarEvent2 calendarEvent) {
		boolean has_permission = Utils.hasPermission(context, "android.permission.WRITE_CALENDAR");
		if (!has_permission) {
			Utils.toast(context, R.string.calendar_disable_hint);
			return -1;
		}
		
		long calId = checkAndAddCalendarAccount(context);// 你要添加日历事件的日历ID
		if (calId<=0) {
			return -1;
		}
		// 添加事件，如果添加失败，返回false
		ContentValues event = getCalendarEvent(calId, calendarEvent);
		Uri newEvent = context.getContentResolver().insert(Uri.parse(eventUri), event);
		if (newEvent == null)
			return -1;

		// 设置日程时间EventId
		calendarEvent.setEventId(ContentUris.parseId(newEvent));
		Log.e("local addEventID: ",calendarEvent.getEventId()+"");
		Log.e("local addEvent: ",calendarEvent.toString());
		// 设置事件提醒，如果添加失败，返回false
		ContentValues values = new ContentValues();
		values.put(Reminders.EVENT_ID, ContentUris.parseId(newEvent));
		values.put(Reminders.MINUTES, calendarEvent.getRemind());
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		Uri uri = context.getContentResolver().insert(Uri.parse(reminderUri), values);
		if (uri == null)
			return -1;
		
		return (int)ContentUris.parseId(newEvent);
	}
	
	private ContentValues getCalendarEvent(long calendarId, CalendarEvent2 event) {
		ContentValues values = new ContentValues();
		values.put(Events.TITLE, event.getTitle());
		values.put(Events.DESCRIPTION, event.getDescription());
		values.put(Events.CALENDAR_ID, calendarId);

		values.put(Events.DTSTART, event.getBeginTime());
		values.put(Events.DTEND, event.getEndTime());
		values.put(Events.HAS_ALARM, 1); 						// 设置有闹钟提醒
		values.put(Events.EVENT_TIMEZONE, "Asia/Shanghai");		// 这个是时区，必须有
//		if(event.getRepeat().length() != 0) {
		values.put(Events.RRULE, event.getRepeat());
//		}
		return values;
	}
	
	/**
	 * 删除日程事件<br />
	 * @param context
	 * @param eventId 需要删除的日程事件
	 * @return 删除成功，返回true；否则返回false。
	 */
	public boolean delOneData(Context context, long eventId) {
		boolean has_permission = Utils.hasPermission(context, "android.permission.WRITE_CALENDAR");
		if (!has_permission) {
			Utils.toast(context, R.string.calendar_disable_hint);
			return false;
		}
		long calId = checkAndAddCalendarAccount(context);// 你要添加日历事件的日历ID
		if (calId<=0) {
			return false;
		}
		CalendarEvent2 event=new CalendarEvent2();
		event.setEventId(eventId);
		ArrayList<CalendarEvent2> events = new ArrayList<CalendarEvent2>();
		events.add(event);
		return delDatas(context, events);
	}
	
	/**
	 * 删除日程事件
	 * @param context
	 * @param events 需要删除的日程事件List
	 * @return 删除成功，返回true；否则返回false。
	 */
	public boolean delDatas(Context context, List<CalendarEvent2> events) {
		boolean has_permission = Utils.hasPermission(context, "android.permission.WRITE_CALENDAR");
		if (!has_permission) {
			Utils.toast(context, R.string.calendar_disable_hint);
			return false;
		}
		long calId = checkAndAddCalendarAccount(context);// 你要添加日历事件的日历ID
		if (calId<=0) {
			return false;
		}
		StringBuilder selection = new StringBuilder(Events._ID + " in(");
		String[] selectionArgs = new String[]{calId+""};
		// 拼接查询条件
		for(int i = 0; i < events.size(); i++) {
			selection.append(String.valueOf(events.get(i).getEventId()));
			Log.e("delete:",String.valueOf(events.get(i).getEventId()));
			if(i < events.size() - 1)
				selection.append(",");
		}
		selection.append(") and "+Events.CALENDAR_ID+" = ?");
		Log.e("delete:",events.toString());
		// 删除Events表
		int rows = context.getContentResolver().delete(Uri.parse(eventUri), selection.toString(),selectionArgs);
		if(rows == -1)
			return false;
		
		return true;
	}
	
	public boolean updateData(Context context,CalendarEvent2 calendarEvent) {
		boolean has_permission = Utils.hasPermission(context, "android.permission.WRITE_CALENDAR");
		if (!has_permission) {
			Utils.toast(context, R.string.calendar_disable_hint);
			return false;
		}
		long calId = checkAndAddCalendarAccount(context);// 你要添加日历事件的日历ID
		if (calId<=0) {
			return false;
		}
		// 更新事件
		ContentValues values = getCalendarEvent(calId, calendarEvent);
		Uri updateUri = ContentUris.withAppendedId(Uri.parse(eventUri), calendarEvent.getEventId());
		int rows = context.getContentResolver().update(updateUri, values, null, null);
		if (rows <= 0)
			return false;
		
		// 更新提醒
		values = new ContentValues();
		values.put(Reminders.MINUTES, calendarEvent.getRemind());
		values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
		rows = context.getContentResolver().update(Uri.parse(reminderUri), values, 
				Reminders.EVENT_ID + " = ?", new String[]{ String.valueOf(calendarEvent.getEventId()) } );
		if (rows <= 0)
			return false;
		
		return true;
	}
	
	/**
	 * 查所有
	 * @param context
	 * @return list
	 */
	public List<CalendarEvent2> queryData(Context context) {
		List<CalendarEvent2> calendarEvents=new ArrayList<CalendarEvent2>();
		boolean has_permission = Utils.hasPermission(context, "android.permission.WRITE_CALENDAR");
		if (!has_permission) {
			Utils.toast(context, R.string.calendar_disable_hint);
			return calendarEvents;
		}
		int calId = checkAndAddCalendarAccount(context);
		String selection = Events.CALENDAR_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(calId) };
		fillData(context, selection, selectionArgs, calendarEvents);
		return calendarEvents;
	}
	
	/**
	 * 
	 * @param context 上下文
	 * @param flag 查之前时间或者之后时间，flag值为0查之前，值为1查将来
	 * @return list
	 */
	public List<CalendarEvent2> querySomeDaysData(Context context,int flag,GregorianCalendar calendar
			,int year,int month,int day) {
		List<CalendarEvent2> calendarEvents=new ArrayList<CalendarEvent2>();
		boolean has_permission = Utils.hasPermission(context, "android.permission.WRITE_CALENDAR");
		if (!has_permission) {
			Utils.toast(context, R.string.calendar_disable_hint);
			return calendarEvents;
		}
		int calId = checkAndAddCalendarAccount(context);
		String selection = Events.CALENDAR_ID + " = ?";
		if (flag==0) {
			selection+=eventSelectionWhereGone(calendar, year, month, day);
		}
		else {
			selection+=eventSelectionWhereFuture(calendar, year, month, day);
		}
		String[] selectionArgs = new String[] { String.valueOf(calId) };
		fillData(context, selection, selectionArgs, calendarEvents);
		return calendarEvents;
	}
	
	/**
	 * 前后两年内数据
	 * @param context
	 * @param calendar 当前时间
	 * @return list
	 */
	public List<CalendarEvent2> queryTwoYearsData(Context context,GregorianCalendar calendar) {
		List<CalendarEvent2> calendarEvents=new ArrayList<CalendarEvent2>();
		boolean has_permission = Utils.hasPermission(context, "android.permission.WRITE_CALENDAR");
		if (!has_permission) {
			Utils.toast(context, R.string.calendar_disable_hint);
			return calendarEvents;
		}
		int calId = checkAndAddCalendarAccount(context);
		String selection = Events.CALENDAR_ID + " = ?";
		selection+=eventSelectionWhereTwoYears(calendar);
		String[] selectionArgs = new String[] { String.valueOf(calId) };
		fillData(context, selection, selectionArgs, calendarEvents);
		return calendarEvents;
	}
	
	/**
	 * 指定月内开始时间查询，小黑点显示支持方法
	 * @param context
	 * @param calendar 当前时间
	 * @return list
	 */
	public List<GregorianCalendar> queryOneMonthStratTimeData(Context context,GregorianCalendar calendar) {
		List<GregorianCalendar> calendars=new ArrayList<GregorianCalendar>();
		boolean has_permission = Utils.hasPermission(context, "android.permission.WRITE_CALENDAR");
		if (!has_permission) {
			Utils.toast(context, R.string.calendar_disable_hint);
			return calendars;
		}
		int calId = checkAndAddCalendarAccount(context);
		String selection = Events.CALENDAR_ID + " = ?";
		selection+=eventSelectionWhereCurrentMonth(calendar);
		String[] selectionArgs = new String[] { String.valueOf(calId) };
		fillStartTime(context, selection, selectionArgs, calendars);
		return calendars;
	}

	private void fillData(Context context, String selection,
			String[] selectionArgs, List<CalendarEvent2> calendarEvents) {
		selection+=" and (deleted != 1)";
		Cursor cursor = context.getContentResolver().query(Uri.parse(eventUri),null, selection, selectionArgs, null);
		if (cursor.moveToFirst()) {
			do {
				CalendarEvent2 calendarEvent=new CalendarEvent2();
				String title = cursor.getString(cursor.getColumnIndex(Events.TITLE));
				String description = cursor.getString(cursor.getColumnIndex(Events.DESCRIPTION));
				String repeat = cursor.getString(cursor.getColumnIndex(Events.RRULE));
				long eventId = cursor.getLong(cursor.getColumnIndex(Events._ID));
				long beginTime = cursor.getLong(cursor.getColumnIndex(Events.DTSTART));
				long endTime = cursor.getLong(cursor.getColumnIndex(Events.DTEND));
				int remind = getRemindMunites(context, eventId);
				calendarEvent.setEventId(eventId);
				calendarEvent.setDescription(description);
				calendarEvent.setTitle(title);
				calendarEvent.setBeginTime(beginTime);
				calendarEvent.setEndTime(endTime);
				calendarEvent.setRemind(remind);
				calendarEvent.setRepeat(repeat);
				calendarEvent.setCalendarId(cursor.getLong(cursor.getColumnIndex(Events.CALENDAR_ID)));
				calendarEvents.add(calendarEvent);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}
	
	private void fillStartTime(Context context, String selection,
			String[] selectionArgs, List<GregorianCalendar> calendarEvents) {
		selection+=" and (deleted != 1)";
		Cursor cursor = context.getContentResolver().query(Uri.parse(eventUri),null, selection, selectionArgs, null);
		if (cursor.moveToFirst()) {
			do {
				long beginTime = cursor.getLong(cursor.getColumnIndex(Events.DTSTART));
				GregorianCalendar calendar=new GregorianCalendar();
				calendar.setTimeInMillis(beginTime);
				calendarEvents.add(calendar);
			} while (cursor.moveToNext());
		}
		cursor.close();
	}
	
	/**
	 * 根据Event_id获取日程事件的提醒时间
	 * 
	 * @param context
	 * @param eventId
	 * @return 返回日程事件提醒时间（分钟）
	 */
	private int getRemindMunites(Context context, long eventId) {
		int munite = 0;

		Uri queryUri = Uri.parse(reminderUri);
		String selection = Reminders.EVENT_ID + " = ?";
		String[] selectionArgs = new String[] { String.valueOf(eventId) };
		Cursor cursor = context.getContentResolver().query(queryUri, null, selection, selectionArgs, null);
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			munite = cursor.getInt(cursor.getColumnIndex(Reminders.MINUTES));
		}
		cursor.close();
		return munite;
	}
	/**
	 * 日程事件查询条件
	 * @param year 从当前时间起，多少年后
	 * @param month 从当前时间起，多少个月后
	 * @param day 从当前时间起，多少天后
	 * @return
	 */
	public String eventSelectionWhereFuture(GregorianCalendar calendar,int year, int month, int day) {
		String time1=String.valueOf(calendar.getTimeInMillis());
		String time2=String.valueOf(getFutureTime(calendar,year, month, day));
		String where = " AND " + Events.DTSTART + " >= " + time1 + 
		" AND " + Events.DTSTART + " <= " + time2;
		
		return where;
	}
	
	/**
	 * 日程事件查询条件
	 * @param year 从当前时间起，多少年前
	 * @param month 从当前时间起，多少个月前
	 * @param day 从当前时间起，多少天前
	 * @return
	 */
	public String eventSelectionWhereGone(GregorianCalendar calendar,int year, int month, int day) {
		String time1=String.valueOf(calendar.getTimeInMillis());
		String time2=String.valueOf(getGoneTime(calendar,year, month, day));
		String where = " AND " + Events.DTSTART + " >= " + time2 + 
		" AND " + Events.DTSTART + " <= " + time1;
		
		return where;
	}
	
	/**
	 * 日程事件查询条件两年内
	 * @return
	 */
	public String eventSelectionWhereTwoYears(GregorianCalendar calendar) {
		GregorianCalendar tempCalendar=new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
		String time1=String.valueOf(getFutureTime(calendar, 1, 0, 0));
		String time2=String.valueOf(getGoneTime(tempCalendar,1, 0, 0));
		String where = " AND " + Events.DTSTART + " >= " + time2 + 
		" AND " + Events.DTSTART + " <= " + time1;
		
		return where;
	}
	
	/**
	 * 获取将来的时间戳
	 * @param year 多少年后
	 * @param month 多少个月后
	 * @param day 多少天后
	 * @return
	 */
	private long getFutureTime(GregorianCalendar calendar,int year, int month, int day) {
		if(year >= 0)
			calendar.add(GregorianCalendar.YEAR, year);
		if(month >= 0)
			calendar.add(GregorianCalendar.MONTH, month);
		if(day >= 0)
			calendar.add(GregorianCalendar.DAY_OF_MONTH, day);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 获取过去的时间戳
	 * @param year 多少年前
	 * @param month 多少个月前
	 * @param day 多少天前
	 * @return
	 */
	private long getGoneTime(GregorianCalendar calendar,int year, int month, int day) {
		if(year >= 0)
			calendar.add(GregorianCalendar.YEAR, -year);
		if(month >= 0)
			calendar.add(GregorianCalendar.MONTH, -month);
		if(day >= 0)
			calendar.add(GregorianCalendar.DAY_OF_MONTH, -day);
		return calendar.getTimeInMillis();
	}
	
	/**
	 * 日程事件查询条件---本月的
	 * @param calendar 当前时间
	 * @return
	 */
	public String eventSelectionWhereCurrentMonth(GregorianCalendar calendar) {
		int min=calendar.getActualMinimum(Calendar.DATE);
		int max=calendar.getActualMaximum(Calendar.DATE);
		calendar.set(Calendar.DATE,min);
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		String time1=String.valueOf(calendar.getTimeInMillis());
		calendar.set(Calendar.DATE,max);
		calendar.set(Calendar.HOUR_OF_DAY,23);
		calendar.set(Calendar.MINUTE,59);
		calendar.set(Calendar.SECOND,59);
		String time2=String.valueOf(calendar.getTimeInMillis());
		String where = " AND " + Events.DTSTART + " >= " + time1 + 
		" AND " + Events.DTSTART + " <= " + time2;
		return where;
	}
}