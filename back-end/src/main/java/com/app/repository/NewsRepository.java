package com.app.repository;

import com.app.DTO.DTOComment;
import com.app.DTO.DTONews;
import com.app.entities.Comment;
import com.app.entities.News;
import com.app.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface NewsRepository extends CrudRepository<News, Long> {
    News findOneById(Long id);

    Page<News> findAllBy(Pageable p);

    @Query(
            value = "SELECT comment_date,fullname,img_profile,text FROM comment WHERE news_id = ?",
            nativeQuery = true
    )
    List<Comment> findAllCommentById(Long id);

    @Query(
            value = "SELECT users.id  FROM users WHERE users.job_title IN('DEV', 'PM') UNION SELECT profile_id " +
                    "FROM user_subscriptions WHERE user_id = ?;", nativeQuery = true)
    Set<Long> allSubscriiptionId(Long id);

    @Query("SELECT NEW com.app.DTO.DTONews(n.id, n.date, n.title, n.profileID, n.content, n.amountOfComent) FROM News n WHERE id IN :allSubscriiptionId")
    Page<DTONews> news(@Param("allSubscriiptionId") Set<Long> allSubscriiptionId, Pageable p);

    @Query("SELECT NEW com.app.DTO.DTOComment(c.id, c.imgProfile, c.comment_date, c.text) FROM Comment c WHERE news_id = :id")
    Page<DTOComment> comments(@Param("id") Long id, Pageable p);
}
