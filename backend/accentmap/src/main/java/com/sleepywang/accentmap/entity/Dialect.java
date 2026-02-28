package com.sleepywang.accentmap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@TableName("dialects")
public class Dialect {
    @TableId(type = IdType.AUTO)
    private int dId;
    private String dName;
    private int lId;
    @TableField(exist = false)
    private Language language;
}
