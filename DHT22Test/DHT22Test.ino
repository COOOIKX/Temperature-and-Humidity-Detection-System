#include <DHT.h>
#include <ESP8266WiFi.h>
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>

#define DHTPIN 2 // 设置连接到esp8266的GPIO引脚 其实是D4！！
#define DHTTYPE DHT22 
DHT dht(DHTPIN, DHTTYPE);    
float currentTemperature = 0;
float currentHumidity = 0;
unsigned long currentUnixTime = 0;
//获取温湿度信息
void getTH() {
  delay(3000);
  float RH = dht.readHumidity();  
  float T = dht.readTemperature();
  currentTemperature = T;
  currentHumidity = RH;
}

//封装成json格式
char messageBuff[1024];
void getJson() {
  StaticJsonDocument<1024> doc;
  JsonObject root = doc.to<JsonObject>();
  root["Temperature"] = currentTemperature;
  root["Humidity"] = currentHumidity;
  root["ID"] = 2;
  root["Timestamp"] = currentUnixTime;
  serializeJson(doc, messageBuff);
}

//ESP8266WiFi连接信息 2.4G
const char* ssid = "nbu-wire1ess";
const char* password = "216001912";
//WiFi连接
void connectWiFi() {
  WiFi.begin(ssid,password);
  while(WiFi.status()!=WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi Connected.");
  Serial.println("");
}

// MQTT Broker
const char *mqttServer = "123.249.47.82";
const int mqttPort = 1883;
const char *topic = "eps8266-1912";
WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);
// MQTT服务器连接
void connectMQTTServer() {
  String clientID = "ESP8266-" + WiFi.macAddress();
  if(mqttClient.connect(clientID.c_str())) {
    Serial.println("MQTT Server Connected.");
    Serial.println("Server Address: ");
    Serial.println(mqttServer);
    Serial.println("ClientId:");
    Serial.println(clientID);
  } else {
    Serial.print("MQTT Server Connect Failed. Client State:");
    Serial.println(mqttClient.state());
    delay(3000);
  }
}
//信息发布
void publishMQTTmsg() {
  getJson();
  if(mqttClient.publish(topic,messageBuff)) {
    Serial.println("Sended."); 
  } else {
    Serial.println("Send Failed."); 
  }
  memset(messageBuff,0,1024);
}

//NTP 参数
const char *ntpServerName = "pool.ntp.org";
const int timeZone = 0;

WiFiUDP udp;
NTPClient timeClient(udp, ntpServerName, timeZone * 60);

void getUnixTime() {
  timeClient.update();
  unsigned long unixTime = timeClient.getEpochTime();
  if(unixTime < currentUnixTime) {
    currentUnixTime+=3;
  } else {
    currentUnixTime = unixTime; 
  }
  return;
}

void dataTest() {
  Serial.print("Unix Time: ");
  Serial.println(currentUnixTime);
  Serial.print("Temperature: ");
  Serial.println(currentTemperature);
  Serial.print("Humidity: ");
  Serial.println(currentHumidity);
  return;
}


void setup(){ 
  Serial.begin(9600);
   
  //传感器启用
  dht.begin();
  //连接网络   
  connectWiFi();
  //连接到NTP
  timeClient.begin();
  //设置MQTT服务器和端口号
  mqttClient.setServer(mqttServer,mqttPort);
  //连接MQTT服务器
  connectMQTTServer();        
}



void loop() {
  if(mqttClient.connected()) {
    getTH();
    getUnixTime();
    dataTest();
    publishMQTTmsg();
  } else {
    connectMQTTServer();
  }
}


