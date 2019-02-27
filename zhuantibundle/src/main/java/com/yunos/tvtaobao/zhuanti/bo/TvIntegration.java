package com.yunos.tvtaobao.zhuanti.bo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenjiajuan on 17/4/21.
 */

public class TvIntegration implements Serializable {
    public List<TvIntegrationItem> result;

    public static class TvIntegrationItem{
        public String id;
        public String title;
        public String pointSchemeId;
        public long showAt;
        public   long beginAt;
        public long endAt;

        @Override
        public String toString() {
            return "TvIntegrationItem{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", pointSchemeId='" + pointSchemeId + '\'' +
                    ", showAt=" + showAt +
                    ", beginAt=" + beginAt +
                    ", endAt=" + endAt +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TvIntegration{" +
                "data=" + result.toString() +
                '}';
    }
}
