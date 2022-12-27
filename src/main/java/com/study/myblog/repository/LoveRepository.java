package com.study.myblog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.study.myblog.domain.Love;

public interface LoveRepository extends JpaRepository<Love, Integer> {

    @Query(value = "SELECT * FROM love WHERE userId = :userId AND postId = :postId", nativeQuery = true)
    Optional<Love> mFindUesrIdandPostId(@Param("userId") Integer userId, @Param("postId") Integer postId);
}
