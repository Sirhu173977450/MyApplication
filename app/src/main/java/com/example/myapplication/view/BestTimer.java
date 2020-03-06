package com.example.myapplication.view;

import android.os.Handler;

import androidx.annotation.Nullable;


/**
 * 定时器类
 *
 * @author pengjianbo <pengjianbosoft@gmail.com>
 * $Id$
 */
public class BestTimer implements Runnable {

    @Nullable
    private Handler mTickRequestHandler = new Handler();

    private BestTimerListener mBestTimerListener;

    private boolean bCancel = true;

    private long timeInterval;

    @Override
    public void run() {
        if (mBestTimerListener != null) {
            mBestTimerListener.timerUp();
        }

        if (!bCancel) {
            if (mTickRequestHandler != null) {
                mTickRequestHandler.postDelayed(this, timeInterval);
            }
        }
    }

    /**
     * 开启定时器
     *
     * @param interval
     */
    public void startTimer(long interval) {
        bCancel = false;
        timeInterval = interval;
        mTickRequestHandler = new Handler();
        mTickRequestHandler.postDelayed(this, timeInterval);
    }

    /**
     * 取消定时器
     */
    public void cancelTimer() {
        bCancel = true;
        if (mTickRequestHandler != null) {
            mTickRequestHandler.removeCallbacks(this);
            mTickRequestHandler = null;
        }
    }

    public boolean isbCancel() {
        return bCancel;
    }

    /**
     * 设置定时器监听
     *
     * @param l
     */
    public void setTimerListener(BestTimerListener l) {

        mBestTimerListener = l;
    }

    public interface BestTimerListener {

        void timerUp();
    }
}
