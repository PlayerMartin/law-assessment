# Spring Boot vs .NET Core / C# Assessment Template

This project is a simple Task Manager designed to showcase **Spring Boot** concepts for a **.NET developer**.
It implements a standard MVC architecture using Thymeleaf (servers-side rendering) and JPA (ORM).

## Quick Concepts Map

| Spring Boot Concept | .NET / C# Equivalent |
|---------------------|----------------------|
| `pom.xml` / `build.gradle` | `.csproj` (NuGet packages) |
| `@SpringBootApplication` `main()` | `Program.cs` / `Startup.cs` |
| `application.properties` | `appsettings.json` |
| `@Controller` | `Controller` (MVC) |
| `@Service` | Service Class (registered in DI) |
| `@Repository` (Interface) | `DbContext` / `DbSet` |
| `@Entity` | `[Table]` / POCO Class |
| `@Autowired` | Constructor Injection |
| Thymeleaf (`.html`) | Razor (`.cshtml`) |
| Gradle (`./gradlew`) | `dotnet` CLI |

## Project Structure

- **src/main/resources/application.properties**: Configuration (DB connection, Logging).
- **src/main/java/.../model/Task.java**: The database entity.
- **src/main/java/.../repository/TaskRepository.java**: The data access layer.
- **src/main/java/.../service/TaskService.java**: The business logic.
- **src/main/java/.../controller/TaskController.java**: The web controller.

## How to Run

1. Open terminal in this folder.
2. Run the application:
   - **Windows**: `.\gradlew.bat bootRun`
   - **Mac/Linux**: `./gradlew bootRun`
3. Access the app at: `http://localhost:8080`
4. Access Database Console: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:testdb`
   - User: `sa`
   - Password: `password`

## Key Differences to Note

- **Magic Repositories**: In Spring Data JPA, you just define an *Interface* extending `JpaRepository`. You don't implement it. Spring generates the code at runtime.
- **Annotations**: Spring relies heavily on annotations (`@`) for everything (Dependency Injection, Routing, DB Mapping).
- **Gradle**: This project uses Gradle. It creates a wrapper (`gradlew`) so you don't even need Gradle installed globally.
