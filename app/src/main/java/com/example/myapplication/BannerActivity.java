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

import java.util.List;

public class BannerActivity extends AppCompatActivity {

    private TTAdNative mTTAdNative;
    private FrameLayout mBannerContainer,banner_container2;
    private Context mContext;
    private TTAdDislike mTTAdDislike;
    private Button mButtonDownload;
    private Button mButtonLandingPage;


    private TTNativeExpressAd mTTAd;
    private boolean mHasShowDownloadActive = false;
    private long startTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner);
       // TTAdManager接口中的方法，context可以是Activity或Application
        mContext = this/*.getApplicationContext()*/;
        mBannerContainer = (FrameLayout) findViewById(R.id.banner_container);
        banner_container2 = (FrameLayout) findViewById(R.id.banner_container2);
        mButtonDownload = (Button) findViewById(R.id.btn_banner_download);
        mButtonLandingPage = (Button) findViewById(R.id.btn_banner_landingpage);
        mButtonDownload.setOnClickListener(mClickListener);
        mButtonLandingPage.setOnClickListener(mClickListener);

        //step2:创建TTAdNative对象，createAdNative(Context context) banner广告context需要传入Activity对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        //step3:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);

    }

    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_banner_download) {
                loadExpressAd("940898292");
//                loadExpressAd("936667044");
//                loadBannerAd("936667044");
            } else if (v.getId() == R.id.btn_banner_landingpage) {
                loadExpressAd("940898292");
//                loadExpressAd("936667044");
//                loadBannerAd("936667044");
            }

        }
    };

    private void loadExpressAd2(String codeId) {
        banner_container2.removeAllViews();
        float expressViewWidth = 700;
        float expressViewHeight = 100;
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //期望模板广告view的size,单位dp
//                .setImageAcceptedSize(640,320 )//这个参数设置即可，不影响模板广告的size
                .setImageAcceptedSize(600,150 )//这个参数设置即可，不影响模板广告的size
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Toast.makeText(BannerActivity.this, "load error : " + code + ", " + message,Toast.LENGTH_SHORT).show();
                banner_container2.removeAllViews();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
//                mTTAd.setSlideIntervalTime(30*1000);
                bindAdListener2(mTTAd);
                startTime = System.currentTimeMillis();
                mTTAd.render();
            }
        });
    }
    private void bindAdListener2(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
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
                Toast.makeText(mContext, " 渲染成功:",Toast.LENGTH_SHORT).show();
                banner_container2.removeAllViews();
                banner_container2.addView(view);
            }
        });
        //dislike设置
        bindDislike(ad, false);
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
    private void loadExpressAd(String codeId) {
        mBannerContainer.removeAllViews();
        float expressViewWidth = 700;
        float expressViewHeight = 100;
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(codeId) //广告位id
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(expressViewWidth,expressViewHeight) //期望模板广告view的size,单位dp
//                .setImageAcceptedSize(640,320 )//这个参数设置即可，不影响模板广告的size
                .setImageAcceptedSize(600,150 )//这个参数设置即可，不影响模板广告的size
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadBannerExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Toast.makeText(BannerActivity.this, "load error : " + code + ", " + message,Toast.LENGTH_SHORT).show();
                mBannerContainer.removeAllViews();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0){
                    return;
                }
                mTTAd = ads.get(0);
