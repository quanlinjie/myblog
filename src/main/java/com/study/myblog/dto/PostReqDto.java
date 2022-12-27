package com.study.myblog.dto;

import java.util.List;

import com.study.myblog.domain.Category;
import com.study.myblog.domain.Post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostReqDto {

    private List<Post> posts;
    private List<Category> categorys;
}
