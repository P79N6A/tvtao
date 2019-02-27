package com.tvtaobao.voicesdk.register.bo;

import android.os.Parcel;
import android.os.Parcelable;

import com.tvtaobao.voicesdk.register.type.RegisterType;
import com.yunos.tv.core.CoreApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/19
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class Register implements Parcelable {
    public String className;
    public String resgistedType = RegisterType.ADD;
    private String packageName;
    public String bizType;
    public String[] registedArray;
    private JSONArray dataArray;
    private ConcurrentHashMap<String, String> registedMap = new ConcurrentHashMap();


    public Register() {
        packageName = CoreApplication.getApplication().getPackageName();
    }

    public ConcurrentHashMap<String, String> getRegistedMap() {
        return registedMap;
    }

    public void setRegistedMap(ConcurrentHashMap<String, String> registedMap) {
        this.registedMap = registedMap;
        dataArray = new JSONArray();

        for (Map.Entry<String, String> entry : registedMap.entrySet()) {
            try {
                JSONObject object = new JSONObject();
                JSONArray key = new JSONArray();
                key.put(entry.getKey());
                object.put("key", key.toString());
                object.put("value", entry.getValue());
                dataArray.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static final Creator<Register> CREATOR = new Creator<Register>() {
        @Override
        public Register createFromParcel(Parcel in) {
            return new Register(in);
        }

        @Override
        public Register[] newArray(int size) {
            return new Register[size];
        }
    };

    protected Register(Parcel in) {
        packageName = in.readString();
        className = in.readString();
        resgistedType = in.readString();
        registedArray = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(packageName);
        parcel.writeString(className);
        parcel.writeString(resgistedType);
        parcel.writeStringArray(registedArray);
    }

    /**
     * 语点注册接口请求的params数据
     *
     * @return
     */
    public String getParams() {
        JSONObject object = new JSONObject();
        try {
            object.put("bizType", bizType);
            object.put("data", dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}
