package com.yunos.tvtaobao.biz.request.core;


/**
 * 系统代码
 * @author tianxiang
 * @date 2012-10-8 15:12:23
 */
public enum ServiceCode {
    NET_WORK_ERROR(1, "网络连接错误"),
    HTTP_ERROR(100, "网络连接错误或读取数据超时"),
    DATA_PARSE_ERROR(101, "数据解析错误"),
    SERVICE_OK(200, "成功"),
    API_NOT_LOGIN(102, "您尚未登录淘宝账号，请先登录。"),
    CLIENT_LOGIN_ERROR(901,"用户登录失败"),//客户端登录失败，与未登录区分开
    API_NO_PERMISSION(103, "没有权限"),
    API_SID_INVALID(104, "您登录的账号已过期,请重新登录或者换账号登录。"),
    API_NO_DATA(105, "没有数据"),
    API_ERROR(106, "获取数据失败，请稍后重试"),
    API_ERROR_HTTP(419, "HTTP网络请求出错"),
    API_ERRCODE_AUTH_REJECT(107, "亲，你的网络不给力啊。"),
    USER_PASSWORD_NOT_MATCH(300, "您输入的密码和账户名不匹配。"),
    USER_IS_TAOBAO_EMPLOYEE(301, "你好像是小二吧，请换个账号吧"),
    USER_NEED_CHECK_CODE(302, "需要校验码"),
    USER_STAT_USER_DELETE(304, "此会员已被删除。"),
    USER_STAT_USER_FROZEN(305, "用户已被冻结。"),
    USER_GET_APPTOKEN_FAIL(306, "动态密钥读取失败，请重新尝试登录。"),
    USER_NOT_FOUND(307, "该账户名不存在"),
    USER_CHECKCODE_INCORRECT(308, "验证码错误，请重新输入。"),
    PUSH_MESSAGE_REG_ERROR(309, "注册DeviceId失败"),
    PUSH_MESSAGE_ERROR_DEVICE_ID(310, "DeviceId需要重新注册"),
    USER_LOGIN_OTHER_FAIL(333, "登陆失败，请重新尝试登录。"),
    NO_ADDRESS(334, "您还没有设置收货地址，请先到淘宝网设置收货地址！"),
    ORDER_MORE(335,"您的未付款订单过多。"),
    SESSION_DATAOUT(336, "登陆超时，请重新尝试登录。"),
    ADD_CART_FAILURE(337, "加入购物车失败"),
    BONUS_BALANCES_INSUFFICIENT(338, "红包余额不够本次支付！"),
    REPEAT_CREATE(1142, "亲，您今天的一次抢购机会已经用完了，明天再来吧"),
    BUYER_ALIPAY_NOT_FOUND(10000, "您的账号登录已过期请重新登录，或者换账号登录。"),
    CREATE_ALIPAY_ORDER_ERROR(10001, "请绑定支付宝账号或检查支付宝账号状态是否正常"),
    //小二？你可不能买, 发扬小二精神吧, 把有限的商品让给无限的用户吧：）
    TAOBAO_CLERK_NOT_BUYER(10010, "小二？你可不能买, 发扬小二精神吧, 把有限的商品让给无限的用户吧：）"),

    //TODO 增加错误情况
    //MtopSdk 内部错误
    ANDROID_SYS_NO_NETWORK(1, "网络连接错误"),
    ANDROID_SYS_NETWORK_ERROR(100, "网络连接错误或读取数据超时"),
    FAIL_SYS_SESSION_EXPIRED(104, "您登录的账号已过期,请重新登录或者换账号登录。"),
    FAIL_SYS_ILEGEL_SIGN(104, "您登录的账号已过期,请重新登录或者换账号登录。"),
    ANDROID_SYS_JSONDATA_BLANK(500, "返回数据为空"),
    ANDROID_SYS_JSONDATA_PARSE_ERROR(501, "解析数据错误"),
    ANDROID_SYS_MTOPREQUEST_INVALID_ERROR(502, "非法请求"),
    ANDROID_SYS_MTOPPROXYBASE_INIT_ERROR(503,"初始化失败"),
    ANDROID_SYS_GENERATE_MTOP_SIGN_ERROR(504, "签名失败"),
    ANDROID_SYS_API_FLOW_LIMIT_LOCKED(505, "哎哟喂,被挤爆啦,请稍后重试"),
    ANDROID_SYS_API_41X_ANTI_ATTACK(506, "哎哟喂,被挤爆啦,请稍后重试"),
    ANDROID_SYS_MTOP_APICALL_ASYNC_TIMEOUT(507, "请求超时"),
    FAIL_BIZ_PERSON_LIMIT_EXCEED(508, "已达到单用户购买数量上限,请看看其他商品吧"),
    DUPLICATED_ORDER_ERROR(509, "请勿重复提交订单");

    private ServiceCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private int code;

    private String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
