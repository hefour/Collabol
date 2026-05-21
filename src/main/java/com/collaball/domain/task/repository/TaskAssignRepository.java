package com.collaball.domain.task.repository;

import com.collaball.domain.task.entity.TaskAssign;
import com.collaball.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskAssignRepository extends JpaRepository<TaskAssign, Long> {

    @Query("select ta.user from TaskAssign ta where ta.task.id = :taskId")
    List<User> findUsersByTaskId(@Param("taskId") Long taskId);

    @Modifying
    @Query("delete from TaskAssign ta where ta.task.id = :taskId")
    void deleteByTaskId(@Param("taskId") Long taskId);

    @Modifying
    @Query("delete from TaskAssign ta where ta.task.id in :taskIds")
    void deleteByTaskIdIn(@Param("taskIds") List<Long> taskIds);
}
