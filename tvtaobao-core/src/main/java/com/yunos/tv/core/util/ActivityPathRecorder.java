package com.yunos.tv.core.util;

import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.yunos.tv.core.common.AppDebug;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhujun on 28/02/2017.
 */

public class ActivityPathRecorder {

    private final String TAG = ActivityPathRecorder.class.getSimpleName();

    private final Object nodeLock = new Object();

    public interface PathNode {
        Uri getCurrentUri();

        boolean isIgnored();

        boolean isFirstNode();

        int getHashCode();

        int getSecondHashCode();

        int getPreviousSecondHashCode();

        int getPreviousNodeHash();

        Uri getPreviousNodeUri();

        boolean recordNewIntent();

    }

    public interface PathFilter {
        void filterPath(List<ActivityNodeInfo> originPath);
    }

    private SparseArray<PathFilter> mFilters = new SparseArray<PathFilter>();

    public void applyFilter(@NonNull PathFilter filter, @NonNull String filterName) {
        mFilters.append(filterName.hashCode(), filter);
    }

    public void removeFilter(@NonNull String filterName) {
        mFilters.remove(filterName.hashCode());
    }


    public static final String INTENTKEY_PATHRECORDER_PREVIOUSACTIVITY = "pr_previous_activity";
    public static final String INTENTKEY_PATHRECORDER_PREVIOUSURI = "pr_previous_uri";
    public static final String INTENTKEY_PATHRECORDER_PREVIOUSINTENT = "pr_previous_intent";
    public static final String INTENTKEY_PATHRECORDER_URI = "pr_input_uri";
    public static final String INTENTKEY_FIRST = "pr_is_first";

