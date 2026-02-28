package com.sleepywang.accentmap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("counties")
public class County {
    @TableId(type = IdType.AUTO)
    private int cId;
    private String cName;
    private String adcode;
    private String polygon;

}
