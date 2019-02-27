package com.yunos.tv.app.widget;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Point;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;


public class Input {
	private static final String TAG = "Input"; 
	private static final boolean mbDebug = true;
	
	public Input(){
	}
	
	
	private ArrayList<PathOutPutInfo> mPathOutPutInfoArray = new ArrayList<PathOutPutInfo>();
	
	 
	
	public ArrayList<PathOutPutInfo> getOutputInfo(){
		return mPathOutPutInfoArray;
	}

	
	private boolean readTotal(DataInputStream dis) throws IOException{
		int fileFlag;
		int count;
		
		boolean readFlag = true;
		
		fileFlag = dis.readInt();
        
        if(fileFlag != OutPut.mFileFlag){
        	return false;
        }
       
        count = dis.readInt();       
        if(mbDebug){
        	Log.v(TAG, "read totalCount " + count);          
        }
        
        for(int i = 0; i < count; i++){
        	PathOutPutInfo item = new PathOutPutInfo();
        	
        	if(mbDebug){
        		Log.v(TAG, "Start read buff item index i = " + i);        	
        	}
        	
        	if(!inPutItem(item, dis)){
        		readFlag = false;
        		if(mbDebug){
        			Log.v(TAG, "read ret item i = " + i + " readS " + readFlag);
        		}
        		
        		break;
        	}
        	
        	mPathOutPutInfoArray.add(item);
        }
        
        return readFlag;
	}
	
	//
	@SuppressWarnings("finally")
	public boolean inPutArrayFromRaw(Context context, int id){
		InputStream istream = null;		
		DataInputStream dis = null;
		boolean readFlag = false;
		try {  
			istream = context.getResources().openRawResource(id);  
			dis = new DataInputStream(istream);  
			
			readFlag = readTotal(dis);
			
		}catch(NotFoundException e){
			e.printStackTrace();  
		} catch (IOException e) {  
			Log.v(TAG, " IOException catch printStackTracet read");  

           e.printStackTrace();  
		} finally {  
           try {  
        	   if(mbDebug){
        		   Log.v(TAG, " finnal colse + readFlag = " + readFlag);  
        	   }
           		if(dis != null){
           			dis.close();
           		}
           	
           		if(istream != null) {  
           			istream.close();  
           		}  
           	} catch (final IOException e) {  
        	   	e.printStackTrace();  
           	}   finally {             
        	   return readFlag;
           	}
		} 		
	}
	
	@SuppressWarnings("finally")
	public boolean inPutArray(Context context, String name){
		boolean readS = true;
		
		DataInputStream dis = null;  
		FileInputStream is  = null;
		
        try {  
        	File file = new File(LocalCache.getDataCachePath(context), name);
        	
        	if(!file.exists()){
        		return false;
        	}
        	
        	is = new FileInputStream(file);
            dis = new DataInputStream(is);  //对于FileOutputStream而言，hehe.txt若不存在，会自动创建  
            
            readS = readTotal(dis);
            
            if(mbDebug){
            	Log.v(TAG, "read end bufer readS = " + readS);
            }
            
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
            readS = false;
        } catch (IOException e) {  
        	
        	Log.v(TAG, " IOException catch printStackTracet read failed" );  
        	
            e.printStackTrace();  
            readS = false;
        } finally {  
            try {  
            	if(mbDebug){
            		Log.v(TAG, " finnal xxxxxxxxx + readS = " + readS);  
            	}
            	
                if(dis != null) {  
                	dis.close();  
                }  
                
                if(is != null){
                	is.close();
                }
            } catch (final IOException e) {  
                e.printStackTrace();  
            }   finally {             
            	return readS;
            }
        }  
	}	
	
	
	private  ArrayList<Point> ByteArr2PointSerArrlist(byte arr [], int count){		
		
		int size = arr.length;
		
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
		byteBuffer.put(arr, 0, size).position(0);		
		
		final IntBuffer buffer = byteBuffer.asIntBuffer();
		
		ArrayList<Point> arrList = new ArrayList<Point>();
		
		Point pt = null;
		

		final int ArrPoint[] = new int[count*2];
		
		buffer.get(ArrPoint);
		
		int x;
		int y;
		
		for(int i = 0 ; i < count; i++){
			x = ArrPoint[i*2];
			y = ArrPoint[i*2 + 1];
			
			pt = new Point(x, y);
			
			arrList.add(pt);
		}		
		
		return arrList;		
	}
	
	
	private boolean inPutItem(PathOutPutInfo outPutItem, DataInputStream dos){
		ArrayList<Point> head = null;
		ArrayList<Point> tail = null;
		 
		boolean hasHeadData = false;
		boolean hasTailData = false;
		
		int headSize;
		int tailSize;
				
		try {  
			hasHeadData = dos.readBoolean();
			if(mbDebug){
				Log.v(TAG, "hasHeadData = " + hasHeadData);
			}
			
			if(hasHeadData){
				
				headSize = dos.readInt();// writeLong(head.size());
				int bufferSize = 2*headSize*Integer.SIZE/Byte.SIZE;
				byte buffer[] = new byte[bufferSize];
				
				if(mbDebug){
					Log.v(TAG, "headSize = " + headSize);
				}
				
				int flag = dos.read(buffer);
				
				if(buffer.length == flag){
					//data = PointSerArrlist2ByteArr(head);
					head = ByteArr2PointSerArrlist(buffer, headSize);
					//save int
					
					//dos.write(data);			
					
					outPutItem.setmArrHead(head);
				}
			}	
		}catch(IOException e){
			 return false;
		}		
		
		
		try {  
			//dos.writeBoolean(hasTailData);
			hasTailData = dos.readBoolean();
			if(mbDebug){
				Log.v("TraceActivity", "hasTailData = " + hasTailData);
			}
			if(hasTailData){
				//dos.writeLong(head.size());
				tailSize = dos.readInt();
				if(mbDebug){
					Log.v("TraceActivity", "tailSize = " + tailSize);
				}
				
				byte buffer[] = new byte[2*tailSize*Integer.SIZE/Byte.SIZE];
				int flag = dos.read(buffer);
				if(buffer.length == flag){
					//data = PointSerArrlist2ByteArr(tail);
					tail = ByteArr2PointSerArrlist(buffer, tailSize);
					//save int
					
					//dos.write(data);				
					
					outPutItem.setmArrTail(tail);
				}
			}			
		}catch(IOException e){
			 return false;
		}
		
		return true;
	}
}
