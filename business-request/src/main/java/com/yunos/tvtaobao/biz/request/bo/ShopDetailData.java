package com.yunos.tvtaobao.biz.request.bo;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenjiajuan on 17/12/15.
 *
 * @describe
 */

public class ShopDetailData implements Serializable {

    // 合并分页请求数据
    public void marge(ShopDetailData data) {
        if(data ==null) return;
        if(genreIds ==null || genreIds.length() == 0){
            genreIds = data.genreIds;
        }
        if(haveNext ==null || haveNext.length() == 0){
            haveNext = data.haveNext;
        }
        if(pageNo ==null || pageNo.length() == 0){
            pageNo = data.pageNo;
        }
        if(pageSize ==null || pageSize.length() == 0){
            pageSize = data.pageSize;
        }
        if(genreIds ==null || genreIds.length() == 0){
            genreIds = data.genreIds;
        }
        if(totalNum ==null || totalNum.length() == 0){
            totalNum = data.totalNum;
        }
        if(totalPage ==null || totalPage.length() == 0){
            totalPage = data.totalPage;
        }

        // 合并 店铺信息
        if(storeDetailDTO ==null ){
            storeDetailDTO = data.storeDetailDTO;
        }else {
            if(TextUtils.isEmpty(storeDetailDTO.shopId) && data.storeDetailDTO!=null){
                storeDetailDTO = data.storeDetailDTO;
            }
        }

        // 合并 优惠信息
        if(voucher ==null){
            voucher = data.voucher;
        }else {
            if(TextUtils.isEmpty(voucher.deductDesc)){
                voucher = data.voucher;
            }
        }

        // 合并 商品信息
        if(itemGenreWithItemsList ==null ){
            itemGenreWithItemsList = data.itemGenreWithItemsList;
        }else {
            if(data.itemGenreWithItemsList!=null){
                itemGenreWithItemsList.addAll(data.itemGenreWithItemsList);
            }
        }

    }


    private String genreIds;
    private String haveNext;
    private String pageNo;
    private String pageSize;

    private String totalNum;
    private String totalPage;

    private StoreDetailDTOBean storeDetailDTO;
    private VoucherBean voucher;
    private List<ItemGenreWithItemsListBean> itemGenreWithItemsList;

    public String getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(String genreIds) {
        this.genreIds = genreIds;
    }

    public String getHaveNext() {
        return haveNext;
    }

