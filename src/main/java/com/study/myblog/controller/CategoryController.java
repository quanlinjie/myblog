package com.study.myblog.controller;

//import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.study.myblog.domain.Category;
import com.study.myblog.domain.User;
import com.study.myblog.dto.CategoryWriteReqDto;
import com.study.myblog.dto.LoginUser;
import com.study.myblog.service.CategoryService;
import com.study.myblog.util.Script;
import com.study.myblog.util.UtilValid;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class CategoryController {

    private final CategoryService categoryService;
    //private final HttpSession session;

    @GetMapping("/s/category/write-form")
    public String writeForm() {
        return "/category/writeForm";
    }

    @PostMapping("/s/category")
    public @ResponseBody String write(
            @Valid CategoryWriteReqDto categoryWriteReqDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal LoginUser loginUser) {

        UtilValid.요청에러처리(bindingResult);

        User principal = loginUser.getUser();
        Category category = categoryWriteReqDto.toEntity(principal);
        categoryService.카테고리등록(category);
        return Script.href("/s/category/write-form", "카테고리등록완료");
    }
}
