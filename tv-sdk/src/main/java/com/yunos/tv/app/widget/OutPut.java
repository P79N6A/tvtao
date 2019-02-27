package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;


public class OutPut {
	private static final String TAG = "OutPut"; 
	private static final boolean mbDebug = true;
	
	public OutPut(ArrayList<PathOutPutInfo> outPutInfo){
		mPathOutPutInfoArray = outPutInfo;
	}
	
	
	ArrayList<PathOutPutInfo> mPathOutPutInfoArray;
	
	 /*
	public static File getDataCachePath(Context context) {
		if (dataCachePath == null) {
			//path: /data/data/com.mypackage.path/app_local_cache
			dataCachePath = context.getDir("local_cache",  Context.MODE_WORLD_WRITEABLE).getAbsolutePath();
		}
		File file = new File(dataCachePath);
		if(mbDebug){
			Log.v(TAG, "dataCachePath = " + dataCachePath);
		}
		if (!file.exists()) {
			file.mkdirs();
		}
		return file;
	}
	*/

			
	public static final int mFileFlag = 0xFF00FF00;
	
	@SuppressWarnings("finally")
	public boolean outPutArray(Context context, String name){
		boolean writeS = true;
		
		DataOutputStream dos = null;  
		OutputStream os = null;
		File file;
        try {  
        	file = new File(LocalCache.getDataCachePath(context), name);
        	
        	if(file.exists()){
        		file.delete();
        	}
        	
        	
			try {  
				file.createNewFile();
			}catch(IOException e){
				return false;
			}

        	
        	int count = mPathOutPutInfoArray.size();
        	
        	os = new FileOutputStream(file);        	
        	
            dos = new DataOutputStream(os);  //对于FileOutputStream而言，hehe.txt若不存在，会自动创建  
            dos.writeInt(mFileFlag);  
            dos.writeInt(count);
            
            
            boolean writeFlag = false;
            for(int i = 0; i < count; i++){
            	if(mbDebug){
            		Log.v(TAG, "Start write buff index i = " + i);
            	}
            	
            	writeFlag = outPutItem(mPathOutPutInfoArray.get(i), dos);
            	
            	if(!writeFlag){
            		writeS = false;
            		break;
            	}
            }
            
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
            writeS = false;
        } catch (IOException e) {          	
            e.printStackTrace();  
            writeS = false;
        } finally {  
            try {            	
            	
                if(dos != null) {  
                    dos.close();  
                }  
                
                if(os != null){
                	os.close();
                }
            } catch (final IOException e) {  
                e.printStackTrace();  
            }   finally {             
            	return writeS;
            }
        }  
	}
	
	

	private byte[] PointSerArrlist2ByteArr(ArrayList<Point> arrItem){		
		
		int arrSize = arrItem.size();
		int byteBufferSize = 2 * arrSize*Integer.SIZE / Byte.SIZE;
		
		int arrData[] = new int[arrSize*2];
		
		int count = arrItem.size();
		Point pt;
		
		for(int i = 0 ; i < count; i++){
			pt = arrItem.get(i);
			arrData[i*2 ]    = pt.x;
			arrData[i*2 + 1] = pt.y;
		}				
	
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(byteBufferSize).order(ByteOrder.nativeOrder());
		final IntBuffer buffer = byteBuffer.asIntBuffer();
		
		int len = arrData.length;
		buffer.put(arrData, 0, len).position(0);
		
		
		return byteBuffer.array();
		
	}
	
	
	private boolean outPutItem(PathOutPutInfo outPutItem, DataOutputStream dos){
		ArrayList<Point> head = outPutItem.getmArrHead();
		ArrayList<Point> tail = outPutItem.getmArrTail();
		 
		boolean hasHeadData = false;
		boolean hasTailData = false;
		
		int headSize;
		int tailSize;
		
		
		byte data[];
		if(head != null){
			hasHeadData = true;
		}
		
		if(tail != null){
			hasTailData = true;
		}
		
		try {  
			dos.writeBoolean(hasHeadData);
			if(mbDebug){
				Log.v(TAG, "Start write hasHeadData " + hasHeadData);
			}
			
			if(hasHeadData){
				headSize = head.size();
				if(mbDebug){
					Log.v(TAG, "Start write headSize " + headSize);
				}
				
				dos.writeInt(headSize);
				data = PointSerArrlist2ByteArr(head);
				//save int
				
				dos.write(data);				
			}	
		}catch(IOException e){
			 return false;
		}		
		
		try {  
			dos.writeBoolean(hasTailData);
			if(mbDebug){
				Log.v(TAG, "Start write hasTailData " + hasTailData);
			}
			
			if(hasTailData){
				tailSize = tail.size(); 
				if(mbDebug){
					Log.v(TAG, "Start write tailSize " + tailSize);
				}
				
				dos.writeInt(tailSize);
				data = PointSerArrlist2ByteArr(tail);
				//save int
				
				dos.write(data);				
			}			
		}catch(IOException e){
			 return false;
		}
		
		return true;
	}
}
