package com.yunos.tvtaobao.biz.request.bo;

import java.util.List;

/**
 * Created by linmu on 2018/9/8.
 */

public class CollectionsInfo {

        private String categoryTips;
        private List<NewCollection> favList;
        private PageInfo pageInfo;


        public void setCategoryTips(String categoryTips) {
            this.categoryTips = categoryTips;
        }
        public String getCategoryTips() {
            return categoryTips;
        }

        public void setFavList(List<NewCollection> favList) {
            this.favList = favList;
        }
        public List<NewCollection> getFavList() {
            return favList;
        }


        public void setPageInfo(PageInfo pageInfo) {
            this.pageInfo = pageInfo;
        }
        public PageInfo getPageInfo() {
            return pageInfo;
        }



    }
