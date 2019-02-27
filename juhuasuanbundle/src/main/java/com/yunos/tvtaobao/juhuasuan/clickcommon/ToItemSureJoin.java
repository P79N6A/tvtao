package com.yunos.tvtaobao.juhuasuan.clickcommon;


import android.os.Handler.Callback;
import android.os.Message;

import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.activity.JuBaseActivity;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.DialogParams;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuTItemRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuTbItemRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.util.DialogUtils;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.TvTaoBaoDialog;
import com.yunos.tvtaobao.biz.request.bo.Address;
import com.yunos.tvtaobao.biz.request.bo.JoinGroupResult;
import com.yunos.tvtaobao.biz.request.bo.TbItemDetail;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;

import java.util.List;

/**
 * 调用SKU选择页面
 * @author hanqi
 */
public class ToItemSureJoin {

    private static MyBusinessRequest mBusinessRequest;

    /**
     * 调用聚划算详情接口失败时，判断是否是网络错误后，回调
     */
    public static final int WHAT_JUITEM_ERROR = 1;
    /**
     * 调用淘宝商品详情（包括sku等信息）接口失败时回调
     */
    public static final int WHAT_TBITEMDETAIL_ERROR = 2;
    /**
     * 调用收货地址接口，返回数据对象不是LIST时回调
     */
    public static final int WHAT_ADDRESS_TYPE_ERROR = 3;
    /**
     * 调用收货地址接口，返回数据对象为空时，弹出错误框之后回调
     */
    public static final int WHAT_ADDRESS_EMPTY = 4;
    /**
     * 调用收货地址接口,成功时回调
     */
    public static final int WHAT_ADDRESS_SUCCESS = 5;
    /**
     * 调用参团接口失败时回调
     */
    public static final int WHAT_JOINGROUP_ERROR = 6;
    /**
     * 调用参团成功时回调
     */
    public static final int WHAT_JOINGROUP_SUCCESS = 7;
    /**
     * 检测商品状态为不可购买状态时回调
     */
    public static final int WHAT_ITEM_UNABLE_BUY = 8;

    public static void sureJoin(JuBaseActivity activity, final String cateId, final Long juId, final String from,
                                final Callback callback) {
        if (null == juId) {
            return;
        }
        mBusinessRequest.requestGetItemById(juId,
                new JuTItemRetryRequestListener<Callback>(activity, callback, cateId) {

                    @Override
                    public void onSuccess(BaseActivity baseActivity, ItemMO data) {
                        JuBaseActivity activity = null;
                        if (baseActivity instanceof JuBaseActivity) {
                            activity = (JuBaseActivity) baseActivity;
                        }
                        if (null == activity) {
                            return;
                        }
                        if (null == data) {
                            activity.afterApiLoad(false, null, data);
                            Callback callback = mT.get();
                            if (null != callback) {
                                if (null != callback) {
                                    Message m = Message.obtain();
                                    m.what = WHAT_JUITEM_ERROR;
                                    m.arg1 = ServiceCode.SERVICE_OK.getCode();
                                    m.obj = null;
                                    callback.handleMessage(m);
                                }
                            }
                            return;
                        }
                        sureJoin(activity, data, from, callback);
                    }

                    @Override
                    public boolean onUserError(BaseActivity baseActivity, int resultCode, String msg) {
                        if (baseActivity instanceof JuBaseActivity) {
                            ((JuBaseActivity) baseActivity).afterApiLoad(false, msg, null);
                        }
                        Callback callback = mT.get();
                        if (null != callback) {
                            if (null != callback) {
                                Message m = Message.obtain();
                                m.what = WHAT_JUITEM_ERROR;
                                m.arg1 = resultCode;
                                m.obj = msg;
                                callback.handleMessage(m);
                            }
                        }

                        return super.onUserError(baseActivity, resultCode, msg);
                    }

                });
    }

    public static void sureJoin(JuBaseActivity activity, final ItemMO item, final String from, final Callback callback) {
        if (null == item) {
            return;
        }
        mBusinessRequest.requestGetItemDetail(item.getItemId(), new JuTbItemRetryRequestListener<Callback>(activity,
                callback) {

            @Override
            public void onSuccess(BaseActivity baseActivity, TbItemDetail itemDetail) {
                JuBaseActivity activity = null;
                if (baseActivity instanceof JuBaseActivity) {
                    activity = (JuBaseActivity) baseActivity;
                }
                if (null == activity) {
                    return;
                }
                if (null == itemDetail) {
                    activity.afterApiLoad(false, null, itemDetail);
                    Callback callback = mT.get();
                    if (null != callback) {
                        if (null != callback) {
                            Message m = Message.obtain();
                            m.what = WHAT_TBITEMDETAIL_ERROR;
                            m.arg1 = ServiceCode.SERVICE_OK.getCode();
                            m.obj = null;
                            callback.handleMessage(m);
                        }
                    }
                    return;
                }
                sureJoin(activity, item, itemDetail, from, callback);
            }

            @Override
            public boolean onUserError(BaseActivity baseActivity, int resultCode, String msg) {
                if (baseActivity instanceof JuBaseActivity) {
                    ((JuBaseActivity) baseActivity).afterApiLoad(false, msg, null);
                }
                if (!NetWorkUtil.isNetWorkAvailable()) {
                    Callback callback = mT.get();
                    if (null != callback) {
                        if (null != callback) {
                            Message m = Message.obtain();
                            m.what = WHAT_JUITEM_ERROR;
                            m.arg1 = resultCode;
                            m.obj = msg;
                            callback.handleMessage(m);
                        }
                    }
                }
                return super.onUserError(baseActivity, resultCode, msg);
            }

        });
    }

