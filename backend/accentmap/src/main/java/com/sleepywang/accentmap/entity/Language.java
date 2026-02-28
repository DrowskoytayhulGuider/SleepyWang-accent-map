package com.sleepywang.accentmap.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("languages")
public class Language {
    @TableId(type = IdType.AUTO)
    private int lId;
    private String lName;

}
