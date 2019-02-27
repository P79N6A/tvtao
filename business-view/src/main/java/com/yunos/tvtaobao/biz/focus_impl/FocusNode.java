package com.yunos.tvtaobao.biz.focus_impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tv.core.common.AppDebug;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by GuoLiDong on 2018/10/16.
 * 聚焦节点，汇集一些查找，聚焦回掉的功能
 */

public class FocusNode implements FocusConsumer, FocusFinder {
    public static boolean DEBUG = false;
    private static final String TAG = FocusNode.class.getSimpleName();
    private List<Pair<FocusNode, Rect>> focusChildren = new ArrayList<>();
    private List<WeakReference<FocusNode>> history = new ArrayList<>(4);
    private int historyIterator = 0;
    private FocusConsumer focusConsumer = null;
    private FocusFinder focusFinder = null;
    private FocusNode innerNode = null;
    private FocusNode parentNode = null;
    private Rect rectInParentNode = null;
    private Routine currRoutine;
    private FocusNode currRoutineResult;
    private RoutineCallBack routineCallBack;
    private NodeInnerState innerState = NodeInnerState.idle;
    private NodeOuterState nodeOuterState = null;
    private long priority = 0;
    private long buildCount = 0;
    private long focusBalance = 0;
    private long focusEnterCount = 0;
    private long focusLeaveCount = 0;
    private long focusClickCount = 0;
    private long focusDrawCount = 0;
    private boolean nodeFocusable = true;
    private boolean nodeHasFocus = false;
    private boolean isRoot = false;

    private Binder mBinder;

    Paint paint;

    private FocusNode() {
    }

