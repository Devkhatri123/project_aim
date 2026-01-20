package com.newsApi.newsApi.repository;

import com.newsApi.newsApi.model.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SearchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NewsRepo extends JpaRepository<News,String> {
    @Query(value = "select headline from News where is_debunk = ?1 and pub_date <= DATE_SUB(now(), interval ?2 day)", nativeQuery = true)
    List<Object> getNewsHeadings( boolean is_debunk, Integer days);


    List<News> findByCategory(String category, Pageable pageable);

    @Query(value = "SELECT n FROM News n WHERE n.isDebunk = :debunk")
    Page<News> getNewsByDebunk(@Param("debunk") boolean debunk, Pageable pageable);
}
