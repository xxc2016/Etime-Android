package com.student.xxc.etime.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.student.xxc.etime.entity.Trace;
import com.student.xxc.etime.impl.GetRxJavaTrace_Interface;
import com.student.xxc.etime.view.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapTimeHelper {
    public List<Integer> list;
    public List<LatLonPoint> pointList;
    public List<String> askList;
    public MainActivity.MyHandler myhandler;
    String key="319d0ce7e9696a05188d69622cb31511";
    String walkUrl="https://restapi.amap.com/v3/direction/walking?";
    String busUrl="https://restapi.amap.com/v3/direction/transit/integrated?";
    String driveUrl="https://restapi.amap.com/v3/direction/driving?";
    String idSearchUrl="https://restapi.amap.com/v3/place/detail?parameters";
    private static final int REQUEST_CODE_INIT_DATA = 666;
    OkHttpClient okHttpClient;
    Context context;
    public MapTimeHelper(Context context,MainActivity.MyHandler handler){
        okHttpClient = new OkHttpClient();
        list=new ArrayList<>();
        pointList=new ArrayList<>();
        this.context=context;
        this.myhandler = handler;
    }

    public String value(LatLonPoint point){
        return String.valueOf(point.getLongitude())+","+String.valueOf(point.getLatitude());
    }

    public void updateSort()
    {
//        Looper.prepare();
        Bundle bundle = new Bundle();
        bundle.putInt("response",REQUEST_CODE_INIT_DATA);
        Message message = myhandler.obtainMessage();
        message.setData(bundle);
        message.sendToTarget();
    }

    public void idToPoint(String id){
        PoiSearch poiSearch = new PoiSearch(context, null);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {

            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {
                pointList.add(poiItem.getLatLonPoint());
            }
        });
        poiSearch.searchPOIIdAsyn(id);// 异步搜索
    }
    public void getPredictTime(final int type, LatLonPoint s, LatLonPoint e){
        String u="";
        switch (type){
            case 1: {
                u = walkUrl + "origin=" + value(s) + "&destination=" + value(e) + "&output=JSON" + "&key=" + key ;
                break;
            }
            case 2:{
                u=busUrl + "origin=" + value(s) + "&destination=" + value(e) + "&city=大连&output=JSON" + "&key=" + key;
                break;
            }
            case 3:{
                u=driveUrl + "origin=" + value(s) + "&destination=" + value(e) + "&extensions=base&output=JSON" + "&key=" + key;
                break;
            }
            default:{
                Log.e("getUrl", "type error!");
            }
        }
        Request request =new Request.Builder().
                url(u).get().build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("call failure", "onFailure: "+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res= response.body().toString();
                JsonObject jsonObject = (JsonObject) new JsonParser().parse(res).getAsJsonObject();
                int val=0;
                switch (type){
                    case 1:{
                        val=jsonObject.get("route").getAsJsonObject().get("paths").getAsJsonArray().get(0).getAsJsonObject().get("duration").getAsInt();
                        break;
                    }
                    case 2:{
                        val=jsonObject.get("route").getAsJsonObject().get("transits").getAsJsonObject().get("0").getAsJsonObject().get("duration").getAsInt();
                        break;
                    }
                    case 3:{
                        val=jsonObject.get("route").getAsJsonObject().get("paths").getAsJsonObject().get("0").getAsJsonObject().get("duration").getAsInt();
                    }
                }
                Log.e("time",String.valueOf(val/60));
                list.add(val/60);
            }
        });
    }

    public void askForTracePoint(List<String>  traceList, final boolean flag)//请求日程地点经纬度
    {
        askList  =traceList;
        if(askList==null || askList.size()==0)
        {
            Log.i("rxjava","-----------------------------------"+"nothing to ask!");
            if(flag)
            {
                askForPointDistance(traceList,flag);
            }
            return ;
        }

        Log.i("rxjava","-----------------------------------"+"start to ask");
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://restapi.amap.com")
                .build();
        GetRxJavaTrace_Interface request = retrofit.create(GetRxJavaTrace_Interface.class);

        List<String> temp = new LinkedList<String>();//从askList中找出需要询问的
        for(int i=0;i<askList.size();i++)
        {
             if(SiteHelper.getLanLonPoint(askList.get(i))==null)
             {
                 temp.add(askList.get(i));
             }
        }

        if(temp.size()==0)
        {
            Log.i("rxjava","-----------------------------------"+"nothing to ask!");
            askForPointDistance(askList,flag);//查询不同的距离
            return ;
        }

        Observable<JsonObject>[] Array = new Observable[temp.size()];
        for(int i=0;i<temp.size();i++)
        {
            Array[i] = request.getPoint(temp.get(i),key,"json");
        }

        Observable.mergeArray(Array).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JsonObject value) {
                        Log.i("rxjava","--------------------------------"+(value.toString()));
                        JsonObject jsonObject = (JsonObject) new JsonParser().parse((value.toString())).getAsJsonObject();
                        String siteId = jsonObject.get("pois").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
                        String l= jsonObject.get("pois").getAsJsonArray().get(0).getAsJsonObject().get("location").getAsString();
                        Log.i("rxjava","--------------------------------"+(l));
                        String [] latLon = l.split(",");
                        LatLonPoint latLonPoint  = new LatLonPoint(Double.valueOf(latLon[1]),Double.valueOf(latLon[0]));
                        SiteHelper.setLanLonPoint(siteId,latLonPoint.getLatitude(),latLonPoint.getLongitude());
                        pointList.add(latLonPoint);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("rxjava","----------------------over!");
                        MapTimeHelper.this.askForPointDistance(askList,flag);
                    }
                });
    }


    public void askForPointDistance(final List<String> askList,final boolean flag)//请求日程地点距离
    {
        if(askList==null || askList.size()==0)
        {
            if(flag)
            {
                updateSort();
            }
            return ;
        }

        Log.i("rxjava","----------------------------------size:"+askList.size());
        Log.i("rxjava","-----------------------------------"+"start to ask");
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://restapi.amap.com")
                .build();
        GetRxJavaTrace_Interface request = retrofit.create(GetRxJavaTrace_Interface.class);


       int count = 0;//查询的数量
        String [][] temp = new String[askList.size()*askList.size()][2];
        final String [][] latLonTemp = new String[askList.size()][2];

        for(int i=0;i<askList.size();i++)
        {
            String result = SiteHelper.getLanLonPoint(askList.get(i));
            if(result==null)
            {
                Log.i("rxjava","---------------------------"+"latlon is not prepared!");
                return ;
            }
            latLonTemp[i][0] = result;
            latLonTemp[i][1] = askList.get(i);
        }

        for(int i=0;i<askList.size();i++)
        {
            for(int j=0;j<askList.size();j++) {

                if(SiteHelper.getDistance(askList.get(i),askList.get(j))==-1)
                {
                    count++;
                    temp[count-1][0] = latLonTemp[i][0];
                    temp[count-1][1] = latLonTemp[j][0];
                    Log.i("rxjava","------------------------------------"+temp[count-1][0]+"   "+ temp[count-1][1]);
                }
            }
        }

        if(count==0)
        {
            Log.i("rxjava","-----------------------------------"+"not need to ask");
            if(flag)//为真的时候为调用只能排序
            {
                updateSort();
            }
            return;
        }

        Observable<JsonObject>[] Array = new Observable[count];
        for(int i=0;i<count;i++)
        {
            Array[i] = request.getDistance(temp[i][0],temp[i][1], "json",key);
        }

        Observable.mergeArray(Array).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(JsonObject value) {

                        try {
                            int d = value.get("route").getAsJsonObject().get("paths").getAsJsonArray().get(0).getAsJsonObject().get("duration").getAsInt();
                            d /= 60;
                            String orign = value.get("route").getAsJsonObject().get("origin").getAsString();
                            String destination = value.get("route").getAsJsonObject().get("destination").getAsString();

                            Log.i("rxjava", "--------------------------------" +d +"   "+ orign+"  "+ destination);

                            String siteId_a = null,siteId_b = null;
                            for(int i=0;i<askList.size();i++)
                            {
                                if(latLonTemp[i][0].equals(orign))
                                {
                                    siteId_a = latLonTemp[i][1];
                                }

                                if(latLonTemp[i][0].equals(destination))
                                {
                                    siteId_b = latLonTemp[i][1];
                                }
                            }

                            if(siteId_a!=null && siteId_b!=null)
                            {
                                SiteHelper.setDistance(siteId_a,siteId_b,d);
                            }

                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.i("rxjava","----------------------over!");

                        if(flag)//为真的时候为调用只能排序
                        {
                            updateSort();
                        }
                    }
                });
    }

}
