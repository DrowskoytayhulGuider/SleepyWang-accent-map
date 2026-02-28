package com.sleepywang.accentmap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sleepywang.accentmap.dto.AccentIdDTO;
import com.sleepywang.accentmap.dto.FullAccentName;
import com.sleepywang.accentmap.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AccentMapper  extends BaseMapper<Accent> {
    @Select("Select * from accents where a_id!=0")
    List<Accent>getAllAccents();
    @Select("Select a.a_id,a.a_name,s.s_id from accents a,subdialects s where a.s_id=s.s_id and a.s_id=#{sId}")
    List<Accent>getAccentsBySId(@Param("sId") int sId);
    @Select("SELECT cd.cd_id,l.l_id,d.d_id,s.s_id,a.a_id,l.l_name,d.d_name,s.s_name,a.a_name FROM counties c JOIN county_dialect cd ON cd.c_id = c.c_id JOIN languages l ON cd.l_id = l.l_id " +
            "JOIN dialects d ON cd.d_id = d.d_id JOIN subdialects s ON cd.s_id = s.s_id LEFT JOIN accents a ON cd.a_id = a.a_id " +
            "WHERE c.adcode = #{adcode} ORDER BY cd.cd_id")
    List<FullAccentName> getAccentByAdcode(@Param("adcode")String adcode);
    @Insert("insert into county_dialect (c_id,l_id,d_id,s_id,a_id) select c_id,#{accentIdDTO.lId},#{accentIdDTO.dId},#{accentIdDTO.sId},#{accentIdDTO.aId}" +
            " from counties where adcode=#{adcode}")
    @Options(useGeneratedKeys = true,keyProperty = "accentIdDTO.cdId")
    void insertNewAccentForCounty(@Param("adcode") String adcode, @Param("accentIdDTO") AccentIdDTO accentIdDTO);
    @Delete("delete from county_dialect where cd_id=#{cdId}")
    void deleteCountyDialect(@Param("cdId")int cdId);
}
