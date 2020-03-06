package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;

import java.util.List;

/**
 * 插屏广告
 */
public class InScreenActivity extends AppCompatActivity {

    private TTAdNative mTTAdNative;
    private FrameLayout mBannerContainer;
    private Context mContext;
    private TTAdDislike mTTAdDislike;
    private Button mButtonDownload;


    private TTNativeExpressAd mTTAd;
    private boolean mHasShowDownloadActive = false;
    private long startTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_in_screen);
       // TTAdManager接口中的方法，context可以是Activity或Application
        mContext = this/*.getApplicationContext()*/;
        mBannerContainer = (FrameLayout) findViewById(R.id.banner_container);
        mButtonDownload = (Button) findViewById(R.id.btn_banner_download);
        mButtonDownload.setOnClickListener(mClickListener);

        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);

    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_banner_download) {
                loadExpressAd("936667066");
//                loadBannerAd("936667044");
            }

        }
    };

    private void loadExpressAd(String codeId) {
        float expressViewWidth = 350;
        float expressViewHeight = 350;
//        try{
//            expressViewWidth = Float.parseFloat(mEtWidth.getText().toString());
//            expressViewHeight = Float.parseFloat(mEtHeight.getText().toString());
//        }catch (Exception e){
//            expressViewHeight = 0; //高度设置为0,则高度会自适应
//        }
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //期望模板广告view的size,单位dp
                .setImageAcceptedSize(640,320 )//这个参数设置即可，不影响模板广告的size
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadInteractionExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Toast.makeText(InScreenActivity.this, "load error : " + code + ", " + message,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                mTTAd.render();
            }
        });
    }

    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.AdInteractionListener() {

            @Override
            public void onAdDismiss() {
                Toast.makeText(mContext, "广告关闭",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked(View view, int type) {
                Toast.makeText(mContext, "广告被点击",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdShow(View view, int type) {
                Toast.makeText(mContext, "广告展示",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView","render fail:"+(System.currentTimeMillis() - startTime));
                Toast.makeText(mContext, msg+" code:"+code,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView","render suc:"+(System.currentTimeMillis() - startTime));
                //返回view的宽高 单位 dp
                Toast.makeText(mContext, "渲染成功",Toast.LENGTH_SHORT).show();
                mTTAd.showInteractionExpressAd(InScreenActivity.this);

            }
        });
        if (ad.getInteractionType() != TTAdConstant.INTERACTION_TYPE_DOWNLOAD){
            return;
        }
        ad.setDownloadListener(new TTAppDownloadListener() {
            @Override
            public void onIdle() {
                Toast.makeText(mContext, " 点击开始下载:",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                if (!mHasShowDownloadActive) {
                    mHasShowDownloadActive = true;
                    Toast.makeText(mContext, " 下载中，点击暂停:",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                Toast.makeText(mContext, " 下载暂停，点击继续:",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                Toast.makeText(mContext, " 下载失败，点击重新下载:",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInstalled(String fileName, String appName) {
                Toast.makeText(mContext, " 安装完成，点击图片打开:",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                Toast.makeText(mContext, " 点击安装:",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTTAd != null) {
            mTTAd.destroy();
        }
    }
}
