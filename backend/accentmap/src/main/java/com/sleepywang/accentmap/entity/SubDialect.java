package com.sleepywang.accentmap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("subdialects")
public class SubDialect {
    @TableId(type = IdType.AUTO)
    private int sId;
    private String sName;
    private int dId;
    @TableField(exist = false)
    private Dialect dialect;
}
