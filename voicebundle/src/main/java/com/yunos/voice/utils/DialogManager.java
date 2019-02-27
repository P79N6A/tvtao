package com.yunos.voice.utils;

import android.app.Dialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pan on 2017/11/7.
 */

public class DialogManager {

    private List<WeakReference<Dialog>> mDialogList;
    private static DialogManager manager;
    private DialogManager() {
        mDialogList = new ArrayList<>();
        mDialogList.clear();
    }

    public static DialogManager getManager() {
        if (manager == null) {
            synchronized (DialogManager.class) {
                if (manager == null)
                    manager = new DialogManager();
            }
        }

        return manager;
    }

    /**
     *
     */
    public void onClearAllDialogList() {
        mDialogList.clear();
    }

    /**
     * 隐藏所有dialog，并清理list
     */
    public void dismissAllDialog() {
        int size = mDialogList.size();
        for (int i = 0 ; i < size ; i++) {
            if (mDialogList.get(i) != null && mDialogList.get(i).get() != null) {
                if (mDialogList.get(i).get().isShowing()) {
                    mDialogList.get(i).get().dismiss();
                    mDialogList.remove(i);
                }
            }
        }
        onClearAllDialogList();
    }

    /**
     * 隐藏除当前dialog之外的，前面所有的dialog
     */
    public void dismissBeforeDialog() {
        int size = mDialogList.size() - 1;
        for (int i = 0 ; i < size ; i++) {
            if (mDialogList.get(i) != null && mDialogList.get(i).get() != null) {
                if (mDialogList.get(i).get().isShowing()) {
                    mDialogList.get(i).get().dismiss();
                    mDialogList.remove(i);
                }
            }
        }
    }

    public void pushDialog(Dialog dialog) {
        mDialogList.add(new WeakReference<>(dialog));
    }
}
