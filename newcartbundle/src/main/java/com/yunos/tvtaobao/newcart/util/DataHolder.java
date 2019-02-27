package com.yunos.tvtaobao.newcart.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanqihui on 2018/7/17.
 */

public class DataHolder {
    private static DataHolder instance;

    public static DataHolder getInstance() {
        if (instance == null) {
            synchronized (DataHolder.class) {
                if (instance == null) {
                    instance = new DataHolder();
                }
            }
        }
        return instance;
    }

    private Map<String, Object> map = new HashMap<>();

    /**
     * 数据存储
     *
     * @param id
     * @param object
     */
    public void saveData(String id, Object object) {
        map.put(id, object);
    }

    /**
     * 获取数据
     *
     * @param id
     * @return
     */
    public Object getData(String id) {
        if (map != null) {
//            WeakReference<Object> weakReference = map.get(id);
//            if(weakReference!=null) {
//                return weakReference.get();
//            }
            return map.get(id);
        }
        return null;
    }

    /**
     * 删除数据
     *
     * @param id
     * @return
     */
    public void clearData(String id) {
        if (map != null) {
//            WeakReference<Object> weakReference = map.get(id);
//            if(weakReference!=null) {
//                weakReference.clear();
//            }
            map.remove(id);
        }
    }
}
