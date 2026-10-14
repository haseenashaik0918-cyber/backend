package com.taskmanager.backend.task;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByUserId(String userId);

    Optional<Task> findByIdAndUserId(String id, String userId);

    void deleteByIdAndUserId(String id, String userId);
}
