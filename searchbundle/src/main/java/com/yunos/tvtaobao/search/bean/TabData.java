package com.yunos.tvtaobao.search.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : panbeixing
 *     time   : 2019/1/7
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class TabData {
    public List<ItemData> Datas;

    public TabData() {
        Datas = new ArrayList<>();
    }

    public void put(String content, boolean focused) {
        ItemData itemData = new ItemData();
        itemData.content = content;
        itemData.hasFocus = focused;
        Datas.add(itemData);
    }

    public class ItemData {
        private String content;
        private boolean hasFocus;

        public String getContent() {
            return content;
        }

        public boolean hasFocus() {
            return hasFocus;
        }
    }
}
