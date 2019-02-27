package com.yunos.tvtaobao.biz.focus_impl;

import android.graphics.Rect;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by GuoLiDong on 2018/10/23.
 * 定义一些常用的焦点寻址、聚焦逻辑
 */
public class FocusUtils {

    /**
     * 在兄弟节点中找到最靠近的返回，适用于当前节点处理不了这个事件的情况
     *
     * @param child
     * @param keyCode
     * @param event
     * @return
     */
    public static FocusNode findInBrothers(final FocusNode child, final int keyCode, final KeyEvent event) {
        List<FocusNode> possibleList = new ArrayList<>();
        if (child.getParentNode() != null) {
            for (Pair<FocusNode, Rect> iterator : child.getParentNode().getFocusChildren()) {
                if (iterator.first != child
                        && iterator.first.getRectInParentNode() != null
                        && child.getRectInParentNode() != null) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (iterator.first.getRectInParentNode().top < child.getRectInParentNode().top
                                && iterator.first.getRectInParentNode().bottom < child.getRectInParentNode().bottom
                                && iterator.first.getRectInParentNode().right > child.getRectInParentNode().left
                                && iterator.first.getRectInParentNode().left < child.getRectInParentNode().right) {
                            possibleList.add(iterator.first);
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (iterator.first.getRectInParentNode().bottom > child.getRectInParentNode().bottom
                                && iterator.first.getRectInParentNode().top > child.getRectInParentNode().top
                                && iterator.first.getRectInParentNode().right > child.getRectInParentNode().left
                                && iterator.first.getRectInParentNode().left < child.getRectInParentNode().right) {
                            possibleList.add(iterator.first);
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (iterator.first.getRectInParentNode().left < child.getRectInParentNode().left
                                && iterator.first.getRectInParentNode().right < child.getRectInParentNode().right
                                && iterator.first.getRectInParentNode().bottom > child.getRectInParentNode().top
                                && iterator.first.getRectInParentNode().top < child.getRectInParentNode().bottom) {
                            possibleList.add(iterator.first);
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (iterator.first.getRectInParentNode().right > child.getRectInParentNode().right
                                && iterator.first.getRectInParentNode().left > child.getRectInParentNode().left
                                && iterator.first.getRectInParentNode().bottom > child.getRectInParentNode().top
                                && iterator.first.getRectInParentNode().top < child.getRectInParentNode().bottom) {
                            possibleList.add(iterator.first);
                        }
                    }
                }
            }
        }


        //排序，距离最小的放最前
        Collections.sort(possibleList, new Comparator<FocusNode>() {
            @Override
            public int compare(FocusNode o1, FocusNode o2) {
                int distanceO1 = 0, distanceO2 = 0;
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    distanceO1 = (int) (Math.pow(o1.getRectInParentNode().centerX() - child.getRectInParentNode().centerX(), 2)
                            + Math.pow(o1.getRectInParentNode().bottom - child.getRectInParentNode().top, 2));
                    distanceO2 = (int) (Math.pow(o2.getRectInParentNode().centerX() - child.getRectInParentNode().centerX(), 2)
                            + Math.pow(o2.getRectInParentNode().bottom - child.getRectInParentNode().top, 2));
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    distanceO1 = (int) (Math.pow(o1.getRectInParentNode().centerX() - child.getRectInParentNode().centerX(), 2)
                            + Math.pow(o1.getRectInParentNode().top - child.getRectInParentNode().bottom, 2));
                    distanceO2 = (int) (Math.pow(o2.getRectInParentNode().centerX() - child.getRectInParentNode().centerX(), 2)
                            + Math.pow(o2.getRectInParentNode().top - child.getRectInParentNode().bottom, 2));
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    distanceO1 = (int) (Math.pow(o1.getRectInParentNode().right - child.getRectInParentNode().left, 2)
                            + Math.pow(o1.getRectInParentNode().centerY() - child.getRectInParentNode().centerY(), 2));
                    distanceO2 = (int) (Math.pow(o2.getRectInParentNode().right - child.getRectInParentNode().left, 2)
                            + Math.pow(o2.getRectInParentNode().centerY() - child.getRectInParentNode().centerY(), 2));
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    distanceO1 = (int) (Math.pow(o1.getRectInParentNode().left - child.getRectInParentNode().right, 2)
                            + Math.pow(o1.getRectInParentNode().centerY() - child.getRectInParentNode().centerY(), 2));
                    distanceO2 = (int) (Math.pow(o2.getRectInParentNode().left - child.getRectInParentNode().right, 2)
                            + Math.pow(o2.getRectInParentNode().centerY() - child.getRectInParentNode().centerY(), 2));
                }
                return distanceO1 - distanceO2;
            }
        });

        // 排序，最优先的放最前
        Collections.sort(possibleList, new Comparator<FocusNode>() {
            @Override
            public int compare(FocusNode o1, FocusNode o2) {
                long rlt = o2.getPriority() - o1.getPriority();
                return ((rlt > 0) ? (1) : ((rlt < 0) ? (-1) : (0)));
            }
        });

        if (!possibleList.isEmpty()) {
            return possibleList.get(0);
        }

        return null;
    }

