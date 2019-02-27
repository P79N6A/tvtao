package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;


public class Cat implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -1673011182508847114L;
    //    类目列表     cats    数组  数组元素为对象，对象各属性如下（id,name,imagePath,subCats）。     N
    //    类目id     cats[*].id  String  店铺内类目id
    //    Y
    //    类目名称     cats[*].name    String  根据版本号看是否转义，下面的二级类目名同理。  Y
    //    类目图片路径   cats[*].imagePath   String 
    //    Y
    //    二级类目列表   cats.subCats    数组  元素为二级类目，其它属于也是id,name,imagePath     Y
    //    店铺内二级类目id    cats.subCats[*].id  String  店铺内二级类目id   Y
    //    店铺内二级类目名称    cats.subCats[*].name    String  店铺内二级类目名称   Y
    //    店铺内二级类目图片路径  cats.subCats[*].imagePath   String  店铺内二级类目图片路径     Y
    private String    id;
    private String    name;
    private String    imagePath; 
    public int        currentPage;
    public boolean    isLast;

    //    private String subCats;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    } 

}
