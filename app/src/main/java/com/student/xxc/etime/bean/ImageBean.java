package com.student.xxc.etime.bean;

import java.util.List;

public class ImageBean {

    private List<String> imagePath_src;
    private List<String>  imagePath;
    private List<Integer>  imagePosition;//图片位置码
    private String source;
    private int requestCode;
    private int responseCode;


    public static final int  IMAGE_GET_SOURCE_REQUEST = 0x06001;//请求图片原图;
    public static final int  IMAGE_GET_SOURCE_RESPONSE_SUCCESSED = 0x06002;// 请求图片原图成功
    public static final int  IMAGE_GET_SOURCE_RESPONSE_FAILED = 0x06003;// 请求图片原图失败
    public static final int  UNKNOWN_ERROR = 0x06004;//未知错误



    public List<String> getImagePath() {
        return imagePath;
    }

    public List<String> getImagePath_src() {
        return imagePath_src;
    }

    public void setImagePath(List<String> imagePath) {
        this.imagePath = imagePath;
    }

    public void setImagePath_src(List<String> imagePath_src) {
        this.imagePath_src = imagePath_src;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public List<Integer> getImagePosition() {
        return imagePosition;
    }

    public void setImagePosition(List<Integer> imagePosition) {
        this.imagePosition = imagePosition;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
