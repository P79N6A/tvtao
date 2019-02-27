package com.yunos.tvtaobao.biz.focus_impl;

import android.view.KeyEvent;

/**
 * Created by GuoLiDong on 2018/10/23.
 * 焦点寻址节点，能完成内部焦点寻址逻辑
 */
public interface FocusFinder {
    /**
     * 基于事件驱动的焦点寻址
     * @param routine
     * @param keyCode
     * @param event
     * @return
     */
    FocusNode findNext(Routine routine, int keyCode, KeyEvent event);

    /**
     * 基于已有节点状态的寻址
      * @return
     */
    FocusNode findFittestLeaf();

    class Routine {
        public long id = -1;
        private Routine(){}
        public Routine(long id) {
            this.id = id;
        }
        public static Routine getNew(){
            return new Routine(System.currentTimeMillis());
        }
    }
}
