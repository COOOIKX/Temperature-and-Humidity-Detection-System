package com.example.springboot.entity;

import lombok.Data;

/**
 * Function:
 * Writer：COOOIKX
 * Time: 2023/12/24 0:44
 */
@Data
public class EspIoT {
    private int hardid;
    private double tempmax;
    private double tempmin;
    private double humimax;
    private double humimin;
    private String model;
    private int datanum;
    private Boolean state;
    private String description;
}
