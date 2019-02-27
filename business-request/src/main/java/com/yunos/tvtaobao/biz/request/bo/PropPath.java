/**
 * 
 */
package com.yunos.tvtaobao.biz.request.bo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author tianxiang
 * @date 2012-10-26 上午11:24:30
 */
public class PropPath implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1252568582085185382L;
	private Set<Pvid> items;

	public PropPath(String path) {
		String[] array = path.split(";");
		items = new HashSet<Pvid>();
		for (String str : array) {
			String[] temp = str.split(":");
			items.add(new Pvid(Long.valueOf(temp[0]), Long.valueOf(temp[1])));
		}
	}

	public void addItem(Long pid, Long vid) {
		if (items == null) {
			items = new HashSet<Pvid>();
		}

		items.add(new Pvid(pid, vid));
	}

	public boolean contains(Pvid pvid) {
		if (pvid == null) {
			return true;
		}

		for (Pvid item : items) {
			if (item.isMatch(pvid)) {
				return true;
			}
		}

		return false;
	}

	public boolean contains(List<Pvid> idList) {
		if (idList == null) {
			return true;
		}

		boolean match = true;
		for (Pvid id : idList) {
			match = match && contains(id);
			if (!match) {
				return false;
			}
		}

		return true;
	}

	public static class Pvid implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1435715796820368855L;

		private Long pid;

		private Long vid;

		public Pvid(Long pid, Long vid) {
			this.pid = pid;
			this.vid = vid;
		}

		public boolean isMatch(Long pid, Long vid) {
			return this.pid.equals(pid) && this.vid.equals(vid);
		}

		public boolean isMatch(Pvid pvid) {
			return this.pid.equals(pvid.getPid())
					&& this.vid.equals(pvid.getVid());
		}

		public Long getPid() {
			return pid;
		}

		public Long getVid() {
			return vid;
		}

		@Override
		public String toString() {
			return pid + ":" + vid;
		}

		@Override
		public int hashCode() {
			return pid.hashCode() + vid.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof Pvid)) {
				return false;
			}

			Pvid temp = (Pvid) obj;

			return isMatch(temp);
		}
	}

	public Set<Pvid> getItems() {
		return items;
	}

}