    public FocusNode(Binder mBinder) {
        this.mBinder = mBinder;
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.GREEN);
        paint.setPathEffect(new DashPathEffect(new float[]{1, 4}, 0));
    }

    public long getBuildCount() {
        return buildCount;
    }

    private void invalidateAttachView() {
        if (mBinder != null && mBinder.getView() != null) {
            invalidateViewTree(mBinder.getView());
        }
    }

    private void invalidateViewTree(View view){
        if (view!=null){
            view.invalidate();
            if (view instanceof ViewGroup){
                for (int i=0;i<((ViewGroup) view).getChildCount();i++){
                    invalidateViewTree(((ViewGroup) view).getChildAt(i));
                }
            }
        }
    }

    @Override
    public boolean onFocusEnter() {
        innerState = NodeInnerState.focusEnterIng;
        nodeHasFocus = true;
        focusEnterCount++;
        focusBalance++;
        AppDebug.d(this.getClass().getSimpleName(), this.hashCode() + " : " + "onFocusEnter"
                + " focusEnterCount:" + focusEnterCount + " focusBalance:" + focusBalance);
        boolean rtn = false;
        try {
            rtn = false;
            if (focusConsumer != null) {
                rtn = focusConsumer.onFocusEnter();
            }
            invalidateAttachView();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        innerState = NodeInnerState.focusEnterDone;
        return rtn;
    }

    @Override
    public boolean onFocusLeave() {
        innerState = NodeInnerState.focusLeaveIng;
        nodeHasFocus = false;
        focusLeaveCount++;
        focusBalance = 0;
        AppDebug.d(this.getClass().getSimpleName(), this.hashCode() + " : " + "onFocusEnter"
                + " focusLeaveCount:" + focusLeaveCount + " focusBalance:" + focusBalance);
        boolean rtn = false;
        try {
            rtn = false;
            if (focusConsumer != null) {
                rtn = focusConsumer.onFocusLeave();
            }
            invalidateAttachView();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        innerState = NodeInnerState.focusLeaveDone;
        return rtn;
    }

    @Override
    public boolean onFocusClick() {
        innerState = NodeInnerState.focusLeaveIng;
        focusClickCount++;
        AppDebug.d(this.getClass().getSimpleName(), this.hashCode() + " : " + "onFocusEnter"
                + " focusClickCount:" + focusClickCount);
        boolean rtn = false;
        try {
            rtn = false;
            if (focusConsumer != null) {
                rtn = focusConsumer.onFocusClick();
            }
            invalidateAttachView();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        innerState = NodeInnerState.focusLeaveDone;
        return rtn;
    }

    @Override
    public boolean onFocusDraw(Canvas canvas) {
        focusDrawCount++;
        if (nodeFocusable && nodeHasFocus) {
            if (focusConsumer != null) {
                return focusConsumer.onFocusDraw(canvas);
            } else {
                if (DEBUG) {
                    canvas.drawRect(new Rect(0, 0
                            , mBinder.getView().getWidth()
                            , mBinder.getView().getHeight()), paint);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 计入历史
     *
     * @param focusNode
     */
    private void pushToHistory(FocusNode focusNode) {
        if (focusNode != null) {
            if (!history.isEmpty()
                    && history.get(history.size() - 1).get() == focusNode) {
                // 与历史最新条目相同的条目不计入历史
            } else {
                history.add(new WeakReference<FocusNode>(focusNode));
            }
            if (history.size() > 3) {
                history.remove(0);
            }
        }
    }

    /**
     * 获取历史记录中最新条目
     *
     * @return
     */
    public FocusNode historyFirst() {
        if (!history.isEmpty()) {
            historyIterator = history.size() - 1;
            FocusNode rtn = history.get(historyIterator).get();
            return rtn;
        }
        return null;
    }

    /**
     * 获取历史记录中其他条目
     *
     * @return
     */
    public FocusNode historyNext() {
        if (!history.isEmpty()) {
            historyIterator--;
            if (historyIterator < 0 || historyIterator > (history.size() - 1)) {
                return null;
            }
            FocusNode rtn = history.get(historyIterator).get();
            return rtn;
        }
        return null;
    }

    public long getFocusEnterCount() {
        return focusEnterCount;
    }

    public long getFocusLeaveCount() {
        return focusLeaveCount;
    }

    public long getFocusClickCount() {
        return focusClickCount;
    }

    public long getFocusDrawCount() {
        return focusDrawCount;
    }

    public long getFocusBalance() {
        return focusBalance;
    }

    public NodeInnerState getInnerState() {
        return innerState;
    }

    public NodeOuterState getNodeOuterState() {
        return nodeOuterState;
    }

    public void setNodeOuterState(NodeOuterState nodeOuterState) {
        this.nodeOuterState = nodeOuterState;
    }

    public FocusFinder getFocusFinder() {
        return focusFinder;
    }

    public void setFocusFinder(FocusFinder focusFinder) {
        this.focusFinder = focusFinder;
    }

    public FocusConsumer getFocusConsumer() {
        return focusConsumer;
    }

    public void setFocusConsumer(FocusConsumer focusConsumer) {
        this.focusConsumer = focusConsumer;
    }

    public FocusNode getInnerNode() {
        return innerNode;
    }

    public void setInnerNode(FocusNode innerNode) {
        this.innerNode = innerNode;
        pushToHistory(this.innerNode);
    }

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public FocusNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(FocusNode parentNode) {
        this.parentNode = parentNode;
    }

    public Rect getRectInParentNode() {
        return rectInParentNode;
    }

    public void setRectInParentNode(Rect rectInParentNode) {
        this.rectInParentNode = rectInParentNode;
    }

    public boolean isNodeFocusable() {
        return nodeFocusable;
    }

    public void setNodeFocusable(boolean nodeFocusable) {
        this.nodeFocusable = nodeFocusable;
        if (getParentNode() != null) {
            getParentNode().rebuildChildren();
        }
    }

    public boolean isNodeHasFocus() {
        return nodeHasFocus;
    }

    public RoutineCallBack getRoutineCallBack() {
        return routineCallBack;
    }

    public void setRoutineCallBack(RoutineCallBack routineCallBack) {
        this.routineCallBack = routineCallBack;
    }

    public List<Pair<FocusNode, Rect>> getFocusChildren() {
        return focusChildren;
    }

    public void rebuildChildren() {
        this.focusChildren = FocusUtils.buildBranch(this);
        buildCount++;
        AppDebug.d(TAG, this.hashCode() + " rebuildChildren (" + focusChildren.size() + "," + buildCount + ")");
        // 如果当前聚焦子节点不在这个列表中，清空
        clearInnerChildIfNeed();
    }

    /**
     * 清理内部无效链路
     */
    private void clearInnerChildIfNeed() {
        if (this.focusChildren != null && innerNode != null) {
            boolean find = false;
            for (Pair<FocusNode, Rect> iterator : this.focusChildren) {
                if (iterator != null && iterator.first != null
                        && iterator.first == innerNode) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                setInnerNode(null);
            }
        }
    }

    /**
     * 获取最合适子节点
     *
     * @return
     */
    private FocusNode findFittestChild() {
        if (innerNode != null && FocusUtils.isNodeInParent(innerNode, this)) {
            return innerNode;
        }
        FocusNode his = historyFirst();
        while (his != null) {
            if (FocusUtils.isNodeInParent(his, this)) {
                return his;
            }
            his = historyNext();
        }
        return null;
    }

    /**
     * 返回最合适的页节点
     *
     * @return 首次聚焦时，返回null
     */
    private FocusNode doFindFittestLeaf() {
        FocusNode finalFind = FocusUtils.findPriorityLeaf(this);
        if (finalFind != null) {
            return finalFind;
        }
        FocusNode iterator = findFittestChild();
        while (iterator != null) {
            finalFind = iterator;
            iterator = iterator.findFittestChild();
        }
        if (finalFind != null) {
            FocusNode leaf = FocusUtils.findClosestLeaf2LT(finalFind);
            return leaf;
        }
        return null;
    }

    /**
     * 重新聚焦到叶节点
     *
     * @return
     */
    public boolean refocusToLeaf() {
        FocusNode leaf = findFittestLeaf();
        if (leaf != null) {
            if (FocusUtils.buildFocusPath(leaf, this)) {
                FocusUtils.focusEnterToLeaf(this);
                return true;
            }
        }
        return false;
    }

    /**
     * 请求聚焦
     *
     * @param stopNode 遍历终止节点
     * @return
     */
    public boolean requestFocus(FocusNode stopNode) {
        if (!isNodeHasFocus()) {
            if (FocusUtils.buildFocusPath(this, stopNode)) {
                FocusUtils.focusEnterToLeaf(stopNode);
                return true;
            }
        }
        return false;
    }

    public Binder getBinder() {
        return mBinder;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public void invalidate() {
        View v = getBinder().getView();
        int loop = 20;
        while (true) {
            loop--;
            if (v != null) {
                v.invalidate();
                if (v.getParent() instanceof View) {
                    v = (View) v.getParent();
                }
            }
            if (v == null) {
                break;
            }
            if (parentNode == null) {
                break;
            }
            if (v == parentNode.getBinder().getView()) {
                break;
            }
            if (loop < 0) {
                break;
            }
        }
    }

    public FocusNode doFindNext(Routine routine, int keyCode, KeyEvent event) {
        if (FocusUtils.isLeafFocusNode(this)) {
            if (isNodeFocusable() && !isNodeHasFocus()) {
                // 本节点是叶节点 可聚焦 且 没有被聚焦
                return this;
            }
            return null;
        }

        // 本节点不是叶节点，判断当前聚焦子项是否能处理
        if (innerNode != null) {
            FocusNode tmp = innerNode.findNext(routine, keyCode, event);
            if (tmp != null) {
                return tmp;
            } else {
                // 当前聚焦节点不能处理，找最近的兄弟, 判断其是否能处理,
                FocusNode brother = FocusUtils.findInBrothers(innerNode, keyCode, event);
                if (brother != null) {
                    // 有这样的兄弟，询问这大兄弟能否处理
                    tmp = brother.findNext(routine, keyCode, event);
                    if (tmp != null) {
                        // 返回内部可聚焦子节点
                        return tmp;
                    }
                    // 直接返回这个大兄弟
                    return brother;
                }
            }
        } else {
            // 本节点没有当前聚焦的子节点，遍历整个子代Map看是否有合适的可聚焦节点
            FocusNode tmp = FocusUtils.findInChildren(this, keyCode, event);
            if (tmp != null) {
                // 找到最终叶节点，有则返回最终叶节点
                FocusNode leaf = tmp.findNext(routine, keyCode, event);
                if (leaf != null) {
                    return leaf;
                }
                return tmp;
            }
        }
        return null;
    }

    @Override
    public FocusNode findNext(Routine routine, int keyCode, KeyEvent event) {
        AppDebug.d(TAG, this.hashCode() + " findNext (" + ((routine != null) ? (routine.id) : ("null")) + "," + keyCode + ")");
        innerState = NodeInnerState.findNextIng;
        if (currRoutine != null && currRoutine.id == routine.id) {
            innerState = NodeInnerState.findNextDone;
            return currRoutineResult;
        }
        try {
            if (routineCallBack != null) {
                routineCallBack.onPreFindNext();
            }
            rebuildChildren();
            currRoutine = routine;
            if (focusFinder != null) {
                currRoutineResult = focusFinder.findNext(routine, keyCode, event);
            } else {
                currRoutineResult = doFindNext(routine, keyCode, event);
            }
            if (routineCallBack != null) {
                routineCallBack.onPostFindNext();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        innerState = NodeInnerState.findNextDone;
        return currRoutineResult;
    }

    @Override
    public FocusNode findFittestLeaf() {
        FocusUtils.rebuildTotalPath(this);
        if (routineCallBack != null) {
            routineCallBack.onPreFittestLeaf();
        }
        FocusNode rtn = null;
        if (focusFinder != null) {
            rtn = focusFinder.findFittestLeaf();
        } else {
            rtn = doFindFittestLeaf();
        }
        if (routineCallBack != null) {
            routineCallBack.onPostFittestLeaf();
        }
        return rtn;
    }

    public boolean isNodeDoingSomething() {
        if (innerState == NodeInnerState.focusLeaveIng
                || innerState == NodeInnerState.focusEnterIng
                || innerState == NodeInnerState.findNextIng
                || innerState == NodeInnerState.focusClickIng) {
            return true;
        }
        if (nodeOuterState != null) {
            return nodeOuterState.isBusy();
        }
        return false;
    }

    public void resetState() {
        focusChildren.clear();
        history.clear();
        historyIterator = 0;
        //private FocusConsumer focusConsumer = null;
        //private FocusFinder focusFinder = null;
        innerNode = null;
        parentNode = null;
        rectInParentNode = null;
        currRoutine = null;
        currRoutineResult = null;
        //private RoutineCallBack routineCallBack;
        innerState = NodeInnerState.idle;
        priority = 0;
        buildCount = 0;
        focusBalance = 0;
        focusEnterCount = 0;
        focusLeaveCount = 0;
        focusClickCount = 0;
        focusDrawCount = 0;
        nodeFocusable = true;
        nodeHasFocus = false;
        isRoot = false;
    }

    /**
     * 负责完成 焦点节点和 View 的绑定工作，提供双向索引通道
     */
    public interface Binder {
        View getView();

        FocusNode getNode();
    }

    public enum NodeInnerState {
        idle,
        findNextIng, findNextDone,
        focusEnterIng, focusEnterDone,
        focusLeaveIng, focusLeaveDone,
        focusClickIng, focusClickDone,
    }

    public interface NodeOuterState {
        boolean isBusy();
    }

    public interface RoutineCallBack {
        void onPreFindNext();

        void onPostFindNext();

        void onPreFittestLeaf();

        void onPostFittestLeaf();
    }

    public static abstract class RCBImpl implements RoutineCallBack {

        @Override
        public void onPreFindNext() {

        }

        @Override
        public void onPostFindNext() {

        }

        @Override
        public void onPreFittestLeaf() {

        }

        @Override
        public void onPostFittestLeaf() {

        }
    }
}
