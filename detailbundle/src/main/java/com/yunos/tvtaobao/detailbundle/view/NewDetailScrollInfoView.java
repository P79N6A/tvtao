package com.yunos.tvtaobao.detailbundle.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.UiThread;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.taobao.detail.domain.base.Unit;
import com.taobao.detail.domain.component.Button;
import com.taobao.detail.domain.template.LayoutInfo;
import com.yunos.tv.app.widget.Interpolator.Linear;
import com.yunos.tv.blitz.BlitzContextWrapper;
import com.yunos.tv.blitz.global.BzAppConfig;
import com.yunos.tv.blitz.view.BlitzBridgeSurfaceView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.adapter.TabGoodsBaseAdapter;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;
import com.yunos.tvtaobao.biz.request.bo.NewFeiZhuBean;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.biz.widget.TabGoodsItemView;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.activity.DetailFullDescActivity;
import com.yunos.tvtaobao.detailbundle.activity.NewDetailActivity;
import com.yunos.tvtaobao.detailbundle.adapter.DetailDescAdapter;
import com.yunos.tvtaobao.detailbundle.bean.DescImage;
import com.yunos.tvtaobao.detailbundle.flash.DensityUtils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by dingbin on 2017/8/23.
 * <p>
 * Modify by wuhaoteng on 2018/9/19.
 * Desc:用recyclerview取代h5
 */

public class NewDetailScrollInfoView {

    private static final String TAG = "NewDetailScrollInfoView";
    private final int DISTANCE = 150;

    //scrollbar
    private final int m_isShowVScrollBar = 1;

    private final int m_VScrollBarBGWidth = 10;
    private final int m_VScrollBarBlockWidth = 10;

    private final int m_VScrollBarBG_r = 133;
    private final int m_VScrollBarBG_g = 133;
    private final int m_VScrollBarBG_b = 133;
    private final int m_VScrollBarBG_a = 128;

    private final int m_VScrollBar_r = 0;
    private final int m_VScrollBar_g = 0;
    private final int m_VScrollBar_b = 0;
    private final int m_VScrollBar_a = 256;

    private final int m_VScrollBarIsFade = 0;
    private final int m_VScrollBarFadeDuration = 0;
    private final int m_VScrollBarFadeWait = 0;

    private WeakReference<Activity> mDetailActivityReference;

    // 商品详情
    private BlitzBridgeSurfaceView mBlitzBridgeSurfaceView;

    private RecyclerView mDetailDescList;
    private DetailDescAdapter mDetailDescAdapter;
    private List<DescImage> mDescImages;
    private AsyncTask<String, Integer, List<DescImage>> mParseHtmlTask;

    boolean isSimpleOn=SharePreferences.getBoolean("isSimpleOn",false);//容灾开关，全局变量里配置生效

    public NewDetailScrollInfoView(WeakReference<Activity> weakReference) {
        mDetailActivityReference = weakReference;
        onInitScrollInfoView();

    }

    private void onInitScrollInfoView() {
        if (mDetailActivityReference != null && mDetailActivityReference.get() != null) {
            final NewDetailActivity mNewDetailActivity = (NewDetailActivity) mDetailActivityReference.get();
            if (isSimpleOn && DeviceJudge.MemoryType.LowMemoryDevice.equals(DeviceJudge.getMemoryType())) {
                mDetailDescList = (RecyclerView) mNewDetailActivity.findViewById(R.id.new_detail_fulldesc);
                mDetailDescList.setVisibility(View.VISIBLE);
                mDescImages = new ArrayList<>();
                mDetailDescAdapter = new DetailDescAdapter(mDescImages, mDetailActivityReference.get());
                mDetailDescList.setLayoutManager(new LinearLayoutManager(mDetailActivityReference.get()));
                mDetailDescList.setAdapter(mDetailDescAdapter);
                if (mDetailDescList != null) {
                    mDetailDescList.setVisibility(View.VISIBLE);
                    mDetailDescList.setFocusable(false);
                }
            } else {
                mBlitzBridgeSurfaceView = (BlitzBridgeSurfaceView) mNewDetailActivity.findViewById(R.id.new_detail_fulldesc_webview_blitz);
                mBlitzBridgeSurfaceView.setVisibility(View.VISIBLE);
                if (mBlitzBridgeSurfaceView != null) {
                    mBlitzBridgeSurfaceView.setVisibility(View.VISIBLE);
                    mBlitzBridgeSurfaceView.setFocusable(false);
                }
            }
        }
    }

