package com.student.xxc.etime.util;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCacheUtil {
    private LruCache<String,Bitmap>bitmapLruCache;//缓存bitmap

    public void initilize(){
        int maxMemory=(int)(Runtime.getRuntime().maxMemory()/1024);
        int cacheSize=maxMemory/8;
        if(bitmapLruCache==null) {
            bitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount() / 1024;
                }
            };
        }
    }
    public  void addBitmapToCache(String key,Bitmap bitmap){
        if(getBitmapFromCache(key)==null){
            bitmapLruCache.put(key,bitmap);
        }
    }

    public Bitmap getBitmapFromCache(String key) {
        return bitmapLruCache.get(key);
    }
}
