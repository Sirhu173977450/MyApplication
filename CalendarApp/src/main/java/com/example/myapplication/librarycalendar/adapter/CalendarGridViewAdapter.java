package com.example.myapplication.librarycalendar.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication.calendar.R;
import com.example.myapplication.librarycalendar.data.CalendarDate;
import com.example.myapplication.librarycalendar.utils.CalendarsResolver;

/**
 * updated by kylin on 2017年4月17日14:49:02.
 */
public class CalendarGridViewAdapter extends BaseAdapter {

    private List<CalendarDate> mListData = new ArrayList<>();
	List<GregorianCalendar> hasCalsList=new ArrayList<>();

    public CalendarGridViewAdapter(List<CalendarDate> mListData,Context context,int year,int month) {
        this.mListData = mListData;
        GregorianCalendar calendar =new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        hasCalsList= CalendarsResolver.getInstance().queryOneMonthStratTimeData(context, calendar);
    }

    public List<CalendarDate> getListData() {
        return mListData;
    }


    public int getCount() {
        return mListData.size();
    }


    public Object getItem(int position) {
        return position;
    }



    public long getItemId(int position) {
        return position;
    }


    @SuppressWarnings("static-access")
	public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder viewHolder = null;
        CalendarDate calendarDate = mListData.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_calendar, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_day.setText(calendarDate.getSolar().solarDay+"");

        String str;

        if(!TextUtils.isEmpty(calendarDate.getSolar().solar24Term)){
            str =  calendarDate.getSolar().solar24Term;
        }else if(!TextUtils.isEmpty(calendarDate.getSolar().solarFestivalName)){
            str = calendarDate.getSolar().solarFestivalName;
        }else{
            str = calendarDate.getLunar().getChinaDayString(mListData.get(position).getLunar().lunarDay);
        }
        viewHolder.tv_lunar_day.setText(str);
        if(mListData.get(position).isInThisMonth()){
            viewHolder.tv_day.setTextColor(Color.parseColor("#000000"));
        }else{
            viewHolder.tv_day.setTextColor(Color.parseColor("#D7D7D7"));
            viewHolder.tv_lunar_day.setTextColor(Color.parseColor("#D7D7D7"));

        }
        for (int i = 0; i < hasCalsList.size(); i++) {
        	if (calendarDate.getSolar().solarYear==hasCalsList.get(i).get(Calendar.YEAR)
        			&&calendarDate.getSolar().solarMonth==(hasCalsList.get(i).get(Calendar.MONTH)+1)
					&&calendarDate.getSolar().solarDay==hasCalsList.get(i).get(Calendar.DAY_OF_MONTH)) {
            	viewHolder.hasCalendar.setVisibility(View.VISIBLE);
            	break;
    		}
		}
        return convertView;
    }


    public static class ViewHolder {
        private TextView tv_day;
        private TextView tv_lunar_day;
        private View hasCalendar;
        public ViewHolder(View itemView) {
            tv_day = (TextView) itemView.findViewById(R.id.tv_day);
            tv_lunar_day = (TextView) itemView.findViewById(R.id.tv_lunar_day);
            hasCalendar = (View) itemView.findViewById(R.id.hasCalendar);
        }

    }



}

