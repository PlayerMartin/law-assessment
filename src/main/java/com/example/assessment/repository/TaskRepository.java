package com.example.assessment.repository;

import com.example.assessment.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PARALLEL: This is similar to a DbSet<Task> in your DbContext, but with
 * built-in methods.
 * 
 * - JpaRepository<Task, Long>: Provides basic CRUD (save, findAll, findById,
 * delete) for free.
 * - This interface is automatically implemented by Spring at runtime (Magic!).
 * 
 * In C#, you might implement `IRepository<Task>` manually or just use
 * `_context.Tasks`.
 * Spring Data JPA creates the implementation for you.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Custom query method - Spring parses the method name to generate the SQL!
    // Equivalent to: _context.Tasks.Where(t => t.Completed == completed).ToList();
    List<Task> findByCompleted(boolean completed);
}
