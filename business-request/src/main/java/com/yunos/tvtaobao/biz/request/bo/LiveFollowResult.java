package com.yunos.tvtaobao.biz.request.bo;

/**
 * Created by linmu on 2018/8/28.
 */

public class LiveFollowResult {
        private String accountName;
        private String followAccount;
        private String subscribe;
        private String toastMsg;
        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }
        public String getAccountName() {
            return accountName;
        }

        public void setFollowAccount(String followAccount) {
            this.followAccount = followAccount;
        }
        public String getFollowAccount() {
            return followAccount;
        }

        public void setSubscribe(String subscribe) {
            this.subscribe = subscribe;
        }
        public String getSubscribe() {
            return subscribe;
        }

        public void setToastMsg(String toastMsg) {
            this.toastMsg = toastMsg;
        }
        public String getToastMsg() {
            return toastMsg;
        }

    }
