/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页的商品评价
 * 
 * @author tianxiang
 * @date 2012-10-21 上午9:36:00
 */
public class PaginationItemComment implements Serializable{

	/**
     * 
     */
    private static final long serialVersionUID = 7663724653341032521L;

    private Integer totalSize;

	private Integer pageSize;

	private Integer totalPage;

	private Integer currPage;

	private List<ItemComment> comments;

	public Integer getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(Integer totalSize) {
		this.totalSize = totalSize;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public List<ItemComment> getComments() {
		return comments;
	}

	public void setComments(List<ItemComment> comments) {
		this.comments = comments;
	}

	public static PaginationItemComment resolveFromTop(String response)
			throws Exception {
		{
			int index = response.indexOf("{");
			response = response.substring(index);
		}

		JSONObject rateListInfoObj = new JSONObject(response)
				.getJSONObject("rateListInfo");
		JSONObject paginatorObj = rateListInfoObj.getJSONObject("paginator");

		PaginationItemComment comment = new PaginationItemComment();
		comment.setPageSize(paginatorObj.getInt("itemsPerPage"));
		comment.setTotalSize(paginatorObj.getInt("length"));
		comment.setTotalPage(paginatorObj.getInt("pages"));
		comment.setCurrPage(paginatorObj.getInt("page"));

		JSONArray rateArray = rateListInfoObj.getJSONArray("rateList");
		if (rateArray == null) {
			return comment;
		}
		comment.comments = new ArrayList<ItemComment>();
		for (int i = 0; i < rateArray.length(); i++) {
			ItemComment itemComment = ItemComment.resolveFromTop(rateArray
					.getJSONObject(i));
			if (itemComment != null) {
				comment.comments.add(itemComment);
			}
		}

		return comment;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getCurrPage() {
		return currPage;
	}

	public void setCurrPage(Integer currPage) {
		this.currPage = currPage;
	}
}
