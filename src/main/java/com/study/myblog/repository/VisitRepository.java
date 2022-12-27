package com.study.myblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.study.myblog.domain.Visit;
@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

}
