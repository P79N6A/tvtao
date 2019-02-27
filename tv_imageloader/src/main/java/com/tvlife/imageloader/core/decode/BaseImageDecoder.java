/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.tvlife.imageloader.core.decode;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.ContentLengthInputStream;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.tvlife.imageloader.core.assist.ImageSize;
import com.tvlife.imageloader.core.download.BaseImageDownloader;
import com.tvlife.imageloader.core.download.ImageDownloader.Scheme;
import com.tvlife.imageloader.utils.ImageSizeUtils;
import com.tvlife.imageloader.utils.IoUtils;
import com.tvlife.imageloader.utils.L;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Decodes images to {@link Bitmap}, scales them to needed size
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageDecodingInfo
 * @since 1.8.3
 */
public class BaseImageDecoder implements ImageDecoder {

    private String TAG = "BaseImageDecoder";

    protected static final String LOG_SABSAMPLE_IMAGE = "Subsample original image (%1$s) to %2$s (scale = %3$d) [%4$s]";
    protected static final String LOG_SCALE_IMAGE = "Scale subsampled image (%1$s) to %2$s (scale = %3$.5f) [%4$s]";
    protected static final String LOG_ROTATE_IMAGE = "Rotate image on %1$d\u00B0 [%2$s]";
    protected static final String LOG_FLIP_IMAGE = "Flip image horizontally [%s]";
    protected static final String ERROR_CANT_DECODE_IMAGE = "Image can't be decoded [%s]";

    protected final boolean loggingEnabled;
    private Bitmap mBitmap;

