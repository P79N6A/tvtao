package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by linmu on 2018/9/10.
 */


public class FavModule {

    private FavoriteCnt favoriteCnt;
    private FootprintCnt footprintCnt;
    private FollowCnt followCnt;
    private CardPackageCnt cardPackageCnt;
    public void setFavoriteCnt(FavoriteCnt favoriteCnt) {
        this.favoriteCnt = favoriteCnt;
    }
    public FavoriteCnt getFavoriteCnt() {
        return favoriteCnt;
    }

    public void setFootprintCnt(FootprintCnt footprintCnt) {
        this.footprintCnt = footprintCnt;
    }
    public FootprintCnt getFootprintCnt() {
        return footprintCnt;
    }

    public void setFollowCnt(FollowCnt followCnt) {
        this.followCnt = followCnt;
    }
    public FollowCnt getFollowCnt() {
        return followCnt;
    }

    public void setCardPackageCnt(CardPackageCnt cardPackageCnt) {
        this.cardPackageCnt = cardPackageCnt;
    }
    public CardPackageCnt getCardPackageCnt() {
        return cardPackageCnt;
    }

}
