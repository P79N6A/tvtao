package com.yunos.tvtaobao.biz.request.bo;

import com.google.gson.JsonObject;
import com.taobao.detail.domain.base.Unit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dingbin on 2017/5/16.
 */

public class TBDetailResultV6 implements Serializable {


    /**
     * api : wdetail
     * v : 6.0
     * ret : ["SUCCESS::调用成功"]
     * data : {"apiStack":[{"name":"esi","value":"{\"delivery\":{\"from\":\"广东广州\",\"to\":\"浙江杭州\",\"areaId\":\"330100\",\"postage\":\"快递: 免运费\",\"extras\":{}},\"item\":{\"showShopActivitySize\":\"2\",\"sellCount\":\"105\",\"skuText\":\"请选择 颜色分类 \"},\"resource\":{\"share\":{\"name\":\"分享\",\"iconType\":\"1\"},\"bigPromotion\":{},\"entrances\":{}},\"consumerProtection\":{\"items\":[{\"title\":\"20小时内发货\"},{\"title\":\"15天退货\",\"desc\":\"15天退货，退货邮费买家承担\"},{\"title\":\"订单险\",\"desc\":\"保险公司全程担保赔付该商品每笔交易的售后\"},{\"title\":\"1次破损补寄\",\"desc\":\"商品在运输途中出现破损的，消费者可向卖家提出补寄申请，可补寄1次，补寄邮费由卖家承担\"},{\"title\":\"运费险\"},{\"title\":\"蚂蚁花呗\"},{\"title\":\"信用卡支付\"},{\"title\":\"集分宝\"}],\"channel\":{\"logo\":\"https://gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png\",\"title\":\"一站购齐 权威认证 买退无忧\"},\"passValue\":\"all\"},\"skuCore\":{\"sku2info\":{\"0\":{\"price\":{\"priceMoney\":\"1980\",\"priceText\":\"19.8\",\"priceTitle\":\"收藏送杯刷\",\"type\":\"1\"},\"quantity\":\"306\"},\"3273201416612\":{\"price\":{\"priceMoney\":\"1980\",\"priceText\":\"19.8\",\"type\":\"1\"},\"quantity\":\"306\"}},\"skuItem\":{\"location\":\"浙江杭州\"}},\"tradeConsumerProtection\":{\"tradeConsumerService\":{\"service\":{\"icon\":\"//gw.alicdn.com/tfs/TB1SvRnQXXXXXb3aXXXXXXXXXXX-140-42.png\",\"title\":\"一站购齐 权威认真 买退无忧\",\"items\":[{\"title\":\"20小时内发货\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"15天退货\",\"desc\":\"15天退货，退货邮费买家承担\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"订单险\",\"desc\":\"保险公司全程担保赔付该商品每笔交易的售后\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"1次破损补寄\",\"desc\":\"商品在运输途中出现破损的，消费者可向卖家提出补寄申请，可补寄1次，补寄邮费由卖家承担\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"运费险\"}]},\"nonService\":{\"title\":\"其他\",\"items\":[{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"蚂蚁花呗\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"信用卡支付\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"集分宝\"}]}},\"channel\":{\"logo\":\"https://gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png\",\"title\":\"一站购齐 权威认证 买退无忧\"},\"passValue\":\"all\",\"url\":\"https://h5.m.taobao.com/app/detailsubpage/consumer/index.js\",\"type\":\"0\"},\"vertical\":{\"askAll\":{\"askText\":\"可以装多少克？\",\"askIcon\":\"https://img.alicdn.com/tps/TB1tVU6PpXXXXXFaXXXXXXXXXXX-102-60.png\",\"answerText\":\"那种一瓶农夫山泉的水，这个瓶要装两瓶\",\"answerIcon\":\"https://img.alicdn.com/tps/TB1Z7c2LXXXXXXmaXXXXXXXXXXX-132-42.png\",\"linkUrl\":\"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&spm=w-a2141.7631564&itemId=543714768771\",\"title\":\"问大家(24)\",\"questNum\":\"24\",\"showNum\":\"1\",\"modelList\":[{\"askText\":\"可以装多少克？\",\"answerCountText\":\"5个回答\",\"firstAnswer\":\"那种一瓶农夫山泉的水，这个瓶要装两瓶\"},{\"askText\":\"能不能装热水刚开的那种会不会炸掉？？？\",\"answerCountText\":\"8个回答\"}]}},\"params\":{\"trackParams\":{\"layoutId\":null}},\"layout\":{},\"trade\":{\"buyEnable\":\"true\",\"cartEnable\":\"true\",\"buyParam\":{},\"cartParam\":{},\"hintBanner\":{}},\"feature\":{\"cainiaoNoramal\":\"true\",\"hasSku\":\"true\",\"showSku\":\"true\",\"superActTime\":\"false\"},\"price\":{\"price\":{\"priceMoney\":\"1980\",\"priceText\":\"19.8\",\"type\":\"1\"},\"extraPrices\":[{\"priceMoney\":\"3300\",\"priceText\":\"33\",\"priceTitle\":\"价格\",\"type\":\"2\",\"lineThrough\":\"true\"}],\"priceTag\":[{\"text\":\"收藏送杯刷\"},{\"text\":\"淘金币抵2%\",\"bgColor\":\"#ff9204\"}]},\"skuVertical\":{}}"}],"consumerProtection":{"channel":{"logo":"//gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png","title":"一站购齐 权威认证 买退无忧"},"passValue":"all"},"item":{"itemId":"543714768771","title":"韩国ulzzang纸张水杯子创意女学生超萌水瓶可爱卡通扁平纸片塑料","subtitle":"A6纸一般大小的纸张水杯，可以轻松放进书包，就跟书本一样轻薄。弧度杯身更贴合手掌，可爱的小黄鸭图案和马卡龙色杯盖，中和了杯子的硬朗气质。","images":["//img.alicdn.com/imgextra/i2/2100936062/TB2bsHibJFopuFjSZFHXXbSlXXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i3/2100936062/TB2bwjobHxmpuFjSZJiXXXauVXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i1/2100936062/TB2H5btbOBnpuFjSZFzXXaSrpXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i3/2100936062/TB2CK_dbSVmpuFjSZFFXXcZApXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i1/2100936062/TB2U0YobHBmpuFjSZFAXXaQ0pXa_!!2100936062.jpg"],"categoryId":"50006897","rootCategoryId":"122952001","brandValueId":"1422518447","skuText":"请选择颜色分类 ","commentCount":"1003","favcount":"5680","taobaoDescUrl":"//h5.m.taobao.com/app/detail/desc.html?_isH5Des=true#!id=543714768771&type=0&f=TB1yWCcRXXXXXbDXFXX8qtpFXlX&sellerType=C","tmallDescUrl":"//mdetail.tmall.com/templates/pages/desc?id=543714768771","taobaoPcDescUrl":"//h5.m.taobao.com/app/detail/desc.html?_isH5Des=true#!id=543714768771&type=1&f=TB1ohbiOVXXXXaeXpXX8qtpFXlX&sellerType=C","moduleDescUrl":"//hws.m.taobao.com/d/modulet/v5/WItemMouldDesc.do?id=543714768771&f=TB1vFSpRXXXXXaPXpXX8qtpFXlX","moduleDescParams":{"f":"i7/540/710/543714768771/TB1vFSpRXXXXXaPXpXX8qtpFXlX","id":"543714768771"},"h5moduleDescUrl":"//mdetail.tmall.com/templates/pages/itemDesc?id=543714768771","titleIcon":"//gtms01.alicdn.com/tps/i1/TB1g5mZJFXXXXcuXpXXgBrbGpXX-36-36.png","themeType":"theme11"},"mockData":"{\"delivery\":{},\"feature\":{\"hasSku\":true,\"showSku\":true},\"price\":{\"price\":{\"priceText\":\"33.00\"}},\"skuCore\":{\"sku2info\":{\"0\":{\"price\":{\"priceMoney\":3300,\"priceText\":\"33.00\",\"priceTitle\":\"价格\"},\"quantity\":400},\"3273201416609\":{\"price\":{\"priceMoney\":3300,\"priceText\":\"33.00\",\"priceTitle\":\"价格\"},\"quantity\":100},\"3273201416610\":{\"price\":{\"priceMoney\":3300,\"priceText\":\"33.00\",\"priceTitle\":\"价格\"},\"quantity\":100},\"3273201416611\":{\"price\":{\"priceMoney\":3300,\"priceText\":\"33.00\",\"priceTitle\":\"价格\"},\"quantity\":100},\"3273201416612\":{\"price\":{\"priceMoney\":3300,\"priceText\":\"33.00\",\"priceTitle\":\"价格\"},\"quantity\":100}},\"skuItem\":{\"hideQuantity\":true}},\"trade\":{\"buyEnable\":true,\"cartEnable\":true}}","params":{"trackParams":{"brandId":"1422518447","BC_type":"C","categoryId":"50006897"}},"props":{"groupProps":[{"基本信息":[{"品牌":"kiwi君"},{"杯子样式":"有盖"},{"流行元素":"卡通"},{"货号":"kiwi-A86"},{"颜色分类":"杏色,粉红色,天蓝色,青色"},{"容量":"301mL(含)-400mL(含)"},{"材质":"塑料"},{"价格区间":"30元-39.9元"}]}]},"rate":{"totalCount":"1003","rateList":[{"content":"宝贝收到了，非常漂亮，不漏水，没味道。但清洗会困难些，因口太小。只能想想办法咯。快递哥态度好。","userName":"t**9","headPic":"//gtms03.alicdn.com/tps/i3/TB1yeWeIFXXXXX5XFXXuAZJYXXX-210-210.png_80x80.jpg","memberLevel":"4","dateTime":"2017-05-11","skuInfo":"颜色分类:粉红色","tmallMemberLevel":"0"}]},"resource":{"entrances":{"askAll":{"icon":"https://gw.alicdn.com/tps/TB1J7X6KXXXXXc4XXXXXXXXXXXX-102-60.png","text":"\"可以装多少克？\"","link":"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&itemId=543714768771"}}},"seller":{"userId":"2100936062","shopId":"111089355","shopName":"Kiwi君","shopUrl":"tmall://page.tm/shop?item_id=543714768771&shopId=111089355","shopIcon":"//img.alicdn.com/imgextra//19/2a/TB1WROGKXXXXXcqXVXXSutbFXXX.jpg","fans":"18.8万","certIcon":"//gtms04.alicdn.com/tps/i4/TB1DXthJXXXXXbUXFXXO518_pXX-132-132.png","allItemCount":"371","newItemCount":"32","shopCard":"掌柜近期上新32件宝贝，速览","sellerType":"C","shopType":"C","evaluates":[{"title":"宝贝描述","score":"4.7 ","type":"desc","level":"0"},{"title":"卖家服务","score":"4.7 ","type":"serv","level":"0"},{"title":"物流服务","score":"4.7 ","type":"post","level":"0"}],"sellerNick":"freshgreenkiwi","creditLevel":"16","tagIcon":"//gtms02.alicdn.com/tps/i2/TB1tFeOJFXXXXXdXVXXf2K3IVXX-80-24.png","starts":"2014-05-24 14:43:34","goodRatePercentage":"99.57%"},"skuBase":{"skus":[{"skuId":"3273201416609","propPath":"1627207:3232484"},{"skuId":"3273201416610","propPath":"1627207:3455405"},{"skuId":"3273201416611","propPath":"1627207:30155"},{"skuId":"3273201416612","propPath":"1627207:3232480"}],"props":[{"pid":"1627207","name":"颜色分类","values":[{"vid":"3232484","name":"天蓝色","image":"//img.alicdn.com/imgextra/i1/2100936062/TB2rzfmbS8mpuFjSZFMXXaxpVXa_!!2100936062.jpg"},{"vid":"3455405","name":"青色","image":"//img.alicdn.com/imgextra/i1/2100936062/TB2eaDlbNBmpuFjSZFDXXXD8pXa_!!2100936062.jpg"},{"vid":"30155","name":"杏色","image":"//img.alicdn.com/imgextra/i2/2100936062/TB23EmLbQqvpuFjSZFhXXaOgXXa_!!2100936062.jpg"},{"vid":"3232480","name":"粉红色","image":"//img.alicdn.com/imgextra/i3/2100936062/TB2NgnobHBmpuFjSZFAXXaQ0pXa_!!2100936062.jpg"}]}]},"vertical":{"jyj":{"logoText":"【优质网店】2年好店 权威认证"},"askAll":{"askText":"可以装多少克？","askIcon":"https://gw.alicdn.com/tps/TB1J7X6KXXXXXc4XXXXXXXXXXXX-102-60.png","answerText":"那种一瓶农夫山泉的水，这个瓶要装两瓶","answerIcon":"https://img.alicdn.com/tps/TB1Z7c2LXXXXXXmaXXXXXXXXXXX-132-42.png","linkUrl":"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&itemId=543714768771","title":"问大家(24)","questNum":"24","modelList":[{"askText":"可以装多少克？","answerCountText":"5个回答","firstAnswer":"那种一瓶农夫山泉的水，这个瓶要装两瓶"},{"askText":"能不能装热水刚开的那种会不会炸掉？？？","answerCountText":"8个回答"}]}}}
     */

