package com.study.myblog.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.study.myblog.domain.Category;
import com.study.myblog.domain.Post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostRespDto {
    private Page<Post> posts;
    private List<Category> categorys;
    private Integer userId;
    private Integer prev;
    private Integer next;
    private List<Integer> pageNumbers;
    private Long totalCount;
}
