package com.example.springboot.controller;

import com.example.springboot.commmon.Result;
import com.example.springboot.entity.EspIoT;
import com.example.springboot.entity.Record;
import com.example.springboot.service.EspService;
import com.example.springboot.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Function:
 * Writer：COOOIKX
 * Time: 2023/12/11 14:22
 */
@CrossOrigin
@RestController
@Controller
@RequestMapping("/api")
public class RecordController {

    @Autowired
    private MqttConsumerConfig client;
    @Autowired
    RecordService recordService;
    @Autowired
    EspService espService;

    @Value("${spring.mqtt.client.id}")
    private String clientId;

    @RequestMapping("/connect")
    @ResponseBody
    public String connect(){
        client.connect();
        return clientId + "连接到服务器";
    }

    @RequestMapping("/disConnect")
    @ResponseBody
    public String disConnect(){
        client.disConnect();
        return clientId + "与服务器断开连接";
    }

    @GetMapping("/data")
    public Result getRecord() {
        //return new Random().nextInt(40);
        Record record = recordService.getNewRecord();
        return Result.success(record);
    }

    @GetMapping("/alldatas")
    public Result getAllData() {
        List<Record> records = recordService.getAllData();
        return Result.success(records);
    }

    @GetMapping("/count")
    public Result getCount() {
        int result = recordService.getRecordCount();
        return Result.success(result);
    }


    @GetMapping("/avgtemp")
    public Result getAvgTemp() {
        List<Record> records = recordService.getAllData();
        double sum = 0;
        for (Record instance:records) {
            sum = sum + Double.parseDouble(instance.getTemperature());
        }
        return Result.success(sum / records.size());
    }

    @GetMapping("/avghum")
    public Result getAvgHum() {
        List<Record> records = recordService.getAllData();
        double sum = 0;
        for (Record instance:records) {
            sum = sum + Double.parseDouble(instance.getHumidity());
        }
        return Result.success(sum / records.size());
    }

    @GetMapping("/latestdata/{froms}")
    public Result getLatestTwentyRecordsById(@PathVariable Integer froms) {
        List<Record> records = recordService.getLatestTwentyRecordsById(froms);
        records.sort(new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
//                long x1 = Long.parseLong(o1.getTimestamp());
//                long x2 = Long.parseLong(o2.getTimestamp());
                return o1.getId().compareTo(o2.getId());
            }
        });
        return Result.success(records);
    }

    @GetMapping("/getIotById/{froms}")
    public Result getIotById(@PathVariable Integer froms) {
        EspIoT iot = espService.getIotById(froms);
        return Result.success(iot);
    }

    @GetMapping("/getAllIot")
    public Result getAllIot() {
        List<EspIoT> espIoTS = espService.getAllIot();
        for (EspIoT iot:espIoTS) {
            int id = iot.getHardid();
            int recordCount = recordService.getRecordCountByIotId(id);
            espService.setSendCount(id,recordCount);
            iot.setDatanum(recordCount);
        }
        return Result.success(espIoTS);
    }

    @GetMapping("/getIotCount")
    public Result getIotCount() {
        int result = espService.getIotCount();
        return Result.success(result);
    }


    @GetMapping("/abnormaltempdata")
    public Result getAbnormalTempData() {
        List<Record> records = recordService.getAllData();
        EspIoT iot1 = espService.getIotById(1);
        EspIoT iot2 = espService.getIotById(2);
        int count = 0;
        for (Record instance: records) {
            double curTemp = Double.parseDouble(instance.getTemperature());
            int id = Integer.parseInt(instance.getFroms());
            if(id == 1) {
                if(!(iot1.getTempmin() <= curTemp && curTemp <= iot1.getTempmax())) {
                    count++;
                }

            } else if(id == 2) {
                if(!(iot2.getTempmin() <= curTemp && curTemp <= iot2.getTempmax())) {
                    count++;
                }

            }
        }
        return Result.success(count);
    }
    @GetMapping("/abnormalhumidata")
    public Result getAbnormalHumiData() {
        List<Record> records = recordService.getAllData();
        EspIoT iot1 = espService.getIotById(1);
        EspIoT iot2 = espService.getIotById(2);
        int count = 0;
        for (Record instance: records) {
            double curHumi = Double.parseDouble(instance.getHumidity());
            int id = Integer.parseInt(instance.getFroms());
            if(id == 1) {

                if(!(iot1.getHumimin() <= curHumi && curHumi <= iot1.getHumimax())) {
                    count++;
                }
            } else if(id == 2) {

                if(!(iot2.getHumimin() <= curHumi && curHumi <= iot2.getHumimax())) {
                    count++;
                }
            }
        }
        return Result.success(count);
    }

    @PutMapping("/updateIot")
    public Result updateIot(@RequestBody EspIoT iot) {
        espService.updateIot(iot);
        return Result.success();
    }
    @DeleteMapping("/deleteIot/{id}")
    public Result deleteIot(@PathVariable Integer id) {
        espService.deleteIot(id);
        return Result.success();
    }
    @PostMapping("/addIot")
    public Result add(@RequestBody EspIoT iot) {
        try {
            espService.insertIot(iot);
        } catch (Exception e) {
            if(e instanceof DuplicateKeyException) {
                return Result.error("Insert Wrong");
            } else {
                return Result.error("System Wrong");
            }
        }
        return Result.success();
    }
}
