package com.student.xxc.etime.bean;

import java.util.List;

public class TraceBean {

    private List<Trace> traces;
    private String userAccount;
    private String source;
    private int requestCode;
    private int responseCode;

    public final static int  UP_STORE_REQUEST = 0x01001;//上传日志请求
    public final static int  UP_STORE_RESPONSE_SUCCESSED = 0x01002;//上传日志成功
    public final static int  UP_STORE_RESPONSE_FAILED =0x01003;//上传日志失败
    public final static int  DOWN_LOAD_REQUEST = 0x01004;//下载日志请求
    public final static int  DOWN_LOAD_REUQEST_SUCCESSED = 0x01005;//下载日志成功
    public final static int  DOWN_LOAD_REQUEST_FAILED =0x01006;//下载日志失败
    public final static int  UNKNOWN_ERROR =  0x01007; //未知错误

    public static class Trace {  //用于上传和下载用户日程
        public String userAccount;
        /*public String time;
        public String event;
        public String date;
        public boolean finish;
        public int traceId;
        public int imageType;
        public boolean important;
        public boolean urgent;
        public boolean fix;
        public int predict;*/
        public String ESTime;//最早开始时间
        public String LETime;//最晚结束时间
        public String time;//插入时间
        public String event;//事件
        public String date;//日期
        public boolean finish;//结束标识
        public int traceId;//每个的唯一索引 在traceManager中保存计数
        public boolean hasESTime;//有最早开始时间
        public boolean hasLETime;//有最晚开始时间
        public String siteId;//地址标识号
        public String siteText;//地址文字
        public int predict;//持续时间


        public  Trace(String userAccount,String ESTime,String LETime,String time,String event,String date,boolean finish,int traceId
                ,boolean hasESTime,boolean hasLETime,String siteId,String siteText,int predict) {
            this.userAccount = userAccount;
            this.time = time;
            this.ESTime =ESTime;
            this.LETime = LETime;
            this.event =event;
            this.date = date;
            this.finish = finish;
            this.hasESTime = hasESTime;
            this.hasLETime = hasLETime;
            this.siteId = siteId;
            this.siteText  =siteText;
            this.traceId = traceId;
            this.predict = predict;
        }

        public  Trace(String userAccount,String ESTime,String LETime,String time,String event,String date,int finish,int traceId
                ,int hasESTime,int hasLETime,String siteId,String siteText,int predict) {
            this.userAccount = userAccount;
            this.time = time;
            this.ESTime =ESTime;
            this.LETime = LETime;
            this.event =event;
            this.date = date;
            this.finish = judgeFinish_boolean(finish);
            this.hasESTime = judge_hasEst_boolean(hasESTime);
            this.hasLETime = judge_hasLet_boolean(hasLETime);
            this.siteId = siteId;
            this.siteText  =siteText;
            this.traceId = traceId;
            this.predict = predict;
        }



       /* public  Trace(String userAccount,String time,String event,String date,int finish,int traceId
                ,int important,int urgent,int fix,int predict)
        {
            this.userAccount = userAccount;
            this.time = time;
            this.event = event;
            this.date  = date;
            this.finish  =judgeFinish_boolean(finish);
            this.traceId = traceId;
            this.important = judgeImportant_boolean(important);
            this.urgent = judgeUrgent_boolean(urgent);
            this.fix = judgeFix_boolean(fix);
            this.predict = predict;
        }*/


        /*public static boolean judgeImportant_boolean(int important)
        {
            if(important==1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }


        public static boolean judgeUrgent_boolean(int urgent)
        {
            if(urgent==1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }*/

        public static boolean judgeFinish_boolean(int finish)
        {
            if(finish==1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        /*public static boolean judgeFix_boolean(int fix)
        {
            if(fix==1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }*/

        public static boolean judge_hasEst_boolean(int hasESTime)
        {
            if(hasESTime==1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        public static boolean judge_hasLet_boolean(int hasLETime)
        {
            if(hasLETime==1)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setTraces(List<Trace> traces) {
        this.traces = traces;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public List<Trace> getTraces() {
        return traces;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }


    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
