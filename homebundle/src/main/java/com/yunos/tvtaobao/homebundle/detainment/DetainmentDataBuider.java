package com.yunos.tvtaobao.homebundle.detainment;


import android.content.Context;
import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.DetainMentBo;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.util.FileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;

public class DetainmentDataBuider {

    private static final String TAG = "DetainmentDataBuider";
    private static final String PATH_FILE = "detainment.txt";
    private static final String CONTENT = "content";
    private static final String USERID = "userId";

    private static final long TIME_SPACE = 1 * 24 * 3600000; // 有效时间的间隔

    private BusinessRequest mBusinessRequest;
    private Context mContext;
    private String mFilePath;

    private DetainmentRequestListener mDetainmentRequestListener;

    public DetainmentDataBuider(Context context) {
        mContext = context;
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mFilePath = FileUtil.getApplicationPath(context) + "/" + PATH_FILE;
    }

    public void checkDetainmentData() {
        String current_userId = User.getUserId();
        if (TextUtils.isEmpty(current_userId)) {
            if (FileUtil.fileIsExists(mFilePath)) {
                current_userId = getUserIdFromFile();
            }
        }
        AppDebug.v(TAG, TAG + ".requestDetainmentData --> current_userId = " + current_userId);

        DetainMentDataRequestListener listener = new DetainMentDataRequestListener(
                new WeakReference<DetainmentDataBuider>(this), current_userId);
        //mBusinessRequest.requestDetainMent(current_userId, listener);
        mBusinessRequest.guessLike("TRADE_CART",listener);
}

    public boolean isFileValid() {
        boolean isvalid = false;
        if (FileUtil.fileIsExists(mFilePath)) {
            File file = new File(mFilePath);
            long modifiedTime = file.lastModified();
            long currentTime = System.currentTimeMillis();
            AppDebug.v(TAG, TAG + ".isFileValid --> modifiedTime = " + modifiedTime + "; currentTime = " + currentTime);
            if (currentTime - modifiedTime <= TIME_SPACE) {
                isvalid = true;
            }
            if (!isvalid) {
                file.delete();
            }
        }
        return isvalid;
    }

    public DetainMentBo[] getDetainMentDataFromFile(String current_userId) {

        DetainMentBo[] data_bo = null;
        AppDebug.v(TAG, TAG + ".getDetainMentDataFromFile --> current_userId = " + current_userId + "; mFilePath = "
                + mFilePath);

        if (!isFileValid()) {
            return null;
        }

        String content = FileUtil.read(mContext, mFilePath);
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        try {
            JSONObject obj = new JSONObject(content);
            String user_id = obj.optString(USERID);
            if (!TextUtils.isEmpty(user_id) && !TextUtils.equals(current_userId, user_id)) {
                return null;
            }
            String data = obj.optString(CONTENT);
            data_bo = DetainMentBo.resolve(data);
        } catch (JSONException e) {
        }
        return data_bo;
    }

    public void onRequestData(String current_userId, DetainmentRequestListener l) {
        if (TextUtils.isEmpty(current_userId)) {
            if (FileUtil.fileIsExists(mFilePath)) {
                current_userId = getUserIdFromFile();
            }
        }
        AppDebug.v(TAG, TAG + ".requestDetainmentData --> current_userId = " + current_userId);

        mDetainmentRequestListener = l;

        DetainMentDataRequestListener listener = new DetainMentDataRequestListener(
                new WeakReference<DetainmentDataBuider>(this), current_userId);
        listener.setNotify(true);
        //mBusinessRequest.requestDetainMent(current_userId, listener);
        mBusinessRequest.guessLike("TRADE_CART",listener);
    }

    private String getUserIdFromFile() {
        String content = SharePreferences.getString(USERID, null);
        return content;
    }

    private void saveDetainmentData(String data) {
        if (!TextUtils.isEmpty(data)) {
            SharePreferences.put(USERID, data);
        }
    }

    private void onNotify(GuessLikeGoodsBean guessLikeGoodsBean) {
        if (guessLikeGoodsBean!=null) {
            if (mDetainmentRequestListener != null) {
                mDetainmentRequestListener.onDetainmentRequestDone(guessLikeGoodsBean);
            }
        }
    }

    private static class DetainMentDataRequestListener implements RequestListener<GuessLikeGoodsBean> {

        private WeakReference<DetainmentDataBuider> reference;
        private String user_id;
        private boolean notify;

        public DetainMentDataRequestListener(WeakReference<DetainmentDataBuider> ref, String user_id) {
            reference = ref;
            notify = false;
            this.user_id = user_id;
        }

        public void setNotify(boolean notify) {
            this.notify = notify;
        }

        @Override
        public void onRequestDone(GuessLikeGoodsBean data, int resultCode, String msg) {
            if (reference != null && reference.get() != null) {
                DetainmentDataBuider detainmentDataBuider = reference.get();
                if (resultCode == 200) {
                    if (detainmentDataBuider != null) {
                        detainmentDataBuider.saveDetainmentData(user_id);
                        if (notify) {
                            detainmentDataBuider.onNotify(data);
                        }
                    }
                }
            }
        }
    }

    public static interface DetainmentRequestListener {

        boolean onDetainmentRequestDone(GuessLikeGoodsBean guessLikeGoodsBean);
    }

}
