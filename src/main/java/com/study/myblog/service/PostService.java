package com.study.myblog.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.study.myblog.domain.Category;
import com.study.myblog.domain.Love;
import com.study.myblog.domain.Post;
import com.study.myblog.domain.User;
import com.study.myblog.domain.Visit;
import com.study.myblog.dto.LoveRespDto;
import com.study.myblog.dto.LoveRespDto.PostDto;
import com.study.myblog.dto.PostDetailRespDto;
import com.study.myblog.dto.PostRespDto;
import com.study.myblog.dto.PostWriteReqDto;
import com.study.myblog.handler.ex.CustomApiException;
import com.study.myblog.handler.ex.CustomException;
import com.study.myblog.repository.CategoryRepository;
import com.study.myblog.repository.LoveRepository;
import com.study.myblog.repository.PostRepository;
import com.study.myblog.repository.VisitRepository;
import com.study.myblog.util.UtilFileUpload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final VisitRepository visitRepository;
    private final LoveRepository loveRepository;
    private final EntityManager em; // IoC 컨테이너에서 가져옴.

    @Value("${file.path}")
    private String uploadFolder;

    @Transactional
    public PostRespDto 게시글목록보기(Integer pageOwnerId, Pageable pageable) {

        Page<Post> postsEntity = postRepository.findByUserId(pageOwnerId, pageable);
        List<Category> categorysEntity = categoryRepository.findByUserId(pageOwnerId);

        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 0; i < postsEntity.getTotalPages(); i++) {
            pageNumbers.add(i);
        }

        Visit visitEntity = visitIncrease(pageOwnerId);

        PostRespDto postRespDto = new PostRespDto(postsEntity, categorysEntity, pageOwnerId,
                postsEntity.getNumber() - 1,
                postsEntity.getNumber() + 1, pageNumbers, visitEntity.getTotalCount());

        return postRespDto;
    }

    public PostRespDto 게시글카테고리별보기(Integer pageOwnerId, Integer categoryId,
            org.springframework.data.domain.Pageable pageable) {
        Page<Post> postsEntity = postRepository.findByUserIdAndCategoryId(pageOwnerId, categoryId, pageable);
        List<Category> categorysEntity = categoryRepository.findByUserId(pageOwnerId);
        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 0; i < postsEntity.getTotalPages(); i++) {
            pageNumbers.add(i);
        }

        Visit visitEntity = visitIncrease(pageOwnerId);

        PostRespDto postRespDto = new PostRespDto(postsEntity, categorysEntity, pageOwnerId,
                postsEntity.getNumber() - 1,
                postsEntity.getNumber() + 1, pageNumbers, visitEntity.getTotalCount());
        return postRespDto;

    }

    public List<Category> 게시글쓰기화면(User principal) {
        return categoryRepository.findByUserId(principal.getId());
    }

    public void 게시글쓰기(PostWriteReqDto postWriteReqDto, User principal) {

        // 1. UUID로 파일쓰고 경로 리턴 받기
        String thumnail = UtilFileUpload.write(uploadFolder, postWriteReqDto.getThumnailFile());

        // 2. 카테고리 있는지 확인
        Optional<Category> categoryOp = categoryRepository.findById(postWriteReqDto.getCategoryId());

        // 3. post DB 저장
        if (categoryOp.isPresent()) {
            Post post = postWriteReqDto.toEntity(thumnail, principal, categoryOp.get());
            postRepository.save(post);
        } else {
            throw new CustomException("해당 카테고리가 존재하지 않습니다.");
        }

    }

    // logout 상태일 때!!
    @Transactional
    public PostDetailRespDto 게시글상세보기(Integer id) {
        PostDetailRespDto postDetailRespDto = new PostDetailRespDto();

        // 게시글찾기
        Post postEntity = postFindById(id);

        // 방문자수 증가
        visitIncrease(postEntity.getUser().getId());

        // 리턴값
        postDetailRespDto.setPost(postEntity);
        postDetailRespDto.setPageOwner(false);

        // 좋아요 유무 추가하기
        postDetailRespDto.setLove(false);
        postDetailRespDto.setLoveId(0);

        return postDetailRespDto;

    }

    @Transactional(rollbackFor = CustomApiException.class)
    public void 게시글삭제(Integer id, User principal) {

        Post postEntity = postFindById(id);

        if (authCheck(postEntity.getUser().getId(), principal.getId())) {
            postRepository.deleteById(id);
        } else {
            throw new CustomApiException("삭제 권한이 없습니다");
        }
    }

    // login 상태일 때!!
    @Transactional
    public PostDetailRespDto 게시글상세보기(Integer id, User principal) {
        PostDetailRespDto postDetailRespDto = new PostDetailRespDto();

        // 게시글찾기
        Post postEntity = postFindById(id);

        // 권한체크
        boolean isAuth = authCheck(postEntity.getUser().getId(), principal.getId());

        // 방문자수 증가
        visitIncrease(postEntity.getUser().getId());

        // 리턴값
        postDetailRespDto.setPost(postEntity);
        postDetailRespDto.setPageOwner(isAuth);

        // 좋아요 유무 추가하기(로그인한 사람이 해당 게시글 좋아하는지)
        // 로그인한 사람의 userId와 상세보기한 postId로 Love테이블에서 select해서 row가 있으면true
        Optional<Love> loveOp = loveRepository.mFindUesrIdandPostId(principal.getId(), id);
        if (loveOp.isPresent()) {
            Love loveEntity = loveOp.get();
            postDetailRespDto.setLoveId(loveEntity.getId());
            postDetailRespDto.setLove(true);
        } else {
            postDetailRespDto.setLoveId(0);
            postDetailRespDto.setLove(false);
        }

        return postDetailRespDto;
    }

    @Transactional
    public LoveRespDto 좋아요(Integer postId, User principal) {

        // 숙제 Love를 Dto에 옮겨서 비영속화된 데이터를 응답하기
        Post postEntity = postFindById(postId);

        Love love = new Love();
        love.setUser(principal);
        love.setPost(postEntity);

        Love loveEntity = loveRepository.save(love);
        // 1. DTO 클래스 생성
        // 2. 모델메퍼 함수 호출!! 내가 만든 DTO = 모델메퍼메서드호출(loveEntity, 내가만든DTO.class)
        LoveRespDto loveRespDto = new LoveRespDto();
        loveRespDto.setLoveId(loveEntity.getId());
        PostDto postDto = loveRespDto.new PostDto();
        postDto.setPostId(postEntity.getId());
        postDto.setTitle(postEntity.getTitle());
        loveRespDto.setPost(postDto);

        return loveRespDto;
    }

    @Transactional
    public void 좋아요취소(Integer loveId, User principal) {
        // 권한체크
        loveFindById(loveId);
        loveRepository.deleteById(loveId);
    }

    // 좋아요 한건 찾기
    private Love loveFindById(Integer loveId) {
        Optional<Love> loveOp = loveRepository.findById(loveId);
        if (loveOp.isPresent()) {
            Love loveEntity = loveOp.get();
            return loveEntity;
        } else {
            throw new CustomApiException("해당 좋아요가 존재하지 않습니다");
        }
    }

    private Post postFindById(Integer postId) {
        Optional<Post> postOp = postRepository.findById(postId);
        if (postOp.isPresent()) {
            Post postEntity = postOp.get();
            return postEntity;
        } else {
            throw new CustomApiException("해당 게시글이 존재하지 않습니다");
        }
    }

    private boolean authCheck(Integer principalId, Integer pageOwnerId) {
        boolean isAuth = false;
        if (principalId == pageOwnerId) {
            isAuth = true;
        } else {
            isAuth = false;
        }
        return isAuth;
    }

    private Visit visitIncrease(Integer pageOwnerId) {
        Optional<Visit> visitOp = visitRepository.findById(pageOwnerId);
        if (visitOp.isPresent()) {
            Visit visitEntity = visitOp.get();
            Long totalCount = visitEntity.getTotalCount();
            visitEntity.setTotalCount(totalCount + 1);
            return visitEntity;
        } else {
            log.error("심각!!", "회원가입 시 visit이 안 만들어지는 심각한 오류가 생겼습니다");
            throw new CustomException("일시적 문제 생김, 관리자에게 문의바람");
        }
    }

    //////////////////////////////// 연습 /////////////////////////////////////
    // JPQL -> Java Persistence Query Langauge
    // 복잡한 쿼리(통계쿼리 같은 것), Dto로 받고 싶을 때!!
    public Post emTest1(int id) {
        em.getTransaction().begin(); // 트랜잭션 시작

        // 쿼리를 컴파일시점에 오류 발견을 위해 QueryDSL 사용
        String sql = null;
        if (id == 1) {
            sql = "SELECT * FROM post WHERE id = 1";
        } else {
            sql = "SELECT * FROM post WHERE id = 2";
        }

        TypedQuery<Post> query = em.createQuery(sql, Post.class);
        Post postEntity = query.getSingleResult();

        try {
            // insert()

            // update()
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
        }

        em.close(); // 트랜잭션 종료
        return postEntity;
    }

    // 영속화 비영속화
    public Love emTest2() {
        Love love = new Love();
        em.persist(love); // 영속화
        em.detach(love); // 비영속화
        em.merge(love); // 재 영속화
        em.remove(love); // 영속성 삭제
        return love; // MessageConverter
    }

}