/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.bo
 * FILE NAME: DialogParams.java
 * CREATED TIME: 2015-1-7
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo;


import android.content.Context;
import android.content.DialogInterface;

/**
 * dialog参数组装
 * @version
 * @author hanqi
 * @data 2015-1-7 下午9:27:00
 */
public class DialogParams {

    private Context context;
    private String msg;
    private Integer msgResId;
    private String positiveButtonText;
    private Integer positiveButtonTextResId;
    private DialogInterface.OnClickListener positiveButtonListener;
    private String negativeButtonText;
    private Integer negativeButtonTextResId;
    private DialogInterface.OnClickListener negativeButtonListener;
    private DialogInterface.OnKeyListener onKeyListener;
    private boolean cancelable = true;
    //positiveButton点击之后是否自动消失
    private boolean positiveButtonClickDismiss = true;
    //negativeButton点击之后是否自动消失
    private boolean negativeButtonClickDismiss = true;

    public static DialogParams makeParams(Context context) {
        DialogParams params = new DialogParams(context);
        return params;
    }

    public DialogParams(Context context) {
        this.context = context;
    }

    /**
     * @return the context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public DialogParams setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * @return the msgResId
     */
    public Integer getMsgResId() {
        return msgResId;
    }

    /**
     * @param msgResId the msgResId to set
     */
    public DialogParams setMsgResId(Integer msgResId) {
        this.msgResId = msgResId;
        return this;
    }

    /**
     * @return the positiveButtonText
     */
    public String getPositiveButtonText() {
        return positiveButtonText;
    }

    /**
     * @param positiveButtonText the positiveButtonText to set
     */
    public DialogParams setPositiveButtonText(String positiveButtonText) {
        this.positiveButtonText = positiveButtonText;
        return this;
    }

    /**
     * @return the positiveButtonTextResId
     */
    public Integer getPositiveButtonTextResId() {
        return positiveButtonTextResId;
    }

    /**
     * @param positiveButtonTextResId the positiveButtonTextResId to set
     */
    public DialogParams setPositiveButtonTextResId(Integer positiveButtonTextResId) {
        this.positiveButtonTextResId = positiveButtonTextResId;
        return this;
    }

    /**
     * @return the positiveButtonListener
     */
    public DialogInterface.OnClickListener getPositiveButtonListener() {
        return positiveButtonListener;
    }

    /**
     * @param positiveButtonListener the positiveButtonListener to set
     */
    public DialogParams setPositiveButtonListener(DialogInterface.OnClickListener positiveButtonListener) {
        this.positiveButtonListener = positiveButtonListener;
        return this;
    }

    /**
     * @return the negativeButtonText
     */
    public String getNegativeButtonText() {
        return negativeButtonText;
    }

    /**
     * @param negativeButtonText the negativeButtonText to set
     */
    public DialogParams setNegativeButtonText(String negativeButtonText) {
        this.negativeButtonText = negativeButtonText;
        return this;
    }

    /**
     * @return the negativeButtonTextResId
     */
    public Integer getNegativeButtonTextResId() {
        return negativeButtonTextResId;
    }

    /**
     * @param negativeButtonTextResId the negativeButtonTextResId to set
     */
    public DialogParams setNegativeButtonTextResId(Integer negativeButtonTextResId) {
        this.negativeButtonTextResId = negativeButtonTextResId;
        return this;
    }

    /**
     * @return the negativeButtonListener
     */
    public DialogInterface.OnClickListener getNegativeButtonListener() {
        return negativeButtonListener;
    }

    /**
     * @param negativeButtonListener the negativeButtonListener to set
     */
    public DialogParams setNegativeButtonListener(DialogInterface.OnClickListener negativeButtonListener) {
        this.negativeButtonListener = negativeButtonListener;
        return this;
    }

    /**
     * @return the onKeyListener
     */
    public DialogInterface.OnKeyListener getOnKeyListener() {
        return onKeyListener;
    }

    /**
     * @param onKeyListener the onKeyListener to set
     */
    public DialogParams setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.onKeyListener = onKeyListener;
        return this;
    }

    /**
     * @return the cancelable
     */
    public Boolean getCancelable() {
        return cancelable;
    }

    /**
     * @param cancelable the cancelable to set
     */
    public DialogParams setCancelable(Boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    /**
     * @return the positiveButtonClickDismiss
     */
    public boolean isPositiveButtonClickDismiss() {
        return positiveButtonClickDismiss;
    }

    /**
     * @param positiveButtonClickDismiss the positiveButtonClickDismiss to set
     */
    public DialogParams setPositiveButtonClickDismiss(boolean positiveButtonClickDismiss) {
        this.positiveButtonClickDismiss = positiveButtonClickDismiss;
        return this;
    }

    /**
     * @return the negativeButtonClickDismiss
     */
    public boolean isNegativeButtonClickDismiss() {
        return negativeButtonClickDismiss;
    }

    /**
     * @param negativeButtonClickDismiss the negativeButtonClickDismiss to set
     */
    public DialogParams setNegativeButtonClickDismiss(boolean negativeButtonClickDismiss) {
        this.negativeButtonClickDismiss = negativeButtonClickDismiss;
        return this;
    }
}
