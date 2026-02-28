package com.sleepywang.accentmap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sleepywang.accentmap.entity.SubDialect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SubDialectMapper extends BaseMapper<SubDialect> {
    @Select("Select * from subdialects where s_id!=0")
    List<SubDialect> getAllSubDialects();
    SubDialect getSubDialectBySId(@Param("sId") int sId);
    @Select("Select s.s_id,s.s_name,d.d_id from subdialects s,dialects d where s.d_id=d.d_id and d.d_id=#{dId}")
    List<SubDialect>getSubDialectsByDId(@Param("dId") int dId);
}
