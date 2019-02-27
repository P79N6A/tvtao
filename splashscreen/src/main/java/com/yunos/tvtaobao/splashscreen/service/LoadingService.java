/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.loading
 * FILE NAME: LoadingService.java
 * CREATED TIME: 2014-10-27
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
package com.yunos.tvtaobao.splashscreen.service;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.LoadingBo;
import com.yunos.tvtaobao.biz.util.FileUtil;
import com.yunos.tvtaobao.biz.util.MD5Util;
import com.yunos.tvtaobao.biz.util.TimeUtil;
import com.yunos.tvtaobao.splashscreen.config.BaseConfig;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载打开应用的广告图片
 * @author tingmeng.ytm
 *
 */
public class LoadingService extends Service {

    private String TAG = "LoadingService";
    private boolean isDoing = false;
    private DisplayImageOptions option;
    private String bitmapPath;
    private List<LoadingBo> listLoading = new ArrayList<LoadingBo>();
    private BusinessRequest mBusinessRequest;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        File filesDir = getFilesDir();
        if (filesDir == null) {
            AppDebug.w(TAG, "onCreate filesDir null stop service");
            stopSelf();
            return;
        }
        bitmapPath = getFilesDir().toString() + "/" + BaseConfig.LOADING_CACHE_DIR;
        FileUtil.createDir(bitmapPath);
        AppDebug.w(TAG, "onCreate filesDir " + bitmapPath);
        option = new DisplayImageOptions.Builder().cacheOnDisc(false).cacheInMemory(false).setBitmapPath(bitmapPath)
                .build();
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppDebug.i("LoadingService", "Service onStartCommand");
        //如果正在处理中,直接返回
        if (isDoing || !NetWorkUtil.isNetWorkAvailable()) {
            return super.onStartCommand(intent, flags, startId);
        }
        isDoing = true;

        requestData();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 请求接口数据
     */
    private void requestData() {
        if (mBusinessRequest != null) {
            mBusinessRequest.getAdvertsRequest(new GetgetAdvertsRequestListener(new WeakReference<LoadingService>(this)));
        }
    }

    /**
     * 下载新文件
     */
    private void downloadNewFile() {
        if (listLoading.size() == 0) {
            return;
        }

        //扫描本地已存在的文件
        List<String> items = FileUtil.scan(new File(bitmapPath));
        Map<String, String> vaildItems = new HashMap<String, String>();
        for (int i = 0; i < listLoading.size(); i++) {
            LoadingBo mLoadingBo = listLoading.get(i);
            //如果当前时间小于结束时间,就要下载
            if (TimeUtil.compareToCurrentTime(mLoadingBo.getEndTime()) <= 0) {
                //放到可用列表中
                vaildItems.put(mLoadingBo.getMd5(), mLoadingBo.getMd5());
                String md5 = "";
                try {
                    md5 = MD5Util.getFileMD5(new File(getFilesDir() + "/" + BaseConfig.LOADING_CACHE_DIR + "/"
                            + mLoadingBo.getMd5()));
                } catch (Exception e) {
                    AppDebug.i("loading", "file not found");
                }
                if (!TextUtils.isEmpty(md5)) {
                    md5 = md5.toUpperCase();
                }
                //如果已缓存的图片跟要下载的相同,跳过
                if (mLoadingBo.getMd5().equals(md5)) {
                    continue;
                }
                //下载图片，完成之前自动关闭service
                ImageLoaderManager.getImageLoaderManager(this).loadImage(mLoadingBo.getImgUrl(), mLoadingBo.getMd5(),
                        option, null);
            }
        }
        

        //更新json缓存
        String loadingJsonNew = JSON.toJSONString(listLoading);
        FileUtil.write(this, getFilesDir() + "/" + BaseConfig.LOADING_CACHE_JSON, loadingJsonNew);

        //删除无用的缓存文件
        for (int i = 0; i < items.size(); i++) {
            if (TextUtils.isEmpty(items.get(i))) {
                continue;
            }
            int start = items.get(i).lastIndexOf("/");
            String fileName = items.get(i).substring(start + 1, items.get(i).length());
            boolean isSuccess = false;
            if (vaildItems == null || vaildItems.isEmpty()) {
                isSuccess = FileUtil.deleteFile(new File(items.get(i)));
                AppDebug.i("DeleteFile", "delete file " + fileName + " result:" + isSuccess);
                continue;
            }

            if (!vaildItems.containsKey(fileName)) {
                isSuccess = FileUtil.deleteFile(new File(items.get(i)));
                AppDebug.i("DeleteFile", "delete file " + fileName + " result:" + isSuccess);
            }
        }
    }

    /**
     * 获取广告Loading图片
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年2月13日 上午11:39:50
     */
    private static class GetgetAdvertsRequestListener implements RequestListener<List<LoadingBo>> {

        private WeakReference<LoadingService> ref;

        public GetgetAdvertsRequestListener(WeakReference<LoadingService> service) {
            ref = service;
        }

        @Override
        public void onRequestDone(List<LoadingBo> data, int resultCode, String msg) {
            LoadingService service = ref.get();
            AppDebug.i(service.TAG, service.TAG + ",GetgetAdvertsRequestListener resultCode = " + resultCode);
            if (service != null) {
                service.isDoing = false;
                if (resultCode == 200) {
                    service.listLoading = data;
                    service.downloadNewFile();
                    // 图片下载请求完成就直接停止服务
                    service.stopSelf();
                }
            }
        }
    }

    private void test() {
//        final LoadingBo mLoadingBo = new LoadingBo();
//        mLoadingBo.setMd5("4D9D635FD27A25A8F118583B22ABDBB9");
//        mLoadingBo
//                .setImgUrl("http://e.hiphotos.baidu.com/image/h%3D1080%3Bcrop%3D0%2C0%2C1920%2C1080/sign=0e4f8539708da977512f822b8861c37a/241f95cad1c8a7868dc80c046409c93d71cf50dd.jpg");
//        mLoadingBo.setStartTime("2014-10-26 00:00:01");
//        mLoadingBo.setEndTime("2014-10-27 00:00:00");
//        listLoading.add(mLoadingBo);
//
//        final LoadingBo mLoadingBo2 = new LoadingBo();
//        mLoadingBo2.setMd5("033C5DCE7B78A783978676FDC9CB3DAC");
//        mLoadingBo2
//                .setImgUrl("http://f.hiphotos.baidu.com/image/h%3D1080%3Bcrop%3D0%2C0%2C1920%2C1080/sign=005edeba4510b912a0c1f2fefbcdc760/a2cc7cd98d1001e9f44e8d0eba0e7bec54e79745.jpg");
//        mLoadingBo2.setStartTime("2014-10-27 00:00:01");
//        mLoadingBo2.setEndTime("2014-10-28 00:00:00");
//        listLoading.add(mLoadingBo2);
//
//        final LoadingBo mLoadingBo3 = new LoadingBo();
//        mLoadingBo3.setMd5("2D8E68B17847534E26422AAA1C03917E");
//        mLoadingBo3
//                .setImgUrl("http://b.hiphotos.baidu.com/image/h%3D1080%3Bcrop%3D0%2C0%2C1920%2C1080/sign=75439d9cc9ef7609230b9d9f16ed98af/0bd162d9f2d3572c686805dd8813632762d0c32d.jpg");
//        mLoadingBo3.setStartTime("2014-10-28 00:00:01");
//        mLoadingBo3.setEndTime("2014-10-30 00:00:00");
//        listLoading.add(mLoadingBo3);
    }

}
