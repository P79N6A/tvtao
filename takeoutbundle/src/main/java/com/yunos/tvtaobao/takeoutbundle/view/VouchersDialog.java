package com.yunos.tvtaobao.takeoutbundle.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.yunos.CloudUUIDWrapper;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.VouchersMO;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.adapter.VouchersAdapter;

import java.util.Map;

/**
 * Created by chenjiajuan on 17/12/26.
 *
 * @describe 代金券
 */

public class VouchersDialog  extends Dialog{
    private String TAG="VouchersDialog";
    private Context context;
    private RecyclerView ryVouchers;
    private VouchersAdapter vouchersAdapter;
    private static VouchersDialog vouchersDialog;
    private String  pageName;
    private String shopId;

    public static VouchersDialog getInstance(Context context){
        if (vouchersDialog==null){
            synchronized (VouchersDialog.class){
                if (vouchersDialog==null){
                    vouchersDialog=new VouchersDialog(context);
                }
            }
        }
        return vouchersDialog;
    }

    public VouchersDialog(@NonNull Context context) {
        this(context, R.style.Dialog_vouchers);
    }

    public VouchersDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context=context;
        setContentView(R.layout.dialog_vouchers);
        Window window=getWindow();
        WindowManager.LayoutParams params=window.getAttributes();
        params.dimAmount=0.0f; //对话框背景
        params.gravity= Gravity.TOP;
        params.width= WindowManager.LayoutParams.MATCH_PARENT;
        params.height=WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ryVouchers= (RecyclerView) this.findViewById(R.id.ry_vouchers);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ryVouchers.setLayoutManager(linearLayoutManager);
        int space= (int) context.getResources().getDimension(R.dimen.dp_18);
        AppDebug.e(TAG,"space = "+space+" density = "+DeviceUtil.getDensityFromDevice(context));
        space= (int) (DeviceUtil.getDensityFromDevice(context)*space);
        ryVouchers.addItemDecoration(new SpaceItemDecoration(space));
    }


    public void setVouchersMO(String pageName,VouchersMO vouchersMO){
        vouchersAdapter=new VouchersAdapter(context,vouchersMO);
        ryVouchers.setAdapter(vouchersAdapter);
        this.pageName=pageName;
        if (!isShowing()){
            show();
            if (vouchersMO!=null&&vouchersMO.getLatestStatus()!=null){
                //代金券浮层曝光事件
                this.shopId=vouchersMO.getLatestStatus().getStoreId();
                utVouchersDialogExpose(pageName,shopId);
            }

        }
    }

    @Override
    public void dismiss() {
        if (vouchersAdapter!=null){
            vouchersAdapter=null;
        }
        if (ryVouchers!=null){
            ryVouchers=null;
        }
        if (vouchersDialog!=null){
            vouchersDialog=null;
        }
        //代金券浮层关闭事件
        utVouchersDialogDismiss(pageName,shopId);
        super.dismiss();
    }
    /**
     * //代金券浮层曝光事件
     * @param pageName 页面名称
     * @param shopId  店铺id
     */
    private void utVouchersDialogExpose(String pageName,String shopId) {
        Map<String, String> properties = Utils.getProperties();
        properties.put("shop id",shopId);

        properties.put("uuid", CloudUUIDWrapper.getCloudUUID());

        Utils.utCustomHit(pageName,"Expose_waimai_shop_grant_voucher",properties);
    }

    /**
     *代金券浮层关闭事件
     * @param pageName
     * @param shopId
     */

    private void utVouchersDialogDismiss(String pageName, String shopId) {
        Map<String,String> properties=Utils.getProperties();
        properties.put("shop id",shopId);

        properties.put("uuid",CloudUUIDWrapper.getCloudUUID());

        Utils.utControlHit(pageName,"Page_waimai_grant_voucher_key_back",properties);
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                outRect.bottom = 0;
            } else {
                outRect.bottom = space;
            }
            outRect.top=0;


        }
    }
}
