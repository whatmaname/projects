#include <SoftwareSerial.h>
#include <Stepper.h>
#define TRIG 4
#define ECHO 5
#define TRIG2 6
#define ECHO2 7

SoftwareSerial BTSerial(2,3);   // 2번 수신(RX), 3번 송신(TX)
int stepsPerRevolution = 1024;    // 1024 = 180도
Stepper myStepper(stepsPerRevolution,11,9,10,8); 
int current_floor = 1;  // 현재 엘레베이터 층수를 저장하는 변수
boolean car = false;          // car = false 이면 차가 접근하지 않은 상태, car = true 이면 차가 접근한 상태
int tmp = 0;
 
void setup() {
  pinMode(TRIG,OUTPUT);
  pinMode(ECHO,INPUT);
  pinMode(TRIG2,OUTPUT);
  pinMode(ECHO2,INPUT);
  myStepper.setSpeed(30);//스텝모터 회전속도설정 
  Serial.begin(9600);     
  BTSerial.begin(9600);
}

void loop() {
  CarDistance();//초음파센서로 차가들어왔는지 확인
  if(car == true){//차가 들어왔다면
    if(current_floor == 1){
      tmp = 0;  //1층이면 tmp에 0을저장
    }else if(current_floor == 2){
      tmp = -3000; //2층이면 tmp에 -3000을저장
    }else if(current_floor == 3){
      tmp = -5200; //3층이면 tmp에 -5200을저장
    }
    myStepper.step(tmp); //스텝모터를 tmp 만큼 회전
    car = false; //다음 차가 들어왔을시 car변수에 true를 저장할 수 있게 car에 false를 저장
    BTSerial.println("1");
  }
  
  if(BTSerial.available()){
    byte data = BTSerial.read();

    // 1024 : 0.5바퀴, 2048 : 1바퀴, 4096 : 2바퀴
    if(data == '1'){//안드로이드로부터 받은 데이터가‘1’이라면
      if(current_floor == 1){
        tmp = 0; //1층이면 tmp에 0을저장
      }else if(current_floor == 2){
        tmp = -3000; //2층이면 tmp에 -3000을저장
      }else if(current_floor == 3){
        tmp = -5200;  //3층이면 tmp에 -5200을저장
      }
    } else if(data == '2'){
      if(current_floor == 1){
        tmp = 3000;
      }else if(current_floor == 2){
        tmp = 0;
      }else if(current_floor == 3){
        tmp = -2200;
      }
    } else if(data == '3'){
      if(current_floor == 1){
        tmp = 5200;
      }else if(current_floor == 2){
        tmp = 2200;
      }else if(current_floor == 3){
        tmp = 0;
      }
    } else {
      tmp = 0;
    }
    myStepper.step(tmp);
    ElvDistance(); //엘리베이터 현재 층수 확인
    BTSerial.println(current_floor); //안드로이드에 현재층수 전송
  }
  ElvDistance();
  delay(1000);
}

void ElvDistance(){
  digitalWrite(TRIG,LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG,HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG,LOW);
  long distance = pulseIn(ECHO,HIGH) / 58.2;

  if(distance > 24){
    current_floor = 1;
  } else if(distance > 13) {
    current_floor = 2;
  } else if(distance > 5) {
    current_floor = 3;
  }

  Serial.print("거리 : ");
  Serial.print(distance);
  Serial.print("cm, ");
  if(current_floor == 1){
    Serial.println("현재층수 : 1층");
  } else if(current_floor == 2){
    Serial.println("현재층수 : 2층");
  } else if(current_floor == 3){
    Serial.println("현재층수 : 3층");
  }
}

void CarDistance(){
  digitalWrite(TRIG2,LOW);
  delayMicroseconds(2);
  digitalWrite(TRIG2,HIGH);
  delayMicroseconds(10);
  digitalWrite(TRIG2,LOW);
  long distance2 = pulseIn(ECHO2,HIGH) / 58.2;

  if(distance2 < 5){
    car = true;
    Serial.println("차가 접근하였습니다.");
  }
}
