package com.example.weatherforecast;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name = "response", strict = false)
public class Weather {
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
        @ElementList(name = "item" ,inline = true,required = false)
        ArrayList<item> itemlist;
    }

    @Root(name = "item", strict = false)
    static public class item {
        @Element(name = "regId")
        String regId;

        @Element(name = "taMin3",required = false)
        int taMin3;
        @Element(name = "taMax3",required = false)
        int taMax3;
        @Element(name = "taMin4",required = false)
        int taMin4;
        @Element(name = "taMax4",required = false)
        int taMax4;
        @Element(name = "taMin5",required = false)
        int taMin5;
        @Element(name = "taMax5",required = false)
        int taMax5;
        @Element(name = "taMin6",required = false)
        int taMin6;
        @Element(name = "taMax6",required = false)
        int taMax6;
        @Element(name = "taMin7",required = false)
        int taMin7;
        @Element(name = "taMax7",required = false)
        int taMax7;


        @Element(name = "wf3Am",required = false)
        String wf3Am;
        @Element(name = "wf3Pm",required = false)
        String wf3Pm;
        @Element(name = "wf4Am",required = false)
        String wf4Am;
        @Element(name = "wf4Pm",required = false)
        String wf4Pm;
        @Element(name = "wf5Am",required = false)
        String wf5Am;
        @Element(name = "wf5Pm",required = false)
        String wf5Pm;
        @Element(name = "wf6Am",required = false)
        String  wf6Am;
        @Element(name = "wf6Pm",required = false)
        String wf6Pm;
        @Element(name = "wf7Am",required = false)
        String wf7Am;
        @Element(name = "wf7Pm",required = false)
        String wf7Pm;

        public String getRegId() {
            return regId;
        }

        public int getTaMin3() {
            return taMin3;
        }

        public int getTaMax3() {
            return taMax3;
        }

        public int getTaMin4() {
            return taMin4;
        }

        public int getTaMax4() {
            return taMax4;
        }

        public int getTaMin5() {
            return taMin5;
        }

        public int getTaMax5() {
            return taMax5;
        }

        public int getTaMin6() {
            return taMin6;
        }

        public int getTaMax6() {
            return taMax6;
        }

        public int getTaMin7() {
            return taMin7;
        }

        public int getTaMax7() {
            return taMax7;
        }

        public String getWf3Am() {
            return wf3Am;
        }

        public String getWf3Pm() {
            return wf3Pm;
        }

        public String getWf4Am() {
            return wf4Am;
        }

        public String getWf4Pm() {
            return wf4Pm;
        }

        public String getWf5Am() {
            return wf5Am;
        }

        public String getWf5Pm() {
            return wf5Pm;
        }

        public String getWf6Am() {
            return wf6Am;
        }

        public String getWf6Pm() {
            return wf6Pm;
        }

        public String getWf7Am() {
            return wf7Am;
        }

        public String getWf7Pm() {
            return wf7Pm;
        }

    }

}
