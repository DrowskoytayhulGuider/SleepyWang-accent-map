package com.sleepywang.accentmap.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sleepywang.accentmap.entity.Marker;
import lombok.Generated;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MarkerMapper extends BaseMapper<Marker> {
    List<Marker> getAllMarkers();
    @Insert("Insert into markers(lng,lat,remark,l_id,d_id,s_id,a_id) values(#{lng},#{lat},#{remark},#{lId}," +
            "#{dId},#{sId},#{aId})")
    @Options(useGeneratedKeys = true,keyProperty = "mId")
    void insertMark(Marker marker);
    List<Marker> getMarkersByAccent(@Param("language") String language,@Param("dialect")String dialect,
                                    @Param("subdialect")String subdialect,@Param("accent")String accent);
    @Update("UPDATE markers m " +
            "JOIN languages l ON l.l_name = #{lName} " +
            "JOIN dialects d ON d.d_name = #{dName} " +
            "JOIN subdialects s ON s.s_name = #{sName} " +
            "JOIN accents a ON a.a_name = #{aName} " +
            "SET " +
            "  m.remark = #{remark}," +
            "  m.l_id = l.l_id," +
            "  m.d_id = d.d_id," +
            "  m.s_id = s.s_id," +
            "  m.a_id = a.a_id " +
            "WHERE m.m_id = #{mId}")
    void updateMarker(Marker marker);
}
