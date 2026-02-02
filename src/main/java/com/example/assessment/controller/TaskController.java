package com.example.assessment.controller;

import com.example.assessment.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * PARALLEL: This is a standard MVC Controller.
 * 
 * - @Controller: Indicates this class handles web requests and returns Views
 * (HTML).
 * Equivalent to inheriting from `Controller` in ASP.NET MVC.
 * 
 * Differences:
 * - In C#, actions return `IActionResult`. In Spring, they usually return
 * `String` (the view name).
 * - `Model model`: Similar to `ViewBag` or passing a model object to
 * `View(model)`.
 */
@Controller
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // @GetMapping("/") -> [HttpGet("/")]
    @GetMapping("/")
    public String index(Model model) {
        // Add data to the view - like View(model) or ViewBag
        model.addAttribute("tasks", taskService.getAllTasks());

        // Return "index" -> Looks for src/main/resources/templates/index.html
        return "index";
    }

    // @PostMapping("/add") -> [HttpPost("/add")]
    @PostMapping("/add")
    public String addTask(@RequestParam String description) {
        taskService.addTask(description);
        return "redirect:/"; // Redirect to execute the GetMapping("/") again
    }

    @PostMapping("/toggle/{id}")
    public String toggleTask(@PathVariable Long id) {
        taskService.toggleTask(id);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/";
    }
}