//                mTTAd.setSlideIntervalTime(30*1000);
                bindAdListener(mTTAd);
                startTime = System.currentTimeMillis();
                mTTAd.render();
            }
        });
    }

    private void bindAdListener(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
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
                Toast.makeText(mContext, " 渲染成功:",Toast.LENGTH_SHORT).show();
                mBannerContainer.removeAllViews();
                mBannerContainer.addView(view);
            }
        });
        //dislike设置
        bindDislike(ad, false);
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

    /**
     * 设置广告的不喜欢, 注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
     * @param ad
     * @param customStyle 是否自定义样式，true:样式自定义
     */
    private void bindDislike(TTNativeExpressAd ad, boolean customStyle) {
        if (customStyle) {
            //使用自定义样式
            List<FilterWord> words = ad.getFilterWords();
            if (words == null || words.isEmpty()) {
                return;
            }

            final DislikeDialog dislikeDialog = new DislikeDialog(this, words);
            dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                @Override
                public void onItemClick(FilterWord filterWord) {
                    //屏蔽广告
                    Toast.makeText(mContext, "点击 " + filterWord.getName(),Toast.LENGTH_SHORT).show();
                    //用户选择不喜欢原因后，移除广告展示
                    mBannerContainer.removeAllViews();
                }
            });
            ad.setDislikeDialog(dislikeDialog);
            return;
        }
        //使用默认模板中默认dislike弹出样式
        ad.setDislikeCallback(BannerActivity.this, new TTAdDislike.DislikeInteractionCallback() {
            @Override
            public void onSelected(int position, String value) {
                Toast.makeText(mContext, "点击 " + value,Toast.LENGTH_SHORT).show();
                //用户选择不喜欢原因后，移除广告展示
                mBannerContainer.removeAllViews();
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "点击取消 " ,Toast.LENGTH_SHORT).show();
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


//    private void loadBannerAd(String codeId) {
//        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
//        AdSlot adSlot = new AdSlot.Builder()
//                .setCodeId(codeId) //广告位id
//                .setSupportDeepLink(true)
////                .setAdCount(3) //请求广告数量为1到3条
////                .setImageAcceptedSize(600, 257)
//                .setImageAcceptedSize(600, 150)
//                .build();
//        //step5:请求广告，对请求回调的广告作渲染处理
//        mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {
//
//            @Override
//            public void onError(int code, String message) {
//                Toast.makeText(MainActivity.this, "load error : " + code + ", " + message,Toast.LENGTH_SHORT).show();;
//                mBannerContainer.removeAllViews();
//            }
//
//            @Override
//            public void onBannerAdLoad(final TTBannerAd ad) {
//                if (ad == null) {
//                    return;
//                }
//                View bannerView = ad.getBannerView();
//                if (bannerView == null) {
//                    return;
//                }
//                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
//                ad.setSlideIntervalTime(30 * 1000);
//                mBannerContainer.removeAllViews();
//                mBannerContainer.addView(bannerView);
//                //设置广告互动监听回调
//                ad.setBannerInteractionListener(new TTBannerAd.AdInteractionListener() {
//                    @Override
//                    public void onAdClicked(View view, int type) {
//                        Toast.makeText(MainActivity.this, "广告被点击",Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onAdShow(View view, int type) {
//                        Toast.makeText(MainActivity.this, "广告被点击",Toast.LENGTH_SHORT).show();
//                        Toast.makeText(mContext, "广告展示",Toast.LENGTH_SHORT).show();
//                    }
//                });
//                //（可选）设置下载类广告的下载监听
//                bindDownloadListener(ad);
//                //在banner中显示网盟提供的dislike icon，有助于广告投放精准度提升
//                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
//                    @Override
//                    public void onSelected(int position, String value) {
//                        Toast.makeText(mContext, "点击 " + value,Toast.LENGTH_SHORT).show();
//                        //用户选择不喜欢原因后，移除广告展示
//                        mBannerContainer.removeAllViews();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Toast.makeText(mContext, "点击取消 ",Toast.LENGTH_SHORT).show();;
//                    }
//                });
//
//                //获取网盟dislike dialog，您可以在您应用中本身自定义的dislike icon 按钮中设置 mTTAdDislike.showDislikeDialog();
//                /*mTTAdDislike = ad.getDislikeDialog(new TTAdDislike.DislikeInteractionCallback() {
//                        @Override
//                        public void onSelected(int position, String value) {
//                            TToast.show(mContext, "点击 " + value);
//                        }
//
//                        @Override
//                        public void onCancel() {
//                            TToast.show(mContext, "点击取消 ");
//                        }
//                    });
//                if (mTTAdDislike != null) {
//                    XXX.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            mTTAdDislike.showDislikeDialog();
//                        }
//                    });
//                } */
//
//            }
//        });
//    }
//
//    private boolean mHasShowDownloadActive = false;
//
//    private void bindDownloadListener(TTBannerAd ad) {
//        ad.setDownloadListener(new TTAppDownloadListener() {
//            @Override
//            public void onIdle() {
//                Toast.makeText(MainActivity.this, "点击图片开始下载", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
//                if (!mHasShowDownloadActive) {
//                    mHasShowDownloadActive = true;
//                    Toast.makeText(MainActivity.this, "下载中，点击图片暂停", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                Toast.makeText(MainActivity.this, "下载暂停，点击图片继续", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                Toast.makeText(MainActivity.this, "下载失败，点击图片重新下载", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onInstalled(String fileName, String appName) {
//                Toast.makeText(MainActivity.this, "安装完成，点击图片打开", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                Toast.makeText(MainActivity.this, "点击图片安装", Toast.LENGTH_LONG).show();
//            }
//        });
//    }


}