    /**
     * @param loggingEnabled Whether debug logs will be written to LogCat. Usually should match
     *            {@link com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder#writeDebugLogs()
     *            ImageLoaderConfiguration.writeDebugLogs()}
     */
    public BaseImageDecoder(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    public void setTestBitmap(Bitmap bm) {
        mBitmap = bm;
    }

    /**
     * Decodes image from URI into {@link Bitmap}. Image is scaled close to incoming {@linkplain ImageSize target size}
     * during decoding (depend on incoming parameters).
     * @param decodingInfo Needed data for decoding image
     * @return Decoded bitmap
     * @throws FileNotFoundException
     * @throws IOException if some I/O exception occurs during image reading
     * @throws UnsupportedOperationException if image URI has unsupported scheme(protocol)
     */
    //    	public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {
    //    		Bitmap decodedBitmap;
    //    		ImageFileInfo imageInfo;
    //    
    //    		InputStream imageStream = getImageStream(decodingInfo);
    //    		try {
    //    		    // 原本的代码
    //    //			imageInfo = defineImageSizeAndRotation(imageStream, decodingInfo);
    //    //			imageStream = resetStream(imageStream, decodingInfo);
    //    //			Options decodingOptions = prepareDecodingOptions(imageInfo.imageSize, decodingInfo); 
    //    //			decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);   
    //    			
    //    			
    //    			
    //    		    // 修改后的代码
    //    			decodedBitmap = BitmapFactory.decodeStream(imageStream); 
    //    			
    //    		} finally {
    //    			IoUtils.closeSilently(imageStream);
    //    		}
    //    
    //    		if (decodedBitmap == null) {
    //    			L.e(ERROR_CANT_DECODE_IMAGE, decodingInfo.getImageKey());
    //    		} 
    //    		  
    //    		return decodedBitmap;
    //    	}

    private boolean wirteTofile(String allPath, String path, ImageDecodingInfo decodingInfo, InputStream imageStream) {

        final int MAX_BYTE = 1024;
        byte[] inOutb;

        boolean saveSuccess = false;
        File file = new File(allPath);

        //        File parentfile = file.getParentFile();
        //        if (!parentfile.exists()) {
        //            parentfile.mkdir();
        //        }

        FileOutputStream outStream = null;
        try {

            outStream = new FileOutputStream(file);

            inOutb = new byte[MAX_BYTE];
            while (true) {
                int temp = imageStream.read(inOutb, 0, inOutb.length);
                if (temp == -1) {
                    break;
                }
                outStream.write(inOutb, 0, temp);
            }
            saveSuccess = true;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }
        finally {

            IoUtils.closeSilently(outStream);
            outStream = null;
            inOutb = null;

        }

        if (loggingEnabled) {
            Log.i(TAG, "wirteTofile:     allPath = " + allPath);
        }

        return saveSuccess;
    }

    public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {

        Bitmap decodedBitmap = null;
        InputStream imageStream = null;
        ImageFileInfo imageInfo;

        String imageUrl = decodingInfo.getImageUri();

        boolean cancelDecode = false;

        try {

            // 通过图片地址获取图片输入流
            imageStream = getImageStream(decodingInfo);

            if (imageStream != null) {

                String path = decodingInfo.getBitmapPath();
                String name = decodingInfo.getBitmapName();
                String allPath = path + "/" + name;

                if (loggingEnabled) {
                    Log.i(TAG, "decode:   path   = " + path + ";  name = " + name + ";  allPath = " + allPath);
                }

                if ((!TextUtils.isEmpty(path)) && (!TextUtils.isEmpty(name))) {

                    boolean saveSuccess = false;
                    saveSuccess = wirteTofile(allPath, path, decodingInfo, imageStream);
                    if (saveSuccess) {

                        cancelDecode = decodingInfo.cancelLoadIfneed();
                        if (loggingEnabled) {
                            L.i("decode --> " + " ; imageUrl = " + imageUrl + "; cancelDecode = " + cancelDecode);
                        }

                        if (!cancelDecode) {
                            decodedBitmap = BitmapFactory.decodeFile(allPath);
                        }

                        // 解析图片
                        //                        imageInfo = defineImageSizeAndRotation(imageStream, decodingInfo);
                        //                        imageStream = resetStream(imageStream, decodingInfo);
                        //                        Options decodingOptions = prepareDecodingOptions(imageInfo.imageSize, decodingInfo);
                        //                        decodedBitmap = BitmapFactory.decodeFile(allPath, decodingOptions);
                    }

                } else {

                    Options decodingOptions = null;

                    // 解析图片 
                    if (decodingInfo.isDecodeSample()) {
                        imageInfo = defineImageSizeAndRotation(imageStream, decodingInfo);
                        imageStream = resetStream(imageStream, decodingInfo);
                        decodingOptions = prepareDecodingOptions(imageInfo.imageSize, decodingInfo);
                    } else { 
                        decodingOptions = decodingInfo.getDecodingOptions();
                    }

                    cancelDecode = decodingInfo.cancelLoadIfneed();
                    
                    if (loggingEnabled) {
                        L.i("decode --> " + " ; imageUrl = " + imageUrl + "; cancelDecode = " + cancelDecode + "; decodingOptions.inSampleSize = " + decodingOptions.inSampleSize);
                    }

                    if (!cancelDecode) {
                        decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);
                    }

                    // 为了不使图片丢失清晰度， 则 1 : 1 进行采样[ inSampleSize默认为1]
                    //                    decodedBitmap = BitmapFactory.decodeStream(imageStream);
                }
            }

        }
        catch (IOException e) {
            if (loggingEnabled) {
                L.e("decode --> " + " ; imageUrl = " + imageUrl + "; IOException = " + e.toString());
            }
        }
        finally {
            IoUtils.closeSilently(imageStream);
        }

        if ((decodedBitmap == null) && (!cancelDecode)) {
            L.e(ERROR_CANT_DECODE_IMAGE, decodingInfo.getImageKey());
        }

        return decodedBitmap;
    }

