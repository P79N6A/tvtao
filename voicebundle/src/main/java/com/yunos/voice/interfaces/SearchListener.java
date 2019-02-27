package com.yunos.voice.interfaces;

import com.tvtaobao.voicesdk.bo.JinnangDo;
import com.tvtaobao.voicesdk.bo.ProductDo;

import java.util.List;

/**
 * Created by pan on 2017/9/19.
 */

public interface SearchListener {

    void onSearchResult(String keyword, List<ProductDo> products, List<JinnangDo> jinnangs);
}
