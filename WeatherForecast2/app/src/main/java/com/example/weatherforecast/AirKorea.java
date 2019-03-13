package com.example.weatherforecast;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "response", strict = false)
public class AirKorea {
    //@SerializedName("list")
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
        @Element(name = "numOfRows")
        int numOfRows;

        @Element(name = "pageNo")
        int pageNo;

        @Element(name = "totalCount")
        int totalCount;
    }

    @Root(name = "items", strict = false)
    static public class Items {
        @ElementList(name = "item" ,inline = true)
        ArrayList<item> itemlist;
    }

    @Root(name = "item", strict = false)
    static public class item {


        @Element(name = "stationName")
        // @SerializedName("stationName")

                String stationName;
        @Element(name = "dataTime")
        //@SerializedName("dataTime")
                String dataTime;
        @Element(name = "pm10Value",required = false)
        //@SerializedName("pm10Value")
                String pm10Value;
        @Element(name = "pm25Value",required = false)
        //@SerializedName("pm25Value")
                String pm25Value;
        @Element(name = "pm10Grade",required = false)
//        @SerializedName("pm10Grade")
                String pm10Grade;
        @Element(name = "pm25Grade",required = false)
//        @SerializedName("pm25Grade")
                String pm25Grade;

        public String getStationName() {
            return stationName;
        }

        public void setStationName(String stationName) {
            this.stationName = stationName;
        }

        public String getDataTime() {
            return dataTime;
        }

        public void setDataTime(String dataTime) {
            this.dataTime = dataTime;
        }

        public String getPm10Value() {
            return pm10Value;
        }

        public void setPm10Value(String pm10Value) {
            this.pm10Value = pm10Value;
        }

        public String getPm2_5Value() {
            return pm25Value;
        }

        public void setPm2_5Value(String pm2_5Value) {
            this.pm25Value = pm2_5Value;
        }

        public String getPm10Grade() {
            return pm10Grade;
        }

        public void setPm10Grade(String  pm10Grade) {
            this.pm10Grade = pm10Grade;
        }

        public String getPm2_5Grade() {
            return pm25Grade;
        }

        public void setPm2_5Grade(String  pm2_5Grade) {
            this.pm25Grade = pm2_5Grade;
        }

    }
}