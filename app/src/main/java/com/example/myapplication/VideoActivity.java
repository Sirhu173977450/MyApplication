package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;

import java.util.List;

public class VideoActivity extends AppCompatActivity {

    private TTAdNative mTTAdNative;
    private Context mContext;
    private TTAdDislike mTTAdDislike;
    private Button mButtonDownload;
    private Button mButtonLandingPage;
    private Button mBtn_load;


    private TTNativeExpressAd mTTAd;
    private boolean mHasShowDownloadActive = false;
    private long startTime = 0;


    private TTRewardVideoAd mttRewardVideoAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
       // TTAdManager接口中的方法，context可以是Activity或Application
        mContext = this/*.getApplicationContext()*/;
        mButtonDownload = (Button) findViewById(R.id.btn_banner_download);
        mButtonLandingPage = (Button) findViewById(R.id.btn_banner_landingpage);
        mBtn_load = (Button) findViewById(R.id.btn_load);
        mButtonDownload.setOnClickListener(mClickListener);
        mButtonLandingPage.setOnClickListener(mClickListener);
        mBtn_load.setOnClickListener(mClickListener);

        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);

    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_banner_download) {
                loadAd("936667740",TTAdConstant.HORIZONTAL);
//                loadBannerAd("936667044");
            } else if (v.getId() == R.id.btn_banner_landingpage) {
                loadAd("936667740",TTAdConstant.VERTICAL);
//                loadBannerAd("936667044");
            }else if (v.getId() == R.id.btn_load) {
                if (mttRewardVideoAd != null) {
                    //step6:在获取到广告后展示
                    //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

                    //展示广告，并传入广告展示的场景
                    mttRewardVideoAd.showRewardVideoAd(VideoActivity.this,TTAdConstant.RitScenes.CUSTOMIZE_SCENES,"scenes_test");
                    mttRewardVideoAd = null;
                } else {
                    Toast.makeText(mContext, "rewardVideoAd 请先加载广告" ,Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        }
    };

    private void loadAd(String codeId, int orientation) {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(1080, 1920)
                .setRewardName("金币") //奖励的名称
                .setRewardAmount(3)  //奖励的数量
                .setUserID("user123")//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(orientation) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                Toast.makeText(mContext, "rewardVideoAd  : "+message ,Toast.LENGTH_SHORT).show();
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                Toast.makeText(mContext, "rewardVideoAd video cached" ,Toast.LENGTH_SHORT).show();
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                Toast.makeText(mContext, "rewardVideoAd loaded" ,Toast.LENGTH_SHORT).show();
                mttRewardVideoAd = ad;
                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {

                    @Override
                    public void onAdShow() {
                        Toast.makeText(mContext, "rewardVideoAd show" ,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdVideoBarClick() {
                        Toast.makeText(mContext, "rewardVideoAd bar click" ,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdClose() {
                        Toast.makeText(mContext, "rewardVideoAd close" ,Toast.LENGTH_SHORT).show();
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                        Toast.makeText(mContext, "rewardVideoAd complete" ,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVideoError() {
                        Toast.makeText(mContext, "rewardVideoAd error" ,Toast.LENGTH_SHORT).show();
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
                        Toast.makeText(mContext, "verify:" + rewardVerify + " amount:" + rewardAmount +
                                " name:" + rewardName,Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSkippedVideo() {
                        Toast.makeText(mContext, "rewardVideoAd has onSkippedVideo",Toast.LENGTH_SHORT).show();
                    }
                });
                mttRewardVideoAd.setDownloadListener(new TTAppDownloadListener() {
                    @Override
                    public void onIdle() {
                        mHasShowDownloadActive = false;
                    }

                    @Override
                    public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                        if (!mHasShowDownloadActive) {
                            mHasShowDownloadActive = true;
                            Toast.makeText(mContext, "下载中，点击下载区域暂停",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                        Toast.makeText(mContext, "下载暂停，点击下载区域继续",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                        Toast.makeText(mContext, "下载失败，点击下载区域重新下载",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                        Toast.makeText(mContext, "下载完成，点击下载区域重新下载",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onInstalled(String fileName, String appName) {
                        Toast.makeText(mContext, "安装完成，点击下载区域打开",Toast.LENGTH_SHORT).show();
                    }
                });
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
