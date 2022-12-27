package com.study.myblog.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.study.myblog.domain.Category;
import com.study.myblog.domain.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryWriteReqDto {

    @Size(min = 1, max = 60)
    @NotBlank
    private String title;

    public Category toEntity(User principal) {
        Category category = new Category();
        category.setTitle(title);
        category.setUser(principal);
        return category;
    }
}
