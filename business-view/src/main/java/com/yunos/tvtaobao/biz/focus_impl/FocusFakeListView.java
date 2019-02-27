package com.yunos.tvtaobao.biz.focus_impl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.core.common.AppDebug;

import java.util.Observable;

/**
 * Created by GuoLiDong on 2018/11/10.
 */
public class FocusFakeListView extends FakeListView implements FocusNode.Binder {
    private static final String TAG = FocusFakeListView.class.getSimpleName();

    public FocusFakeListView(Context context) {
        this(context, null);
    }

    public FocusFakeListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusFakeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        focusNode.setFocusFinder(focusNodeProxy);
        focusNode.setFocusConsumer(focusNodeProxy);
        focusNode.setNodeOuterState(focusNodeProxy);
    }

    FocusNodeProxy focusNodeProxy = new FocusNodeProxy();

    FocusNode focusNode = new FocusNode(this);

    private Positions positions = new Positions();

    private FocusConsumer focusConsumer = null;

    private NoNextListener noNextListener = null;

    public FocusConsumer getFocusConsumer() {
        return focusConsumer;
    }

    public void setFocusConsumer(FocusConsumer focusConsumer) {
        this.focusConsumer = focusConsumer;
    }

    public NoNextListener getNoNextListener() {
        return noNextListener;
    }

    public void setNoNextListener(NoNextListener noNextListener) {
        this.noNextListener = noNextListener;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public FocusNode getNode() {
        return focusNode;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        focusNode.onFocusDraw(canvas);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof FocusNode.Binder) {
            ((FocusNode.Binder) child).getNode().resetState();
        }
    }

    @Override
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (child instanceof FocusNode.Binder) {
            FocusUtils.focusLeaveToLeaf(((FocusNode.Binder) child).getNode());
        }
    }

    @Override
    protected void onAdapterDataSetChange(Adapter.NotifyParam notifyParam) {
        super.onAdapterDataSetChange(notifyParam);
        if (getAdapter().getItemCount() == 0) {
            positions.clear();
        }
    }

    public FocusNode getFocusNode(int pos) {
        ViewHolder vh = getViewHolder(pos);
        if (vh != null) {
            if (vh.itemView instanceof FocusNode.Binder) {
                return ((FocusNode.Binder) vh.itemView).getNode();
            }
        }
        return null;
    }

    public int getCandidatePos(int rmvPos) {
        Pair<Integer, Integer> pair = getLayoutRange();
        if (rmvPos >= pair.first && rmvPos <= pair.second) {
            if (rmvPos >= (pair.first + pair.second) / 2 && rmvPos != pair.second) {
                return positions.getNextUpPos(rmvPos);
            }
        }
        return NO_POS;
    }

    public void innerFocusOn(int pos) {
        FocusNode fn = getFocusNode(pos);
        if (fn != null) {
            AppDebug.d(TAG, tagObjHashCode() + " innerFocusOn : " + pos + " ok ");
            FocusUtils.buildFocusPath(fn, this.getNode());
        } else {
            AppDebug.d(TAG, tagObjHashCode() + " innerFocusOn : failed cause can not find FocusNode for " + pos);
        }
    }

    public void selectOn(final int pos, final Runnable runWhenScrollOver) {
        selectOn(pos, false, null, runWhenScrollOver);
    }

    public void selectOn(final int pos, final Runnable beforeSetNotify, final Runnable runWhenScrollOver) {
        selectOn(pos, false, beforeSetNotify, runWhenScrollOver);
    }

    public void selectOn(final int pos, final boolean immediately, final Runnable beforeSetNotify, final Runnable runWhenScrollOver) {
        AppDebug.d(TAG, tagObjHashCode() + " selectOn param: " + pos);
        int position = Math.max(0, pos);
        position = Math.min(getAdapter().getItemCount() - 1, position);
        position = Math.max(0, position);
        if (getAdapter() != null && position >= 0 && position < getAdapter().getItemCount()) {
            AppDebug.d(TAG, tagObjHashCode() + " selectOn real: " + position);
            positions.setNow(position, beforeSetNotify);
            scrollToPos(pos, immediately, runWhenScrollOver);
        }
    }

    public void refocusOnIfNeed(final FocusManager focusManager, int pos) {
        int tmpPos = positions.checkPos(pos);
        final int finalPos = (NO_POS == tmpPos) ? (positions.getPosAfterNotify()) : (tmpPos);
        if (getAdapter().getItemCount() > 0 && finalPos != NO_POS) {
            selectOn(finalPos, new Runnable() {
                @Override
                public void run() {
                    if (getNode().isNodeHasFocus()) {
                        if (focusManager != null) {
                            focusManager.focusOn(getNode());
                        }
                    } else {
                        innerFocusOn(finalPos);
                    }
                }
            });
        }
    }

    public void refocusOnIfNeed(final FocusManager focusManager) {
        refocusOnIfNeed(focusManager, positions.getPosAfterNotify());
    }

    public class Positions extends Observable {
        private int[] unSelectableItems = null;

        private int now = NO_POS;
        private int old = NO_POS;

        public void clear() {
            now = NO_POS;
            old = NO_POS;
        }

        private boolean setNow(int focusPosition, Runnable beforeSetNotify) {
            AppDebug.d(TAG, tagObjHashCode() + " " + this.hashCode() + " setNow " + focusPosition);
            old = this.now;
            if (focusPosition != this.now) {
                this.now = focusPosition;
                AppDebug.d(TAG, tagObjHashCode() + " " + this.hashCode() + " setNow " + focusPosition + " success !");
                if (beforeSetNotify != null) {
                    try {
                        beforeSetNotify.run();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                setChanged();
                notifyObservers();
                return true;
            }
            return false;
        }

        public int getNow() {
            return now;
        }

        public int getOld() {
            return old;
        }

        private boolean isPosUnSelectable(int pos) {
            if (unSelectableItems != null) {
                for (int i = 0; i < unSelectableItems.length; i++) {
                    if (unSelectableItems[i] == pos) {
                        return true;
                    }
                }
            }
            return false;
        }

        public void setUnSelectableItem(int... index) {
            unSelectableItems = index;
        }

        public int getPosAfterNotify() {
            if (getAdapter().getItemCount() > 0) {
                if (getNow() >= 0
                        && getNow() < getAdapter().getItemCount()) {
                    int pos = getNow();
                    if (isPosUnSelectable(pos)) {
                        pos = getNextDownPos(pos);
                    }
                    if (isPosUnSelectable(pos)) {
                        pos = getNextUpPos(pos);
                    }
                    return pos;
                } else {
                    int pos = getAdapter().getItemCount() - 1;
                    if (isPosUnSelectable(pos)) {
                        pos = getNextDownPos(pos);
                    }
                    if (isPosUnSelectable(pos)) {
                        pos = getNextUpPos(pos);
                    }
                    return pos;
                }
            }
            return NO_POS;
        }

        private int checkPos(int pos) {
            if (pos < 0) {
                pos = 0;
            }
            if (pos >= getAdapter().getItemCount()) {
                pos = getAdapter().getItemCount() - 1;
            }
            if (!isPosUnSelectable(pos)) {
                return pos;
            }
            return NO_POS;
        }

        private int getNextDownPos(int pos) {
            int step = 1;
            int now = pos;
            int newPos = now;
            while (true) {
                int tmp = now + step;
                if (tmp < getAdapter().getItemCount()) {
                    if (!isPosUnSelectable(tmp)) {
                        newPos = tmp;
                        break;
                    }
                    step++;
                } else {
                    break;
                }
            }
            return newPos;
        }

        private int getNextUpPos(int pos) {
            int step = -1;
            int now = pos;
            int newPos = now;
            while (true) {
                int tmp = now + step;
                if (tmp >= 0) {
                    if (!isPosUnSelectable(tmp)) {
                        newPos = tmp;
                        break;
                    }
                    step--;
                } else {
                    break;
                }
            }
            return newPos;
        }

        private boolean hasNextDownPos(int pos) {
            int step = 1;
            int now = pos;
            boolean rtn = false;
            while (true) {
                int tmp = now + step;
                if (tmp < getAdapter().getItemCount()) {
                    if (!isPosUnSelectable(tmp)) {
                        rtn = true;
                        break;
                    }
                    step++;
                } else {
                    break;
                }
            }
            return rtn;
        }

        private boolean hasNextUpPos(int pos) {
            int step = -1;
            int now = pos;
            boolean rtn = false;
            while (true) {
                int tmp = now + step;
                if (tmp >= 0) {
                    if (!isPosUnSelectable(tmp)) {
                        rtn = true;
                        break;
                    }
                    step--;
                } else {
                    break;
                }
            }
            return rtn;
        }
    }

    public Positions getPositions() {
        return positions;
    }

    public interface NoNextListener {
        void onNoNext(int keyCode, KeyEvent event);
    }

    private class FocusNodeProxy implements FocusFinder, FocusConsumer, FocusNode.NodeOuterState {

        private class FindNextStuff {
            static final int FLAG_KEY_UP = 0x00000001;
            static final int FLAG_KEY_DOWN = 0x00000002;
            static final int FLAG_ARRIVE_FIRST = 0x00000004;
            static final int FLAG_ARRIVE_LAST = 0x00000008;
            static final int FLAG_EVENT_EAT = 0x00000010;
            static final int FLAG_ALREADY_FOCUS_ON = 0x00000020;
            int flag = 0x00000000;
            FocusNode target;
            Routine routine;
            int keyCode;
            KeyEvent event;

            public FindNextStuff(FocusNode target, Routine routine, int keyCode, KeyEvent event) {
                this.target = target;
                this.routine = routine;
                this.keyCode = keyCode;
                this.event = event;
            }

            public boolean isEventConsumed() {
                return (flag & FLAG_EVENT_EAT) == FLAG_EVENT_EAT;
            }

            public boolean isNeedScrollForKeyUp() {
                return (flag & FLAG_KEY_UP) == FLAG_KEY_UP;
            }

            public boolean isNeedScrollForKeyUDown() {
                return (flag & FLAG_KEY_DOWN) == FLAG_KEY_DOWN;
            }

            public boolean isArriveTop() {
                return (flag & FLAG_ARRIVE_FIRST) == FLAG_ARRIVE_FIRST;
            }

            public boolean isArriveBottom() {
                return (flag & FLAG_ARRIVE_LAST) == FLAG_ARRIVE_LAST;
            }

            public boolean isAlreadyFocusOn() {
                return (flag & FLAG_ALREADY_FOCUS_ON) == FLAG_ALREADY_FOCUS_ON;
            }
        }

        FindNextStuff findNextStuffCache = null;

        private void dealWithFindNextStuff(FindNextStuff findNextStuff) {
            if (focusNode.isNodeHasFocus()) {
                AppDebug.d(TAG, tagObjHashCode() + " dealWithFindNextStuff has focus");

                findNextStuff.flag |= FindNextStuff.FLAG_ALREADY_FOCUS_ON;

                if (findNextStuff.target != null) {
                    findNextStuff.flag |= FindNextStuff.FLAG_EVENT_EAT;
                } else {
                    if (findNextStuff.keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (positions.hasNextDownPos(positions.getNow())) {
                            findNextStuff.flag |= FindNextStuff.FLAG_EVENT_EAT;
                            findNextStuff.flag |= FindNextStuff.FLAG_KEY_DOWN;
                        } else {
                            findNextStuff.flag |= FindNextStuff.FLAG_ARRIVE_LAST;
                        }
                    } else if (findNextStuff.keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (positions.hasNextUpPos(positions.getNow())) {
                            findNextStuff.flag |= FindNextStuff.FLAG_EVENT_EAT;
                            findNextStuff.flag |= FindNextStuff.FLAG_KEY_UP;
                        } else {
                            findNextStuff.flag |= FindNextStuff.FLAG_ARRIVE_FIRST;
                        }
                    }
                }
            } else {
                AppDebug.d(TAG, tagObjHashCode() + " dealWithFindNextStuff no focus");
                if (findNextStuff.target != null) {
                    findNextStuff.flag |= FindNextStuff.FLAG_EVENT_EAT;
                }
            }

            if (findNextStuff.isArriveTop() || findNextStuff.isArriveBottom()) {
                findNextStuffCache = null;
                if (noNextListener != null) {
                    noNextListener.onNoNext(findNextStuff.keyCode, findNextStuff.event);
                }
            } else {
                if (!findNextStuff.isEventConsumed()) {
                    findNextStuffCache = null;
                }
            }
        }

        private void doFocusEnter() {
            Runnable focusOnTask = new Runnable() {
                @Override
                public void run() {
                    focusNode.rebuildChildren();
                    FocusNode posNode = getFocusNode(positions.getNow());
                    if (posNode != null) {
                        for (int i = 0; i < focusNode.getFocusChildren().size(); i++) {
                            Pair<FocusNode, Rect> tmp = focusNode.getFocusChildren().get(i);
                            if (tmp.first == posNode) {
                                FocusNode leaf = tmp.first.findFittestLeaf();
                                FocusUtils.buildFocusPath(leaf, focusNode);
                                FocusUtils.focusEnterToLeaf(tmp.first);
                                scrollToPos(positions.getNow(), true, null);
                            } else {
                                FocusUtils.focusLeaveToLeaf(tmp.first);
                            }
                        }
                    }
                }
            };

            if (findNextStuffCache != null) {
                if (findNextStuffCache.target != null) {
                    ViewHolder vh = getViewHolder(findNextStuffCache.target.getBinder().getView());
                    if (vh != null) {
                        if (vh.getPosNow() != positions.getNow()
                                && positions.getNow() != NO_POS
                                && !findNextStuffCache.isAlreadyFocusOn()) {
                            // 发现重新聚焦上来，发现内部焦点和选中位置不一致了，优先取选中位置
                            focusOnTask.run();
                        } else {
                            FocusUtils.buildFocusPath(findNextStuffCache.target, focusNode);
                            FocusUtils.focusEnterToLeaf(focusNode);
                            selectOn(vh.getPosNow(), null);
                        }
                    } else {
                        focusOnTask.run();
                    }
                } else {
                    if (findNextStuffCache.isNeedScrollForKeyUp()) {
                        selectOn(positions.getNow() - 1, null);
                    } else if (findNextStuffCache.isNeedScrollForKeyUDown()) {
                        selectOn(positions.getNow() + 1, null);
                    }
                }
                findNextStuffCache = null;
            } else {
                focusOnTask.run();
            }
        }

        private void doFocusLeave() {
            for (int i = 0; i < focusNode.getFocusChildren().size(); i++) {
                Pair<FocusNode, Rect> tmp = focusNode.getFocusChildren().get(i);
                FocusUtils.focusLeaveToLeaf(tmp.first);
            }
        }

        private void doFocusClick() {
            FocusNode posNode = getFocusNode(positions.getNow());
            if (posNode != null) {
                for (int i = 0; i < focusNode.getFocusChildren().size(); i++) {
                    Pair<FocusNode, Rect> tmp = focusNode.getFocusChildren().get(i);
                    if (tmp.first == posNode) {
                        FocusUtils.focusClickToLeaf(tmp.first);
                        break;
                    }
                }
            }
        }

        @Override
        public boolean onFocusEnter() {
            doFocusEnter();
            if (focusConsumer != null) {
                return focusConsumer.onFocusEnter();
            }
            return true;
        }

        @Override
        public boolean onFocusLeave() {
            doFocusLeave();
            if (focusConsumer != null) {
                return focusConsumer.onFocusLeave();
            }
            return true;
        }

        @Override
        public boolean onFocusClick() {
            doFocusClick();
            if (focusConsumer != null) {
                return focusConsumer.onFocusClick();
            }
            return true;
        }

        @Override
        public boolean onFocusDraw(Canvas canvas) {
            return false;
        }

        @Override
        public FocusNode findNext(Routine routine, int keyCode, KeyEvent event) {
            FocusNode target = focusNode.doFindNext(routine, keyCode, event);
            AppDebug.d(TAG, tagObjHashCode() + " findNext " + ((target != null) ? (target.hashCode()) : ("null")));

            findNextStuffCache = new FindNextStuff(target, routine, keyCode, event);

            dealWithFindNextStuff(findNextStuffCache);

            if (findNextStuffCache != null) {
                if (findNextStuffCache.isEventConsumed()) {
                    return focusNode;
                }
            }

            return null;
        }

        @Override
        public FocusNode findFittestLeaf() {
            return focusNode;
        }

        @Override
        public boolean isBusy() {
            return isNotifyDoing() || isScrollDoing();
        }
    }
}
