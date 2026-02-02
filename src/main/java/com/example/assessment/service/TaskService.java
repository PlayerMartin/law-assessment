package com.example.assessment.service;

import com.example.assessment.model.Task;
import com.example.assessment.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PARALLEL: This is your Business Logic Layer.
 * 
 * - @Service: Registers this class in the Dependency Injection container (IOC).
 * Equivalent to `services.AddScoped<TaskService>();` in C# Startup.cs /
 * Program.cs.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    // Constructor Injection (Preferred way in both Java and C#)
    // Spring automatically injects the TaskRepository implementation here.
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public void addTask(String description) {
        Task task = new Task(description);
        taskRepository.save(task);
    }

    public void toggleTask(Long id) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setCompleted(!task.isCompleted());
            taskRepository.save(task);
        });
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}
