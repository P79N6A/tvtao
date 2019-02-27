package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeCatesResponse extends BaseMO {

    private static final long serialVersionUID = -7587642820388329581L;
    private HomeBackgroundBo homeBackground;
    private ArrayList<HomeItemsBo> items;
    private ArrayList<HomeCatesBo> cates;

    public static HomeCatesResponse resolveFromMTOP(JSONObject obj) throws JSONException {
        if (null == obj) {
            return null;
        }
        HomeCatesResponse item = new HomeCatesResponse();
        item.setHomeBackground(HomeBackgroundBo.resolveFromMTOP(obj.optJSONObject("homeBackground")));
        if (obj.has("items")) {
            ArrayList<HomeItemsBo> list = new ArrayList<HomeItemsBo>();
            JSONArray array = obj.getJSONArray("items");
            for (int i = 0; i < array.length(); i++) {
                HomeItemsBo it = HomeItemsBo.resolveFromMTOP(array.getJSONObject(i));
                list.add(it);
            }
            item.setItems(list);
        }
        if (obj.has("cates")) {
            ArrayList<HomeCatesBo> list = new ArrayList<HomeCatesBo>();
            JSONArray array = obj.getJSONArray("cates");
            for (int i = 0; i < array.length(); i++) {
                HomeCatesBo it = HomeCatesBo.resolveFromMTOP(array.getJSONObject(i));
                list.add(it);
            }
            item.setCates(list);
        }
        return item;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HomeCatesResponse [homeBackground=" + homeBackground + ", items=" + items + ", cates=" + cates + "]";
    }

    /**
     * @return the homeBackground
     */
    public HomeBackgroundBo getHomeBackground() {
        return homeBackground;
    }

    /**
     * @param homeBackground the homeBackground to set
     */
    public void setHomeBackground(HomeBackgroundBo homeBackground) {
        this.homeBackground = homeBackground;
    }

    /**
     * @return the items
     */
    public ArrayList<HomeItemsBo> getItems() {
        return items;
    }

    /**
     * @param items the items to set
     */
    public void setItems(ArrayList<HomeItemsBo> items) {
        this.items = items;
    }

    /**
     * @return the cates
     */
    public ArrayList<HomeCatesBo> getCates() {
        return cates;
    }

    /**
     * @param cates the cates to set
     */
    public void setCates(ArrayList<HomeCatesBo> cates) {
        this.cates = cates;
    }

}
