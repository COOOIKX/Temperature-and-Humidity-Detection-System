package com.example.springboot.entity;

import lombok.Data;

/**
 * Function:
 * Writer：COOOIKX
 * Time: 2023/12/11 14:21
 */
@Data
public class Record {
    private Integer id;
    private String froms;
    private String timestamp;
    private String temperature;
    private String humidity;
}
