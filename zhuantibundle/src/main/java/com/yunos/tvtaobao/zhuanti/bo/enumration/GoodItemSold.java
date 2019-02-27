package com.yunos.tvtaobao.zhuanti.bo.enumration;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenjiajuan on 17/4/22.
 */

public class GoodItemSold  implements Serializable {
    public List<ItemsArray> itemsArray;
    public static class ItemsArray{
        public String title;
        public String sold;
        public String item_id;

        @Override
        public String toString() {
            return "ItemsArray{" +
                    "title='" + title + '\'' +
                    ", sold='" + sold + '\'' +
                    ", item_id='" + item_id + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GoodItemSold{" +
                "itemsArray=" + itemsArray.toString()+
                '}';
    }
}
