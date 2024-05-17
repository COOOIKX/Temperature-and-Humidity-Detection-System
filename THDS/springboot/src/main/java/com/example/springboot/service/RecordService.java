package com.example.springboot.service;

import com.example.springboot.entity.Record;
import com.example.springboot.mapper.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function:
 * Writer：COOOIKX
 * Time: 2023/12/11 14:29
 */
@Service
public class RecordService {
    @Autowired
    RecordMapper recordMapper;

    public Record getNewRecord() {
        return recordMapper.getNewRecord();
    }

    public void insertRecord(Record record) {
        recordMapper.insert(record);
    }

    public int getRecordCount() {
        return recordMapper.getRecordCount();
    }

    public List<Record> getAllData() {
        return recordMapper.getAllRecords();
    }

    public List<Record> getLatestTwentyRecordsById(int id) {
        return recordMapper.getLatestTwentyRecordsById(id);
    }

    public int getRecordCountByIotId(int id) {
        return recordMapper.getRecordCountByIotId(id);
    }



}
