package com.yunos.tvtaobao.tvlive.tvtaomsg.utility;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tvlive.tvtaomsg.po.Protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 编解码
 * Created by zhiping on 16/12/21.
 */
public class ByteCodec {

    //解码
    public static Protocol decode(byte[] brr) {

        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(brr);

            Protocol p = new Protocol();
            p.setBizType(bin.read());
            p.setVersion(bin.read());
            p.setLength(bin.read());
            byte[] data = new byte[p.getLength()];
            bin.read(data,0,data.length);

            p.setData(data);
            return p;
        } catch (Exception e) {
            AppDebug.e("TvTaobaoReceviceService", "TvTaobaoReceviceService.ByteCodec.decode e : " + e);
            return null;
        }
    }

    //编码
    public static byte[] encode(int bizType,int version,byte[] obj)throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        setHead(bizType, version, obj, bout);

        bout.write(obj);

        return bout.toByteArray();
    }

    //设置头
    private static void setHead(int bizType, int version, byte[] obj, ByteArrayOutputStream bout) {
        bout.write(bizType);
        bout.write(version);
        bout.write(obj.length);
    }

    public static void main(String[] args) throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("name","张有良");
        String str = JSON.toJSONStringWithDateFormat(obj, "yyyy-MM-dd HH:mm:ss");
        byte[] brr = str.getBytes("UTF-8");
        System.out.println(brr.length);

        brr = encode(2,1,brr);
        System.out.println(brr.length);

        Protocol p = decode(brr);

        System.out.println("------------------");
        System.out.println(p.getBizType());
        System.out.println(p.getVersion());
        System.out.println(p.getLength());
        System.out.println(p.getData());

        str = new String(p.getData(),"UTF-8");
        System.out.println(str);

        obj = JSON.parseObject(str);
        System.out.println(obj);
    }
}
