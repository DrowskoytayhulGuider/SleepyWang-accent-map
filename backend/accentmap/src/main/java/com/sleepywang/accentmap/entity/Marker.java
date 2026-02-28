package com.sleepywang.accentmap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("markers")
public class Marker {
    @TableId(type = IdType.AUTO)
    private int mId;
    private double lng,lat;
    private String remark;
    private int lId,dId,sId,aId;
    private String lName;
    private String dName;
    private String sName;
    private String aName;
}
