package com.study.myblog.service;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.study.myblog.domain.User;
import com.study.myblog.domain.Visit;
import com.study.myblog.dto.PasswordResetReqDto;
import com.study.myblog.dto.UpdateDto;
import com.study.myblog.handler.ex.CustomApiException;
import com.study.myblog.handler.ex.CustomException;
import com.study.myblog.repository.UserRepository;
import com.study.myblog.repository.VisitRepository;
import com.study.myblog.util.EmailUtil;
import com.study.myblog.util.UtilFileUpload;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailUtil emailUtil;
    private final VisitRepository visitRepository;

    @Value("${file.path}")
    private String uploadFolder;

    @Transactional
    public User 회원가입(User user) {
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);

        User userEntity = userRepository.save(user);

        Visit visit = new Visit();
        visit.setTotalCount(0L);
        visit.setUser(userEntity);
        visitRepository.save(visit);

        return userEntity;
    }

    public boolean 유저네임중복체크(String username) {
        Optional<User> userOp = userRepository.findByUsername(username);

        if (userOp.isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    @Transactional
    public void 패스워드초기화(PasswordResetReqDto passwordResetReqDto) {
        Optional<User> userOp = userRepository.findByUsernameAndEmail(
                passwordResetReqDto.getUsername(),
                passwordResetReqDto.getEmail());

        if (userOp.isPresent()) {
            User userEntity = userOp.get();
            String encPassword = bCryptPasswordEncoder.encode("9999");
            userEntity.setPassword(encPassword);
        } else {
            throw new CustomException("해당 이메일이 존재하지 않습니다");
        }
        emailUtil.sendEmail("won5354@gmail.com", "비밀번호초기화", "초기화된 비밀번호: 9999");
    }

    @Transactional
    public void 프로파일이미지변경(User principal, MultipartFile profileImgFile, HttpSession session) {
        String profileImg = UtilFileUpload.write(uploadFolder, profileImgFile);

        Optional<User> userOp = userRepository.findById(principal.getId());
        if (userOp.isPresent()) {
            User userEntity = userOp.get();
            userEntity.setProfileImg(profileImg);
            session.setAttribute("principal", userEntity);
        } else {
            throw new CustomApiException("해당 유저를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public User 회원정보수정(Integer id, UpdateDto updateDto) {
        Optional<User> userOp = userRepository.findById(id);
        if (userOp.isPresent()) {
            User userEntity = userOp.get();
            userEntity.setEmail(updateDto.getEmail());
            return userEntity;
        } else {
            throw new RuntimeException("아이디를 찾을 수 없습니다");
        }
    }
}
