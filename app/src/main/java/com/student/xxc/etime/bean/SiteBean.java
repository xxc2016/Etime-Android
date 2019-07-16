package com.student.xxc.etime.bean;

import java.util.List;

public class SiteBean {//高德地图解析Bean类  网络工具自动生成

    private String status;
    private String count;
    private String info;
    private String infocode;
    private List<Pois> pois;
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setCount(String count) {
        this.count = count;
    }
    public String getCount() {
        return count;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    public String getInfo() {
        return info;
    }

    public void setInfocode(String infocode) {
        this.infocode = infocode;
    }
    public String getInfocode() {
        return infocode;
    }

    public void setPois(List<Pois> pois) {
        this.pois = pois;
    }
    public List<Pois> getPois() {
        return pois;
    }

    static public class Pois {

        private String id;
        private String parent;
        private String childtype;
        private String name;
        private List<String> tag;
        private String type;
        private String typecode;
        private List<String> biz_type;
        private String address;
        private String location;
        private String tel;
        private List<String> postcode;
        private List<String> website;
        private List<String> email;
        private String pcode;
        private String pname;
        private String citycode;
        private String cityname;
        private String adcode;
        private String adname;
        private List<String> importance;
        private List<String> shopid;
        private String shopinfo;
        private List<String> poiweight;
        private String gridcode;
        private List<String> distance;
        private String navi_poiid;
        private List<String> entr_location;
        private List<String> business_area;
        private List<String> exit_location;
        private String match;
        private String recommend;
        private String timestamp;
        private String indoor_map;
        private Indoor_data indoor_data;
        private List<String> indoor_src;
        private String groupbuy_num;
        private String discount_num;
        private Biz_ext biz_ext;
        private List<String> event;
        private Deep_info deep_info;
        private List<String> rich_content;
        private List<String> children;
        private List<Photos> photos;

        static public class Photos {

            private String title;
            private String url;
        }

        static public class Indoor_data {

            private List<String> cpid;
            private List<String> floor;
            private List<String> truefloor;
            private List<String> cmsid;
        }

        static public class Deep_info {
            private List<String> deepsrc;
        }

        static public class Biz_ext {
            private String rating;
            private List<String> cost;
        }

    }

}