    private ActivityPathRecorder() {
        applyFilter(new ShopPathFilter(), "shopFilter");
        applyFilter(new RelativeCommentPathFilter(), "relativeRecommend");
        task = new TimerTask() {
            @Override
            public void run() {
                gc();
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 15000, 15000);

    }

    private static final Object initLock = new Object();

    private static ActivityPathRecorder instance;

    private TimerTask task;
    private Timer timer;

    public static ActivityPathRecorder getInstance() {

        if (instance == null) {
            synchronized (initLock) {
                if (instance == null)
                    instance = new ActivityPathRecorder();
            }
        }
        return instance;

    }

    private SparseArray<SparseArray<ActivityNodeInfo>> nodeTable = new SparseArray<SparseArray<ActivityNodeInfo>>();
    private SparseIntArray destroyedHashCodes = new SparseIntArray();

    public void onDestroy(PathNode node) {
        /*
         * do nothing but clean up node tables
         */

        tryRemove(node);
    }

    private void tryRemove(PathNode node) {
        AppDebug.d(TAG, "try remove node:" + node);
        ActivityNodeInfo info = getNodeForActivity(node);
        if (info == null)
            return;
        removeInfoNode(info);
    }

    private void removeInfoNode(ActivityNodeInfo info) {
        SparseArray<ActivityNodeInfo> list = nodeTable.get(info.activityHashCode);
        if (list == null || list.get(info.secondaryHash) == null)
            return;
        ActivityNodeInfo nodeInfo = list.get(info.secondaryHash);
        if (nodeInfo.hasChildren()) {
            destroyedHashCodes.put(nodeInfo.activityHashCode, nodeInfo.activityHashCode);
            return;
        }
        AppDebug.d(TAG, "removing node:" + info.getUri());
        list.remove(nodeInfo.secondaryHash);
        ActivityNodeInfo pre = findPrevNode(info);
        if (pre != null)
            pre.removeChild(info);
        if (list.size() == 0) {
            synchronized (nodeLock) {
                nodeTable.remove(info.activityHashCode);
            }
        } else {
            destroyedHashCodes.put(info.activityHashCode, info.activityHashCode);
        }
    }

    private void gc() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IllegalThreadStateException("should be called outside main thread");
        }
        AppDebug.d(TAG, "running gc");
        for (int i = destroyedHashCodes.size() - 1; i >= 0; i--) {
            int hashCode = destroyedHashCodes.valueAt(i);
            SparseArray<ActivityNodeInfo> list = nodeTable.get(hashCode);
            if (list == null || list.size() == 0) {
                destroyedHashCodes.removeAt(i);
                continue;
            }
            for (int j = list.size() - 1; j >= 0; j--) {
                ActivityNodeInfo nodeInfo = list.valueAt(j);
                if (nodeInfo.hasChildren()) continue;
                AppDebug.d(TAG, "gc remove node:" + nodeInfo.getUri() + ":" + nodeInfo.getActivityHashCode());
                list.removeAt(j);
                ActivityNodeInfo preNode = findPrevNode(nodeInfo);
                if (preNode != null) preNode.removeChild(nodeInfo);
            }
            if (list.size() == 0) {
                synchronized (nodeLock) {
                    AppDebug.d(TAG, "gc remove list:" + hashCode);
                    nodeTable.remove(hashCode);
                }
                destroyedHashCodes.removeAt(i);
            }
        }
        AppDebug.d(TAG, "running gc complete");
        AppDebug.d(TAG, "after gc : nodeTable.size:" + nodeTable.size());
    }


    private void addPath(PathNode node) {
        AppDebug.w(TAG, "addPath node " + node);
        AppDebug.w(TAG, "addPath isIgnored " + node.isIgnored());
        if (node.isIgnored())
            return;

        ActivityNodeInfo info = new ActivityNodeInfo(node);
        AppDebug.w(TAG, "addPath isFirstNode " + node.isFirstNode());
        if (node.isFirstNode()) {
            info.setPreviousActivityHashCode(-1);
        } else {
            AppDebug.w(TAG, "addPath className : " + node.getClass().getSimpleName());
            AppDebug.e(TAG, "addPath previousActivity : " + node.getPreviousNodeHash() + "  ,prevUri : " + node.getPreviousNodeUri());
            int previousActivity = node.getPreviousNodeHash();
            Uri prevUri = node.getPreviousNodeUri();
            info.setPreviousActivityHashCode(previousActivity);
            info.setPreviousActivityUri(prevUri);
            info.previousIntentHashCode = node.getPreviousSecondHashCode();
        }
        ActivityNodeInfo pre = findPrevNode(info);
        if (pre != null)
            pre.addChild(info);
        AppDebug.w(TAG, "addPath previousActivityUri " + info.previousActivityUri);
        SparseArray<ActivityNodeInfo> list = nodeTable.get(info.getActivityHashCode());
        AppDebug.w(TAG, "addPath list " + list);
        if (list == null) {
            list = new SparseArray<ActivityNodeInfo>();
            synchronized (nodeLock) {
                nodeTable.put(info.getActivityHashCode(), list);
            }
        }
        AppDebug.d(TAG, "add node:" + node.getClass().getSimpleName() + node.hashCode() + ",uri " + info.getUri() + " previousActivity:" + info.previousActivityHashCode + " preuri:" + info.previousActivityUri);
        AppDebug.i(TAG, "------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        list.put(info.secondaryHash, info);
    }

    public void recordPathNode(PathNode node) {
        addPath(node);
    }

    private ActivityNodeInfo getNodeForActivity(PathNode node) {
        SparseArray<ActivityNodeInfo> list = nodeTable.get(node.getHashCode());
        AppDebug.d(TAG, "getNodeForActivity:" + list);
        if (list == null || list.size() == 0)
            return null;
//        if (list.size() == 1)
//            return list.get(0);//TODO not sure if should return the only node without check
        return list.get(node.getSecondHashCode());
    }

    private ActivityNodeInfo findPrevNode(ActivityNodeInfo info) {
        SparseArray<ActivityNodeInfo> activityList = nodeTable.get(info.previousActivityHashCode);
        if (activityList == null || activityList.size() == 0) {
            return null;
        }
        AppDebug.i(TAG, "findPrev: info " + info.getActivityHashCode() + " uri:" + info.previousActivityUri);
        //TODO change to sparseArray.get()
        return activityList.get(info.previousIntentHashCode);
    }

    /**
     * 获取当前activity为终点的路径
     *
     * @param node
     * @return
     */
    public List<String> getCurrentPath(PathNode node) {
        List<ActivityNodeInfo> list = new ArrayList<ActivityNodeInfo>();
        ActivityNodeInfo info = getNodeForActivity(node);
        if (info == null)
            return new ArrayList<String>();
        list.add(info);

        while (-1 != info.previousActivityHashCode) {
            ActivityNodeInfo pre = findPrevNode(info);
            if (pre == null)
                break;
            list.add(pre);
            info = pre;
        }
        for (int i = 0; i < mFilters.size(); i++) {
            PathFilter filter = mFilters.valueAt(i);
            filter.filterPath(list);
        }
        List<String> path = new ArrayList<String>();
        for (int j = 0; j < list.size(); j++) {
            ActivityNodeInfo nodeInfo = list.get(j);
            //TODO : 目前去掉聚划算部分null跳转
            if (nodeInfo.getUri() == null)
                continue;
            if (nodeInfo.getUri().toString().contains("module=detail"))
                continue;

            path.add(nodeInfo.getUri() + "");
        }
        list.clear();
        return path;
    }


    public static final class ActivityNodeInfo {
        private Uri uri;
        private int activityHashCode;
        private int previousIntentHashCode = -1;
        private int previousActivityHashCode = -1;
        private Uri previousActivityUri;
        private int secondaryHash = -1;

        private SparseArray<ActivityNodeInfo> children;

        public boolean hasChildren() {
            return children != null && children.size() > 0;
        }

        public void addChild(ActivityNodeInfo info) {
            if (info == this)
                return;
            if (info.previousActivityHashCode != this.activityHashCode)
                return;
            if (children == null)
                children = new SparseArray<ActivityNodeInfo>();
            children.put(info.secondaryHash, info);
        }

        public void removeChild(ActivityNodeInfo info) {
            if (info.previousActivityHashCode != this.activityHashCode)
                return;
            if (children != null)
                children.remove(info.secondaryHash);
        }

        public Uri getUri() {
            return uri;
        }

        private int getActivityHashCode() {
            return activityHashCode;
        }

        private void setPreviousActivityUri(Uri previousActivityUri) {
            this.previousActivityUri = previousActivityUri;
        }

        private ActivityNodeInfo(@NonNull PathNode node) {
            setActivity(node);
        }

        private void setActivity(@NonNull PathNode node) {
            this.activityHashCode = node.getHashCode();
            this.uri = node.getCurrentUri();
            this.secondaryHash = node.getSecondHashCode();

        }

        private void setPreviousActivityHashCode(int previousActivityHashCode) {
            this.previousActivityHashCode = previousActivityHashCode;
        }

//        @Override
//        public int hashCode() {
//            if (uri == null)
//                return ("" + activityHashCode).hashCode();
//            return (activityHashCode + uri.toString()).hashCode();
//        }

        //TODO improve
        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof ActivityNodeInfo)) {
                return false;
            }
            if (hashCode() == o.hashCode())
                return uri.equals(((ActivityNodeInfo) o).getUri());
            return false;
        }
    }

    /**
     * filter the origin path and remove redundant path nodes such as shop->detail1->shop->detail2
     * and return the filtered path as shop->detail2
     */
    private static class ShopPathFilter implements PathFilter {
        @Override
        public void filterPath(List<ActivityNodeInfo> originPath) {
            ActivityNodeInfo shopNode = null;
            Log.e("ShopPathFilter", "original path:" + originPath.size());
            ListIterator<ActivityNodeInfo> iterator = originPath.listIterator();
            while (iterator.hasNext()) {
                ActivityNodeInfo info = iterator.next();
                Log.e("ShopPathFilter", "original uri:" + info.getUri());
                if (info.getUri() == null)
                    continue;
                if ("detail".equals(info.getUri().getQueryParameter("module"))) {//begin searching
                    if (!iterator.hasNext())
                        continue;
                    ActivityNodeInfo info1 = iterator.next();
                    if (info1.getUri() == null)
                        continue;
                    if ("shop".equals(info1.getUri().getQueryParameter("module")) && info.previousActivityHashCode == info1.getActivityHashCode()) {
                        if (shopNode == null) {
                            shopNode = info1;
                        } else {
                            String shopId = shopNode.getUri().getQueryParameter("shopId");
                            if (TextUtils.equals(shopId, info1.getUri().getQueryParameter("shopId"))) {

                                //remove shop
                                Log.d("ShopPathFilter", "remove current node");
                                iterator.remove();
                                Log.d("ShopPathFilter", "removed path1:" + originPath.size());


                                //remove detail
                                Log.d("ShopPathFilter", "remove previous node");
                                iterator.previous();
                                iterator.remove();
                                Log.d("ShopPathFilter", "removed path2:" + originPath.size());
                            } else {
                                shopNode = info1;//case: d1<-s1<-d2<-s2<-d3<-s2
                                //                              ~~~~~~~~~~~~~~~
                            }
                        }
                    } else {
                        shopNode = null;//reset and continue searching
                    }
                } else {
                    shopNode = null;//reset
                }
            }
            Log.d("ShopPathFilter", "filtered path:" + originPath.size());
        }
    }

    /**
     * 详情页相关推荐跳转，清空
     */
    private static class RelativeCommentPathFilter implements PathFilter {
        @Override
        public void filterPath(List<ActivityNodeInfo> originPath) {

            ListIterator<ActivityNodeInfo> iterator = originPath.listIterator();
            while (iterator.hasNext()) {
                ActivityNodeInfo info = iterator.next();
                if (info.getUri() != null && "relative_recomment".equals(info.getUri().getQueryParameter("module"))) {
                    originPath.clear();
                    return;
                }
            }
        }
    }

}
