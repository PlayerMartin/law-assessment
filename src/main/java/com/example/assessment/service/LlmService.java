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
public class LlmService {

    private final TaskRepository taskRepository;

    @Autowired
    public LlmService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
}
