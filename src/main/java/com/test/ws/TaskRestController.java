package com.test.ws;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.test.dao.TaskRepository;
import com.test.entities.Task;

@RestController
public class TaskRestController {

	@Autowired
	private TaskRepository taskRepository;
		
	@GetMapping("/api/tasks")
	public List<Task> listTasks(){
		return taskRepository.findAll();
	}
	
	@PostMapping("/api/tasks")
	public Task save (@RequestBody Task t) {
		return taskRepository.save(t);
		
	}
}
