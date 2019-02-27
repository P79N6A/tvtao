package com.yunos.tvtaobao.biz.request.bo;


import java.io.Serializable;
import java.util.ArrayList;

public class TopicsEntity implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 7812436683558501952L;

    private TopicsEntityLayout layout;

    private TopicsEntityImage images;

    private ArrayList<TopicsEntityItem> items;

    public TopicsEntityLayout getLayout() {
        return layout;
    }

    public void setLayout(TopicsEntityLayout layout) {
        this.layout = layout;
    }

    public TopicsEntityImage getImages() {
        return images;
    }

    public void setImages(TopicsEntityImage images) {
        this.images = images;
    }

    public ArrayList<TopicsEntityItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<TopicsEntityItem> items) {
        this.items = items;
    }

}
