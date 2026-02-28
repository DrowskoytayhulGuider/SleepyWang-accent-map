package com.sleepywang.accentmap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sleepywang.accentmap.entity.County;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CountyMapper extends BaseMapper<County> {
    List<County> getCountiesByAccent(@Param("language") String language, @Param("dialect") String dialect,
                                     @Param("subDialect") String subDialect, @Param("accent") String accent);
}