    /***
     * 初始化 scroll bar
     */
    public void onInitScrollbar() {
        if (mDetailActivityReference != null && mDetailActivityReference.get() != null) {
            final NewDetailActivity mNewDetailActivity = (NewDetailActivity) mDetailActivityReference.get();

            mNewDetailActivity.showScrollBar(m_isShowVScrollBar);

            mNewDetailActivity.setScrollBarColor(m_VScrollBarBG_r, m_VScrollBarBG_g, m_VScrollBarBG_b,
                    m_VScrollBarBG_a, m_VScrollBar_r, m_VScrollBar_g, m_VScrollBar_b, m_VScrollBar_a);

            mNewDetailActivity
                    .setScrollBarFade(m_VScrollBarIsFade, m_VScrollBarFadeDuration, m_VScrollBarFadeWait);

            mNewDetailActivity.setScrollBarWidth(m_VScrollBarBGWidth, m_VScrollBarBlockWidth);
        }
    }

    /***
     * 滚动 scroll bar
     */
    public void onListScroll(int dy) {
        mDetailDescList.scrollBy(0, dy);
    }

    /**
     * 释放 详情页面的VIEW
     */
    public void onCleanAndDestroy() {
        if (mDetailDescList != null) {
            mDetailDescList.setVisibility(View.GONE);
            mDetailDescList = null;
        }

        if (mParseHtmlTask != null) {
            mParseHtmlTask.cancel(true);
            mParseHtmlTask = null;
        }
    }

