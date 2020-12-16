package com.example.myapplication.librarycalendar.utils;

import java.io.Serializable;

/**
 * 日程事件对象类
 * @author 朱城委
 *
 * @date 2017年2月10日 下午3:03:16
 */
public class CalendarEvent implements Serializable{
	private static final long serialVersionUID = 1L;

	private long eventId;
	
	private String title;
	private String description;
	
	/** 开始时间 */
	private long beginTime;
	
	/** 结束时间 */
	private long endTime;
	
	/** 提醒时间(分钟) */
	private int remind;
	
	/** 标示多选 */
	private boolean isChoose;
	
	/** 事件重复 */
	private String repeat;
	
	/** 时间地点 */
	private String address;
	
	/** 账户ID */
	private long calendarId;
	
	public long getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(long calendarId) {
		this.calendarId = calendarId;
	}

	public CalendarEvent() {
		this("", "");
	}
	
	public CalendarEvent(String title) {
		this(title, "");
	}

	public CalendarEvent(String title, String description) {
		this.title = title;
		this.description = description;
		setBeginTime(System.currentTimeMillis() + 1000 * 60);
		isChoose = false;
	}
	
	public CalendarEvent(CalendarEvent calendarEvent) {
		copy(calendarEvent);
	}
	
	/**
	 * 把{@code calendarEvent}的值赋给自己。
	 * @param calendarEvent
	 */
	public void copy(CalendarEvent calendarEvent) {
		eventId = calendarEvent.eventId;
		title = calendarEvent.title;
		description = calendarEvent.description;
		beginTime = calendarEvent.beginTime;
		endTime = calendarEvent.endTime;
		remind = calendarEvent.remind;
		isChoose = false;
		repeat = calendarEvent.repeat;
	}
	
	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public String getTitle() {
		return title == null ? "" : title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description == null ? "" : description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public long getBeginTime() {
		return beginTime;
	}

	/**
	 * 设置日程开始时间（单位：ms）<br />
	 * 如果日程结束时间早于开始时间，设置结束时间为开始时间加1小时。
	 * @param beginTime
	 */
	public void setBeginTime(long beginTime) {
		this.beginTime = beginTime;
		
//		if(beginTime < System.currentTimeMillis()) {
//			this.beginTime = System.currentTimeMillis() + 1000 * 60 * 60;
//		}
		
//		if(endTime < beginTime) {
//			endTime = beginTime + 1000 * 60 * 60;
//		}
	}

	public long getEndTime() {
		return endTime;
	}

	/**
	 * 设置日程结束时间(单位：ms)
	 * @param endTime 如果结束时间早于开始时间，则开始时间加1小时。
	 */
	public void setEndTime(long endTime) {
//		if(endTime < beginTime) {
//			this.endTime = beginTime + 1000 * 60 * 60;
//		}
//		else {
			this.endTime = endTime;
//		}
	}

	public int getRemind() {
		return remind;
	}

	/**
	 * 设置提醒时间（分钟）
	 * @param remind 如果参数小于0，则设置提醒事件为0。
	 */
	public void setRemind(int remind) {
		if(remind >= 0)
			this.remind = remind;
		else
			this.remind = 0;
	}

	public boolean isChoose() {
		return isChoose;
	}

	public void setChoose(boolean isChoose) {
		this.isChoose = isChoose;
	}
	
	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "{" +
				"title: " + title +
				", description: " + description + 
				", beginTime: " + beginTime +
				", endTime: " + endTime + 
				", repeat: " + repeat + 
				", remind: " + remind +
				", address: " + address +
				"}";
	}
}
