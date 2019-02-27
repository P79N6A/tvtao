package com.tvtaobao.voicesdk.bo;

import com.tvtaobao.voicesdk.utils.JSONUtil;

import org.json.JSONObject;

/**
 * Created by pan on 2017/9/19.
 */

public class JinnangDo {
    private String name;
    private String content;
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static JinnangDo resolverData(JSONObject object) {
        JinnangDo mDo = new JinnangDo();
        mDo.setName(JSONUtil.getString(object, "name"));
        mDo.setContent(JSONUtil.getString(object, "content"));
        mDo.setType(Integer.parseInt(JSONUtil.getString(object, "type")));
        return mDo;
    }
}
