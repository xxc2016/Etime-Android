package com.student.xxc.etime.impl;

import java.util.LinkedList;

public class TraceReceiver {//用于传递日程参数作为算法支撑

    public LinkedList<Trace> traceList;//存储日程的序列
    public LinkedList<LinkedList<Integer>>  costTable;//日程之间的路程花费时间  序号为TraceList的顺序

    static class Trace{//日程类
        int label;//事件标记号
        int priority;//时间优先权
        int last_time;//持续时间
        int earliest_start_time;//最早开始 距离时间
        int latest_end_time;//最晚结束 距离时间
    }
}
