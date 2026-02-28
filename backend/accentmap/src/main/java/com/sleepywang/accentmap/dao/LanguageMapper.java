package com.sleepywang.accentmap.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sleepywang.accentmap.entity.Language;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LanguageMapper extends BaseMapper<Language> {
    @Select("Select * from languages where l_id!=0")
    List<Language> getAllLanguages();
    @Select("select * from languages where l_id=#{lId}")
    Language getLanguageByLId(@Param("lId") int lId);
}
