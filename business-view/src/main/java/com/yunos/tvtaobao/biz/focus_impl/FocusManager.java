package com.yunos.tvtaobao.biz.focus_impl;

import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;

import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GuoLiDong on 2018/10/12.
 * 焦点管理器，链接上下文 - 焦点处理框架
 * 接入按键事件，然后进行焦点寻址流程，然后进行聚焦流程
 */

public class FocusManager {
    private static String TAG = FocusManager.class.getSimpleName();


    private List<Runnable> cachedTask = new ArrayList<>();
    private FocusContext focusContext;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean enable = false;
    private FocusRoot focusRoot;

    private FocusManager() {
    }

    private FocusManager(FocusContext focusContext) {
        this.focusContext = focusContext;
    }

    public static FocusManager create(FocusContext focusContext) {
        return new FocusManager(focusContext);
    }

    public boolean isEnable() {
        return enable;
    }

    public void enable(boolean enable) {
        this.enable = enable;
        if (focusContext != null && focusContext.getAttachedWindow() != null) {
            focusRoot = (FocusRoot) focusContext.getAttachedWindow().getDecorView().findViewWithTag(FocusRoot.TAG_FLAG_FOR_ROOT);
        }
    }

    public void destroy() {
        focusContext = null;
        mainHandler = null;
        focusRoot = null;
        cachedTask = null;
    }

    public void onKeyEvent(final int keyCode, final KeyEvent event) {
        if (!enable) {
            return;
        }
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            runInWorkThread(new Runnable() {
                @Override
                public void run() {
                    move(keyCode, event);
                }
            });
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            runInWorkThread(new Runnable() {
                @Override
                public void run() {
                    move(keyCode, event);
                }
            });
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            runInWorkThread(new Runnable() {
                @Override
                public void run() {
                    move(keyCode, event);
                }
            });
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            runInWorkThread(new Runnable() {
                @Override
                public void run() {
                    move(keyCode, event);
                }
            });
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_ENTER) {
            runInWorkThread(new Runnable() {
                @Override
                public void run() {
                    sure();
                }
            });
        }
    }

    /**
     * 聚焦在一个节点上
     *
     * @param next
     */
    public void focusOn(final FocusNode next) {
        runInWorkThread(new Runnable() {
            @Override
            public void run() {
                if (focusRoot.getNode().getBuildCount()==0){
                    FocusUtils.rebuildTotalPath(focusRoot.getNode());
                }
                FocusNode candidateNode = next;
                if (FocusUtils.isLeafFocusNode(candidateNode)){
                    // 最好的情况
                } else {
                    candidateNode = candidateNode.findFittestLeaf();
                }
                final FocusNode leaf = candidateNode;
                runInActivityThread(new Runnable() {
                    @Override
                    public void run() {
                        long time = System.currentTimeMillis();
                        FocusNode old = focusRoot.getCurrFocusOn();
                        focusRoot.setCurrFocusOn(leaf);
                        FocusUtils.syncFocusState(leaf, old,null);
                        AppDebug.d(TAG, " focusOn() cost time : " + (System.currentTimeMillis() - time));
                    }
                });
            }
        });
    }

    private void move(int keyCode, KeyEvent event) {
        if (focusRoot.getCurrFocusOn()!=null){
            if (focusRoot.getCurrFocusOn().isNodeDoingSomething()){
                AppDebug.d(TAG, " move() cancel cause innerState doing "
                        + focusRoot.getCurrFocusOn().getInnerState());
                return;
            }
        }
        long time = System.currentTimeMillis();
        focusRoot.getNode().rebuildChildren();
        FocusFinder.Routine thisRoutine = new FocusFinder.Routine(time);
        if (focusRoot.getCurrFocusOn() != null) {
            FocusNode next = FocusUtils.findNext(thisRoutine, focusRoot.getCurrFocusOn(), keyCode, event);
            if (next != null) {
                focusOn(next);
            } else {
                // 没找到下一个处理节点，什么都不做
                AppDebug.d(TAG, " no nextInNowNode target ! ");
            }
        } else {
            if (focusRoot != null) {
                FocusNode next = focusRoot.getNode().findNext(thisRoutine, keyCode, event);
                if (next != null) {
                    focusRoot.setCurrFocusOn(next);
                    FocusUtils.syncFocusState(next, null,null);
                } else {
                    AppDebug.d(TAG, " no nextInNowNode target ! ");
                }
            } else {
                AppDebug.d(TAG, " no focusRoot !!! ");
            }
        }
        AppDebug.d(TAG, " move() cost time : " + (System.currentTimeMillis() - time));
    }

    private void sure() {
        if (focusRoot != null && focusRoot.getCurrFocusOn() != null) {
            runInActivityThread(new Runnable() {
                @Override
                public void run() {
                    FocusUtils.focusClickToLeaf(focusRoot.getNode());
                }
            });
        } else {
            AppDebug.d(TAG, " no currFocusOn for sure! ");
        }
    }


    private void runInWorkThread(final Runnable runnable) {
        if (mainHandler != null) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            synchronized (cachedTask) {
                cachedTask.add(runnable);
            }
        }
    }

    private void runInActivityThread(final Runnable runnable) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}