    /**
     * 在子节点中找到可能存在的目标，适用于当前节点的内部焦点为null的情况
     *
     * @param from
     * @param keyCode
     * @param event
     * @return
     */
    public static FocusNode findInChildren(final FocusNode from, final int keyCode, final KeyEvent event) {
        if (isLeafFocusNode(from)) {
            return null;
        }
        List<FocusNode> possibleList = new ArrayList<>();
        if (from != null
                && from.getBinder() != null
                && from.getBinder().getView() != null) {
            for (Pair<FocusNode, Rect> iterator : from.getFocusChildren()) {
                if (iterator.first.getRectInParentNode() != null) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (iterator.first.getRectInParentNode().top < from.getBinder().getView().getHeight()) {
                            possibleList.add(iterator.first);
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (iterator.first.getRectInParentNode().bottom > 0) {
                            possibleList.add(iterator.first);
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (iterator.first.getRectInParentNode().left < from.getBinder().getView().getWidth()) {
                            possibleList.add(iterator.first);
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (iterator.first.getRectInParentNode().right > 0) {
                            possibleList.add(iterator.first);
                        }
                    }
                }
            }
        }

        //排序，距离最小的放最前
        Collections.sort(possibleList, new Comparator<FocusNode>() {
            @Override
            public int compare(FocusNode o1, FocusNode o2) {
                int distanceO1 = 0, distanceO2 = 0;
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    distanceO1 = (int) (Math.pow(o1.getRectInParentNode().centerX() - from.getBinder().getView().getWidth() / 2, 2)
                            + Math.pow(o1.getRectInParentNode().bottom - from.getBinder().getView().getHeight(), 2));
                    distanceO2 = (int) (Math.pow(o2.getRectInParentNode().centerX() - from.getBinder().getView().getWidth() / 2, 2)
                            + Math.pow(o2.getRectInParentNode().bottom - from.getBinder().getView().getHeight(), 2));
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    distanceO1 = (int) (Math.pow(o1.getRectInParentNode().centerX() - from.getBinder().getView().getWidth() / 2, 2)
                            + Math.pow(o1.getRectInParentNode().top - 0, 2));
                    distanceO2 = (int) (Math.pow(o2.getRectInParentNode().centerX() - from.getBinder().getView().getWidth() / 2, 2)
                            + Math.pow(o2.getRectInParentNode().top - 0, 2));
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    distanceO1 = (int) (Math.pow(o1.getRectInParentNode().right - from.getBinder().getView().getWidth(), 2)
                            + Math.pow(o1.getRectInParentNode().centerY() - from.getBinder().getView().getHeight() / 2, 2));
                    distanceO2 = (int) (Math.pow(o2.getRectInParentNode().right - from.getBinder().getView().getWidth(), 2)
                            + Math.pow(o2.getRectInParentNode().centerY() - from.getBinder().getView().getHeight() / 2, 2));
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    distanceO1 = (int) (Math.pow(o1.getRectInParentNode().left - 0, 2)
                            + Math.pow(o1.getRectInParentNode().centerY() - from.getBinder().getView().getHeight() / 2, 2));
                    distanceO2 = (int) (Math.pow(o2.getRectInParentNode().left - 0, 2)
                            + Math.pow(o2.getRectInParentNode().centerY() - from.getBinder().getView().getHeight() / 2, 2));
                }
                return distanceO1 - distanceO2;
            }
        });

        // 排序，最优先的放最前
        Collections.sort(possibleList, new Comparator<FocusNode>() {
            @Override
            public int compare(FocusNode o1, FocusNode o2) {
                long rlt = o2.getPriority() - o1.getPriority();
                return ((rlt > 0) ? (1) : ((rlt < 0) ? (-1) : (0)));
            }
        });

        if (!possibleList.isEmpty()) {
            return possibleList.get(0);
        }

        return null;
    }

