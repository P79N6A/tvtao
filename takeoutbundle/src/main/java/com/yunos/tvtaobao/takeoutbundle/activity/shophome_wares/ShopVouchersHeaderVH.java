package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.yunos.tvtaobao.takeoutbundle.R;

/**
 * @Author：wuhaoteng
 * @Date:2018/12/19
 * @Desc：
 */
public class ShopVouchersHeaderVH extends RecyclerView.ViewHolder {
    private ImageView rightImg;
    private ImageView leftImg;

    public ShopVouchersHeaderVH(Context context, View itemView) {
        super(itemView);
        rightImg = (ImageView) itemView.findViewById(R.id.img_right);
        leftImg = (ImageView) itemView.findViewById(R.id.img_left);
        doDrawPath(context);
    }


    //绘制路径: 绘制一个菱形
    private void doDrawPath(Context context) {
        int dp_10 = (int)context.getResources().getDimension(R.dimen.dp_10);
        int width = dp_10;
        int height = dp_10;
        Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        canvas.drawColor(Color.parseColor("#00000000"));

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#4cffffff"));
        paint.setStrokeWidth(5);

        //绘制路径
        Path path = new Path();
        //从哪个点开始绘制
        path.moveTo(0, width / 2);
        //然后绘制到哪个点
        path.lineTo(height / 2, width);
        //然后再绘制到哪个点
        path.lineTo(height, width / 2);
        //然后再绘制到哪个点
        path.lineTo(height / 2, 0);
        //然后再绘制到哪个点
        path.lineTo(0, width / 2);

        //按路径绘制，就是一个菱形
        canvas.drawPath(path, paint);

        rightImg.setImageBitmap(bm);
        leftImg.setImageBitmap(bm);
    }

}
