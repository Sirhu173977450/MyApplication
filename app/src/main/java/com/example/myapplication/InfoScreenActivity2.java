package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.FilterWord;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.example.myapplication.view.ILoadMoreListener;
import com.example.myapplication.view.LoadMoreListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class InfoScreenActivity2 extends AppCompatActivity {

    private static final String TAG = "InfoScreenActivity";
    private Context mContext;

    private static final int AD_POSITION = 3;
    private static final int LIST_ITEM_COUNT = 30;
    private LoadMoreListView mListView;
    private MyAdapter myAdapter;
    private List<TTNativeExpressAd> mData;
    private EditText mEtWidth;
    private EditText mEtHeight;
    private Button mButtonLoadAd;
    private TTAdNative mTTAdNative;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_screen);
        mContext = this/*.getApplicationContext()*/;
        mTTAdNative = TTAdManagerHolder.get().createAdNative(getApplicationContext());
        //申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);

        mListView = (LoadMoreListView) findViewById(R.id.my_list);
        mData = new ArrayList<>();
        myAdapter = new MyAdapter(mContext, mData);
        mListView.setAdapter(myAdapter);
        mListView.setLoadMoreListener(new ILoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadListAd();
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadListAd();
            }
        }, 500);
    }

    @SuppressWarnings("CanBeFinal")
    private static class MyAdapter extends BaseAdapter {

        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_GROUP_PIC_AD = 1;
        private static final int ITEM_VIEW_TYPE_SMALL_PIC_AD = 2;
        private static final int ITEM_VIEW_TYPE_LARGE_PIC_AD = 3;
        private static final int ITEM_VIEW_TYPE_VIDEO = 4;
        private static final int ITEM_VIEW_TYPE_VERTICAL_IMG = 5;//竖版图片

        private int mVideoCount = 0;


        private List<TTNativeExpressAd> mData;
        private Context mContext;

        private Map<AdViewHolder, TTAppDownloadListener> mTTAppDownloadListenerMap = new WeakHashMap<>();

        public MyAdapter(Context context, List<TTNativeExpressAd> data) {
            this.mContext = context;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size(); // for test
        }

        @Override
        public TTNativeExpressAd getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //信息流广告的样式，有大图、小图、组图和视频，通过ad.getImageMode()来判断
        @Override
        public int getItemViewType(int position) {
            TTNativeExpressAd ad = getItem(position);
            if (ad == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
                return ITEM_VIEW_TYPE_SMALL_PIC_AD;
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG) {
                return ITEM_VIEW_TYPE_LARGE_PIC_AD;
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {
                return ITEM_VIEW_TYPE_GROUP_PIC_AD;
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
                return ITEM_VIEW_TYPE_VIDEO;
            } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VERTICAL_IMG) {
                return ITEM_VIEW_TYPE_VERTICAL_IMG;
            } else {
                Toast.makeText(mContext,"图片展示样式错误",Toast.LENGTH_SHORT).show();
                return ITEM_VIEW_TYPE_NORMAL;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 5;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TTNativeExpressAd ad = getItem(position);
            switch (getItemViewType(position)) {
                case ITEM_VIEW_TYPE_SMALL_PIC_AD:
                case ITEM_VIEW_TYPE_LARGE_PIC_AD:
                case ITEM_VIEW_TYPE_GROUP_PIC_AD:
                case ITEM_VIEW_TYPE_VERTICAL_IMG:
                case ITEM_VIEW_TYPE_VIDEO:
                    return getVideoView(convertView, parent, ad);
                default:
                    return getNormalView(convertView, parent, position);
            }
        }

        //渲染视频广告，以视频广告为例，以下说明
        @SuppressWarnings("RedundantCast")
        private View getVideoView(View convertView, ViewGroup parent, @NonNull final TTNativeExpressAd ad) {
            final AdViewHolder adViewHolder;
            try {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_ad_native_express, parent, false);
                    adViewHolder = new AdViewHolder();
                    adViewHolder.videoView = (FrameLayout) convertView.findViewById(R.id.iv_listitem_express);
                    convertView.setTag(adViewHolder);
                } else {
                    adViewHolder = (AdViewHolder) convertView.getTag();
                }

                //绑定广告数据、设置交互回调
                bindData(convertView, adViewHolder, ad);
                if (adViewHolder.videoView != null) {
                    //获取视频播放view,该view SDK内部渲染，在媒体平台可配置视频是否自动播放等设置。
                    View video = ad.getExpressAdView();
                    if (video != null) {
                        if (video.getParent() == null) {
                            adViewHolder.videoView.removeAllViews();
                            adViewHolder.videoView.addView(video);
//                            ad.render();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }

        /**
         * 非广告list
         *
         * @param convertView
         * @param parent
         * @param position
         * @return
         */
        @SuppressWarnings("RedundantCast")
        @SuppressLint("SetTextI18n")
        private View getNormalView(View convertView, ViewGroup parent, int position) {
            NormalViewHolder normalViewHolder;
            if (convertView == null) {
                normalViewHolder = new NormalViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_normal, parent, false);
                normalViewHolder.idle = (TextView) convertView.findViewById(R.id.text_idle);
                convertView.setTag(normalViewHolder);
            } else {
                normalViewHolder = (NormalViewHolder) convertView.getTag();
            }
            normalViewHolder.idle.setText("ListView item " + position);
            return convertView;
        }

        /**
         * 设置广告的不喜欢，注意：强烈建议设置该逻辑，如果不设置dislike处理逻辑，则模板广告中的 dislike区域不响应dislike事件。
         *
         * @param ad
         * @param customStyle 是否自定义样式，true:样式自定义
         */
        private void bindDislike(final TTNativeExpressAd ad, boolean customStyle) {
            if (customStyle) {
                //使用自定义样式
                List<FilterWord> words = ad.getFilterWords();
                if (words == null || words.isEmpty()) {
                    return;
                }

                final DislikeDialog dislikeDialog = new DislikeDialog(mContext, words);
                dislikeDialog.setOnDislikeItemClick(new DislikeDialog.OnDislikeItemClick() {
                    @Override
                    public void onItemClick(FilterWord filterWord) {
                        //屏蔽广告
                        Toast.makeText(mContext,"点击"+ filterWord.getName(),Toast.LENGTH_SHORT).show();
                        //用户选择不喜欢原因后，移除广告展示
                        mData.remove(ad);
                        notifyDataSetChanged();
                    }
                });
                ad.setDislikeDialog(dislikeDialog);
                return;
            }
            //使用默认模板中默认dislike弹出样式
            ad.setDislikeCallback((Activity) mContext, new TTAdDislike.DislikeInteractionCallback() {
                @Override
                public void onSelected(int position, String value) {
                    Toast.makeText(mContext,"点击"+ value,Toast.LENGTH_SHORT).show();
                    //用户选择不喜欢原因后，移除广告展示
                    mData.remove(ad);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(mContext,"点击取消",Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void bindData(View convertView, final AdViewHolder adViewHolder, TTNativeExpressAd ad) {
            //设置dislike弹窗，这里展示自定义的dialog
            bindDislike(ad, true);
            switch (ad.getInteractionType()) {
                case TTAdConstant.INTERACTION_TYPE_DOWNLOAD:
                    bindDownloadListener(adViewHolder, ad);
                    break;
            }
        }


        private void bindDownloadListener(final AdViewHolder adViewHolder, TTNativeExpressAd ad) {
            TTAppDownloadListener downloadListener = new TTAppDownloadListener() {
                private boolean mHasShowDownloadActive = false;

                @Override
                public void onIdle() {
                    if (!isValid()) {
                        return;
                    }
                    Toast.makeText(mContext, "点击广告开始下载",Toast.LENGTH_SHORT).show();
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    if (!mHasShowDownloadActive) {
                        mHasShowDownloadActive = true;
                        Toast.makeText(mContext, " 下载中，点击暂停",Toast.LENGTH_SHORT).show();
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    Toast.makeText(mContext, " 下载暂停",Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    Toast.makeText(mContext, " 下载失败，重新下载",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onInstalled(String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    Toast.makeText(mContext, " 安装完成，点击打开",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDownloadFinished(long totalBytes, String fileName, String appName) {
                    if (!isValid()) {
                        return;
                    }
                    Toast.makeText(mContext, " 下载成功，点击安装",Toast.LENGTH_SHORT).show();

                }

                @SuppressWarnings("BooleanMethodIsAlwaysInverted")
                private boolean isValid() {
                    return mTTAppDownloadListenerMap.get(adViewHolder) == this;
                }
            };
            //一个ViewHolder对应一个downloadListener, isValid判断当前ViewHolder绑定的listener是不是自己
            ad.setDownloadListener(downloadListener); // 注册下载监听器
            mTTAppDownloadListenerMap.put(adViewHolder, downloadListener);
        }


        private static class AdViewHolder {
            FrameLayout videoView;
        }

        private static class NormalViewHolder {
            TextView idle;
        }
    }
    /**
     * 加载feed广告
     */
    private void loadListAd() {
        float expressViewWidth = 350;
        float expressViewHeight = 350;
        try {
            expressViewWidth = Float.parseFloat(mEtWidth.getText().toString());
            expressViewHeight = Float.parseFloat(mEtHeight.getText().toString());
        } catch (Exception e) {
            expressViewHeight = 0; //高度设置为0,则高度会自适应
        }
        //step4:创建feed广告请求类型参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId("940898292")
//                .setCodeId("901121125")
                .setSupportDeepLink(true)
                .setImageAcceptedSize(640, 320)
                .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight) //期望模板广告view的size,单位dp
                .setAdCount(3) //请求广告数量为1到3条
                .build();
        //step5:请求广告，调用feed广告异步请求接口，加载到广告后，拿到广告素材自定义渲染
        mTTAdNative.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }
                Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }

                if (ads == null || ads.isEmpty()) {
                    Toast.makeText(mContext, "on FeedAdLoaded: ad is null!",Toast.LENGTH_SHORT).show();
                    return;
                }

                for (int i = 0; i < LIST_ITEM_COUNT; i++) {
                    mData.add(null);
                }
                bindAdListener(ads);
            }
        });
    }

    private void bindAdListener(final List<TTNativeExpressAd> ads) {
        final int count = mData.size();
        for (TTNativeExpressAd ad : ads) {
            final TTNativeExpressAd adTmp = ad;
            int random = (int) (Math.random() * LIST_ITEM_COUNT) + count - LIST_ITEM_COUNT;
            mData.set(random, adTmp);
            myAdapter.notifyDataSetChanged();

            adTmp.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
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
                    Toast.makeText(mContext, msg + " code:" + code,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRenderSuccess(View view, float width, float height) {
                    //返回view的宽高 单位 dp
                    Toast.makeText(mContext, "渲染成功",Toast.LENGTH_SHORT).show();
                    myAdapter.notifyDataSetChanged();
                }
            });
            ad.render();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
