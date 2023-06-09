package com.li.blog.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName UserVo
 * @Description TODO
 * @Author Nine
 * @Date 2023/4/12 17:01
 * @Version 1.0
 */
@Data
public class UserVo {
   private String imgurl;
   private String github;
   private String email;
   private String record;
   private String nickname;
   private String title;
   private String title2;
   private String name;
   private List<String> backList;
}
