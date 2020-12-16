package com.example.myapplication.librarycalendar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.myapplication.calendar.R;
import com.example.myapplication.librarycalendar.utils.CalendarEvent;
import com.example.myapplication.librarycalendar.utils.CalendarEvent2;
import com.example.myapplication.librarycalendar.utils.DateUtils;

import java.util.List;

/**
 * @author 朱城委
 *
 * @date 2017年2月10日 下午3:10:44
 */
public class CalendarEventAdapter extends BaseAdapter {
	private List<CalendarEvent2> datas;
	private LayoutInflater inflater;

	/** 标示多选状态 */
	private boolean multiChoice;
	
	public CalendarEventAdapter(Context context, List<CalendarEvent2> datas) {
		this.datas = datas;
		inflater = LayoutInflater.from(context);
		
		multiChoice = false;
	}
	
	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder viewHolder;
		
		if(view == null) {
			view = inflater.inflate(R.layout.item_calendar_event, null);
			viewHolder = new ViewHolder();
			
			viewHolder.time			= (TextView)view.findViewById(R.id.tv_calendar_event_time);
			viewHolder.title 		= (TextView)view.findViewById(R.id.tv_calendar_event_title);
			viewHolder.description 	= (TextView)view.findViewById(R.id.tv_calendar_event_description);
			viewHolder.startTime 		= (TextView)view.findViewById(R.id.tv_event_start_time);
			viewHolder.endTime 		= (TextView)view.findViewById(R.id.tv_event_end_time);
			viewHolder.checkBox 	= (CheckBox)view.findViewById(R.id.cb_calendar_event);
			view.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder)view.getTag();
		}
		
		CalendarEvent2 event = datas.get(position);
		viewHolder.time.setText(String.valueOf(DateUtils.timeStamp2Date(event.getBeginTime(), "yyyy-MM-ddEEEE")));
		viewHolder.title.setText("标题："+event.getTitle());
		viewHolder.description.setText("描述："+event.getDescription());
		viewHolder.startTime.setText("开始："+String.valueOf(DateUtils.timeStamp2Date(event.getBeginTime(), "HH:mm")));
		viewHolder.endTime.setText("结束："+String.valueOf(DateUtils.timeStamp2Date(event.getEndTime(), "HH:mm")));
		if(multiChoice) {
			viewHolder.checkBox.setVisibility(View.VISIBLE);
			viewHolder.checkBox.setChecked(event.isChoose());
		}
		else {
			viewHolder.checkBox.setVisibility(View.GONE);
		}

		return view;
	}
	
	public boolean isMultiChoice() {
		return multiChoice;
	}

	public void setMultiChoice(boolean multiChoice) {
		this.multiChoice = multiChoice;
	}

	class ViewHolder {
		public TextView time;
		public TextView title;
		public TextView description;
		public TextView startTime;
		public TextView endTime;
		public CheckBox checkBox;
	}
}
