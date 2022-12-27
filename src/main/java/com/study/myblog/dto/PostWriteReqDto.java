package com.study.myblog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import com.study.myblog.domain.Category;
import com.study.myblog.domain.Post;
import com.study.myblog.domain.User;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PostWriteReqDto {

    @NotBlank
    private Integer categoryId;

    @Size(min = 1, max = 60)
    @NotBlank
    private String title;

    private MultipartFile thumnailFile;

    @NotNull
    private String content;

    @Builder
    public PostWriteReqDto(@NotBlank Integer categoryId, @Size(min = 1, max = 60) @NotBlank String title,
            MultipartFile thumnailFile, @NotNull String content) {
        this.categoryId = categoryId;
        this.title = title;
        this.thumnailFile = thumnailFile;
        this.content = content;
    }

    public Post toEntity(String thumnail, User principal, Category category) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setThumnail(thumnail);
        post.setUser(principal);
        post.setCategory(category);
        return post;
    }
}
