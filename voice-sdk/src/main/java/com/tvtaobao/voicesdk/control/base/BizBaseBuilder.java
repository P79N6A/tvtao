package com.tvtaobao.voicesdk.control.base;

import com.tvtaobao.voicesdk.control.BuyIndexControl;
import com.tvtaobao.voicesdk.control.CheckBillControl;
import com.tvtaobao.voicesdk.control.GoodsSearchControl;
import com.tvtaobao.voicesdk.control.LogisticsControl;
import com.tvtaobao.voicesdk.control.OpenIndexControl;
import com.tvtaobao.voicesdk.control.PageIntentControl;
import com.tvtaobao.voicesdk.control.TakeOutAgainControl;
import com.tvtaobao.voicesdk.control.TakeOutProgressControl;
import com.tvtaobao.voicesdk.control.TakeOutSearchControl;
import com.tvtaobao.voicesdk.type.DomainType;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/4/20
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class BizBaseBuilder {

    public static BizBaseControl builder(String intent) {
        switch (intent) {
            case DomainType.TYPE_SEARCH_GOODS:
                return new GoodsSearchControl();
            case DomainType.TYPE_CHECK_BILL:
                return new CheckBillControl();
            case DomainType.TYPE_CHECK_ORDER:
                return new LogisticsControl();
            case DomainType.TAKEOUT_SEARCH:
                return new TakeOutSearchControl();
            case DomainType.TAKEOUT_AGAIN:
                return new TakeOutAgainControl();
            case DomainType.TAKEOUT_PROGRESS:
                return new TakeOutProgressControl();
            case DomainType.TAKEOUT_GOTO_INDEX:
            case DomainType.TYPE_OPEN_PAGE:
                return new PageIntentControl();
            case DomainType.TYPE_BUY_INDEX:
                return new BuyIndexControl();
            case DomainType.TYPE_OPEN_INDEX:
                return new OpenIndexControl();
//                    case DomainType.TYPE_EXIT_TVTAOBAO:
//                        try {
//                            DialogManager.getManager().dismissAllDialog();
//                            ActivityQueueManager.getInstance().onClearAllMapActivityList();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        } finally {
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                            System.exit(0);
//                        }
//                        break;
            default:
                return null;
        }
    }
}
