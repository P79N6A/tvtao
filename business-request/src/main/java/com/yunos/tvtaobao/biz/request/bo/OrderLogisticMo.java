package com.yunos.tvtaobao.biz.request.bo;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 订单物流结构
 * @author zhuli.zhul
 * @date 2013 2013-6-26 下午5:26:54
 */
public class OrderLogisticMo implements Serializable {

    /** */
    private static final long              serialVersionUID = -7613669091555822045L;

    /** 物流号 */
    private String                         logisticNo;

    /** 物流流水 */
    private ArrayList<OrderLogisticInfoMo> transitList;

    /** 公司名称 */
    private String                         partnerName;

    /** 物流类型 */
    private String                         logisType;

    /** maill */
    private String                         mailNo;

    public String getLogisticNo() {
        return logisticNo;
    }

    public void setLogisticNo(String logisticNo) {
        this.logisticNo = logisticNo;
    }

    public ArrayList<OrderLogisticInfoMo> getTransitList() {
        return transitList;
    }

    public void setTransitList(ArrayList<OrderLogisticInfoMo> transitList) {
        this.transitList = transitList;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getLogisType() {
        return logisType;
    }

    public void setLogisType(String logisType) {
        this.logisType = logisType;
    }

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public static OrderLogisticMo resolveFromMTOP(JSONObject obj) throws JSONException {
        if (obj == null) {
            return null;
        }
        OrderLogisticMo orderLogisticMo = new OrderLogisticMo();
        ArrayList<OrderLogisticInfoMo> orderLogisticInfoList = new ArrayList<OrderLogisticInfoMo>();
        if (!obj.isNull("orderList")) {

            JSONArray orderListJsonArray = obj.getJSONArray("orderList");
            JSONObject orderList = (JSONObject) orderListJsonArray.get(0);
            if (!orderList.isNull("logisticNo")) {
                orderLogisticMo.setLogisticNo(orderList.getString("logisticNo"));
            }
            if (!orderList.isNull("bagList")) {
                JSONArray bagListArray = (JSONArray) orderList.getJSONArray("bagList");
                JSONObject bagList = (JSONObject) bagListArray.get(0);
                if (!bagList.isNull("logisType")) {
                    orderLogisticMo.setLogisType(bagList.getString("logisType"));
                }
                if (!bagList.isNull("partnerName")) {
                    orderLogisticMo.setPartnerName(bagList.getString("partnerName"));
                }
                if (!bagList.isNull("mailNo")) {
                    orderLogisticMo.setMailNo(bagList.getString("mailNo"));
                }
                if (!bagList.isNull("transitList")) {
                    JSONArray transitListArray = bagList.getJSONArray("transitList");
                    for (int i = 0; i < transitListArray.length(); i++) {
                        JSONObject temp = transitListArray.getJSONObject(i);
                        OrderLogisticInfoMo tempMo = new OrderLogisticInfoMo();
                        if (!temp.isNull("message")) {
                            tempMo.setMessage(temp.getString("message"));
                        }
                        if (!temp.isNull("time")) {
                            tempMo.setTime(temp.getString("time"));
                            orderLogisticInfoList.add(tempMo);
                        }
                        
                    }
                    orderLogisticMo.setTransitList(orderLogisticInfoList);
                }
            }

        }
        return orderLogisticMo;
    }

    @Override
    public String toString() {
        return "OrderLogisticMo [logisticNo=" + logisticNo + ", transitList=" + transitList + ", partnerName=" + partnerName + ", logisType="
                + logisType + ", mailNo=" + mailNo + "]";
    }

}
