package com.yunos.tv.app.file.cache;

public class BuildVideoM3u8FileContent {

	public static String buildContent(String[] bandwidthList, String[] urlList){
		final String header = "#EXTM3U";
		final String bandDiscription = "#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=";
		StringBuffer contentBuffer = new StringBuffer();
		contentBuffer.append(header);
		contentBuffer.append("\r\n");
		for(int i = 0; i < bandwidthList.length; i++){
			if(i < urlList.length){
				contentBuffer.append(bandDiscription);
				contentBuffer.append(bandwidthList[i]);
				contentBuffer.append("\r\n");
				contentBuffer.append(urlList[i]);
				contentBuffer.append("\r\n");
			}
		}
		String content = contentBuffer.toString();
		return content;
	}
	
}
