package com.sleepywang.accentmap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("accents")
public class Accent {
    @TableId(type = IdType.AUTO)
    private int aId;
    private String aName;
    private int sId;
    @TableField(exist = false)
    private SubDialect subDialect;
}
