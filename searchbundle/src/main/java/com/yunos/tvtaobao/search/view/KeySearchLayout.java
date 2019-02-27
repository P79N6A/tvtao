package com.yunos.tvtaobao.search.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.activity.KeySearchActivity;
import com.yunos.tvtaobao.search.widget.ImeFullView;

/**
 * <pre>
 *     author : panbeixing
 *     time   : 2018/12/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class KeySearchLayout extends ConstraintLayout {
    private final String TAG = "KeySearchLayout";

    public KeySearchLayout(Context context) {
        super(context);
    }

    public KeySearchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeySearchLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View focusSearch(int direction) {
        AppDebug.i(TAG, TAG + ".focusSearch direction : " + direction);
        View sView = super.focusSearch(direction);
        AppDebug.i(TAG, TAG + ".focusSearch sView : " + sView);
        return sView;
    }

    @Override
    public View focusSearch(View focused, int direction) {
        AppDebug.i(TAG, TAG + ".focusSearch focused : " + focused + " ,direction : " + direction);
        View sView = super.focusSearch(focused, direction);
        AppDebug.i(TAG, TAG + ".focusSearch sView : " + sView);
        if (sView != null) {
            if (direction == FOCUS_DOWN || direction == FOCUS_UP) {
                ViewParent curFocusParent = focused.getParent();
                if (curFocusParent instanceof RecyclerView) {
                    if (curFocusParent != sView) {
                        ViewGroup recyclerView = (ViewGroup) curFocusParent.getParent();
                        return FocusFinder.getInstance().findNextFocus(recyclerView, focused, direction);
                    }
                }
            }

            if (direction == FOCUS_RIGHT) {
                ViewParent viewParent = sView.getParent();
                AppDebug.i(TAG, TAG + ".focusSearch viewParent : " + viewParent);
                if (viewParent != null) {
                    if (viewParent instanceof CenterRecyclerView) {
                        CenterRecyclerView centerRecyclerView = (CenterRecyclerView) viewParent;
                        View nextView = centerRecyclerView.findNextFocused(focused, direction);
                        if (nextView != null) {
                            return nextView;
                        }
                    }

                    if (viewParent instanceof RecyclerView) {
                        RecyclerView recyclerView = (RecyclerView) viewParent;
                        int id = recyclerView.getId();
                        AppDebug.i(TAG, TAG + ".focusSearch id : " + id);
                        if (focused != null && focused.getParent() != null &&
                                focused.getParent() instanceof ImeFullView) {
                            if (id == R.id.recycler_search_history) {
                                AppDebug.i(TAG, TAG + ".focusSearch recycler_search_history ");
                                View nextView = recyclerView.getChildAt(0);
                                if (nextView != null) {
                                    return nextView;
                                }
                            } else if (id == R.id.recycler_search_discovery) {
                                RecyclerView historyView = (RecyclerView) findViewById(R.id.recycler_search_history);
                                if (historyView.getVisibility() == VISIBLE) {
                                    View nextView = historyView.getChildAt(0);
                                    if (nextView != null) {
                                        return nextView;
                                    }
                                }
                                View nextView = recyclerView.getChildAt(0);
                                if (nextView != null) {
                                    return nextView;
                                }
                            }
                        }

                        if (focused != null && focused.getParent() != null) {
                            ViewParent viewParentFocused = focused.getParent();
                            if (viewParentFocused instanceof RecyclerView) {
                                RecyclerView recyclerViewFocused = (RecyclerView) viewParentFocused;
                                int idFocused = recyclerViewFocused.getId();
                                if (idFocused != id) {
                                    return focused;
                                }
                            }
                        }

                    }

                }
                if (sView instanceof TextView) {
                    if (sView.getId() == R.id.tv_delete_search_history) {
                        return focused;
                    }
                }


            }

            if (direction == FOCUS_LEFT) {
                if (sView instanceof ImeFullView) {
                    ImeFullView imeFullView = (ImeFullView) sView;
                    View nextView = imeFullView.findLastFocused(focused, direction);
                    if (nextView != null) {
                        return nextView;
                    }
                }

                ViewParent viewParent = sView.getParent();
                AppDebug.i(TAG, TAG + ".focusSearch viewParent : " + viewParent);
                if (viewParent != null) {
                    if (viewParent instanceof ImeFullView) {
                        ImeFullView imeFullView = (ImeFullView) viewParent;
                        View nextView = imeFullView.findLastFocused(focused, direction);
                        if (nextView != null) {
                            return nextView;
                        }
                    }
                }
            }


////            当焦点在搜索发现的列表上，通过计算的方式去获取向上聚焦到历史搜索列表的item
//            if (direction == FOCUS_UP) {
//                ViewParent viewParentLast = focused.getParent();
//                if (viewParentLast instanceof RecyclerView) {
//                    RecyclerView recyclerView = (RecyclerView) viewParentLast;
//                    int id = recyclerView.getId();
//                    AppDebug.i(TAG, TAG + ".focusSearch id : " + id);
//                    if ((sView instanceof ImeFullView || sView.getParent() instanceof ImeFullView) && id == R.id.recycler_search_discovery) {
//                        RecyclerView historyView = (RecyclerView) findViewById(R.id.recycler_search_history);
//                        View nextView  = null;
//                        if (historyView.getVisibility() == VISIBLE) {
//                            int lastYDistence =0;
//                            int lastDistence = 0;
//                            Rect mFocusedRect = new Rect();
//                            //焦点Rect，该Rect是相对focused视图本身的
//                            focused.getFocusedRect(mFocusedRect);
//                            //将当前focused视图的坐标系，转换到root的坐标系中，统一坐标，以便进行下一步的计算
//                            this.offsetDescendantRectToMyCoords(focused, mFocusedRect);
//                            //得到整数的中心点x，y坐标
//                            int xFocused = mFocusedRect.centerX();
//                            int yFocused = mFocusedRect.centerY();
//                            AppDebug.i(TAG, TAG  +"focused    xFocused =  " + xFocused+ " yFocused =  " + yFocused);
//                            if (historyView.getChildCount() == 1) {
//                                nextView = historyView.getChildAt(0);
//                            } else {
//                                for (int i = historyView.getChildCount() - 1; i >= 0; i--) {
//                                    View childView = historyView.getChildAt(i);
//                                    childView.getFocusedRect(mFocusedRect);
//                                    this.offsetDescendantRectToMyCoords(childView, mFocusedRect);
//                                    int xNextView = mFocusedRect.centerX();
//                                    int yNextView = mFocusedRect.centerY();
//                                    AppDebug.i(TAG, TAG + i +"    xNextView =  " + xNextView+ " yNextView =  " + yNextView);
//                                    //计算Y的距离
//                                    int nowYDistence = yFocused - yNextView;
//                                    //计算两点之间的距离
//                                    int nowDistence = (int) Math.sqrt(Math.abs((xFocused - xNextView) * (xFocused - xNextView))
//                                            + Math.abs((yFocused - yNextView) * (yFocused - yNextView)));
//                                    AppDebug.i(TAG, TAG + i +"    nowYDistence =  " + nowYDistence+ " nowDistence =  " + nowDistence);
//                                    if (i == historyView.getChildCount() - 1 || nowYDistence < lastYDistence
//                                            || (nowYDistence <= lastYDistence && nowDistence < lastDistence)) {
//                                        AppDebug.i(TAG, TAG +"------------"+ i+"------------");
//
//                                        nextView = childView;
//                                        lastDistence = nowDistence;
//                                        lastYDistence = nowYDistence;
//                                    }
//                                }
//                            }
//                        }
//                        if (nextView != null) {
//                            return nextView;
//                        }
//                    }
//                }
//
//            }
        }
        return sView;
    }

    @Override
    public View findFocus() {
        AppDebug.i(TAG, TAG + ".findFocus " + super.findFocus());
//        View view = super.findFocus();
        if (KeySearchActivity.isClearHistory) {
            KeySearchActivity.isClearHistory = false;
            RecyclerView discoveryView = (RecyclerView) findViewById(R.id.recycler_search_discovery);
            if (discoveryView != null) {
                View nextView = discoveryView.getChildAt(0);
                if (nextView != null) {
                    nextView.requestFocus();
                    return nextView;
                }
            }
        }

        if (KeySearchActivity.clickDiscoveryPosition >= 0) {
            RecyclerView discoveryView = (RecyclerView) findViewById(R.id.recycler_search_discovery);
            if (discoveryView != null) {
                View nextView = discoveryView.getChildAt(KeySearchActivity.clickDiscoveryPosition);
                if (nextView != null) {
                    nextView.requestFocus();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            KeySearchActivity.clickDiscoveryPosition = -1;
                        }
                    }, 500);
                    return nextView;
                }
            }

        }
        return super.findFocus();
    }

    @Override
    public View getFocusedChild() {
        AppDebug.i(TAG, TAG + ".getFocusedChild " + super.getFocusedChild());
        return super.getFocusedChild();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        AppDebug.i(TAG, TAG + ".dispatchKeyEvent");
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        AppDebug.i(TAG, TAG + ".requestChildFocus child : " + child + " " + focused);
    }
}
