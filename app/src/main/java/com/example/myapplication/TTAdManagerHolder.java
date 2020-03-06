package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.cmcm.cmgame.CmGameSdk;
import com.cmcm.cmgame.gamedata.CmGameAppInfo;
import com.example.myapplication.smallgame.CmGameImageLoader;

/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {

    private static boolean sInit;


    public static TTAdManager get() {
        if (!sInit) {
            throw new RuntimeException("TTAdSdk is not init, please check.");
        }
        return TTAdSdk.getAdManager();
    }

    public static void init(Context context) {
        doInit(context);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context) {
        if (!sInit) {
            TTAdSdk.init(context, buildConfig(context));
            sInit = true;
        }
    }

    private static TTAdConfig buildConfig(Context context) {

        CmGameAppInfo cmGameAppInfo = new CmGameAppInfo();
        cmGameAppInfo.setAppId("demo");                             // GameSdkID，向我方申请
        cmGameAppInfo.setAppHost("https://xyx-sdk-svc.cmcm.com");   // 游戏host地址，向我方申请

        // 设置游戏的广告id
        CmGameAppInfo.TTInfo ttInfo = new CmGameAppInfo.TTInfo();
        ttInfo.setGameListFeedId("901121737"); // 游戏列表，信息流广告，自渲染
        ttInfo.setRewardVideoId("901121365");   // 激励视频
        ttInfo.setFullVideoId("901121375");     // 全屏视频，插屏场景下展示
        ttInfo.setExpressInteractionId("901121133"); // 插屏广告，模板渲染，插屏场景下展示
        ttInfo.setExpressBannerId("901121159"); // Banner广告，模板渲染，尺寸：600*150
        //退出底部广告
        ttInfo.setGameEndFeedAdId("901121737"); // 游戏推荐弹框底部广告

        // 游戏列表展示时显示，插屏广告，模板渲染1：1
        // 游戏以tab形式的入口不要使用， 首页弹出广告
//        ttInfo.setGamelistExpressInteractionId("901121536");

        // 游戏加载时展示，下面广告2选1
        // 插屏广告-原生-自渲染-大图
        // 在2019-7-17后，穿山甲只针对部分媒体开放申请，如后台无法申请到这个广告位，则无需调用代码
        ttInfo.setLoadingNativeId("901121435");
        // 此广告申请，所有媒体都可申请，游戏加载时展示，插屏广告1:1，模板渲染
        ttInfo.setGameLoad_EXADId("901121536");

        cmGameAppInfo.setTtInfo(ttInfo);
        CmGameSdk.initCmGameSdk((Application)context, cmGameAppInfo, new CmGameImageLoader(), BuildConfig.DEBUG);
        Log.d("cmgamesdk", "current sdk version : " + CmGameSdk.getVersion());
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
        return new TTAdConfig.Builder()
//                .appId("5001121")
//                .appId("5036667")
                .appId("5040898")
                .useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .appName("APP测试媒体")
                .titleBarTheme(TTAdConstant.TITLE_BAR_THEME_DARK)
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .allowShowPageWhenScreenLock(true) //是否在锁屏场景支持展示广告落地页
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用
                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                .supportMultiProcess(false)//是否支持多进程
                //.httpStack(new MyOkStack3())//自定义网络库，demo中给出了okhttp3版本的样例，其余请自行开发或者咨询工作人员。
                .build();
    }
}
