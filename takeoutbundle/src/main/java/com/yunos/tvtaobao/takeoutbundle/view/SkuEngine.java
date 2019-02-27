package com.yunos.tvtaobao.takeoutbundle.view;

import android.text.TextUtils;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.biz.request.bo.SkuProp;
import com.yunos.tvtaobao.biz.request.item.TakeOutAgainRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjiajuan on 17/12/20.
 *
 * @describe sku属性工具类
 */

public class SkuEngine {
    private static String TAG="SkuEngine";
    private List<ItemListBean.SkuListBean> skuLists;
    private List<ItemListBean.MultiAttrBean.AttrListBean> attrLists;
    private List<SkuProp.Prop> propList;
    private SkuProp skuProp;

    public SkuEngine(String itemId,String title,List<ItemListBean.SkuListBean> skuLists,
                            List<ItemListBean.MultiAttrBean.AttrListBean> attrLists ){
        this.skuLists=skuLists;
        this.attrLists=attrLists;
        skuProp=new SkuProp();
        skuProp.setTitle(title);
        skuProp.setItemId(itemId);
        propList=new ArrayList<>();
    }

    /**
     * 判断是否所有sku都被选中
     * @return
     */
    public  boolean isSkuDataAllSelected(){
        int count=0;
        if (skuLists!=null){
            count++;
        }
        if (attrLists!=null){
            count+=attrLists.size();
        }
        if (propList!=null){
            AppDebug.e(TAG,"isSkuDataAllSelected  propList.size = "+propList.size()+",count =  "+count );
            AppDebug.e(TAG,"propList = "+propList.toString());
            if (propList.size()==count){
                return true;
            }else {
                return false;
            }
        }else {
            AppDebug.e(TAG,"isSkuDataAllSelected  propList is null");
            return false;
        }
    }

    /**
     * 设置shopId
     * @param storeId
     */
    public void setStoreId(String storeId){
        if (skuProp==null){
            return;
        }
        skuProp.setShopId(storeId);
    }

    /**
     * 设置是否含有sku属性
     * @param hasSku
     */
    public  void isHasSku(boolean hasSku){
        if (skuProp==null){
            return;
        }
        skuProp.setHasSku(hasSku);
    }

    public boolean hasSku(){
        return  skuProp==null? false:skuProp.isHasSku();
    }

    /**
     * 获取sku选择的集合
     * @return
     */
    public  SkuProp getSkuProp(){
        return skuProp;
    }

    /**
     * 增加选择的项
     * @param skuId
     * @param skuName
     * @param skuValue
     */
    public void addSelectSkuData(String skuId, String skuName, String skuValue){
        AppDebug.e(TAG,"addSelectSkuData . skuId =  " +skuId+" , skuName = "+skuName+" , skuValue = "+skuValue);
        if (!TextUtils.isEmpty(skuId)&&skuProp!=null){
            skuProp.setSkuId(skuId);
        }
        SkuProp.Prop prop=new SkuProp.Prop();
        prop.setName(skuName);
        prop.setValue(skuValue);
        //移除上一项
        for(int i = 0;i<propList.size();i++){
            SkuProp.Prop lastProp = propList.get(i);
            if(lastProp.getName().equals(skuName)){
                propList.remove(i);
                break;
            }
        }
        propList.add(prop);
        skuProp.setPropList(propList);
        if (propList!=null&&propList.size()>0)
        AppDebug.e(TAG,"addSelectSkuData . skuProp : "+propList.toString());
    }

    /**
     * 更新加入购物车的数量
     * @param count
     */

    public void updateCount(String count){
        if (skuProp==null){
            return;
        }
        skuProp.setCount(count);
    }

    /**
     * 获取该属性的商品总数
     * @return
     */
    public String getCount(){
        if (skuProp==null){
            return null;
        }
        return skuProp.getCount();
    }

    /**
     * 移除取消的项
     * @param skuName
     * @param skuValue
     */
    public void removeSelectedView(String skuName, String skuValue){
        AppDebug.e(TAG,"removeSelectedView , skuName = "+skuName +" ,skuValue = "+skuValue);
        if (propList==null){
            return;
        }
        for (int i=0;i<propList.size();i++){
             if (propList.get(i).getValue().equals(skuValue)&&propList.get(i).getName().equals(skuName)){
                 AppDebug.e(TAG,"removeSelectedView , remove  = "+propList.get(i).toString());
                 propList.remove(i);
                 break;
             }
        }

        AppDebug.e(TAG,"removeSelectedView after remove ,propList : "+propList.toString());

    }

}