    private List<Unit> domainList;

    public List<Unit> getDomainUnit() {
        return domainList;
    }

    public void setDomainList(List<Unit> domainList) {
        this.domainList = domainList;
    }

    /**
     * apiStack : [{"name":"esi","value":"{\"delivery\":{\"from\":\"广东广州\",\"to\":\"浙江杭州\",\"areaId\":\"330100\",\"postage\":\"快递: 免运费\",\"extras\":{}},\"item\":{\"showShopActivitySize\":\"2\",\"sellCount\":\"105\",\"skuText\":\"请选择 颜色分类 \"},\"resource\":{\"share\":{\"name\":\"分享\",\"iconType\":\"1\"},\"bigPromotion\":{},\"entrances\":{}},\"consumerProtection\":{\"items\":[{\"title\":\"20小时内发货\"},{\"title\":\"15天退货\",\"desc\":\"15天退货，退货邮费买家承担\"},{\"title\":\"订单险\",\"desc\":\"保险公司全程担保赔付该商品每笔交易的售后\"},{\"title\":\"1次破损补寄\",\"desc\":\"商品在运输途中出现破损的，消费者可向卖家提出补寄申请，可补寄1次，补寄邮费由卖家承担\"},{\"title\":\"运费险\"},{\"title\":\"蚂蚁花呗\"},{\"title\":\"信用卡支付\"},{\"title\":\"集分宝\"}],\"channel\":{\"logo\":\"https://gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png\",\"title\":\"一站购齐 权威认证 买退无忧\"},\"passValue\":\"all\"},\"skuCore\":{\"sku2info\":{\"0\":{\"price\":{\"priceMoney\":\"1980\",\"priceText\":\"19.8\",\"priceTitle\":\"收藏送杯刷\",\"type\":\"1\"},\"quantity\":\"306\"},\"3273201416612\":{\"price\":{\"priceMoney\":\"1980\",\"priceText\":\"19.8\",\"type\":\"1\"},\"quantity\":\"306\"}},\"skuItem\":{\"location\":\"浙江杭州\"}},\"tradeConsumerProtection\":{\"tradeConsumerService\":{\"service\":{\"icon\":\"//gw.alicdn.com/tfs/TB1SvRnQXXXXXb3aXXXXXXXXXXX-140-42.png\",\"title\":\"一站购齐 权威认真 买退无忧\",\"items\":[{\"title\":\"20小时内发货\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"15天退货\",\"desc\":\"15天退货，退货邮费买家承担\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"订单险\",\"desc\":\"保险公司全程担保赔付该商品每笔交易的售后\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"1次破损补寄\",\"desc\":\"商品在运输途中出现破损的，消费者可向卖家提出补寄申请，可补寄1次，补寄邮费由卖家承担\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"运费险\"}]},\"nonService\":{\"title\":\"其他\",\"items\":[{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"蚂蚁花呗\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"信用卡支付\"},{\"icon\":\"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png\",\"title\":\"集分宝\"}]}},\"channel\":{\"logo\":\"https://gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png\",\"title\":\"一站购齐 权威认证 买退无忧\"},\"passValue\":\"all\",\"url\":\"https://h5.m.taobao.com/app/detailsubpage/consumer/index.js\",\"type\":\"0\"},\"vertical\":{\"askAll\":{\"askText\":\"可以装多少克？\",\"askIcon\":\"https://img.alicdn.com/tps/TB1tVU6PpXXXXXFaXXXXXXXXXXX-102-60.png\",\"answerText\":\"那种一瓶农夫山泉的水，这个瓶要装两瓶\",\"answerIcon\":\"https://img.alicdn.com/tps/TB1Z7c2LXXXXXXmaXXXXXXXXXXX-132-42.png\",\"linkUrl\":\"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&spm=w-a2141.7631564&itemId=543714768771\",\"title\":\"问大家(24)\",\"questNum\":\"24\",\"showNum\":\"1\",\"modelList\":[{\"askText\":\"可以装多少克？\",\"answerCountText\":\"5个回答\",\"firstAnswer\":\"那种一瓶农夫山泉的水，这个瓶要装两瓶\"},{\"askText\":\"能不能装热水刚开的那种会不会炸掉？？？\",\"answerCountText\":\"8个回答\"}]}},\"params\":{\"trackParams\":{\"layoutId\":null}},\"layout\":{},\"trade\":{\"buyEnable\":\"true\",\"cartEnable\":\"true\",\"buyParam\":{},\"cartParam\":{},\"hintBanner\":{}},\"feature\":{\"cainiaoNoramal\":\"true\",\"hasSku\":\"true\",\"showSku\":\"true\",\"superActTime\":\"false\"},\"price\":{\"price\":{\"priceMoney\":\"1980\",\"priceText\":\"19.8\",\"type\":\"1\"},\"extraPrices\":[{\"priceMoney\":\"3300\",\"priceText\":\"33\",\"priceTitle\":\"价格\",\"type\":\"2\",\"lineThrough\":\"true\"}],\"priceTag\":[{\"text\":\"收藏送杯刷\"},{\"text\":\"淘金币抵2%\",\"bgColor\":\"#ff9204\"}]},\"skuVertical\":{}}"}]
     * consumerProtection : {"channel":{"logo":"//gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png","title":"一站购齐 权威认证 买退无忧"},"passValue":"all"}
     * item : {"itemId":"543714768771","title":"韩国ulzzang纸张水杯子创意女学生超萌水瓶可爱卡通扁平纸片塑料","subtitle":"A6纸一般大小的纸张水杯，可以轻松放进书包，就跟书本一样轻薄。弧度杯身更贴合手掌，可爱的小黄鸭图案和马卡龙色杯盖，中和了杯子的硬朗气质。","images":["//img.alicdn.com/imgextra/i2/2100936062/TB2bsHibJFopuFjSZFHXXbSlXXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i3/2100936062/TB2bwjobHxmpuFjSZJiXXXauVXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i1/2100936062/TB2H5btbOBnpuFjSZFzXXaSrpXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i3/2100936062/TB2CK_dbSVmpuFjSZFFXXcZApXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i1/2100936062/TB2U0YobHBmpuFjSZFAXXaQ0pXa_!!2100936062.jpg"],"categoryId":"50006897","rootCategoryId":"122952001","brandValueId":"1422518447","skuText":"请选择颜色分类 ","commentCount":"1003","favcount":"5680","taobaoDescUrl":"//h5.m.taobao.com/app/detail/desc.html?_isH5Des=true#!id=543714768771&type=0&f=TB1yWCcRXXXXXbDXFXX8qtpFXlX&sellerType=C","tmallDescUrl":"//mdetail.tmall.com/templates/pages/desc?id=543714768771","taobaoPcDescUrl":"//h5.m.taobao.com/app/detail/desc.html?_isH5Des=true#!id=543714768771&type=1&f=TB1ohbiOVXXXXaeXpXX8qtpFXlX&sellerType=C","moduleDescUrl":"//hws.m.taobao.com/d/modulet/v5/WItemMouldDesc.do?id=543714768771&f=TB1vFSpRXXXXXaPXpXX8qtpFXlX","moduleDescParams":{"f":"i7/540/710/543714768771/TB1vFSpRXXXXXaPXpXX8qtpFXlX","id":"543714768771"},"h5moduleDescUrl":"//mdetail.tmall.com/templates/pages/itemDesc?id=543714768771","titleIcon":"//gtms01.alicdn.com/tps/i1/TB1g5mZJFXXXXcuXpXXgBrbGpXX-36-36.png","themeType":"theme11"}
     * mockData : {"delivery":{},"feature":{"hasSku":true,"showSku":true},"price":{"price":{"priceText":"33.00"}},"skuCore":{"sku2info":{"0":{"price":{"priceMoney":3300,"priceText":"33.00","priceTitle":"价格"},"quantity":400},"3273201416609":{"price":{"priceMoney":3300,"priceText":"33.00","priceTitle":"价格"},"quantity":100},"3273201416610":{"price":{"priceMoney":3300,"priceText":"33.00","priceTitle":"价格"},"quantity":100},"3273201416611":{"price":{"priceMoney":3300,"priceText":"33.00","priceTitle":"价格"},"quantity":100},"3273201416612":{"price":{"priceMoney":3300,"priceText":"33.00","priceTitle":"价格"},"quantity":100}},"skuItem":{"hideQuantity":true}},"trade":{"buyEnable":true,"cartEnable":true}}
     * params : {"trackParams":{"brandId":"1422518447","BC_type":"C","categoryId":"50006897"}}
     * props : {"groupProps":[{"基本信息":[{"品牌":"kiwi君"},{"杯子样式":"有盖"},{"流行元素":"卡通"},{"货号":"kiwi-A86"},{"颜色分类":"杏色,粉红色,天蓝色,青色"},{"容量":"301mL(含)-400mL(含)"},{"材质":"塑料"},{"价格区间":"30元-39.9元"}]}]}
     * rate : {"totalCount":"1003","rateList":[{"content":"宝贝收到了，非常漂亮，不漏水，没味道。但清洗会困难些，因口太小。只能想想办法咯。快递哥态度好。","userName":"t**9","headPic":"//gtms03.alicdn.com/tps/i3/TB1yeWeIFXXXXX5XFXXuAZJYXXX-210-210.png_80x80.jpg","memberLevel":"4","dateTime":"2017-05-11","skuInfo":"颜色分类:粉红色","tmallMemberLevel":"0"}]}
     * resource : {"entrances":{"askAll":{"icon":"https://gw.alicdn.com/tps/TB1J7X6KXXXXXc4XXXXXXXXXXXX-102-60.png","text":"\"可以装多少克？\"","link":"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&itemId=543714768771"}}}
     * seller : {"userId":"2100936062","shopId":"111089355","shopName":"Kiwi君","shopUrl":"tmall://page.tm/shop?item_id=543714768771&shopId=111089355","shopIcon":"//img.alicdn.com/imgextra//19/2a/TB1WROGKXXXXXcqXVXXSutbFXXX.jpg","fans":"18.8万","certIcon":"//gtms04.alicdn.com/tps/i4/TB1DXthJXXXXXbUXFXXO518_pXX-132-132.png","allItemCount":"371","newItemCount":"32","shopCard":"掌柜近期上新32件宝贝，速览","sellerType":"C","shopType":"C","evaluates":[{"title":"宝贝描述","score":"4.7 ","type":"desc","level":"0"},{"title":"卖家服务","score":"4.7 ","type":"serv","level":"0"},{"title":"物流服务","score":"4.7 ","type":"post","level":"0"}],"sellerNick":"freshgreenkiwi","creditLevel":"16","tagIcon":"//gtms02.alicdn.com/tps/i2/TB1tFeOJFXXXXXdXVXXf2K3IVXX-80-24.png","starts":"2014-05-24 14:43:34","goodRatePercentage":"99.57%"}
     * skuBase : {"skus":[{"skuId":"3273201416609","propPath":"1627207:3232484"},{"skuId":"3273201416610","propPath":"1627207:3455405"},{"skuId":"3273201416611","propPath":"1627207:30155"},{"skuId":"3273201416612","propPath":"1627207:3232480"}],"props":[{"pid":"1627207","name":"颜色分类","values":[{"vid":"3232484","name":"天蓝色","image":"//img.alicdn.com/imgextra/i1/2100936062/TB2rzfmbS8mpuFjSZFMXXaxpVXa_!!2100936062.jpg"},{"vid":"3455405","name":"青色","image":"//img.alicdn.com/imgextra/i1/2100936062/TB2eaDlbNBmpuFjSZFDXXXD8pXa_!!2100936062.jpg"},{"vid":"30155","name":"杏色","image":"//img.alicdn.com/imgextra/i2/2100936062/TB23EmLbQqvpuFjSZFhXXaOgXXa_!!2100936062.jpg"},{"vid":"3232480","name":"粉红色","image":"//img.alicdn.com/imgextra/i3/2100936062/TB2NgnobHBmpuFjSZFAXXaQ0pXa_!!2100936062.jpg"}]}]}
     * vertical : {"jyj":{"logoText":"【优质网店】2年好店 权威认证"},"askAll":{"askText":"可以装多少克？","askIcon":"https://gw.alicdn.com/tps/TB1J7X6KXXXXXc4XXXXXXXXXXXX-102-60.png","answerText":"那种一瓶农夫山泉的水，这个瓶要装两瓶","answerIcon":"https://img.alicdn.com/tps/TB1Z7c2LXXXXXXmaXXXXXXXXXXX-132-42.png","linkUrl":"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&itemId=543714768771","title":"问大家(24)","questNum":"24","modelList":[{"askText":"可以装多少克？","answerCountText":"5个回答","firstAnswer":"那种一瓶农夫山泉的水，这个瓶要装两瓶"},{"askText":"能不能装热水刚开的那种会不会炸掉？？？","answerCountText":"8个回答"}]}}
     */

