package com.example.springboot.mapper;

import com.example.springboot.entity.EspIoT;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EspMapper {

    @Select("select * from `hardware` where hardid=#{id}")
    EspIoT getIotById(Integer froms);

    @Select("select * from `hardware`")
    List<EspIoT> getAllIot();

    @Select("select COUNT(*) from `hardware` where state=1;")
    int getIotCount();

    @Update("update `hardware` set datanum=#{recordCount} where hardid=#{id}")
    void setSendCount(int id, int recordCount);

    @Update("update `hardware` set tempmax = #{tempmax}, tempmin = #{tempmin}, humimax = #{humimax} " +
            ",humimin = #{humimin},description = #{description} where hardid = #{hardid}")
    void update(EspIoT iot);

    @Delete("delete from `hardware` where hardid = #{id}")
    void delete(Integer id);

    @Insert("insert into `hardware` (hardid,tempmax,tempmin,humimax,humimin,model,datanum,state,description) values " +
            "(#{hardid},#{tempmax},#{tempmin},#{humimax},#{humimin},#{model},#{datanum},#{state},#{description})")
    void insert(EspIoT iot);
}
