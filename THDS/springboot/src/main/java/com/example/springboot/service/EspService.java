package com.example.springboot.service;

import com.example.springboot.entity.EspIoT;
import com.example.springboot.mapper.EspMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Function:
 * Writer：COOOIKX
 * Time: 2023/12/24 0:46
 */
@Service
public class EspService {
    @Autowired
    EspMapper espMapper;

    public EspIoT getIotById(Integer froms) {
        return espMapper.getIotById(froms);
    }

    public List<EspIoT> getAllIot() {
        return espMapper.getAllIot();
    }

    public int getIotCount() {
        return espMapper.getIotCount();
    }

    public void setSendCount(int id, int recordCount) {
        espMapper.setSendCount(id,recordCount);
    }

    public void updateIot(EspIoT iot) {
        espMapper.update(iot);
    }

    public void deleteIot(Integer id) {
        espMapper.delete(id);
    }

    public void insertIot(EspIoT iot) {
        espMapper.insert(iot);
    }
}
