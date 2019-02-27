package com.tvtaobao.voicesdk.utils;

import android.text.TextUtils;

import com.bftv.fui.thirdparty.VoiceFeedback;
import com.bftv.fui.thirdparty.bean.AllIntent;
import com.bftv.fui.thirdparty.bean.MiddleData;
import com.bftv.fui.thirdparty.bean.Tips;
import com.tvtaobao.voicesdk.bo.JinnangDo;
import com.tvtaobao.voicesdk.bo.ProductDo;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2017/08/08
 *     desc   : 暴风数据转换工具类
 *              将我们的数据转换成暴风需要的数据格式
 *     version: 1.0
 * </pre>
 */

public class BFDataController {

    public static VoiceFeedback onSearchSuccess(String words, List<ProductDo> productDos, List<JinnangDo> jinnangDos, String message, List<String> tips) {
        VoiceFeedback voiceFb = new VoiceFeedback();
        voiceFb.isHasResult = true;
        voiceFb.listMiddleData = new ArrayList<>();
        voiceFb.listTips = new ArrayList<>();

        if (TextUtils.isEmpty(message)) {
            if (productDos.size() == 0) {
                message = "很抱歉，没有找到与\"" + words + "\"相关的商品";
            } else {
                message = "为您推荐如下几款 " + words + "，您想买哪一款？";
            }
        }
        voiceFb.feedback = message;

        voiceFb.listPrompts = new ArrayList<>();
        if (tips == null || tips.size() == 0) {
            voiceFb.listPrompts.add("想看第三个商品的详情，可以对我说“看看第三个”");
            voiceFb.listPrompts.add("没有喜欢的？可以对我说“换一批”");
        }
        voiceFb.listPrompts.addAll(tips);
        if (jinnangDos.size() > 0) {
            String jinnang = jinnangDos.get(5).getName();  //TODO 产品郭阳阳要求一定要是第6个，我解释过了；如果有问题找产品。目的是为了提示正好对应上面第6个，用户可以看到
            voiceFb.listPrompts.add(0, "想要" + jinnang + "的？可以对我说“" + jinnang + "”");
        }

        voiceFb.type = VoiceFeedback.TYPE_MIDDLE;
        for (int i = 0 ; i < productDos.size() ; i++) {
            voiceFb.listMiddleData.add(makeProduct(productDos.get(i)));
        }

        for (int j = 0; j < jinnangDos.size(); j++) {
            voiceFb.listTips.add(makeJinNang(jinnangDos.get(j)));
        }

        return voiceFb;
    }

    /**
     * 暴风商品数据转换
     * 将我们的数据转换成暴风需要的数据格式
     * @param items
     * @return
     */
    public static MiddleData makeProduct(ProductDo items) {
        MiddleData middleData = new MiddleData();
        middleData.middlePic = items.getPicUrl();
        middleData.title = items.getTitle();
        middleData.sales = items.getBiz30daySold();
        middleData.price = items.getDiscntPrice();
        middleData.label = items.getPostageTextInfo();
        middleData.isCommodity = true;
        List<AllIntent> list = new ArrayList<>();

        //根据skuSize来判断是否是快捷下单商品
        if (Integer.parseInt(items.getStdSkuSize()) <= 1 && !items.getPresale().equals("true")) {
            middleData.isFastTips = true;
        } else {
            middleData.isFastTips = false;
        }

        //是否已经购买过的商品
        if (items.getTopTag() != null && items.getTopTag().equals("1000")) {
            middleData.isBuyTips = true;
        } else {
            middleData.isBuyTips = false;
        }

        //跳转详情页
        AllIntent allIntent = new AllIntent();
        allIntent.type = "uri";
        allIntent.uri = "tvtaobao://home?module=detail&itemId=" + items.getItemId() + "&from=voice_system&from_app=voice_system&notshowloading=true";
        allIntent.entranceWords = "open";
        list.add(allIntent);

        //快捷购买
        AllIntent tobuy = new AllIntent();
        tobuy.type = "uri";
        tobuy.entranceWords = "tobuy";
        tobuy.uri = "tvtaobao://voice?module=createorder&itemId=" + items.getItemId() + "&skuSize=" + items.getStdSkuSize() + "&from=voice_system&from_app=voice_system&notshowloading=true&presale=" + items.getPresale();
        list.add(tobuy);

        //加入购物车
        AllIntent toAddCart = new AllIntent();
        toAddCart.type = "uri";
        toAddCart.uri = "tvtaobao://addcart?itemId=" + items.getItemId();
        toAddCart.entranceWords = "addcart";
        list.add(toAddCart);

        //收藏
        AllIntent toCollection = new AllIntent();
        toCollection.type = "uri";
        toCollection.uri = "tvtaobao://collection?itemId=" + items.getItemId();
        toCollection.entranceWords = "collection";
        list.add(toCollection);
        
        middleData.listIntent = list;
        return middleData;
    }

//    /**
//     * 淘攻略数据转换
//     * 将我们的数据转换成暴风所需要的数据格式
//     * @param strategyDO
//     * @return
//     */
//    public static MiddleData makeStrategy(StrategyDO strategyDO) {
//        MiddleData middleData = new MiddleData();
//        middleData.middlePic = strategyDO.getUrl();
//        middleData.title = "达人说";
//        middleData.isCommodity = false;
//        middleData.isFastTips = false;
//        middleData.isBuyTips = false;
//        middleData.content = strategyDO.getTitle();
//        List<AllIntent> list = new ArrayList<>();
//        AllIntent allIntent = new AllIntent();
//        allIntent.type = "uri";
//        allIntent.uri = "tvtaobao://slideshow?ContentID=" + strategyDO.getFeedId() + "&notshowloading=true";
//        allIntent.entranceWords = "open";
//        list.add(allIntent);
//        middleData.listIntent = list;
//        return middleData;
//    }
    /**
     * 锦囊数据转换
     * 将我们的数据转换成暴风所需要的数据格式
     * @param jinNangDO
     * @return
     */
    public static Tips makeJinNang(JinnangDo jinNangDO) {
        Tips tips = new Tips();
        tips.name = jinNangDO.getName();
        tips.content = jinNangDO.getContent();
        tips.type = jinNangDO.getType();
        return tips;
    }
}
