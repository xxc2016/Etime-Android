package com.student.xxc.etime.helper;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapTimeHelper {
    public List<Integer> list;
    public List<LatLonPoint> pointList;
    String key="dd48fa03abf75348ba4b3b7d53cbf701";
    String walkUrl="https://restapi.amap.com/v3/direction/walking?";
    String busUrl="https://restapi.amap.com/v3/direction/transit/integrated?";
    String driveUrl="https://restapi.amap.com/v3/direction/driving?";
    OkHttpClient okHttpClient;
    Context context;
    public MapTimeHelper(Context context){
        okHttpClient = new OkHttpClient();
        list=new ArrayList<>();
        pointList=new ArrayList<>();
        this.context=context;
    }

    public String value(LatLonPoint point){
        return String.valueOf(point.getLongitude())+","+String.valueOf(point.getLatitude());
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
                        val=jsonObject.get("route").getAsJsonObject().get("paths").getAsJsonObject().get("0").getAsJsonObject().get("duration").getAsInt();
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
                Looper.prepare();

            }
        });
    }
}
