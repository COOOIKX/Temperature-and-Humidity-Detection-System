package com.example.springboot.controller;

import com.example.springboot.entity.Record;
import com.example.springboot.service.RecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.Map;

/**
 * Function:
 * Writer：COOOIKX
 * Time: 2023/12/11 14:32
 */
@Configuration
public class MqttConsumerConfig {
    @Value("${spring.mqtt.username}")
    private String username;

    @Value("${spring.mqtt.password}")
    private String password;

    @Value("${spring.mqtt.url}")
    private String hostUrl;

    @Value("${spring.mqtt.client.id}")
    private String clientId;

    @Value("${spring.mqtt.default.topic}")
    private String defaultTopic;

    private MqttClient client;

    @PostConstruct
    public void init() {
        connect();
    }

    @Autowired
    RecordService recordService;

    public void connect() {
        try {
            client = new MqttClient(hostUrl,clientId,new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setConnectionTimeout(100);
            options.setKeepAliveInterval(20);
            options.setWill("willTopic",(clientId + "与服务器断开连接").getBytes(),0,false);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    System.out.println("MQTT连接断开，发起重连......");
                    try {
                        if(client!=null && !client.isConnected()) {
                            client.reconnect();
                            System.out.println("尝试重新连接");
                        } else {
                            client.connect(options);
                            System.out.println("尝试建立新连接");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String msg = new String(message.getPayload());
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String,Object> map = objectMapper.readValue(msg, Map.class);

                    String Timestamp = String.valueOf(map.get("Timestamp"));
                    String from = String.valueOf(map.get("ID"));
                    String Humidity = String.valueOf(map.get("Humidity"));
                    String Temperature = String.valueOf(map.get("Temperature"));

                    Record record = new Record();
                    record.setFroms(from);
                    record.setTimestamp(Timestamp);
                    record.setTemperature(Temperature);
                    record.setHumidity(Humidity);

                    if(Long.parseLong(Timestamp)<100) return;

                    recordService.insertRecord(record);
                }
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            client.connect(options);
            client.subscribe(defaultTopic,0);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disConnect(){
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic,int qos){
        try {
            client.subscribe(topic,qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
