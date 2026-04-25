package com.freelix.repository;

import com.freelix.entity.Message;
import com.freelix.entity.Project;
import com.freelix.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByProjectOrderBySentAtAsc(Project project);

    List<Message> findByProjectAndIdGreaterThanOrderBySentAtAsc(Project project, Long lastId);

    @Query("SELECT m FROM Message m WHERE m.project = :project AND " +
           "(m.sender = :user OR m.receiver = :user) ORDER BY m.sentAt ASC")
    List<Message> findByProjectAndUser(@Param("project") Project project, @Param("user") User user);

    long countByReceiverAndIsReadFalse(User receiver);

    List<Message> findByReceiverAndIsReadFalse(User receiver);

    // All messages where user is sender or receiver, ordered newest first
    @Query("SELECT m FROM Message m WHERE m.sender = :user OR m.receiver = :user ORDER BY m.sentAt DESC")
    List<Message> findAllByUser(@Param("user") User user);
}