    /**
     * 加载内容
     *
     * @param baseUrl
     * @param data
     * @param mimeType
     * @param encoding
     * @param failUrl
     */
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String failUrl) {
        AppDebug.i(TAG, "loadDataWithBaseURL --> baseUrl = " + baseUrl + "; data = " + data + "; mimeType = "
                + mimeType);

        if (mDetailActivityReference != null && mDetailActivityReference.get() != null) {
            AppDebug.i(TAG, "loadWithData --> data = " + data);

            if (isSimpleOn&&DeviceJudge.MemoryType.LowMemoryDevice.equals(DeviceJudge.getMemoryType())) {
                mParseHtmlTask = new ParseHtmlTask().execute(data);
            } else {
                final NewDetailActivity mNewDetailActivity = (NewDetailActivity) mDetailActivityReference.get();
                mNewDetailActivity.loadDataForWeb(data);
            }
        }
    }


    public void initFootView(TBDetailResultV6 tbDetailResultV6) {
        mDetailDescAdapter.initFootViewOptions(tbDetailResultV6);
    }


    private List<DescImage> parseDescImageHtml(String htmlDoc) {
        List<DescImage> descImages = new ArrayList<>();
        if (htmlDoc == null) {
            return null;
        }
        try {
            StringReader read = new StringReader(htmlDoc);
            InputSource source = new InputSource(read);
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(source);
            //获取所有img节点的集合
            NodeList imgList = document.getElementsByTagName("img");
            //通过nodelist的getLength()方法可以获取imgList的长度
            AppDebug.i(TAG, "一共有" + imgList.getLength() + "张图");
            //遍历每一个img节点
            for (int i = 0; i < imgList.getLength(); i++) {
                AppDebug.i(TAG, "=================下面开始遍历第" + (i + 1) + "张图的内容=================");
                //通过 item(i)方法 获取一个img节点，nodelist的索引值从0开始
                Node img = imgList.item(i);
                //获取img节点的所有属性集合
                NamedNodeMap attrs = img.getAttributes();
                AppDebug.i(TAG, "第 " + (i + 1) + "张图共有" + attrs.getLength() + "个属性");
                //遍历img的属性
                DescImage descImage = new DescImage();
                for (int j = 0; j < attrs.getLength(); j++) {
                    //通过item(index)方法获取img节点的某一个属性
                    Node attr = attrs.item(j);
                    String nodeName = attr.getNodeName();
                    String nodeValue = attr.getNodeValue();
                    if (nodeName.equals("src")) {
                        descImage.setSrc(nodeValue);
                    }

                    if (nodeName.equals("align")) {
                        descImage.setAlign(nodeValue);
                    }
                    //获取属性名
                    AppDebug.i(TAG, "属性名：" + nodeName);
                    //获取属性值
                    AppDebug.i(TAG, "--属性值" + nodeValue);
                }
                descImages.add(descImage);
                //解析img节点的子节点
                NodeList childNodes = img.getChildNodes();
                //遍历childNodes获取每个节点的节点名和节点值
                AppDebug.i(TAG, "第" + (i + 1) + "张图共有" +
                        childNodes.getLength() + "个子节点");
                for (int k = 0; k < childNodes.getLength(); k++) {
                    //区分出text类型的node以及element类型的node
                    if (childNodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
                        //获取了element类型节点的节点名
                        AppDebug.i(TAG, "第" + (k + 1) + "个节点的节点名："
                                + childNodes.item(k).getNodeName());
                        //获取了element类型节点的节点值
                        AppDebug.i(TAG, "--节点值是：" + childNodes.item(k).getFirstChild().getNodeValue());
                    }
                }
                AppDebug.i(TAG, "======================结束遍历第" + (i + 1) + "张图的内容=================");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // add in 20180930；前几个版本记录一下，发现没啥数据量后，说明这样解析没啥问题，再把这个打点去掉
            Map<String, String> params = Utils.getProperties();
            params.put("content", htmlDoc);
            params.put("Exception", e.getMessage());
            Utils.utCustomHit("NewDetailActivity", "new_detail_parse_html_error", params);
        } catch (StackOverflowError error) {
            Map<String, String> params = Utils.getProperties();
            params.put("content", htmlDoc);
            params.put("Exception", error.getMessage());
            Utils.utCustomHit("NewDetailActivity", "new_detail_parse_html_error(StackOverflowError)", params);
        }

        return descImages;
    }



    private class ParseHtmlTask extends AsyncTask<String,Integer,List<DescImage>>{

        @Override
        protected List<DescImage> doInBackground(String... strings) {
            return parseDescImageHtml(strings[0]);
        }

        @Override
        protected void onPostExecute(List<DescImage> descImages) {
            super.onPostExecute(descImages);
            if (descImages != null && mDetailDescAdapter != null) {
                mDescImages.clear();
                mDescImages.addAll(descImages);
                mDetailDescAdapter.notifyDataSetChanged();
            }
        }
    }


    public String getPropsHtml(TBDetailResultV6 tbDetailResultV6) {
        String html = "";
        if (tbDetailResultV6 == null) {
            return html;
        }

        // 商品属性规格
        //List<Unit> props = tBDetailResultVO.props;
        List<Unit> domainUnit = tbDetailResultV6.getDomainUnit();
        if (domainUnit == null || domainUnit.isEmpty()) {
            return html;
        }

        int mSize = domainUnit.size();
        if (mSize > 0) {
            html = "<div style='display:block;clear:both; padding:0px 20px 40px 20px;'><div style='display:block;height:5px;background-color:#c6223f'></div>";
            html = html + "<div style='color:#c6223f;font-size:24px;padding:20px 0px 20px 0px'>商品参数</div>";
            html = html + "<div style='display: block;padding:0px 0px 20px 0px;clear: both;'>";
            for (int i = 0; i < mSize; i++) {
                Unit mUnit = domainUnit.get(i);
                html = html
                        + "<div style='float:left;width:47%;color:#666666;font-size:24px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;padding-right:20px;'>"
                        + mUnit.name + ":" + mUnit.value + "</div>";
                if (i % 2 != 0) {
                    html = html + "</div>";
                    if (i < mSize - 1) {
                        html = html + "<div style='display: block;padding:20px 0px 20px 0px;clear: both;'>";
                    }
                } else if (i == mSize - 1) {
                    html = html + "</div>";
                }
            }
            html = html + "</div>";
        }
        AppDebug.i(TAG, "getPropsHtml html=" + html);
        return html;
    }


}