    private ConsumerProtectionBean consumerProtection;  //服务承诺
    private ItemBean item;
    private String mockData;
    private ParamsBean params;
    private PropsBean props;
    private RateBean rate;
    private ResourceBean resource;
    private SellerBean seller;
    private SkuBaseBean skuBase;
    private VerticalBean vertical;
    private List<ApiStackBean> apiStack;
    private Trade trade;
    private Feature feature;
    private Delivery delivery;
    private PriceBeanX price;
    private String skuKore;//秒杀商品的skucore

    private List<ContractData> contractDataList;//合约机信息

    public String getSkuKore() {
        return skuKore;
    }

    public void setSkuKore(String skuKore) {
        this.skuKore = skuKore;
    }

    public PriceBeanX getPrice() {
        return price;
    }

    public void setPrice(PriceBeanX price) {
        this.price = price;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public ConsumerProtectionBean getConsumerProtection() {
        return consumerProtection;
    }

    public void setConsumerProtection(ConsumerProtectionBean consumerProtection) {
        this.consumerProtection = consumerProtection;
    }

    public ItemBean getItem() {
        return item;
    }

    public void setItem(ItemBean item) {
        this.item = item;
    }

    public String getMockData() {
        return mockData;
    }

    public void setMockData(String mockData) {
        this.mockData = mockData;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public PropsBean getProps() {
        return props;
    }

    public void setProps(PropsBean props) {
        this.props = props;
    }

    public RateBean getRate() {
        return rate;
    }

    public void setRate(RateBean rate) {
        this.rate = rate;
    }

    public ResourceBean getResource() {
        return resource;
    }

    public void setResource(ResourceBean resource) {
        this.resource = resource;
    }

    public SellerBean getSeller() {
        return seller;
    }

    public void setSeller(SellerBean seller) {
        this.seller = seller;
    }

    public SkuBaseBean getSkuBase() {
        return skuBase;
    }

    public void setSkuBase(SkuBaseBean skuBase) {
        this.skuBase = skuBase;
    }

    public VerticalBean getVertical() {
        return vertical;
    }

    public void setVertical(VerticalBean vertical) {
        this.vertical = vertical;
    }

    public List<ApiStackBean> getApiStack() {
        return apiStack;
    }

    public void setContractData(List<ContractData> contractDataList) {
        this.contractDataList = contractDataList;
    }

    public List<ContractData> getContractData() {
        return contractDataList;
    }

    public void setApiStack(List<ApiStackBean> apiStack) {
        this.apiStack = apiStack;
    }

    public static class ContractData implements Serializable {
        public static class VersionData implements Serializable {
            public String versionName;
            public String planId;
            public boolean noShopCart;
            public boolean enableClick;
            public String versionCode;

            private VersionData() {

            }

            public static VersionData resolveVersionData(JSONObject data) {
                if (data == null)
                    return null;
                VersionData versionData = new VersionData();
                versionData.enableClick = data.optBoolean("enableClick");
                versionData.planId = data.optString("planId");
                versionData.noShopCart = data.optBoolean("noShopCart");
                versionData.versionCode = data.optString("versionCode");
                versionData.versionName = data.optString("versionName");
                return versionData;
            }
        }

        public VersionData versionData;
    }


    public static class PriceBeanX implements Serializable {
        private PriceBean price;

        public PriceBean getPrice() {
            return price;
        }

        public void setPrice(PriceBean price) {
            this.price = price;
        }

        /**
         * extraPrices : [{"priceText":"3299","priceTitle":"价格","lineThrough":"true","showTitle":"true"}]
         * price : {"priceText":"2999","showTitle":"false"}
         */

        public static class PriceBean implements Serializable {
            /**
             * priceText : 2999
             * showTitle : false
             * priceTitle :"定金"
             */

            private String priceText;
            private String priceTitle;

            public String getPriceTitle() {
                return priceTitle;
            }

            public void setPriceTitle(String priceTitle) {
                this.priceTitle = priceTitle;
            }

            public String getPriceText() {
                return priceText;
            }

            public void setPriceText(String priceText) {
                this.priceText = priceText;
            }

        }

    }

    public static class Delivery implements Serializable {
        private String postage;

        public String getPostage() {
            return postage;
        }

        public void setPostage(String postage) {
            this.postage = postage;
        }
    }

    public static class Feature implements Serializable {
        private String secKill;
        private String hasSku;

        public String getHasSku() {
            return hasSku;
        }

        public void setHasSku(String hasSku) {
            this.hasSku = hasSku;
        }

        public String getSecKill() {
            return secKill;
        }

        public void setSecKill(String secKill) {
            this.secKill = secKill;
        }
    }

    public static class ConsumerProtectionBean implements Serializable {
        /**
         * channel : {"logo":"//gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png","title":"一站购齐 权威认证 买退无忧"}
         * passValue : all
         */

        private ChannelBean channel;
        private String passValue;

        public ChannelBean getChannel() {
            return channel;
        }

        public void setChannel(ChannelBean channel) {
            this.channel = channel;
        }

        public String getPassValue() {
            return passValue;
        }

        public void setPassValue(String passValue) {
            this.passValue = passValue;
        }

        public static class ChannelBean implements Serializable {
            /**
             * 主图片地址
             * logo : //gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png
             * title : 一站购齐 权威认证 买退无忧  slogo
             */

            private String logo;
            private String title;

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }
    }

    public static class ItemBean implements Serializable {
        /**
         * itemId : 543714768771
         * title : 韩国ulzzang纸张水杯子创意女学生超萌水瓶可爱卡通扁平纸片塑料
         * subtitle : A6纸一般大小的纸张水杯，可以轻松放进书包，就跟书本一样轻薄。弧度杯身更贴合手掌，可爱的小黄鸭图案和马卡龙色杯盖，中和了杯子的硬朗气质。
         * images : ["//img.alicdn.com/imgextra/i2/2100936062/TB2bsHibJFopuFjSZFHXXbSlXXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i3/2100936062/TB2bwjobHxmpuFjSZJiXXXauVXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i1/2100936062/TB2H5btbOBnpuFjSZFzXXaSrpXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i3/2100936062/TB2CK_dbSVmpuFjSZFFXXcZApXa_!!2100936062.jpg","//img.alicdn.com/imgextra/i1/2100936062/TB2U0YobHBmpuFjSZFAXXaQ0pXa_!!2100936062.jpg"]
         * categoryId : 50006897
         * rootCategoryId : 122952001
         * brandValueId : 1422518447
         * skuText : 请选择颜色分类
         * commentCount : 1003
         * favcount : 5680
         * taobaoDescUrl : //h5.m.taobao.com/app/detail/desc.html?_isH5Des=true#!id=543714768771&type=0&f=TB1yWCcRXXXXXbDXFXX8qtpFXlX&sellerType=C
         * tmallDescUrl : //mdetail.tmall.com/templates/pages/desc?id=543714768771
         * taobaoPcDescUrl : //h5.m.taobao.com/app/detail/desc.html?_isH5Des=true#!id=543714768771&type=1&f=TB1ohbiOVXXXXaeXpXX8qtpFXlX&sellerType=C
         * moduleDescUrl : //hws.m.taobao.com/d/modulet/v5/WItemMouldDesc.do?id=543714768771&f=TB1vFSpRXXXXXaPXpXX8qtpFXlX
         * moduleDescParams : {"f":"i7/540/710/543714768771/TB1vFSpRXXXXXaPXpXX8qtpFXlX","id":"543714768771"}
         * h5moduleDescUrl : //mdetail.tmall.com/templates/pages/itemDesc?id=543714768771
         * titleIcon : //gtms01.alicdn.com/tps/i1/TB1g5mZJFXXXXcuXpXXgBrbGpXX-36-36.png
         * themeType : theme11
         */

        private String itemId;
        private String title;
        private String subtitle;
        private String categoryId;
        private String rootCategoryId;
        private String brandValueId;
        private String skuText;
        private String commentCount;
        private String favcount;
        private String taobaoDescUrl;
        private String tmallDescUrl;
        private String taobaoPcDescUrl;
        private String moduleDescUrl;
        private ModuleDescParamsBean moduleDescParams;
        private String h5moduleDescUrl;
        private String titleIcon;
        private String themeType;
        private List<String> images;

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getRootCategoryId() {
            return rootCategoryId;
        }

        public void setRootCategoryId(String rootCategoryId) {
            this.rootCategoryId = rootCategoryId;
        }

        public String getBrandValueId() {
            return brandValueId;
        }

        public void setBrandValueId(String brandValueId) {
            this.brandValueId = brandValueId;
        }

        public String getSkuText() {
            return skuText;
        }

        public void setSkuText(String skuText) {
            this.skuText = skuText;
        }

        public String getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(String commentCount) {
            this.commentCount = commentCount;
        }

        public String getFavcount() {
            return favcount;
        }

        public void setFavcount(String favcount) {
            this.favcount = favcount;
        }

        public String getTaobaoDescUrl() {
            return taobaoDescUrl;
        }

        public void setTaobaoDescUrl(String taobaoDescUrl) {
            this.taobaoDescUrl = taobaoDescUrl;
        }

        public String getTmallDescUrl() {
            return tmallDescUrl;
        }

        public void setTmallDescUrl(String tmallDescUrl) {
            this.tmallDescUrl = tmallDescUrl;
        }

        public String getTaobaoPcDescUrl() {
            return taobaoPcDescUrl;
        }

        public void setTaobaoPcDescUrl(String taobaoPcDescUrl) {
            this.taobaoPcDescUrl = taobaoPcDescUrl;
        }

        public String getModuleDescUrl() {
            return moduleDescUrl;
        }

        public void setModuleDescUrl(String moduleDescUrl) {
            this.moduleDescUrl = moduleDescUrl;
        }

        public ModuleDescParamsBean getModuleDescParams() {
            return moduleDescParams;
        }

        public void setModuleDescParams(ModuleDescParamsBean moduleDescParams) {
            this.moduleDescParams = moduleDescParams;
        }

        public String getH5moduleDescUrl() {
            return h5moduleDescUrl;
        }

        public void setH5moduleDescUrl(String h5moduleDescUrl) {
            this.h5moduleDescUrl = h5moduleDescUrl;
        }

        public String getTitleIcon() {
            return titleIcon;
        }

        public void setTitleIcon(String titleIcon) {
            this.titleIcon = titleIcon;
        }

        public String getThemeType() {
            return themeType;
        }

        public void setThemeType(String themeType) {
            this.themeType = themeType;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }

        public static class ModuleDescParamsBean implements Serializable {
            /**
             * f : i7/540/710/543714768771/TB1vFSpRXXXXXaPXpXX8qtpFXlX
             * id : 543714768771
             */

            private String f;
            private String id;

            public String getF() {
                return f;
            }

            public void setF(String f) {
                this.f = f;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }

    public static class ParamsBean implements Serializable {
        /**
         * trackParams : {"brandId":"1422518447","BC_type":"C","categoryId":"50006897"}
         */

        private TrackParamsBean trackParams;

        public TrackParamsBean getTrackParams() {
            return trackParams;
        }

        public void setTrackParams(TrackParamsBean trackParams) {
            this.trackParams = trackParams;
        }

        public static class TrackParamsBean implements Serializable {
            /**
             * brandId : 1422518447
             * BC_type : C
             * categoryId : 50006897
             */

            private String brandId;
            private String BC_type;
            private String categoryId;

            public String getBrandId() {
                return brandId;
            }

            public void setBrandId(String brandId) {
                this.brandId = brandId;
            }

            public String getBC_type() {
                return BC_type;
            }

            public void setBC_type(String BC_type) {
                this.BC_type = BC_type;
            }

            public String getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(String categoryId) {
                this.categoryId = categoryId;
            }
        }
    }

    public static class PropsBean implements Serializable {
        private List<GroupPropsBean> groupProps;

        public List<GroupPropsBean> getGroupProps() {
            return groupProps;
        }

        public void setGroupProps(List<GroupPropsBean> groupProps) {
            this.groupProps = groupProps;
        }

        public static class GroupPropsBean implements Serializable {
//                private List<基本信息Bean> 基本信息;
//
//                public List<基本信息Bean> get基本信息() {
//                    return 基本信息;
//                }
//
//                public void set基本信息(List<基本信息Bean> 基本信息) {
//                    this.基本信息 = 基本信息;
//                }
//
//                public static class 基本信息Bean {
//                    /**
//                     * 品牌 : kiwi君
//                     * 杯子样式 : 有盖
//                     * 流行元素 : 卡通
//                     * 货号 : kiwi-A86
//                     * 颜色分类 : 杏色,粉红色,天蓝色,青色
//                     * 容量 : 301mL(含)-400mL(含)
//                     * 材质 : 塑料
//                     * 价格区间 : 30元-39.9元
//                     */
//
//                    private String 品牌;
//                    private String 杯子样式;
//                    private String 流行元素;
//                    private String 货号;
//                    private String 颜色分类;
//                    private String 容量;
//                    private String 材质;
//                    private String 价格区间;
//
//                    public String get品牌() {
//                        return 品牌;
//                    }
//
//                    public void set品牌(String 品牌) {
//                        this.品牌 = 品牌;
//                    }
//
//                    public String get杯子样式() {
//                        return 杯子样式;
//                    }
//
//                    public void set杯子样式(String 杯子样式) {
//                        this.杯子样式 = 杯子样式;
//                    }
//
//                    public String get流行元素() {
//                        return 流行元素;
//                    }
//
//                    public void set流行元素(String 流行元素) {
//                        this.流行元素 = 流行元素;
//                    }
//
//                    public String get货号() {
//                        return 货号;
//                    }
//
//                    public void set货号(String 货号) {
//                        this.货号 = 货号;
//                    }
//
//                    public String get颜色分类() {
//                        return 颜色分类;
//                    }
//
//                    public void set颜色分类(String 颜色分类) {
//                        this.颜色分类 = 颜色分类;
//                    }
//
//                    public String get容量() {
//                        return 容量;
//                    }
//
//                    public void set容量(String 容量) {
//                        this.容量 = 容量;
//                    }
//
//                    public String get材质() {
//                        return 材质;
//                    }
//
//                    public void set材质(String 材质) {
//                        this.材质 = 材质;
//                    }
//
//                    public String get价格区间() {
//                        return 价格区间;
//                    }
//
//                    public void set价格区间(String 价格区间) {
//                        this.价格区间 = 价格区间;
//                    }
//                }
        }
    }

    public static class RateBean implements Serializable {
        /**
         * totalCount : 1003
         * rateList : [{"content":"宝贝收到了，非常漂亮，不漏水，没味道。但清洗会困难些，因口太小。只能想想办法咯。快递哥态度好。","userName":"t**9","headPic":"//gtms03.alicdn.com/tps/i3/TB1yeWeIFXXXXX5XFXXuAZJYXXX-210-210.png_80x80.jpg","memberLevel":"4","dateTime":"2017-05-11","skuInfo":"颜色分类:粉红色","tmallMemberLevel":"0"}]
         */

        private String totalCount; //评价总记录数
        private List<RateListBean> rateList; //评价列表

        public String getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(String totalCount) {
            this.totalCount = totalCount;
        }

        public List<RateListBean> getRateList() {
            return rateList;
        }

        public void setRateList(List<RateListBean> rateList) {
            this.rateList = rateList;
        }

        public static class RateListBean implements Serializable {
            /**
             * content : 宝贝收到了，非常漂亮，不漏水，没味道。但清洗会困难些，因口太小。只能想想办法咯。快递哥态度好。
             * userName : t**9
             * headPic : //gtms03.alicdn.com/tps/i3/TB1yeWeIFXXXXX5XFXXuAZJYXXX-210-210.png_80x80.jpg
             * memberLevel : 4
             * dateTime : 2017-05-11
             * skuInfo : 颜色分类:粉红色
             * tmallMemberLevel : 0
             */

            private String content;
            private String userName;
            private String headPic;
            private String memberLevel;
            private String dateTime;
            private String skuInfo;
            private String tmallMemberLevel;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getHeadPic() {
                return headPic;
            }

            public void setHeadPic(String headPic) {
                this.headPic = headPic;
            }

            public String getMemberLevel() {
                return memberLevel;
            }

            public void setMemberLevel(String memberLevel) {
                this.memberLevel = memberLevel;
            }

            public String getDateTime() {
                return dateTime;
            }

            public void setDateTime(String dateTime) {
                this.dateTime = dateTime;
            }

            public String getSkuInfo() {
                return skuInfo;
            }

            public void setSkuInfo(String skuInfo) {
                this.skuInfo = skuInfo;
            }

            public String getTmallMemberLevel() {
                return tmallMemberLevel;
            }

            public void setTmallMemberLevel(String tmallMemberLevel) {
                this.tmallMemberLevel = tmallMemberLevel;
            }
        }
    }

    public static class ResourceBean implements Serializable {
        /**
         * entrances : {"askAll":{"icon":"https://gw.alicdn.com/tps/TB1J7X6KXXXXXc4XXXXXXXXXXXX-102-60.png","text":"\"可以装多少克？\"","link":"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&itemId=543714768771"}}
         */

        private EntrancesBean entrances;

        public EntrancesBean getEntrances() {
            return entrances;
        }

        public void setEntrances(EntrancesBean entrances) {
            this.entrances = entrances;
        }

        public static class EntrancesBean implements Serializable {
            /**
             * askAll : {"icon":"https://gw.alicdn.com/tps/TB1J7X6KXXXXXc4XXXXXXXXXXXX-102-60.png","text":"\"可以装多少克？\"","link":"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&itemId=543714768771"}
             */

            private AskAllBean askAll;

            public AskAllBean getAskAll() {
                return askAll;
            }

            public void setAskAll(AskAllBean askAll) {
                this.askAll = askAll;
            }

            public static class AskAllBean implements Serializable {
                /**
                 * icon : https://gw.alicdn.com/tps/TB1J7X6KXXXXXc4XXXXXXXXXXXX-102-60.png
                 * text : "可以装多少克？"
                 * link : //h5.m.taobao.com/wendajia/question.htm?wdjType=1&itemId=543714768771
                 */

                private String icon;
                private String text;
                private String link;

                public String getIcon() {
                    return icon;
                }

                public void setIcon(String icon) {
                    this.icon = icon;
                }

                public String getText() {
                    return text;
                }

                public void setText(String text) {
                    this.text = text;
                }

                public String getLink() {
                    return link;
                }

                public void setLink(String link) {
                    this.link = link;
                }
            }
        }
    }

    public static class SellerBean implements Serializable {
        /**
         * userId : 2100936062
         * shopId : 111089355
         * shopName : Kiwi君
         * shopUrl : tmall://page.tm/shop?item_id=543714768771&shopId=111089355
         * shopIcon : //img.alicdn.com/imgextra//19/2a/TB1WROGKXXXXXcqXVXXSutbFXXX.jpg
         * fans : 18.8万
         * certIcon : //gtms04.alicdn.com/tps/i4/TB1DXthJXXXXXbUXFXXO518_pXX-132-132.png
         * allItemCount : 371
         * newItemCount : 32
         * shopCard : 掌柜近期上新32件宝贝，速览
         * sellerType : C
         * shopType : C
         * evaluates : [{"title":"宝贝描述","score":"4.7 ","type":"desc","level":"0"},{"title":"卖家服务","score":"4.7 ","type":"serv","level":"0"},{"title":"物流服务","score":"4.7 ","type":"post","level":"0"}]
         * sellerNick : freshgreenkiwi
         * creditLevel : 16
         * tagIcon : //gtms02.alicdn.com/tps/i2/TB1tFeOJFXXXXXdXVXXf2K3IVXX-80-24.png
         * starts : 2014-05-24 14:43:34
         * goodRatePercentage : 99.57%
         */

        private String userId;
        private String shopId;
        private String shopName;
        private String shopUrl;
        private String shopIcon;
        private String fans;
        private String certIcon;
        private String allItemCount;
        private String newItemCount;
        private String shopCard;
        private String sellerType;
        private String shopType;
        private String sellerNick;
        private String creditLevel;
        private String tagIcon;
        private String starts;
        private String goodRatePercentage;
        private List<EvaluatesBean> evaluates;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getShopUrl() {
            return shopUrl;
        }

        public void setShopUrl(String shopUrl) {
            this.shopUrl = shopUrl;
        }

        public String getShopIcon() {
            return shopIcon;
        }

        public void setShopIcon(String shopIcon) {
            this.shopIcon = shopIcon;
        }

        public String getFans() {
            return fans;
        }

        public void setFans(String fans) {
            this.fans = fans;
        }

        public String getCertIcon() {
            return certIcon;
        }

        public void setCertIcon(String certIcon) {
            this.certIcon = certIcon;
        }

        public String getAllItemCount() {
            return allItemCount;
        }

        public void setAllItemCount(String allItemCount) {
            this.allItemCount = allItemCount;
        }

        public String getNewItemCount() {
            return newItemCount;
        }

        public void setNewItemCount(String newItemCount) {
            this.newItemCount = newItemCount;
        }

        public String getShopCard() {
            return shopCard;
        }

        public void setShopCard(String shopCard) {
            this.shopCard = shopCard;
        }

        public String getSellerType() {
            return sellerType;
        }

        public void setSellerType(String sellerType) {
            this.sellerType = sellerType;
        }

        public String getShopType() {
            return shopType;
        }

        public void setShopType(String shopType) {
            this.shopType = shopType;
        }

        public String getSellerNick() {
            return sellerNick;
        }

        public void setSellerNick(String sellerNick) {
            this.sellerNick = sellerNick;
        }

        public String getCreditLevel() {
            return creditLevel;
        }

        public void setCreditLevel(String creditLevel) {
            this.creditLevel = creditLevel;
        }

        public String getTagIcon() {
            return tagIcon;
        }

        public void setTagIcon(String tagIcon) {
            this.tagIcon = tagIcon;
        }

        public String getStarts() {
            return starts;
        }

        public void setStarts(String starts) {
            this.starts = starts;
        }

        public String getGoodRatePercentage() {
            return goodRatePercentage;
        }

        public void setGoodRatePercentage(String goodRatePercentage) {
            this.goodRatePercentage = goodRatePercentage;
        }

        public List<EvaluatesBean> getEvaluates() {
            return evaluates;
        }

        public void setEvaluates(List<EvaluatesBean> evaluates) {
            this.evaluates = evaluates;
        }

        public static class EvaluatesBean implements Serializable {
            /**
             * title : 宝贝描述
             * score : 4.7
             * type : desc
             * level : 0
             */

            private String title;
            private String score;
            private String type;
            private String level;

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getScore() {
                return score;
            }

            public void setScore(String score) {
                this.score = score;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }
        }
    }

    public static class SkuBaseBean implements Serializable {
        private List<SkusBean> skus;
        private List<PropsBeanX> props;

        public List<SkusBean> getSkus() {
            return skus;
        }

        public void setSkus(List<SkusBean> skus) {
            this.skus = skus;
        }

        public List<PropsBeanX> getProps() {
            return props;
        }

        public void setProps(List<PropsBeanX> props) {
            this.props = props;
        }

        public static class SkusBean implements Serializable {
            /**
             * skuId : 3273201416609
             * propPath : 1627207:3232484
             */

            private String skuId;
            private String propPath;

            public String getSkuId() {
                return skuId;
            }

            public void setSkuId(String skuId) {
                this.skuId = skuId;
            }

            public String getPropPath() {
                return propPath;
            }

            public void setPropPath(String propPath) {
                this.propPath = propPath;
            }
        }

        public static class PropsBeanX implements Serializable {
            /**
             * pid : 1627207
             * name : 颜色分类
             * values : [{"vid":"3232484","name":"天蓝色","image":"//img.alicdn.com/imgextra/i1/2100936062/TB2rzfmbS8mpuFjSZFMXXaxpVXa_!!2100936062.jpg"},{"vid":"3455405","name":"青色","image":"//img.alicdn.com/imgextra/i1/2100936062/TB2eaDlbNBmpuFjSZFDXXXD8pXa_!!2100936062.jpg"},{"vid":"30155","name":"杏色","image":"//img.alicdn.com/imgextra/i2/2100936062/TB23EmLbQqvpuFjSZFhXXaOgXXa_!!2100936062.jpg"},{"vid":"3232480","name":"粉红色","image":"//img.alicdn.com/imgextra/i3/2100936062/TB2NgnobHBmpuFjSZFAXXaQ0pXa_!!2100936062.jpg"}]
             */

            private String pid;
            private String name;
            private List<ValuesBean> values;

            public String getPid() {
                return pid;
            }

            public void setPid(String pid) {
                this.pid = pid;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<ValuesBean> getValues() {
                return values;
            }

            public void setValues(List<ValuesBean> values) {
                this.values = values;
            }

            public static class ValuesBean implements Serializable {
                /**
                 * vid : 3232484
                 * name : 天蓝色
                 * image : //img.alicdn.com/imgextra/i1/2100936062/TB2rzfmbS8mpuFjSZFMXXaxpVXa_!!2100936062.jpg
                 */

                private String vid;
                private String name;
                private String image;

                public String getVid() {
                    return vid;
                }

                public void setVid(String vid) {
                    this.vid = vid;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public String getImage() {
                    return image;
                }

                public void setImage(String image) {
                    this.image = image;
                }
            }
        }
    }


    public static class Trade implements Serializable {
        private String redirectUrl;
        private String buyEnable;
        private String cartEnable;


        public String getBuyEnable() {
            return buyEnable;
        }

        public void setBuyEnable(String buyEnable) {
            this.buyEnable = buyEnable;
        }

        public String getCartEnable() {
            return cartEnable;
        }

        public void setCartEnable(String cartEnable) {
            this.cartEnable = cartEnable;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }


    }

    public static class VerticalBean implements Serializable {
        /**
         * jyj : {"logoText":"【优质网店】2年好店 权威认证"}
         * askAll : {"askText":"可以装多少克？","askIcon":"https://gw.alicdn.com/tps/TB1J7X6KXXXXXc4XXXXXXXXXXXX-102-60.png","answerText":"那种一瓶农夫山泉的水，这个瓶要装两瓶","answerIcon":"https://img.alicdn.com/tps/TB1Z7c2LXXXXXXmaXXXXXXXXXXX-132-42.png","linkUrl":"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&itemId=543714768771","title":"问大家(24)","questNum":"24","modelList":[{"askText":"可以装多少克？","answerCountText":"5个回答","firstAnswer":"那种一瓶农夫山泉的水，这个瓶要装两瓶"},{"askText":"能不能装热水刚开的那种会不会炸掉？？？","answerCountText":"8个回答"}]}
         */

        private JyjBean jyj;
        private AskAllBeanX askAll;

        public JyjBean getJyj() {
            return jyj;
        }

        public void setJyj(JyjBean jyj) {
            this.jyj = jyj;
        }

        public AskAllBeanX getAskAll() {
            return askAll;
        }

        public void setAskAll(AskAllBeanX askAll) {
            this.askAll = askAll;
        }

        public static class JyjBean implements Serializable {
            /**
             * logoText : 【优质网店】2年好店 权威认证
             */

            private String logoText;

            public String getLogoText() {
                return logoText;
            }

            public void setLogoText(String logoText) {
                this.logoText = logoText;
            }
        }

        public static class AskAllBeanX implements Serializable {
            /**
             * askText : 可以装多少克？
             * askIcon : https://gw.alicdn.com/tps/TB1J7X6KXXXXXc4XXXXXXXXXXXX-102-60.png
             * answerText : 那种一瓶农夫山泉的水，这个瓶要装两瓶
             * answerIcon : https://img.alicdn.com/tps/TB1Z7c2LXXXXXXmaXXXXXXXXXXX-132-42.png
             * linkUrl : //h5.m.taobao.com/wendajia/question.htm?wdjType=1&itemId=543714768771
             * title : 问大家(24)
             * questNum : 24
             * modelList : [{"askText":"可以装多少克？","answerCountText":"5个回答","firstAnswer":"那种一瓶农夫山泉的水，这个瓶要装两瓶"},{"askText":"能不能装热水刚开的那种会不会炸掉？？？","answerCountText":"8个回答"}]
             */

            private String askText;
            private String askIcon;
            private String answerText;
            private String answerIcon;
            private String linkUrl;
            private String title;
            private String questNum;
            private List<ModelListBean> modelList;

            public String getAskText() {
                return askText;
            }

            public void setAskText(String askText) {
                this.askText = askText;
            }

            public String getAskIcon() {
                return askIcon;
            }

            public void setAskIcon(String askIcon) {
                this.askIcon = askIcon;
            }

            public String getAnswerText() {
                return answerText;
            }

            public void setAnswerText(String answerText) {
                this.answerText = answerText;
            }

            public String getAnswerIcon() {
                return answerIcon;
            }

            public void setAnswerIcon(String answerIcon) {
                this.answerIcon = answerIcon;
            }

            public String getLinkUrl() {
                return linkUrl;
            }

            public void setLinkUrl(String linkUrl) {
                this.linkUrl = linkUrl;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getQuestNum() {
                return questNum;
            }

            public void setQuestNum(String questNum) {
                this.questNum = questNum;
            }

            public List<ModelListBean> getModelList() {
                return modelList;
            }

            public void setModelList(List<ModelListBean> modelList) {
                this.modelList = modelList;
            }

            public static class ModelListBean implements Serializable {
                /**
                 * askText : 可以装多少克？
                 * answerCountText : 5个回答
                 * firstAnswer : 那种一瓶农夫山泉的水，这个瓶要装两瓶
                 */

                private String askText;
                private String answerCountText;
                private String firstAnswer;

                public String getAskText() {
                    return askText;
                }

                public void setAskText(String askText) {
                    this.askText = askText;
                }

                public String getAnswerCountText() {
                    return answerCountText;
                }

                public void setAnswerCountText(String answerCountText) {
                    this.answerCountText = answerCountText;
                }

                public String getFirstAnswer() {
                    return firstAnswer;
                }

                public void setFirstAnswer(String firstAnswer) {
                    this.firstAnswer = firstAnswer;
                }
            }
        }
    }

    public static class ApiStackBean implements Serializable {
        /**
         * name : esi
         * value : {"delivery":{"from":"广东广州","to":"浙江杭州","areaId":"330100","postage":"快递: 免运费","extras":{}},"item":{"showShopActivitySize":"2","sellCount":"105","skuText":"请选择 颜色分类 "},"resource":{"share":{"name":"分享","iconType":"1"},"bigPromotion":{},"entrances":{}},"consumerProtection":{"items":[{"title":"20小时内发货"},{"title":"15天退货","desc":"15天退货，退货邮费买家承担"},{"title":"订单险","desc":"保险公司全程担保赔付该商品每笔交易的售后"},{"title":"1次破损补寄","desc":"商品在运输途中出现破损的，消费者可向卖家提出补寄申请，可补寄1次，补寄邮费由卖家承担"},{"title":"运费险"},{"title":"蚂蚁花呗"},{"title":"信用卡支付"},{"title":"集分宝"}],"channel":{"logo":"https://gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png","title":"一站购齐 权威认证 买退无忧"},"passValue":"all"},"skuCore":{"sku2info":{"0":{"price":{"priceMoney":"1980","priceText":"19.8","priceTitle":"收藏送杯刷","type":"1"},"quantity":"306"},"3273201416612":{"price":{"priceMoney":"1980","priceText":"19.8","type":"1"},"quantity":"306"}},"skuItem":{"location":"浙江杭州"}},"tradeConsumerProtection":{"tradeConsumerService":{"service":{"icon":"//gw.alicdn.com/tfs/TB1SvRnQXXXXXb3aXXXXXXXXXXX-140-42.png","title":"一站购齐 权威认真 买退无忧","items":[{"title":"20小时内发货"},{"icon":"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png","title":"15天退货","desc":"15天退货，退货邮费买家承担"},{"icon":"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png","title":"订单险","desc":"保险公司全程担保赔付该商品每笔交易的售后"},{"icon":"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png","title":"1次破损补寄","desc":"商品在运输途中出现破损的，消费者可向卖家提出补寄申请，可补寄1次，补寄邮费由卖家承担"},{"icon":"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png","title":"运费险"}]},"nonService":{"title":"其他","items":[{"icon":"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png","title":"蚂蚁花呗"},{"icon":"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png","title":"信用卡支付"},{"icon":"//gw.alicdn.com/tfs/TB1O4sFQpXXXXb3apXXXXXXXXXX-200-200.png","title":"集分宝"}]}},"channel":{"logo":"https://gtms02.alicdn.com/tps/i2/TB1NoTeJVXXXXaRXpXXhUknFXXX-140-42.png","title":"一站购齐 权威认证 买退无忧"},"passValue":"all","url":"https://h5.m.taobao.com/app/detailsubpage/consumer/index.js","type":"0"},"vertical":{"askAll":{"askText":"可以装多少克？","askIcon":"https://img.alicdn.com/tps/TB1tVU6PpXXXXXFaXXXXXXXXXXX-102-60.png","answerText":"那种一瓶农夫山泉的水，这个瓶要装两瓶","answerIcon":"https://img.alicdn.com/tps/TB1Z7c2LXXXXXXmaXXXXXXXXXXX-132-42.png","linkUrl":"//h5.m.taobao.com/wendajia/question.htm?wdjType=1&spm=w-a2141.7631564&itemId=543714768771","title":"问大家(24)","questNum":"24","showNum":"1","modelList":[{"askText":"可以装多少克？","answerCountText":"5个回答","firstAnswer":"那种一瓶农夫山泉的水，这个瓶要装两瓶"},{"askText":"能不能装热水刚开的那种会不会炸掉？？？","answerCountText":"8个回答"}]}},"params":{"trackParams":{"layoutId":null}},"layout":{},"trade":{"buyEnable":"true","cartEnable":"true","buyParam":{},"cartParam":{},"hintBanner":{}},"feature":{"cainiaoNoramal":"true","hasSku":"true","showSku":"true","superActTime":"false"},"price":{"price":{"priceMoney":"1980","priceText":"19.8","type":"1"},"extraPrices":[{"priceMoney":"3300","priceText":"33","priceTitle":"价格","type":"2","lineThrough":"true"}],"priceTag":[{"text":"收藏送杯刷"},{"text":"淘金币抵2%","bgColor":"#ff9204"}]},"skuVertical":{}}
         */

        private String name;
        private String value;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
    // }
}
