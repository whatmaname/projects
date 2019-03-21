package com.example.findelectriccarstation;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

import retrofit2.Retrofit;

@Root(name = "response", strict = false)
public class StationInfo {
    @Element(name = "body")
    Body body;
    @Element(name = "header")
    Header header;

    @Root(name = "header", strict = false)
    static public class Header {
        @Element(name = "resultCode")
        int resultCode;
        @Element(name = "resultMsg")
        String resultMsg;

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public void setResultMsg(String resultMsg) {
            this.resultMsg = resultMsg;
        }
    }

    @Root(name = "body", strict = false)
    static public class Body {
        @Element(name = "items")
        Items items;
    }

    @Root(name = "items", strict = false)
    static public class Items {
        @ElementList(name = "item", inline = true)
        ArrayList<Item> itemlist;
    }

    @Root(name = "item", strict = false)
    static public class Item {
        @Element(name = "statNm", required = false)
        String stationName;
        @Element(name = "chgerType", required = false)
        int chgerType;
        @Element(name = "stat", required = false)
        int stat;

        @Element(name = "addrDoro", required = false)
        String addr;
        @Element(name = "lat" , required = false)
        double lat;
        @Element(name = "lng" , required = false)
        double lng;
        @Element(name = "useTime" , required = false)
        String useTime;

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        public int getChgerType() {
            return chgerType;
        }

        public void setChgerType(int chgerType) {
            this.chgerType = chgerType;
        }

        public int getStat() {
            return stat;
        }

        public void setStat(int stat) {
            this.stat = stat;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public String getUseTime() {
            return useTime;
        }

        public void setUseTime(String useTime) {
            this.useTime = useTime;
        }
    }

}
