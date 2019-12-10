package com.app.repository;

import com.app.DTO.DTONews;
import com.app.entities.Comment;
import com.app.entities.News;
import com.app.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends CrudRepository<News, Long> {
    News findOneById(Long id);

    Page<News> findAllBy(Pageable p);

    @Query(
        value = "SELECT id,comment_date,fullname,img_profile,text FROM comment WHERE news_id = ?",
                nativeQuery = true
    )
    List<Comment> findAllCommentById(Long id);
}
