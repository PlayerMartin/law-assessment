package com.example.assessment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PARALLEL: This is equivalent to a C# Entity Framework class defined in your
 * DbContext.
 * 
 * - @Entity: Marks this class as a database table (Similar to how EF treats
 * DbSet<T> types).
 * - @Id: Primary Key (Similar to [Key] attribute in C#).
 * - @GeneratedValue: Auto-increment logic (Similar to Identity columns).
 * - @Data (Lombok): Generates Getters, Setters, toString, etc. (Reduces
 * boilerplat, makes it look like C# properties).
 */
@Entity
@Data // Lombok automatically generates getters and setters (like C# { get; set; })
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    private boolean completed;

    public Task(String description) {
        this.description = description;
        this.completed = false;
    }
}