    public void setHaveNext(String haveNext) {
        this.haveNext = haveNext;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public StoreDetailDTOBean getStoreDetailDTO() {
        return storeDetailDTO;
    }

    public void setStoreDetailDTO(StoreDetailDTOBean storeDetailDTO) {
        this.storeDetailDTO = storeDetailDTO;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public VoucherBean getVoucher() {
        return voucher;
    }

    public void setVoucher(VoucherBean voucher) {
        this.voucher = voucher;
    }

    public List<ItemGenreWithItemsListBean> getItemGenreWithItemsList() {
        return itemGenreWithItemsList;
    }

    public void setItemGenreWithItemsList(List<ItemGenreWithItemsListBean> itemGenreWithItemsList) {
        this.itemGenreWithItemsList = itemGenreWithItemsList;
    }

    public static class StoreDetailDTOBean implements Serializable {
        private String addressText;
        private String agentFee;
        private String alreadyBought;
        private AttributesBean attributes;
        private String averageDeliverTime;
        private String busyLevel;
        private String categoryIds;
        private String city;
        private String currentDeliveryRule;
        private String dataSource;
        private String deliverAmount;
        private String deliverSpent;
        private String deliverTime;
        private String description;
        private String distRst;
        private String distance;
        private String entityType;
        private String foodSafetyCertificateImage;
        private String index;
        private String intervalRestTime;
        private String invoice;
        private String invoiceMinAmount;
        private String isBookable;
        private String latitude;
        private String licenseImage;
        private String longitude;
        private String mobile;
        private String name;
        private String newRestaurant;
        private String noAgentFeeTotal;
        private String notice;
        private String onlinePayment;
        private String orderLimit;
        private String outId;
        private String overArea;
        private String payOnDelivery;
        private String perCapitaPrice;
        private String premium;
        private String reportAndAdviceUrl;
        private String restShowText;
        private String saleCount;
        private String sellerId;
        private String serviceId;
        private String serviceLicenseImage;
        private String shopId;
        private String shopLogo;
        private String shopStatus;
        public ShopStatusDetail shopStatusDetail;
        private String shopStatusIcon;
        private ShopStatusIconMapBean shopStatusIconMap;
        private String starLevel;
        private String starPicUrl;
        private String storeClosed;
        private String storeId;
        private String supportInsurance;
        private String timeEnsure;
        private String valid;
        private WillRestStatusDetailBean willRestStatusDetail;
        private String willResting;
        private List<ActivityListBean> activityList;
        private List<String> phoneList;
        private List<ServiceListBean> serviceList;
        private List<String> servingTime;

        public class ShopStatusDetail implements Serializable {
            public String backGroundColor;
            public String color;
            public String statusDesc;
        }

        public String getAddressText() {
            return addressText;
        }

        public void setAddressText(String addressText) {
            this.addressText = addressText;
        }

        public String getAgentFee() {
            return agentFee;
        }

        public void setAgentFee(String agentFee) {
            this.agentFee = agentFee;
        }

        public String getAlreadyBought() {
            return alreadyBought;
        }

        public void setAlreadyBought(String alreadyBought) {
            this.alreadyBought = alreadyBought;
        }

        public AttributesBean getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBean attributes) {
            this.attributes = attributes;
        }

        public String getAverageDeliverTime() {
            return averageDeliverTime;
        }

        public void setAverageDeliverTime(String averageDeliverTime) {
            this.averageDeliverTime = averageDeliverTime;
        }

        public String getBusyLevel() {
            return busyLevel;
        }

        public void setBusyLevel(String busyLevel) {
            this.busyLevel = busyLevel;
        }

        public String getCategoryIds() {
            return categoryIds;
        }

        public void setCategoryIds(String categoryIds) {
            this.categoryIds = categoryIds;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCurrentDeliveryRule() {
            return currentDeliveryRule;
        }

        public void setCurrentDeliveryRule(String currentDeliveryRule) {
            this.currentDeliveryRule = currentDeliveryRule;
        }

        public String getDataSource() {
            return dataSource;
        }

        public void setDataSource(String dataSource) {
            this.dataSource = dataSource;
        }

        public String getDeliverAmount() {
            return deliverAmount;
        }

        public void setDeliverAmount(String deliverAmount) {
            this.deliverAmount = deliverAmount;
        }

        public String getDeliverSpent() {
            return deliverSpent;
        }

        public void setDeliverSpent(String deliverSpent) {
            this.deliverSpent = deliverSpent;
        }

        public String getDeliverTime() {
            return deliverTime;
        }

        public void setDeliverTime(String deliverTime) {
            this.deliverTime = deliverTime;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDistRst() {
            return distRst;
        }

        public void setDistRst(String distRst) {
            this.distRst = distRst;
        }

        public String getDistance() {
            return distance;
        }

        public void setDistance(String distance) {
            this.distance = distance;
        }

        public String getEntityType() {
            return entityType;
        }

        public void setEntityType(String entityType) {
            this.entityType = entityType;
        }

        public String getFoodSafetyCertificateImage() {
            return foodSafetyCertificateImage;
        }

        public void setFoodSafetyCertificateImage(String foodSafetyCertificateImage) {
            this.foodSafetyCertificateImage = foodSafetyCertificateImage;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getIntervalRestTime() {
            return intervalRestTime;
        }

        public void setIntervalRestTime(String intervalRestTime) {
            this.intervalRestTime = intervalRestTime;
        }

        public String getInvoice() {
            return invoice;
        }

        public void setInvoice(String invoice) {
            this.invoice = invoice;
        }

        public String getInvoiceMinAmount() {
            return invoiceMinAmount;
        }

        public void setInvoiceMinAmount(String invoiceMinAmount) {
            this.invoiceMinAmount = invoiceMinAmount;
        }

        public String getIsBookable() {
            return isBookable;
        }

        public void setIsBookable(String isBookable) {
            this.isBookable = isBookable;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLicenseImage() {
            return licenseImage;
        }

        public void setLicenseImage(String licenseImage) {
            this.licenseImage = licenseImage;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNewRestaurant() {
            return newRestaurant;
        }

        public void setNewRestaurant(String newRestaurant) {
            this.newRestaurant = newRestaurant;
        }

        public String getNoAgentFeeTotal() {
            return noAgentFeeTotal;
        }

        public void setNoAgentFeeTotal(String noAgentFeeTotal) {
            this.noAgentFeeTotal = noAgentFeeTotal;
        }

        public String getNotice() {
            return notice;
        }

        public void setNotice(String notice) {
            this.notice = notice;
        }

        public String getOnlinePayment() {
            return onlinePayment;
        }

        public void setOnlinePayment(String onlinePayment) {
            this.onlinePayment = onlinePayment;
        }

        public String getOrderLimit() {
            return orderLimit;
        }

        public void setOrderLimit(String orderLimit) {
            this.orderLimit = orderLimit;
        }

        public String getOutId() {
            return outId;
        }

        public void setOutId(String outId) {
            this.outId = outId;
        }

        public String getOverArea() {
            return overArea;
        }

        public void setOverArea(String overArea) {
            this.overArea = overArea;
        }

        public String getPayOnDelivery() {
            return payOnDelivery;
        }

        public void setPayOnDelivery(String payOnDelivery) {
            this.payOnDelivery = payOnDelivery;
        }

        public String getPerCapitaPrice() {
            return perCapitaPrice;
        }

        public void setPerCapitaPrice(String perCapitaPrice) {
            this.perCapitaPrice = perCapitaPrice;
        }

        public String getPremium() {
            return premium;
        }

        public void setPremium(String premium) {
            this.premium = premium;
        }

        public String getReportAndAdviceUrl() {
            return reportAndAdviceUrl;
        }

        public void setReportAndAdviceUrl(String reportAndAdviceUrl) {
            this.reportAndAdviceUrl = reportAndAdviceUrl;
        }

        public String getRestShowText() {
            return restShowText;
        }

        public void setRestShowText(String restShowText) {
            this.restShowText = restShowText;
        }

        public String getSaleCount() {
            return saleCount;
        }

        public void setSaleCount(String saleCount) {
            this.saleCount = saleCount;
        }

        public String getSellerId() {
            return sellerId;
        }

        public void setSellerId(String sellerId) {
            this.sellerId = sellerId;
        }

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getServiceLicenseImage() {
            return serviceLicenseImage;
        }

        public void setServiceLicenseImage(String serviceLicenseImage) {
            this.serviceLicenseImage = serviceLicenseImage;
        }

        public String getShopId() {
            return shopId;
        }

        public void setShopId(String shopId) {
            this.shopId = shopId;
        }

        public String getShopLogo() {
            return shopLogo;
        }

        public void setShopLogo(String shopLogo) {
            this.shopLogo = shopLogo;
        }

        public String getShopStatus() {
            return shopStatus;
        }

        public void setShopStatus(String shopStatus) {
            this.shopStatus = shopStatus;
        }

        public String getShopStatusIcon() {
            return shopStatusIcon;
        }

        public void setShopStatusIcon(String shopStatusIcon) {
            this.shopStatusIcon = shopStatusIcon;
        }

        public ShopStatusIconMapBean getShopStatusIconMap() {
            return shopStatusIconMap;
        }

        public void setShopStatusIconMap(ShopStatusIconMapBean shopStatusIconMap) {
            this.shopStatusIconMap = shopStatusIconMap;
        }

        public String getStarLevel() {
            return starLevel;
        }

        public void setStarLevel(String starLevel) {
            this.starLevel = starLevel;
        }

        public String getStarPicUrl() {
            return starPicUrl;
        }

        public void setStarPicUrl(String starPicUrl) {
            this.starPicUrl = starPicUrl;
        }

        public String getStoreClosed() {
            return storeClosed;
        }

        public void setStoreClosed(String storeClosed) {
            this.storeClosed = storeClosed;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getSupportInsurance() {
            return supportInsurance;
        }

        public void setSupportInsurance(String supportInsurance) {
            this.supportInsurance = supportInsurance;
        }

        public String getTimeEnsure() {
            return timeEnsure;
        }

        public void setTimeEnsure(String timeEnsure) {
            this.timeEnsure = timeEnsure;
        }

        public String getValid() {
            return valid;
        }

        public void setValid(String valid) {
            this.valid = valid;
        }

        public WillRestStatusDetailBean getWillRestStatusDetail() {
            return willRestStatusDetail;
        }

        public void setWillRestStatusDetail(WillRestStatusDetailBean willRestStatusDetail) {
            this.willRestStatusDetail = willRestStatusDetail;
        }

        public String getWillResting() {
            return willResting;
        }

        public void setWillResting(String willResting) {
            this.willResting = willResting;
        }

        public List<ActivityListBean> getActivityList() {
            return activityList;
        }

        public void setActivityList(List<ActivityListBean> activityList) {
            this.activityList = activityList;
        }

        public List<String> getPhoneList() {
            return phoneList;
        }

        public void setPhoneList(List<String> phoneList) {
            this.phoneList = phoneList;
        }

        public List<ServiceListBean> getServiceList() {
            return serviceList;
        }

        public void setServiceList(List<ServiceListBean> serviceList) {
            this.serviceList = serviceList;
        }

        public List<String> getServingTime() {
            return servingTime;
        }

        public void setServingTime(List<String> servingTime) {
            this.servingTime = servingTime;
        }

        public static class AttributesBean implements Serializable {
            private String service_id;
            private String recent_order_num;
            private String parent_category_ids;
            private String invoice;
            private String data_source;
            private String minosproduct_available_time;
            private String unit_price;
            private String total_status;
            private String image_url;
            private String description;
            private String out_category_ids;
            private String busy_level;
            private String name_for_url;
            private String catering_service_license;
            private String serving_time;
            private String currentDeliveryRule;
            private String brand_id;
            private String is_dist_rst;
            private String bizOuterId;
            private String brand_info;
            private String authen_status;
            private String deliver_spent;
            private String new_restaurant;
            private String online_payment;
            private String contact_number;
            private String num_ratings;
            private String updated_at;
            private String is_on_time;
            private String operation_labels;
            private String support_insurance;
            private String order_mode;
            private String is_premium;
            private String payment_method;
            private String is_phone_hidden;
            private String no_agent_fee_total;
            private String support_online;
            private String agent_fee;
            private String is_double_cert;
            private String businessTime;
            private String business_license;
            private String phone_list;
            private String book_time_bitmap;
            private String is_koubei_rst;
            private String is_valid;
            private String is_bookable;
            private String city_id;
            private String invoice_min_amount;
            private String open_time_bitmap;
            private String recent_sales;
            private String notice;
            private String order_activity_limit_counter;
            private String is_new_retail;
            private String zeroDeliveryRule;
            private String rst_category;
            private String ranking_score;
            private String category_ids;
            private String is_time_ensure;
            private String rst_category_ka;
            private String register_info;
            private String out_id;

            public String getService_id() {
                return service_id;
            }

            public void setService_id(String service_id) {
                this.service_id = service_id;
            }

            public String getRecent_order_num() {
                return recent_order_num;
            }

            public void setRecent_order_num(String recent_order_num) {
                this.recent_order_num = recent_order_num;
            }

            public String getParent_category_ids() {
                return parent_category_ids;
            }

            public void setParent_category_ids(String parent_category_ids) {
                this.parent_category_ids = parent_category_ids;
            }

            public String getInvoice() {
                return invoice;
            }

            public void setInvoice(String invoice) {
                this.invoice = invoice;
            }

            public String getData_source() {
                return data_source;
            }

            public void setData_source(String data_source) {
                this.data_source = data_source;
            }

            public String getMinosproduct_available_time() {
                return minosproduct_available_time;
            }

            public void setMinosproduct_available_time(String minosproduct_available_time) {
                this.minosproduct_available_time = minosproduct_available_time;
            }

            public String getUnit_price() {
                return unit_price;
            }

            public void setUnit_price(String unit_price) {
                this.unit_price = unit_price;
            }

            public String getTotal_status() {
                return total_status;
            }

            public void setTotal_status(String total_status) {
                this.total_status = total_status;
            }

            public String getImage_url() {
                return image_url;
            }

            public void setImage_url(String image_url) {
                this.image_url = image_url;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getOut_category_ids() {
                return out_category_ids;
            }

            public void setOut_category_ids(String out_category_ids) {
                this.out_category_ids = out_category_ids;
            }

            public String getBusy_level() {
                return busy_level;
            }

            public void setBusy_level(String busy_level) {
                this.busy_level = busy_level;
            }

            public String getName_for_url() {
                return name_for_url;
            }

            public void setName_for_url(String name_for_url) {
                this.name_for_url = name_for_url;
            }

            public String getCatering_service_license() {
                return catering_service_license;
            }

            public void setCatering_service_license(String catering_service_license) {
                this.catering_service_license = catering_service_license;
            }

            public String getServing_time() {
                return serving_time;
            }

            public void setServing_time(String serving_time) {
                this.serving_time = serving_time;
            }

            public String getCurrentDeliveryRule() {
                return currentDeliveryRule;
            }

            public void setCurrentDeliveryRule(String currentDeliveryRule) {
                this.currentDeliveryRule = currentDeliveryRule;
            }

            public String getBrand_id() {
                return brand_id;
            }

            public void setBrand_id(String brand_id) {
                this.brand_id = brand_id;
            }

            public String getIs_dist_rst() {
                return is_dist_rst;
            }

            public void setIs_dist_rst(String is_dist_rst) {
                this.is_dist_rst = is_dist_rst;
            }

            public String getBizOuterId() {
                return bizOuterId;
            }

            public void setBizOuterId(String bizOuterId) {
                this.bizOuterId = bizOuterId;
            }

            public String getBrand_info() {
                return brand_info;
            }

            public void setBrand_info(String brand_info) {
                this.brand_info = brand_info;
            }

            public String getAuthen_status() {
                return authen_status;
            }

            public void setAuthen_status(String authen_status) {
                this.authen_status = authen_status;
            }

            public String getDeliver_spent() {
                return deliver_spent;
            }

            public void setDeliver_spent(String deliver_spent) {
                this.deliver_spent = deliver_spent;
            }

            public String getNew_restaurant() {
                return new_restaurant;
            }

            public void setNew_restaurant(String new_restaurant) {
                this.new_restaurant = new_restaurant;
            }

            public String getOnline_payment() {
                return online_payment;
            }

            public void setOnline_payment(String online_payment) {
                this.online_payment = online_payment;
            }

            public String getContact_number() {
                return contact_number;
            }

            public void setContact_number(String contact_number) {
                this.contact_number = contact_number;
            }

            public String getNum_ratings() {
                return num_ratings;
            }

            public void setNum_ratings(String num_ratings) {
                this.num_ratings = num_ratings;
            }

            public String getUpdated_at() {
                return updated_at;
            }

            public void setUpdated_at(String updated_at) {
                this.updated_at = updated_at;
            }

            public String getIs_on_time() {
                return is_on_time;
            }

            public void setIs_on_time(String is_on_time) {
                this.is_on_time = is_on_time;
            }

            public String getOperation_labels() {
                return operation_labels;
            }

            public void setOperation_labels(String operation_labels) {
                this.operation_labels = operation_labels;
            }

            public String getSupport_insurance() {
                return support_insurance;
            }

            public void setSupport_insurance(String support_insurance) {
                this.support_insurance = support_insurance;
            }

            public String getOrder_mode() {
                return order_mode;
            }

            public void setOrder_mode(String order_mode) {
                this.order_mode = order_mode;
            }

            public String getIs_premium() {
                return is_premium;
            }

            public void setIs_premium(String is_premium) {
                this.is_premium = is_premium;
            }

            public String getPayment_method() {
                return payment_method;
            }

            public void setPayment_method(String payment_method) {
                this.payment_method = payment_method;
            }

            public String getIs_phone_hidden() {
                return is_phone_hidden;
            }

            public void setIs_phone_hidden(String is_phone_hidden) {
                this.is_phone_hidden = is_phone_hidden;
            }

            public String getNo_agent_fee_total() {
                return no_agent_fee_total;
            }

            public void setNo_agent_fee_total(String no_agent_fee_total) {
                this.no_agent_fee_total = no_agent_fee_total;
            }

            public String getSupport_online() {
                return support_online;
            }

            public void setSupport_online(String support_online) {
                this.support_online = support_online;
            }

            public String getAgent_fee() {
                return agent_fee;
            }

            public void setAgent_fee(String agent_fee) {
                this.agent_fee = agent_fee;
            }

            public String getIs_double_cert() {
                return is_double_cert;
            }

            public void setIs_double_cert(String is_double_cert) {
                this.is_double_cert = is_double_cert;
            }

            public String getBusinessTime() {
                return businessTime;
            }

            public void setBusinessTime(String businessTime) {
                this.businessTime = businessTime;
            }

            public String getBusiness_license() {
                return business_license;
            }

            public void setBusiness_license(String business_license) {
                this.business_license = business_license;
            }

            public String getPhone_list() {
                return phone_list;
            }

            public void setPhone_list(String phone_list) {
                this.phone_list = phone_list;
            }

            public String getBook_time_bitmap() {
                return book_time_bitmap;
            }

            public void setBook_time_bitmap(String book_time_bitmap) {
                this.book_time_bitmap = book_time_bitmap;
            }

            public String getIs_koubei_rst() {
                return is_koubei_rst;
            }

            public void setIs_koubei_rst(String is_koubei_rst) {
                this.is_koubei_rst = is_koubei_rst;
            }

            public String getIs_valid() {
                return is_valid;
            }

            public void setIs_valid(String is_valid) {
                this.is_valid = is_valid;
            }

            public String getIs_bookable() {
                return is_bookable;
            }

            public void setIs_bookable(String is_bookable) {
                this.is_bookable = is_bookable;
            }

            public String getCity_id() {
                return city_id;
            }

            public void setCity_id(String city_id) {
                this.city_id = city_id;
            }

            public String getInvoice_min_amount() {
                return invoice_min_amount;
            }

            public void setInvoice_min_amount(String invoice_min_amount) {
                this.invoice_min_amount = invoice_min_amount;
            }

            public String getOpen_time_bitmap() {
                return open_time_bitmap;
            }

            public void setOpen_time_bitmap(String open_time_bitmap) {
                this.open_time_bitmap = open_time_bitmap;
            }

            public String getRecent_sales() {
                return recent_sales;
            }

            public void setRecent_sales(String recent_sales) {
                this.recent_sales = recent_sales;
            }

            public String getNotice() {
                return notice;
            }

            public void setNotice(String notice) {
                this.notice = notice;
            }

            public String getOrder_activity_limit_counter() {
                return order_activity_limit_counter;
            }

            public void setOrder_activity_limit_counter(String order_activity_limit_counter) {
                this.order_activity_limit_counter = order_activity_limit_counter;
            }

            public String getIs_new_retail() {
                return is_new_retail;
            }

            public void setIs_new_retail(String is_new_retail) {
                this.is_new_retail = is_new_retail;
            }

            public String getZeroDeliveryRule() {
                return zeroDeliveryRule;
            }

            public void setZeroDeliveryRule(String zeroDeliveryRule) {
                this.zeroDeliveryRule = zeroDeliveryRule;
            }

            public String getRst_category() {
                return rst_category;
            }

            public void setRst_category(String rst_category) {
                this.rst_category = rst_category;
            }

            public String getRanking_score() {
                return ranking_score;
            }

            public void setRanking_score(String ranking_score) {
                this.ranking_score = ranking_score;
            }

            public String getCategory_ids() {
                return category_ids;
            }

            public void setCategory_ids(String category_ids) {
                this.category_ids = category_ids;
            }

            public String getIs_time_ensure() {
                return is_time_ensure;
            }

            public void setIs_time_ensure(String is_time_ensure) {
                this.is_time_ensure = is_time_ensure;
            }

            public String getRst_category_ka() {
                return rst_category_ka;
            }

            public void setRst_category_ka(String rst_category_ka) {
                this.rst_category_ka = rst_category_ka;
            }

            public String getRegister_info() {
                return register_info;
            }

            public void setRegister_info(String register_info) {
                this.register_info = register_info;
            }

            public String getOut_id() {
                return out_id;
            }

            public void setOut_id(String out_id) {
                this.out_id = out_id;
            }
        }

        public static class ShopStatusIconMapBean implements Serializable {
            /**
             * RESTING : https://gw.alicdn.com/tps/TB1qUeENpXXXXbMapXXXXXXXXXX-144-50.png
             * BOOKING : https://gw.alicdn.com/tps/TB1tv1INpXXXXXDapXXXXXXXXXX-144-50.png
             * SELLING : https://gw.alicdn.com/tps/TB14Ia4NpXXXXbIXFXXXXXXXXXX-144-50.png
             * WILLRESTING : https://gw.alicdn.com/tps/TB1O_qENpXXXXXFapXXXXXXXXXX-144-50.png
             */

            private String RESTING;
            private String BOOKING;
            private String SELLING;
            private String WILLRESTING;

            public String getRESTING() {
                return RESTING;
            }

            public void setRESTING(String RESTING) {
                this.RESTING = RESTING;
            }

            public String getBOOKING() {
                return BOOKING;
            }

            public void setBOOKING(String BOOKING) {
                this.BOOKING = BOOKING;
            }

            public String getSELLING() {
                return SELLING;
            }

            public void setSELLING(String SELLING) {
                this.SELLING = SELLING;
            }

            public String getWILLRESTING() {
                return WILLRESTING;
            }

            public void setWILLRESTING(String WILLRESTING) {
                this.WILLRESTING = WILLRESTING;
            }
        }

        public static class WillRestStatusDetailBean implements Serializable {
            /**
             * backGroundColor : #9bc2f0
             * color : #FFFFFF
             * statusDesc : 即将休息，商家22:30停止接单
             */

            private String backGroundColor;
            private String color;
            private String statusDesc;

            public String getBackGroundColor() {
                return backGroundColor;
            }

            public void setBackGroundColor(String backGroundColor) {
                this.backGroundColor = backGroundColor;
            }

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public String getStatusDesc() {
                return statusDesc;
            }

            public void setStatusDesc(String statusDesc) {
                this.statusDesc = statusDesc;
            }
        }

        public static class ActivityListBean implements Serializable {
            /**
             * description : 在线支付满25减24，满60减28，满99减45
             * fullReduceDetailDTOList : [{"offlineReduce":"0","onLineReduce":"2400","sumCondition":"2500"},{"offlineReduce":"0","onLineReduce":"2800","sumCondition":"6000"},{"offlineReduce":"0","onLineReduce":"4500","sumCondition":"9900"}]
             * icon : //gw.alicdn.com/tfs/TB1gQx4QpXXXXXPXVXXXXXXXXXX-42-42.png
             * id : 56942072
             * name : 满减活动
             * sortId : 8
             * storeId : 0
             * type : 102
             */

            private String description;
            private String icon;
            private String id;
            private String name;
            private String sortId;
            private String storeId;
            private String type;
            private List<FullReduceDetailDTOListBean> fullReduceDetailDTOList;

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getSortId() {
                return sortId;
            }

            public void setSortId(String sortId) {
                this.sortId = sortId;
            }

            public String getStoreId() {
                return storeId;
            }

            public void setStoreId(String storeId) {
                this.storeId = storeId;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<FullReduceDetailDTOListBean> getFullReduceDetailDTOList() {
                return fullReduceDetailDTOList;
            }

            public void setFullReduceDetailDTOList(List<FullReduceDetailDTOListBean> fullReduceDetailDTOList) {
                this.fullReduceDetailDTOList = fullReduceDetailDTOList;
            }

            public static class FullReduceDetailDTOListBean implements Serializable {
                /**
                 * offlineReduce : 0
                 * onLineReduce : 2400
                 * sumCondition : 2500
                 */

                private String offlineReduce;
                private String onLineReduce;
                private String sumCondition;

                public String getOfflineReduce() {
                    return offlineReduce;
                }

                public void setOfflineReduce(String offlineReduce) {
                    this.offlineReduce = offlineReduce;
                }

                public String getOnLineReduce() {
                    return onLineReduce;
                }

                public void setOnLineReduce(String onLineReduce) {
                    this.onLineReduce = onLineReduce;
                }

                public String getSumCondition() {
                    return sumCondition;
                }

                public void setSumCondition(String sumCondition) {
                    this.sumCondition = sumCondition;
                }
            }
        }

        public static class ServiceListBean implements Serializable {
            /**
             * description : 超时送达，立赔粮票
             * icon : https://gw.alicdn.com/tfs/TB1GXVQQpXXXXaKaXXXXXXXXXXX-42-42.png
             * name : 超时赔付
             * type : 3
             */

            private String description;
            private String icon;
            private String name;
            private String type;

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }
        }
    }

    public static class VoucherBean implements Serializable {
        /**
         * conditionDesc : 满￥50可用
         * deductDesc : ￥5
         * description : 商家代金券
         * icon : //gw.alicdn.com/tfs/TB15s7gRXXXXXbUXFXXXXXXXXXX-54-54.gif
         * status : 1
         * statusContent : 领取
         * storeId : 178720349
         * storeName : 梁小猴港式铁板炒饭（常二路店）
         */

        private String conditionDesc;
        private String deductDesc;
        private String description;
        private String icon;
        private String status;
        private String statusContent;
        private String storeId;
        private String storeName;

        public String getConditionDesc() {
            return conditionDesc;
        }

        public void setConditionDesc(String conditionDesc) {
            this.conditionDesc = conditionDesc;
        }

        public String getDeductDesc() {
            return deductDesc;
        }

        public void setDeductDesc(String deductDesc) {
            this.deductDesc = deductDesc;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusContent() {
            return statusContent;
        }

        public void setStatusContent(String statusContent) {
            this.statusContent = statusContent;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }
    }

    public static class ItemGenreWithItemsListBean implements Serializable {

        public enum FocusStatus {
            NO,
            FOCUS,
            TEMPLETE
        }

        public FocusStatus focusStatus;
        public int totleGoodCount;

        /**
         * id : 23265088
         * itemList : [{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545834024255","itemPicts":"//gw.alicdn.com/TLife/1513071278777/TB1EFL3h4rI8KJjy0FpK_65hVXa","multiAttr":{"attrList":[{"name":"甜度","value":["少糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1200","promotionPrice":"1200","promotioned":"false","saleCount":"4","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1200","promotionPrice":"0","quantity":"9989","skuId":"3537775644307","title":"中杯"},{"price":"1500","promotionPrice":"0","quantity":"9985","skuId":"3537775644308","title":"大杯"}],"soldMode":"1","status":"1","stock":"19974","storeId":"157891380","title":"阿华田"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545771634293","itemPicts":"//gw.alicdn.com/TLife/1513071278774/TB10m3ob6gy_uJjSZPxFT5nNpXa","multiAttr":{"attrList":[{"name":"甜度","value":["无糖","少糖","半糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1300","promotionPrice":"1300","promotioned":"false","saleCount":"2","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1300","promotionPrice":"0","quantity":"9989","skuId":"3704549255780","title":"中杯"},{"price":"1500","promotionPrice":"0","quantity":"9985","skuId":"3704549255781","title":"大杯"}],"soldMode":"1","status":"1","stock":"19974","storeId":"157891380","title":"红茶拿铁"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545868913055","itemPicts":"//gw.alicdn.com/TLife/1513071278742/TB1ByEub6gy_uJjSZK94D0vlFXa","multiAttr":{"attrList":[{"name":"甜度","value":["少糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1100","promotionPrice":"1100","promotioned":"false","saleCount":"1","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1300","promotionPrice":"0","quantity":"9999","skuId":"3584336959028","title":"大杯"},{"price":"1100","promotionPrice":"0","quantity":"9988","skuId":"3704545551777","title":"中杯"}],"soldMode":"1","status":"1","stock":"19987","storeId":"157891380","title":"可可鲜奶茶"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545833160250","itemPicts":"//gw.alicdn.com/TLife/1513065816920/TB1ruEah3DD8KJjy0Fd9fwjvXXa","multiAttr":{"attrList":[{"name":"甜度","value":["无糖","少糖","半糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1200","promotionPrice":"1200","promotioned":"false","saleCount":"2","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1500","promotionPrice":"0","quantity":"9978","skuId":"3538880173194","title":"大杯"},{"price":"1200","promotionPrice":"0","quantity":"9986","skuId":"3538880173195","title":"中杯"}],"soldMode":"1","status":"1","stock":"19964","storeId":"157891380","title":"香蕉牛奶"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545835124082","itemPicts":"//gw.alicdn.com/TLife/1513071278759/TB18oQmh26H8KJjSspmp_62WXXa","multiAttr":{"attrList":[{"name":"甜度","value":["无糖","少糖","半糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1300","promotionPrice":"1300","promotioned":"false","saleCount":"2","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1300","promotionPrice":"0","quantity":"9972","skuId":"3537777568386","title":"中杯"},{"price":"1500","promotionPrice":"0","quantity":"9989","skuId":"3537777568387","title":"大杯"}],"soldMode":"1","status":"1","stock":"19961","storeId":"157891380","title":"红豆布丁鲜奶"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545834956309","itemPicts":"//gw.alicdn.com/TLife/1513071278623/TB1WC3ob6gy_uJjSZPxk_WnNpXa","multiAttr":{"attrList":[{"name":"甜度","value":["少糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1200","promotionPrice":"1200","promotioned":"false","saleCount":"1","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1400","promotionPrice":"0","quantity":"10000","skuId":"3704546847212","title":"大杯"},{"price":"1200","promotionPrice":"0","quantity":"9966","skuId":"3704546847213","title":"中杯"}],"soldMode":"1","status":"1","stock":"19966","storeId":"157891380","title":"北海道鲜奶"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545900939886","itemPicts":"//gw.alicdn.com/TLife/1513071278710/TB197YdX6b.heNjSZFApn3hKXXa","multiAttr":{"attrList":[{"name":"甜度","value":["少糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1300","promotionPrice":"1300","promotioned":"false","saleCount":"3","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1300","promotionPrice":"0","quantity":"9970","skuId":"3704547927692","title":"中杯"},{"price":"1500","promotionPrice":"0","quantity":"9978","skuId":"3704547927693","title":"大杯"}],"soldMode":"1","status":"1","stock":"19948","storeId":"157891380","title":"红豆布丁抹茶"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545750541317","itemPicts":"//gw.alicdn.com/TLife/1513071278862/TB1eyEqhYYI8KJjy0FaLT_AiVXa","multiAttr":{"attrList":[{"name":"甜度","value":["无糖","少糖","半糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1300","promotionPrice":"1300","promotioned":"false","saleCount":"2","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1500","promotionPrice":"0","quantity":"9982","skuId":"3537777064958","title":"大杯"},{"price":"1300","promotionPrice":"0","quantity":"9981","skuId":"3537777064959","title":"中杯"}],"soldMode":"1","status":"1","stock":"19963","storeId":"157891380","title":"牛奶三兄弟"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545879838929","itemPicts":"//gw.alicdn.com/TLife/1513071278751/TB1bS7gh2DH8KJjy1Xcb_7pdXXa","multiAttr":{"attrList":[{"name":"甜度","value":["无糖","少糖","半糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1400","promotionPrice":"1400","promotioned":"false","saleCount":"10","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1500","promotionPrice":"0","quantity":"9986","skuId":"3537772652011","title":"中杯加珍珠"},{"price":"1500","promotionPrice":"0","quantity":"9985","skuId":"3537772652012","title":"中杯加红豆"},{"price":"1400","promotionPrice":"0","quantity":"9969","skuId":"3537772652013","title":"中杯"},{"price":"1500","promotionPrice":"0","quantity":"9994","skuId":"3537772652014","title":"中杯加布丁"},{"price":"1500","promotionPrice":"0","quantity":"9988","skuId":"3537772652015","title":"中杯加仙草"}],"soldMode":"1","status":"1","stock":"49922","storeId":"157891380","title":"纯牛奶"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545833720115","itemPicts":"//gw.alicdn.com/TLife/1513071278742/TB1Mh6xh4TI8KJjSspi4D2M4FXa","multiAttr":{"attrList":[{"name":"甜度","value":["无糖","少糖","半糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","温","热","常温"]}]},"price":"1300","promotionPrice":"1300","promotioned":"false","saleCount":"2","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1300","promotionPrice":"0","quantity":"9978","skuId":"3704548659395","title":"中杯"},{"price":"1500","promotionPrice":"0","quantity":"9983","skuId":"3704548659396","title":"大杯"}],"soldMode":"1","status":"1","stock":"19961","storeId":"157891380","title":"抹茶拿铁"},{"bestSelling":"false","checkoutMode":"0","description":"七号茶饮鲜奶采用进口鲜奶。决不向瑕疵妥协。坚持用最健康的原料做最优质的茶饮","flashTimeValid":"false","hasSku":"true","itemAttrList":[],"itemCateId":"23265088","itemId":"545880582571","itemPicts":"//gw.alicdn.com/TLife/1513071278720/TB1v2cAb7fb_uJkHFqDBD0VIVXa","multiAttr":{"attrList":[{"name":"甜度","value":["少糖","标准糖","多糖"]},{"name":"温度","value":["去冰","少冰","标准冰","多冰","温","热"]}]},"price":"1200","promotionPrice":"1200","promotioned":"false","saleCount":"2","sellerId":"2832360567","serviceId":"0","shopId":"157891380","skuList":[{"price":"1200","promotionPrice":"0","quantity":"9989","skuId":"3538878353124","title":"中杯"},{"price":"1500","promotionPrice":"0","quantity":"9992","skuId":"3538878353125","title":"大杯"}],"soldMode":"1","status":"1","stock":"19981","storeId":"157891380","title":"红豆布丁可可"}]
         * light : false
         * name : 鲜奶@可可
         * sort : 9
         * status : 1
         * storeId : 157891380
         * icon : https://gw.alicdn.com/tps/TB1oQBBLpXXXXb5XpXXXXXXXXXX-42-42.png
         */

        private String id;
        private String light;
        private String name;
        private String sort;
        private String status;
        private String storeId;
        private String icon;
        private List<ItemListBean> itemList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLight() {
            return light;
        }

        public void setLight(String light) {
            this.light = light;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public List<ItemListBean> getItemList() {
            return itemList;
        }

        public void setItemList(List<ItemListBean> itemList) {
            this.itemList = itemList;
        }
    }
}
