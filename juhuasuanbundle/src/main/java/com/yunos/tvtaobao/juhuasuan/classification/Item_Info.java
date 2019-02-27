package com.yunos.tvtaobao.juhuasuan.classification;


public class Item_Info {
    
    

    // 请求的页数
    public static  int EACH_REQUEST_PAGE_COUNT   = 100; 
    
    // 界面显示中：每页放几个条目
    public static  int PAGE_COUNT                = 5;
    
    
    // 每次请求时,实际返回的页数
    public   int FACT_RETURN_PAGE_COUNT    = 0;
    

    // 当前请求第几页的数据，[注意： 此处的页数跟页面显示的页数不同]
    public int              mCurrentPages             = 1;
    
    // 当前页请求的容量
    public int              mPageSizes                = EACH_REQUEST_PAGE_COUNT * PAGE_COUNT;
   
    // 总共显示多少页
    public int              mTotalPage                = 0;

    // 请求返回的数据，添加到缓冲区的哪一页
    public int              mCurrentPageOfAddPosition = 0;

}
