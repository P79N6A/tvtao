package com.tvtaobao.voicesdk.bo;

import java.util.List;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/01/24
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class Products {
    private String keyword;
    private List<ProductDo> products;
    private List<JinnangDo> jinnangs;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<ProductDo> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDo> products) {
        this.products = products;
    }

    public List<JinnangDo> getJinnangs() {
        return jinnangs;
    }

    public void setJinnangs(List<JinnangDo> jinnangs) {
        this.jinnangs = jinnangs;
    }
}
