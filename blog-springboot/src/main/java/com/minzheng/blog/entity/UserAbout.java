package com.minzheng.blog.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_user_about")
public class UserAbout {
    /**
     * id
     */
    private Integer id;

    /**
     * 内容
     */
    private String aboutContent;

    private String userAbout;

}