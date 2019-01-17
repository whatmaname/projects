package com.example.pc.mypet;

 public class Animal {
     double kgs;
     double age;
     String kind;
     boolean male;
     int price;
     int imageID ;
     int num;


     Animal(double[] value, String kind, int num){
         this.age = value[0];
         this.kgs=value[1];
         this.kind = kind;
         this.num = num;
         switch (kind.toString()){
             case "소":
                 price = age>15?(int)(kgs * 7600):(int)(kgs * 8700);
                 break;
             case "양":
                 price = age>7?(int)(kgs * 8700):(int)(kgs * 10000);
                 break;
             case "돼지":
                 price = age>10?(int)(kgs * 3560):(int)(kgs * 4720);
                 break;
             case "닭":
                 price = age>5?(int)(kgs * 4000):(int)(kgs * 4500);
                 break;
         }
         int rand = (int)(Math.random()*2)+1;
         switch (rand){
             case 1:
                 male=true;
                 break;
             case 2:
                 male=false;
                 break;
         }
     }

     public  void feed(){ kgs = (int)((kgs+0.2)*100)/100.0;}
     public  void cure(){}

     public String getMale(){
         return male?"수":"암";
     }


     public String slaughter () {
         String meat = null;
         if (kgs<100){
             meat=30+"덩이";
         }else if (kgs<= 500&&kgs>=100){
             meat=50+"덩이";
         }
         else if (kgs<= 1000&&kgs>500){
             meat=70+"덩이";
         }
         return meat;
     }

     public String getInfo () {
         return "품종 :" + kind + "  나이 : " + age + "\n성별 : "+ getMale();
     }
     public String getInfos () {
         return "품종 :" + kind + "  나이 : " + age + "\n무게 : "+ kgs;
     }
     public String getStatus () {
         return "가격 :" + price  ;
     }
     public String getData() { return  "품종 :" + kind + "  나이 : " + age+" kgs : "+kgs + "성별 : "+ getMale();   }

     public int getImgID(){
         switch (kind.toString()){
             case "소":
                 imageID= R.drawable.cow;
                 break;
             case "양":
                 imageID= R.drawable.sheep;
                 break;
             case "돼지":
                 imageID= R.drawable.pig1;
                 break;
             case "닭":
                 imageID= R.drawable.chicken;
                 break;

         }
         return imageID;
     }
 }
