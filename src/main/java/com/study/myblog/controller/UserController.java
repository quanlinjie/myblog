package com.study.myblog.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.multipart.MultipartFile;

import com.study.myblog.domain.User;
import com.study.myblog.dto.JoinReqDto;
import com.study.myblog.dto.LoginUser;
import com.study.myblog.dto.PasswordResetReqDto;
import com.study.myblog.dto.UpdateDto;
import com.study.myblog.handler.ex.CustomException;
import com.study.myblog.service.UserService;
import com.study.myblog.util.UtilValid;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;
    private final HttpSession session;
    //private static final String TAG = "UserController: ";

    @GetMapping("/login-form")
    public String loginForm() {
        return "/user/loginForm";
    }

    @GetMapping("/join-form")
    public String joinForm() {
        return "/user/joinForm";
    }

    @PostMapping("/join")
    public String join(@Valid JoinReqDto joinReqDto, BindingResult bindingResult) {

        userService.회원가입(joinReqDto.toEntity());
        UtilValid.요청에러처리(bindingResult);
        return "redirect:/login-form";
    }

    @GetMapping("/api/user/username-same-check")
    public ResponseEntity<?> usernameSameCheck(String username) {
        boolean isNotSame = userService.유저네임중복체크(username);
        return new ResponseEntity<>(isNotSame, HttpStatus.OK);
    }

    @GetMapping("/user/password-reset-form")
    public String passwordResetForm() {
        return "/user/passwordResetForm";
    }

    @PostMapping("/user/password-reset")
    public String passwordReset(@Valid PasswordResetReqDto passwordResetReqDto, BindingResult bindingResult) {
        UtilValid.요청에러처리(bindingResult);
        userService.패스워드초기화(passwordResetReqDto);

        return "redirect:/login-form";
    }

    @GetMapping("/s/user/{id}")
    public String updateForm(@PathVariable Integer id) {
        return "/user/updateForm";
    }

    @PutMapping("/s/api/user/{id}/profile-img")
    public ResponseEntity<?> profileImgUpdate(@AuthenticationPrincipal LoginUser loginUser,
            MultipartFile profileImgFile) {
        userService.프로파일이미지변경(loginUser.getUser(), profileImgFile, session);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/s/api/user/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, UpdateDto updateDto) {
        User principal = (User) session.getAttribute("principal");
        if (principal.getId() != id) {
            throw new CustomException("권한이 없습니다!");
        }
        User userEntity = userService.회원정보수정(id, updateDto);
        session.setAttribute("principal", userEntity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