    public static void sureJoin(JuBaseActivity activity, final ItemMO item, final TbItemDetail tbItemDetail,
                                final String from, final Callback callback) {
        if (!item.isAbleBuy()) {
            String msg = "";
            if (item.isNotStart()) {
                msg = activity.getString(R.string.jhs_confirm_not_start);
            } else if (item.isOver()) {
                msg = activity.getString(R.string.jhs_confirm_is_over);
            } else {
                msg = activity.getString(R.string.jhs_confirm_no_stock);
            }
            TvTaoBaoDialog dialog = DialogUtils.get(DialogParams.makeParams(activity).setMsg(msg));

            if (null != callback) {
                Message m = Message.obtain();
                m.what = WHAT_ITEM_UNABLE_BUY;
                m.obj = dialog;
                if (!callback.handleMessage(m)) {
                    dialog.show();
                }
            } else {
                dialog.show();
            }
            activity.afterApiLoad(false, msg, item);
            return;
        }
        //        mBusinessRequest.requestGetItemDetail(context, item.getItemId(), new BusinessRequestListener() {
        //
        //            @Override
        //            public boolean onRequestDone(Object data, int resultCode, String msg) {
        //取用户的收货地址，并判断是否有收货地址
        mBusinessRequest.requestGetAddressList(new JuRetryRequestListener<List<Address>>(activity) {

            @Override
            public void onSuccess(BaseActivity baseActivity, List<Address> data) {
                JuBaseActivity activity = null;
                if (baseActivity instanceof JuBaseActivity) {
                    activity = (JuBaseActivity) baseActivity;
                }
                if (null == activity) {
                    return;
                }
                if (null == data || data.size() <= 0) {
                    String msg = activity.getString(R.string.jhs_no_address);
                    activity.afterApiLoad(false, msg, data);
                    if (null != callback) {
                        Message m = Message.obtain();
                        m.what = WHAT_ADDRESS_TYPE_ERROR;
                        m.arg1 = ServiceCode.SERVICE_OK.getCode();
                        m.obj = msg;
                        callback.handleMessage(m);
                    }
                    return;
                }
                Message m = Message.obtain();
                m.what = WHAT_ADDRESS_SUCCESS;
                m.obj = data;
                callback.handleMessage(m);

                //确认收货地址存在，参团
                mBusinessRequest.requestJoinGroup(item.getItemId().toString(), new JuRetryRequestListener<JoinGroupResult>(
                        activity) {

                    @Override
                    public void onSuccess(BaseActivity baseActivity, JoinGroupResult data) {
                        JuBaseActivity activity = null;
                        if (baseActivity instanceof JuBaseActivity) {
                            activity = (JuBaseActivity) baseActivity;
                        }
                        if (null == activity) {
                            return;
                        }

                        String msg = null;
                        Object object = null;
                        if (null != data) {
                            ToTaoBaoSdk.toSureJoin(activity, item.getItemId(), data.getKey(), from);
                            object = data;
                        } else {
                            msg = activity.getString(R.string.jhs_no_data);
                            object = msg;
                        }
                        activity.afterApiLoad(true, msg, data);
                        if (null != callback) {
                            Message m = Message.obtain();
                            m.what = WHAT_JOINGROUP_SUCCESS;
                            m.obj = object;
                            callback.handleMessage(m);
                        }
                    }

                    @Override
                    public boolean onUserError(BaseActivity baseActivity, int resultCode, String msg) {
                        if (baseActivity instanceof JuBaseActivity) {
                            ((JuBaseActivity) baseActivity).afterApiLoad(false, msg, null);
                        }
                        if (null != callback) {
                            Message m = Message.obtain();
                            m.what = WHAT_JOINGROUP_ERROR;
                            m.arg1 = resultCode;
                            m.obj = msg;
                            callback.handleMessage(m);
                        }
                        return super.onUserError(baseActivity, resultCode, msg);
                    }

                });
            }

            @Override
            public boolean onUserError(BaseActivity baseActivity, int resultCode, String msg) {
                if (baseActivity instanceof JuBaseActivity) {
                    ((JuBaseActivity) baseActivity).afterApiLoad(false, msg, null);
                }
                if (null != callback) {
                    Message m = Message.obtain();
                    m.what = WHAT_JOINGROUP_ERROR;
                    m.arg1 = resultCode;
                    m.obj = msg;
                    callback.handleMessage(m);
                }
                return super.onUserError(baseActivity, resultCode, msg);
            }
        });
    }

    static {
        mBusinessRequest = MyBusinessRequest.getInstance();
    }
}
