package com.sleepywang.accentmap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sleepywang.accentmap.entity.Dialect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DialectMapper extends BaseMapper<Dialect> {
    @Select("Select * from dialects where d_id!=0")
    List<Dialect> getAllDialects();
    Dialect getDialectByDId(@Param("dId") int dId);
    @Select("Select d.d_id,d.d_name,l.l_id from dialects d,languages l where d.l_id=l.l_id and l.l_id=#{lId}")
    List<Dialect>getDialectsByLId(@Param("lId") int lId);
}
