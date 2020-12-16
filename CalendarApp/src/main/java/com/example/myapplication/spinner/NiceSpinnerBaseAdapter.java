package com.example.myapplication.spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.myapplication.calendar.R;


/**
 * @author angelo.marchesin
 */

@SuppressLint("NewApi")
@SuppressWarnings("unused")
public abstract class NiceSpinnerBaseAdapter<T> extends BaseAdapter {
    protected Context mContext;
    protected int mSelectedIndex;
    protected int mTextColor;
    protected int mBackgroundSelector;

    public NiceSpinnerBaseAdapter(Context context, int textColor, int backgroundSelector) {
        mContext = context;
        mTextColor = textColor;
        mBackgroundSelector = backgroundSelector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        ImageView imgChecked;
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.spinner_list_item, null);
            textView = (TextView) convertView.findViewById(R.id.tv_tinted_spinner);
            imgChecked = (ImageView)convertView.findViewById(R.id.img_remindtime_checkd);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            	convertView.setBackground(ContextCompat.getDrawable(mContext, mBackgroundSelector));
            }

            holder = new ViewHolder();
            holder.textView = textView;
            holder.imgChecked = imgChecked;
            convertView.setTag(holder);
        } else {
        	holder = (ViewHolder)convertView.getTag();
            textView = holder.textView;
            imgChecked = holder.imgChecked;
        }

        if(position == mSelectedIndex) {
        	imgChecked.setVisibility(View.VISIBLE);
        }
        else {
        	imgChecked.setVisibility(View.INVISIBLE);
        }
        textView.setText(getItem(position).toString());
        textView.setTextColor(mTextColor);

        return convertView;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public void notifyItemSelected(int index) {
        mSelectedIndex = index;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public abstract T getItem(int position);

    @Override
    public abstract int getCount();

    public abstract T getItemInDataset(int position);

    protected static class ViewHolder {
        public TextView textView;
        public ImageView imgChecked;
    }
}