    /**
     * 判断是不是"树叶"节点
     *
     * @param focusNode
     * @return
     */
    public static boolean isLeafFocusNode(final FocusNode focusNode) {
        if (focusNode != null) {
            if (focusNode.getInnerNode() == null
                    && focusNode.getBinder() != null
                    && focusNode.getBinder().getView() != null
                    && focusNode.getFocusChildren().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从 参数 节点开始，向上到根，计算焦点路径
     *
     * @param focusNode
     * @return
     */
    public static List<FocusNode> calculateFocusPath(FocusNode focusNode) {
        if (focusNode != null) {
            List<FocusNode> rtn = new ArrayList<>();
            while (focusNode != null) {
                rtn.add(focusNode);
                focusNode = focusNode.getParentNode();
            }
            return rtn;
        }
        return null;
    }

    public static boolean buildFocusPath(final FocusNode startNode, final FocusNode stopNode) {
        return buildFocusPath(startNode, stopNode, false);
    }

    /**
     * 强制设置一条焦点路径
     *
     * @param startNode
     * @param stopNode
     * @param excludeStopNode 是否包含终止节点
     * @return
     */
    public static boolean buildFocusPath(final FocusNode startNode, final FocusNode stopNode, boolean excludeStopNode) {
        if (startNode != null) {
            if (stopNode != null && !isNodeInParent(startNode, stopNode)) {
                return false;
            }
            List<FocusNode> nowList = calculateFocusPath(startNode);
            if (nowList != null) {
                for (int i = 0; i < nowList.size(); i++) {
                    if (i + 1 < nowList.size()) {
                        if (excludeStopNode) {
                            if (nowList.get(i + 1) == stopNode) {
                                break;
                            }
                            nowList.get(i + 1).setInnerNode(nowList.get(i));
                        } else {
                            nowList.get(i + 1).setInnerNode(nowList.get(i));
                            if (nowList.get(i + 1) == stopNode) {
                                break;
                            }
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 同步 失去焦点、获得焦点 处理流程
     *
     * @param newLeafNode
     * @param oldLeafNode
     * @param stopNode
     */
    public static void syncFocusState(final FocusNode newLeafNode, final FocusNode oldLeafNode, final FocusNode stopNode) {
        if (newLeafNode != null) {
            List<FocusNode> nowList = calculateFocusPath(newLeafNode);
            List<FocusNode> oldList = calculateFocusPath(oldLeafNode);
            List<FocusNode> listSame = new ArrayList<>();
            if (nowList != null && oldList != null) {
                for (int i = 0; i < oldList.size(); i++) {
                    for (int j = 0; j < nowList.size(); j++) {
                        if (nowList.get(j) == oldList.get(i)) {
                            listSame.add(oldList.get(i));
                        }
                    }
                }
            }

            // 构建新的焦点路径
            FocusUtils.buildFocusPath(newLeafNode, stopNode);

            // 清除未发生聚焦状态改变的部分
            for (int i = 0; i < listSame.size(); i++) {
                if (nowList != null) {
                    nowList.remove(listSame.get(i));
                }
                if (oldList != null) {
                    oldList.remove(listSame.get(i));
                }
            }

            // 失去焦点逻辑
            for (int i = 0; oldList != null && i < oldList.size(); i++) {
                if (oldList.get(i) != null) {
                    if (oldList.get(i) == stopNode) {
                        // 此举为防止焦点树上发生循环聚焦问题
                        break;
                    }
                    oldList.get(i).onFocusLeave();
                }
            }

            // 聚焦逻辑
            for (int i = 0; nowList != null && i < nowList.size(); i++) {
                if (nowList.get(i) != null) {
                    if (nowList.get(i) == stopNode) {
                        // 此举为防止焦点树上发生循环聚焦问题
                        break;
                    }
                    if (!nowList.get(i).isNodeHasFocus()) {
                        nowList.get(i).onFocusEnter();
                    }
                }
            }

            if (newLeafNode == oldLeafNode) {
                // 说明焦点寻址遍历中有些节点内部处理了寻址事件，这时侯应该再次通知该控件获得了焦点(FocusRecyclerView是此种情况)
                newLeafNode.onFocusEnter();
            }
        }
    }

    /**
     * 根据当前焦点位置，找到笑一个可能的焦点位置
     *
     * @param currFocusOnNode
     * @param keyCode
     * @param event
     * @return
     */
    public static FocusNode findNext(FocusFinder.Routine routine, final FocusNode currFocusOnNode, final int keyCode, final KeyEvent event) {
        if (currFocusOnNode != null) {
            FocusNode next = currFocusOnNode.findNext(routine, keyCode, event);
            FocusNode iterator = currFocusOnNode.getParentNode();
            int loopCount = 20;
            while (next == null) {
                loopCount--;
                if (iterator != null) {
                    next = iterator.findNext(routine, keyCode, event);
                    iterator = iterator.getParentNode();
                } else {
                    break;
                }
                if (loopCount < 0) {
                    break;
                }
            }
            return next;
        }
        return null;
    }

    /**
     * 判断一个节点是否 属于 另一个节点
     *
     * @param node
     * @param parent
     * @return
     */
    public static boolean isNodeInParent(FocusNode node, FocusNode parent) {
        boolean rtn = false;
        if (parent != null && node != null) {
            if (parent == node) {
                rtn = true;
                return rtn;
            }
            for (Pair<FocusNode, Rect> iterator : parent.getFocusChildren()) {
                if (iterator != null && iterator.first != null) {
                    if (iterator.first == node) {
                        rtn = true;
                        break;
                    } else {
                        rtn = isNodeInParent(node, iterator.first);
                        if (rtn) {
                            break;
                        }
                    }
                }
            }
        }
        return rtn;
    }

    /**
     * 查找最终叶节点
     *
     * @param node
     * @return
     */
    public static FocusNode findPriorityLeaf(FocusNode node) {
        if (node != null) {
            if (isLeafFocusNode(node)) {
                return node;
            }

            FocusNode leaf = null;
            long priority = Long.MIN_VALUE;
            for (Pair<FocusNode, Rect> iterator : node.getFocusChildren()) {
                if (iterator != null
                        && iterator.first != null
                        && iterator.second != null) {
                    if (priority < iterator.first.getPriority() && iterator.first.getPriority() != 0) {
                        priority = iterator.first.getPriority();
                        leaf = iterator.first;
                    }
                }
            }
            if (leaf != null) {
                return findPriorityLeaf(leaf);
            }
        }
        return null;
    }


    /**
     * 查找最终叶节点
     *
     * @param node
     * @return
     */
    public static FocusNode findClosestLeaf2LT(FocusNode node) {
        if (node != null) {
            if (isLeafFocusNode(node)) {
                return node;
            }

            FocusNode leaf = null;
            int distance = Integer.MAX_VALUE;
            for (Pair<FocusNode, Rect> iterator : node.getFocusChildren()) {
                if (iterator != null
                        && iterator.first != null
                        && iterator.second != null) {
                    int tmpDis = (int) (Math.pow(iterator.second.left, 2)
                            + Math.pow(iterator.second.left, 2));
                    if (tmpDis < distance) {
                        distance = tmpDis;
                        leaf = iterator.first;
                    }
                }
            }
            if (leaf != null) {
                return findClosestLeaf2LT(leaf);
            } else {
                return node;
            }
        }
        return null;
    }

    /**
     * 路径失去焦点
     *
     * @param focusNode
     * @return
     */
    public static boolean focusLeaveToLeaf(FocusNode focusNode) {
        FocusNode iterator = focusNode;
        boolean rtn = false;
        while (iterator != null) {
            if (iterator.isNodeHasFocus()) {
                rtn |= iterator.onFocusLeave();
            }
            iterator = iterator.getInnerNode();
        }
        return rtn;
    }

    /**
     * 路径聚焦
     *
     * @param focusNode
     * @return
     */
    public static boolean focusEnterToLeaf(FocusNode focusNode) {
        FocusNode iterator = focusNode;
        boolean rtn = false;
        while (iterator != null && iterator.isNodeFocusable()) {
            if (!iterator.isNodeHasFocus()) {
                rtn |= iterator.onFocusEnter();
            }
            for (Pair<FocusNode, Rect> tmp : iterator.getFocusChildren()) {
                if (tmp.first != iterator.getInnerNode()) {
                    focusLeaveToLeaf(tmp.first);
                }
            }
            iterator = iterator.getInnerNode();
        }
        return rtn;
    }

    /**
     * 点击事件传递
     *
     * @param focusNode
     * @return
     */
    public static boolean focusClickToLeaf(FocusNode focusNode) {
        if (focusNode != null && focusNode.isNodeHasFocus()) {
            FocusNode iterator = focusNode;
            boolean rtn = false;
            List<FocusNode> list = new ArrayList<>();
            while (iterator != null) {
                list.add(iterator);
                iterator = iterator.getInnerNode();
            }
            for (int i = list.size() - 1; i >= 0; i--) {
                rtn = list.get(i).onFocusClick();
                if (rtn) {
                    break;
                }
            }
            return rtn;
        }
        return false;
    }

    /**
     * 递归查找所有直接链接到 startNode 的 node
     *
     * @param startNode
     * @param notNodeView
     * @return
     */
    private static List<Pair<FocusNode, Rect>> buildBranch2(FocusNode startNode, View notNodeView) {
        List<Pair<FocusNode, Rect>> rtn = new ArrayList<>();
        try {
            if (notNodeView != null
                    && startNode != null
                    && startNode.getBinder() != null
                    && startNode.getBinder().getView() != null) {
                if (notNodeView instanceof ViewGroup) {
                    for (int i = 0; i < ((ViewGroup) notNodeView).getChildCount(); i++) {
                        View child = ((ViewGroup) notNodeView).getChildAt(i);
                        if (child instanceof FocusNode.Binder) {
                            if (child.getVisibility() == View.VISIBLE
                                    && ((FocusNode.Binder) child).getNode().isNodeFocusable()) {
                                // 计算位置
                                Rect tmpRect = new Rect();
                                child.getDrawingRect(tmpRect);
                                ((ViewGroup) startNode.getBinder().getView())
                                        .offsetDescendantRectToMyCoords(child, tmpRect);
                                // 设置位置
                                ((FocusNode.Binder) child).getNode()
                                        .setRectInParentNode(tmpRect);
                                // 设置父节点
                                ((FocusNode.Binder) child).getNode()
                                        .setParentNode(startNode);
                                // 添加到父节点管理链表
                                rtn.add(new Pair<FocusNode, Rect>(((FocusNode.Binder) child).getNode()
                                        , tmpRect));
                            } else {
                                // 不可见，不可聚焦，不算
                            }
                        } else {
                            rtn.addAll(buildBranch2(startNode, child));
                        }
                    }
                } else {
                    // notNodeView
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return rtn;
    }

    /**
     * 构建节点树
     *
     * @param branchNode
     * @return
     */
    public static List<Pair<FocusNode, Rect>> buildBranch(FocusNode branchNode) {
        List<Pair<FocusNode, Rect>> rtn = new ArrayList<>();
        try {
            if (branchNode != null
                    && branchNode.getBinder() != null
                    && branchNode.getBinder().getView() != null) {
                View attachedView = branchNode.getBinder().getView();
                if (attachedView instanceof ViewGroup) {
                    for (int i = 0; i < ((ViewGroup) attachedView).getChildCount(); i++) {
                        View child = ((ViewGroup) attachedView).getChildAt(i);
                        if (child instanceof FocusNode.Binder) {
                            if (child.getVisibility() == View.VISIBLE
                                    && ((FocusNode.Binder) child).getNode().isNodeFocusable()) {
                                // 计算位置
                                Rect tmpRect = new Rect();
                                child.getDrawingRect(tmpRect);
                                ((ViewGroup) attachedView).offsetDescendantRectToMyCoords(child, tmpRect);
                                // 设置位置
                                ((FocusNode.Binder) child).getNode().setRectInParentNode(tmpRect);
                                // 设置父节点
                                ((FocusNode.Binder) child).getNode().setParentNode(((FocusNode.Binder) attachedView).getNode());
                                // 添加到父节点管理链表
                                rtn.add(new Pair<FocusNode, Rect>(((FocusNode.Binder) child).getNode()
                                        , tmpRect));
                            } else {
                                // 当然就不算他啦
                            }
                        } else {
                            rtn.addAll(buildBranch2(branchNode, child));
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return rtn;
    }

    /**
     * 重新构建当前焦点分支
     *
     * @param branchNode
     */
    public static void rebuildTotalPath(FocusNode branchNode) {
        if (branchNode != null) {
            branchNode.rebuildChildren();
            for (Pair<FocusNode, Rect> iterator : branchNode.getFocusChildren()) {
                if (iterator != null && iterator.first != null) {
                    rebuildTotalPath(iterator.first);
                }
            }
        }
    }
}
