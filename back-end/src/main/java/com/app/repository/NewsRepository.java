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
            value = "SELECT id FROM users WHERE job_title IN('DEV', 'PM') UNION SELECT profile_id " +
                    "FROM user_subscriptions WHERE user_id = ?", nativeQuery = true)
    Set<Long> allSubscriptionId(Long id);

    @Query("SELECT NEW com.app.DTO.DTONews(n.id, n.date, n.title, n.profileID, n.content) " +
            "FROM News n WHERE n.profileID IN :allSubscriptionId")
    Page<DTONews> news(@Param("allSubscriptionId") Set<Long> allSubscriptionId, Pageable p);

    @Query("SELECT NEW com.app.DTO.DTOComment(p.id, c.imgProfile, c.commentDate, c.text, c.fullName) " +
            "FROM Comment c JOIN Profile p ON c.news.profileID = p.id where c.news.id =:id" )
    Page<DTOComment> comments(@Param("id") Long id, Pageable p);


}