    //        public Bitmap decode(ImageDecodingInfo decodingInfo) throws IOException {
    //    
    //            Bitmap decodedBitmap = null;
    //            ImageFileInfo imageInfo;
    //    
    //            if (loggingEnabled) {
    //                Log.i(TAG, "decode ;   Uri = " + decodingInfo.getImageUri());
    //            }
    //    
    //            switch (Scheme.ofUri(decodingInfo.getImageUri())) {
    //                case HTTP:
    //                case HTTPS:
    //                    decodedBitmap = getBitmapFromNetwork(decodingInfo);
    //                    break;
    //                case FILE:
    //                    decodedBitmap = getBitmapFromFile(decodingInfo);
    //                    break;
    //                default:
    //                    decodedBitmap = getBitmapFromOthers(decodingInfo);
    //                    break;
    //            }
    //    
    //            if (decodedBitmap == null) {
    //                L.e(ERROR_CANT_DECODE_IMAGE, decodingInfo.getImageKey());
    //            }
    //    
    //            return decodedBitmap;
    //    
    //        }

    public Bitmap getBitmapFromFile(ImageDecodingInfo decodingInfo) throws IOException {
        Bitmap decodedBitmap = null;
        ImageFileInfo imageInfo;

        if (loggingEnabled) {
            Log.i(TAG, "getBitmapFromFile ;   Uri = " + decodingInfo.getImageUri());
        }

        String filePath = Scheme.FILE.crop(decodingInfo.getImageUri());
        //      return new ContentLengthInputStream(new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE),
        //              new File(filePath).length());

        //      return  new BufferedInputStream(new FileInputStream(filePath), BUFFER_SIZE);

        FileInputStream fimageStream = new FileInputStream(filePath);
        decodedBitmap = BitmapFactory.decodeStream(fimageStream);
        if (fimageStream != null) {
            fimageStream.close();
        }

        return decodedBitmap;
    }

    public Bitmap getBitmapFromOthers(ImageDecodingInfo decodingInfo) throws IOException {
        Bitmap decodedBitmap = null;
        ImageFileInfo imageInfo;

        if (loggingEnabled) {
            Log.i(TAG, "getBitmapFromOthers ;  Uri = " + decodingInfo.getImageUri());
        }

        InputStream imageStream = getImageStream(decodingInfo);
        try {
            // 原本的代码
            //               imageInfo = defineImageSizeAndRotation(imageStream, decodingInfo);
            //               imageStream = resetStream(imageStream, decodingInfo);
            //               Options decodingOptions = prepareDecodingOptions(imageInfo.imageSize, decodingInfo); 
            //               decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);   

            // 修改后的代码
            decodedBitmap = BitmapFactory.decodeStream(imageStream);

        }

        finally {
            IoUtils.closeSilently(imageStream);
        }

        return decodedBitmap;
    }

    // 改进的代码
    public Bitmap getBitmapFromNetwork(ImageDecodingInfo decodingInfo) throws IOException {
        Bitmap decodedBitmap = null;
        ImageFileInfo imageInfo;

        if (loggingEnabled) {
            Log.i(TAG, "getBitmapFromNetwork ;   Uri = " + decodingInfo.getImageUri());
        }

        HttpURLConnection conn = createConnection(decodingInfo.getImageUri(), decodingInfo.getExtraForDownloader());

        int redirectCount = 0;

        // 重定向
        while (conn.getResponseCode() / 100 == 3 && redirectCount < BaseImageDownloader.MAX_REDIRECT_COUNT) {

            // 要测试一下
            conn.disconnect();

            conn = createConnection(conn.getHeaderField("Location"), decodingInfo.getExtraForDownloader());

            redirectCount++;
        }

        //        ContentLengthInputStream mContentLengthInputStream = null;
        //        try {
        //
        //            mContentLengthInputStream = new ContentLengthInputStream(new BufferedInputStream(conn.getInputStream(), BaseImageDownloader.BUFFER_SIZE), conn.getContentLength());
        //
        //            decodedBitmap = BitmapFactory.decodeStream(mContentLengthInputStream); 
        //
        //        }
        //        catch (IOException e) {
        //            // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
        //            IoUtils.readAndCloseStream(conn.getErrorStream());
        //            throw e;
        //        }
        //        finally {
        //            IoUtils.closeSilently(mContentLengthInputStream);
        //            conn.disconnect();
        //        }

        //        InputStream inputStream = conn.getInputStream();

        BufferedInputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(conn.getInputStream(), BaseImageDownloader.BUFFER_SIZE);
            decodedBitmap = BitmapFactory.decodeStream(inputStream);
        }
        catch (IOException e) {

        }
        finally {
            IoUtils.closeSilently(inputStream);
            conn.disconnect();
        }

