package com.test.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.test.entities.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{

}
