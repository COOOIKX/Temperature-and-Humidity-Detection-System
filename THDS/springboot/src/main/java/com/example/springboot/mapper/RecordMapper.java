package com.example.springboot.mapper;

import com.example.springboot.entity.Record;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecordMapper {
    @Insert("insert into `record` (froms,timestamp,temperature,humidity) " +
            "values (#{froms},#{timestamp},#{temperature},#{humidity})")
    void insert(Record record);


    @Select("select * from `record` order by id desc limit 1;")
    Record getNewRecord();

    @Select("select COUNT(*) from `record`;")
    int getRecordCount();

    @Select("select * from `record` order by timestamp asc")
    List<Record> getAllRecords();

    @Select("select * from `record` where froms = #{id} order by timestamp desc limit 20")
    List<Record> getLatestTwentyRecordsById(int id);

    @Select("select COUNT(*) from `record` where froms=#{id};")
    int getRecordCountByIotId(int id);


}