        //        inputStream.close();

        return decodedBitmap;
    }

    /**
     * Retrieves {@link InputStream} of image by URI (image is located in the network).
     * @param imageUri Image URI
     * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *            DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@link InputStream} of image
     * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
     *             URL.
     */
    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        HttpURLConnection conn = createConnection(imageUri, extra);

        int redirectCount = 0;
        while (conn.getResponseCode() / 100 == 3 && redirectCount < BaseImageDownloader.MAX_REDIRECT_COUNT) {
            conn = createConnection(conn.getHeaderField("Location"), extra);
            redirectCount++;
        }

        InputStream imageStream;
        try {
            imageStream = conn.getInputStream();
        }
        catch (IOException e) {
            // Read all data to allow reuse connection (http://bit.ly/1ad35PY)
            IoUtils.readAndCloseStream(conn.getErrorStream());
            throw e;
        }
        return new ContentLengthInputStream(new BufferedInputStream(imageStream, BaseImageDownloader.BUFFER_SIZE),
                conn.getContentLength());
    }

    /**
     * Create {@linkplain HttpURLConnection HTTP connection} for incoming URL
     * @param url URL to connect to
     * @param extra Auxiliary object which was passed to {@link DisplayImageOptions.Builder#extraForDownloader(Object)
     *            DisplayImageOptions.extraForDownloader(Object)}; can be null
     * @return {@linkplain HttpURLConnection Connection} for incoming URL. Connection isn't established so it still
     *         configurable.
     * @throws IOException if some I/O error occurs during network request or if no InputStream could be created for
     *             URL.
     */
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        String encodedUrl = Uri.encode(url, BaseImageDownloader.ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(BaseImageDownloader.DEFAULT_HTTP_CONNECT_TIMEOUT);
        conn.setReadTimeout(BaseImageDownloader.DEFAULT_HTTP_READ_TIMEOUT);
        return conn;
    }

    protected InputStream getImageStream(ImageDecodingInfo decodingInfo) throws IOException {
        return decodingInfo.getDownloader().getStream(decodingInfo.getImageUri(), decodingInfo.getExtraForDownloader());
    }

    protected ImageFileInfo defineImageSizeAndRotation(InputStream imageStream, ImageDecodingInfo decodingInfo)
            throws IOException {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageStream, null, options);

        ExifInfo exif;
        String imageUri = decodingInfo.getImageUri();
        if (decodingInfo.shouldConsiderExifParams() && canDefineExifParams(imageUri, options.outMimeType)) {
            exif = defineExifOrientation(imageUri);
        } else {
            exif = new ExifInfo();
        }
        return new ImageFileInfo(new ImageSize(options.outWidth, options.outHeight, exif.rotation), exif);
    }

    private boolean canDefineExifParams(String imageUri, String mimeType) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR && "image/jpeg".equalsIgnoreCase(mimeType)
                && Scheme.ofUri(imageUri) == Scheme.FILE;
    }

    protected ExifInfo defineExifOrientation(String imageUri) {
        int rotation = 0;
        boolean flip = false;
        try {
            ExifInterface exif = new ExifInterface(Scheme.FILE.crop(imageUri));
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    flip = true;
                case ExifInterface.ORIENTATION_NORMAL:
                    rotation = 0;
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotation = 90;
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotation = 180;
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    flip = true;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotation = 270;
                    break;
            }
        }
        catch (IOException e) {
            L.w("Can't read EXIF tags from file [%s]", imageUri);
        }
        return new ExifInfo(rotation, flip);
    }

    protected Options prepareDecodingOptions(ImageSize imageSize, ImageDecodingInfo decodingInfo) {
        ImageScaleType scaleType = decodingInfo.getImageScaleType();
        int scale;
        if (scaleType == ImageScaleType.NONE) {
            scale = ImageSizeUtils.computeMinImageSampleSize(imageSize);
        } else {
            ImageSize targetSize = decodingInfo.getTargetSize();
            boolean powerOf2 = scaleType == ImageScaleType.IN_SAMPLE_POWER_OF_2;
            scale = ImageSizeUtils.computeImageSampleSize(imageSize, targetSize, decodingInfo.getViewScaleType(),
                    powerOf2);
        }
        if (scale > 1 && loggingEnabled) {
            L.d(LOG_SABSAMPLE_IMAGE, imageSize, imageSize.scaleDown(scale), scale, decodingInfo.getImageKey());
        }
        if (loggingEnabled) {
            ImageSize targetSize = decodingInfo.getTargetSize();
            L.d("prepareDecodingOptions --> scale = " + scale + "; scaleType = " + scaleType + "; imageWidth = "
                    + imageSize.getWidth() + "; imageHeight = " + imageSize.getHeight() + "; targetWidth = "
                    + targetSize.getWidth() + "; targetHeight = " + targetSize.getHeight());
        }

        Options decodingOptions = decodingInfo.getDecodingOptions();
        decodingOptions.inSampleSize = scale;
        return decodingOptions;
    }

    protected InputStream resetStream(InputStream imageStream, ImageDecodingInfo decodingInfo) throws IOException {
        try {
            imageStream.reset();
        }
        catch (IOException e) {
            IoUtils.closeSilently(imageStream);
            imageStream = getImageStream(decodingInfo);
        }
        return imageStream;
    }

    protected Bitmap considerExactScaleAndOrientaiton(Bitmap subsampledBitmap, ImageDecodingInfo decodingInfo,
            int rotation, boolean flipHorizontal) {
        Matrix m = new Matrix();
        // Scale to exact size if need
        ImageScaleType scaleType = decodingInfo.getImageScaleType();
        if (scaleType == ImageScaleType.EXACTLY || scaleType == ImageScaleType.EXACTLY_STRETCHED) {
            ImageSize srcSize = new ImageSize(subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), rotation);
            float scale = ImageSizeUtils.computeImageScale(srcSize, decodingInfo.getTargetSize(),
                    decodingInfo.getViewScaleType(), scaleType == ImageScaleType.EXACTLY_STRETCHED);
            if (Float.compare(scale, 1f) != 0) {
                m.setScale(scale, scale);

                if (loggingEnabled) {
                    L.d(LOG_SCALE_IMAGE, srcSize, srcSize.scale(scale), scale, decodingInfo.getImageKey());
                }
            }
        }
        // Flip bitmap if need
        if (flipHorizontal) {
            m.postScale(-1, 1);

            if (loggingEnabled)
                L.d(LOG_FLIP_IMAGE, decodingInfo.getImageKey());
        }
        // Rotate bitmap if need
        if (rotation != 0) {
            m.postRotate(rotation);

            if (loggingEnabled)
                L.d(LOG_ROTATE_IMAGE, rotation, decodingInfo.getImageKey());
        }

        Bitmap finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0, subsampledBitmap.getWidth(),
                subsampledBitmap.getHeight(), m, true);
        if (finalBitmap != subsampledBitmap) {
            subsampledBitmap.recycle();
        }
        return finalBitmap;
    }

    protected static class ExifInfo {

        public final int rotation;
        public final boolean flipHorizontal;

        protected ExifInfo() {
            this.rotation = 0;
            this.flipHorizontal = false;
        }

        protected ExifInfo(int rotation, boolean flipHorizontal) {
            this.rotation = rotation;
            this.flipHorizontal = flipHorizontal;
        }
    }

    protected static class ImageFileInfo {

        public final ImageSize imageSize;
        public final ExifInfo exif;

        protected ImageFileInfo(ImageSize imageSize, ExifInfo exif) {
            this.imageSize = imageSize;
            this.exif = exif;
        }
    }
}