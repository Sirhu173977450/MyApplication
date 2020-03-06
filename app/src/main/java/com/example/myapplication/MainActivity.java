package com.example.myapplication;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.view.BestTimer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Notification.VISIBILITY_SECRET;
import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mBt_smallGame;
    private Button mInScreenBt;
    private Button mBannerBt;
    private Button mVideoBt;
    private Button mOpenScreenBt;
    private Button mBt_ShowNotification;
    private Button mInfoBt;
    private TasksCompletedView tcvProjectProcess;
    private AroundCircleView cv_view;
    private TextView tv_countDownTime;
    private int timeMins = 3;
    private List<aBean> arr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DensityAdapterUtil.setDensity(this, this.getApplication());
        setContentView(R.layout.activity_main);
        initView();


    }

    private void initView() {
        tv_countDownTime = (TextView) findViewById(R.id.tv_countDownTime);
        cv_view = (AroundCircleView) findViewById(R.id.cv_view);
        cv_view.setImageResource(R.mipmap.ic_launcher);
        cv_view.setProgress(10);
        tcvProjectProcess = (TasksCompletedView) findViewById(R.id.tcvProjectProcess);
        tcvProjectProcess.setProgress(80);
        mBt_smallGame = (Button) findViewById(R.id.bt_smallGame);
        mBt_smallGame.setOnClickListener(this);
        mInScreenBt = (Button) findViewById(R.id.bt_InScreen);
        mInScreenBt.setOnClickListener(this);
        mBannerBt = (Button) findViewById(R.id.bt_Banner);
        mBannerBt.setOnClickListener(this);
        mVideoBt = (Button) findViewById(R.id.bt_Video);
        mVideoBt.setOnClickListener(this);
        mOpenScreenBt = (Button) findViewById(R.id.bt_OpenScreen);
        mOpenScreenBt.setOnClickListener(this);
        mInfoBt = (Button) findViewById(R.id.bt_Info);
        mBt_ShowNotification = (Button) findViewById(R.id.bt_ShowNotification);
        mBt_ShowNotification.setOnClickListener(this);
        final BestTimer bestTimer = new BestTimer();
        bestTimer.setTimerListener(new BestTimer.BestTimerListener() {
            @Override
            public void timerUp() {
                timeMins -= 1;
                switch (timeMins) {
                    case 3:
                        tv_countDownTime.setText(timeMins + "");
                        break;
                    case 2:
                        tv_countDownTime.setText(timeMins + "");
                        break;
                    case 1:
                        tv_countDownTime.setText(timeMins + "");
                        break;
                }
                if (timeMins <= 0) {
                    tv_countDownTime.setText(timeMins + "");
                    bestTimer.cancelTimer();
                }
            }
        });
        bestTimer.startTimer(1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_smallGame:
                arr.set(1, new aBean("9"));
                Log.e("TAG", "size : " + arr.size());
                Log.e("TAG", "size : " + arr.toString());
//                startActivity(new Intent(this, SmallGameActivity.class));
                break;
            case R.id.bt_InScreen:

                long currentTime = System.currentTimeMillis() / 1000;
                long afterFiveMinute = 1579312800 - 300;
                Log.e("TAG_in: currentTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(new Date(currentTime * 1000)));
                Log.e("TAG_in: afterFiveMinute", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(new Date(afterFiveMinute * 1000)));


//                Log.e("TAG_in: ", new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss", Locale.CHINA)
//                        .format(new Date(System.currentTimeMillis())));
//                Log.e("TAG_in: current :", new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss", Locale.CHINA)
//                        .format(new Date(1579312800000L)));
                Date now = new Date();
                Date now_10 = new Date(1579312500 - 600000); //10分钟前的时间
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//可以方便地修改日期格式
                String nowTime_10 = dateFormat.format(now_10);
                Log.e("TAG", "time : " + nowTime_10);
//                startActivity(new Intent(this,InScreenActivity.class));
                break;
            case R.id.bt_Banner:
                startActivity(new Intent(this, BannerActivity.class));
                break;
            case R.id.bt_Video:
                startActivity(new Intent(this, VideoActivity.class));
                break;
            case R.id.bt_OpenScreen:
                startActivity(new Intent(this, OpenScreenActivity.class));
                break;
            case R.id.bt_Info:
                startActivity(new Intent(this, InfoScreenActivity.class));
                break;
            case R.id.bt_ShowNotification:
                Intent intent = new Intent(this, InfoScreenActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = null;
                NotificationCompat.Builder builder = null;
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {  //8.0
                    NotificationChannel channel = new NotificationChannel("static", "Primary Channel", NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                    builder = new NotificationCompat.Builder(this, "static");
                } else {
                    builder = new NotificationCompat.Builder(this);
                }

                builder.setContentTitle("通知栏标题内容")
                        .setContentText("通知消息正文")
                        .setWhen(100) //指定通知被创建时间
                        .setSmallIcon(R.mipmap.ic_launcher) //指定显示在状态栏的小图标
                        //指定通知的大Icon图标
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)  //设置是否点击就取消通知，消失通知
                        .build();

                notificationManager.notify(1, builder.build());

//                showNotice("0");
                break;
            default:
                break;
        }
    }

    /**
     * 显示到通知栏
     *
     * @param
     */
    private void showNotice(String channelId) {
        String title = "通知栏标题内容";
        String content = "通知消息正文";
//        Intent intent = new Intent(MessageNotificationBroadcastReceiver.Companion.getACTION());
//        intent.setComponent(new ComponentName(getPackageName(),
//                MessageNotificationBroadcastReceiver.class.getName()));
//        intent.putExtra("notificationId", 0);
//        intent.putExtra("data", messageBodyAttr);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(DataHelper.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
//        remoteViews.setImageViewResource(R.id.icon, R.mipmap.ic_launcher);
//        remoteViews.setTextViewText(R.id.title, TextUtils.isEmpty(title) ? "通知" : title);
//        remoteViews.setTextViewText(R.id.content, TextUtils.isEmpty(content) ? "" : content);
        Intent intent = new Intent(this, InfoScreenActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel =
                    new NotificationChannel(channelId, TextUtils.isEmpty(title) ? "通知" : title, IMPORTANCE_DEFAULT);
            notificationChannel.setVibrationPattern(new long[]{100, 100, 200});//设置震动模式
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationChannel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
            notificationChannel.enableLights(true);//闪光灯
            notificationChannel.setShowBadge(true);
            notificationChannel.enableVibration(true);//是否允许震动
            notificationManager.createNotificationChannel(notificationChannel);
            Notification notification = new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setSound(soundUri)
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
//                    .setCustomContentView(remoteViews)
                    .build();
            notificationManager.notify(1, notification);
        } else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
            builder.setDefaults(Notification.DEFAULT_ALL)
                    .setSmallIcon(R.mipmap.ic_launcher)//设置小图标
                    .setContentTitle("通知栏标题内容")
                    .setContentText("通知消息正文")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build().flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, builder.build());
        }
    }
}


