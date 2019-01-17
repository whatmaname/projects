package com.example.kcalcalculator;


//커스텀리스트뷰에 외부데이터 받아서 뿌려줄 클래스
public class itemdata {
    private String titleTxt;
    int subTitle;


     itemdata(String titleTxt,int subTitle){
         this.titleTxt=titleTxt;
         this.subTitle=subTitle;
     }
    String getTitleTxt(){
         return titleTxt;
    }
    String getSubTitle(){
         return subTitle+"kcal";
    }

}
