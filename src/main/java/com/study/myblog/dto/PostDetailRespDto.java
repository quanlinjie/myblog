package com.study.myblog.dto;

import com.study.myblog.domain.Post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostDetailRespDto {
    private Post post;
    private boolean isPageOwner;
    private boolean isLove;
    private Integer loveId;
}
