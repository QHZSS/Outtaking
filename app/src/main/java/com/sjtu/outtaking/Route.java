package com.sjtu.outtaking;

import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 秦皓喆 on 2018/6/23.
 */

public class Route {
    private List<LatLng> routeplan=new ArrayList<>();
    private List<LatLng> address;
    private int[] order;
    private List<OverlayOptions> lines;

    public List<LatLng> getRouteplan() {
        return routeplan;
    }

    public void setRouteplan(List<LatLng> routeplan) {
        this.routeplan = routeplan;
    }

    public void setAddress(List<LatLng> address) {
        this.address = address;
    }

    public List<LatLng> getAddress() {
        return address;
    }

    public void setOrder(int[] order) {
        this.order = order;
    }

    public int[] getOrder() {
        return order;
    }

    public List<OverlayOptions> getLines() {
        return lines;
    }

    public void setLines(List<OverlayOptions> lines) {
        this.lines = lines;
    }

    public void PlanRoute(){
        String url = "http://api.map.baidu.com/direction/v2/driving?"
                + "origin="+address.get(0).latitude+","+address.get(0).longitude+"&destination="+address.get(0).latitude+","+address.get(0).longitude+"&ak=x2Y5ecxTQ45YAEAzgzrFiZt3p1OQOEO0";
        String waypoints =new String();
        for(int i =1;i<address.size();i++){
            waypoints+=address.get(order[i]).latitude+","+address.get(order[i]).longitude+"|";
        }
        waypoints=waypoints.substring(0,waypoints.length()-1);
        url+="&waypoints="+waypoints;
        System.out.println(url);
        URL myURL = null;
        URLConnection httpsConn = null;
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            httpsConn = myURL.openConnection();//建立连接
            if (httpsConn != null) {
                InputStreamReader insr = new InputStreamReader(//传输数据
                        httpsConn.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(insr);
                String data = null;
                if ((data = br.readLine()) != null) {
                    System.out.println(data);
                    //{"status":0,"result":[{"distance":{"text":"425米","value":425},"duration":{"text":"2分钟","value":127}}],"message":"成功"}
                    //Distance = Integer.parseInt(data.substring(data.indexOf("\"value\":") + ("\"value\":").length(), data.indexOf("},\"duration\"")));
                    JSONObject jsonObject=new JSONObject(data);
                    JSONObject result=jsonObject.getJSONObject("result");
                    JSONArray routes=result.getJSONArray("routes");
                    JSONObject route=routes.getJSONObject(0);
                    JSONArray steps=route.getJSONArray("steps");
                    for(int i=0;i<steps.length();i++){
                        JSONObject step=steps.getJSONObject(i);
                        JSONObject start_location=step.getJSONObject("start_location");
                        routeplan.add(new LatLng(start_location.getDouble("lat"),start_location.getDouble("lng")));
                        JSONObject end_location=step.getJSONObject("end_location");
                        routeplan.add(new LatLng(end_location.getDouble("lat"),end_location.getDouble("lng")));
                    }
                }

                insr.close();
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
