package ch.cern.todo.controller;

import ch.cern.todo.exceptions.BadRequestException;
import ch.cern.todo.model.Task;
import ch.cern.todo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Management", description = "API for managing tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @Operation(summary = "Add a new task", description = "Creates a new task in the system")
    @PostMapping("/newTask")
    public ResponseEntity<String> addTask(@RequestBody @Valid Task task, @RequestParam String category, Principal principal) throws BadRequestException {
        String currentClient = principal.getName();
        taskService.createTask(task, category, currentClient);
        return ResponseEntity.status(HttpStatus.CREATED).body("Task created successfully");
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(
            @RequestParam String name,
            @RequestParam LocalDate deadline,
            Principal principal) {
        String currentClient = principal.getName();
        return ResponseEntity.status(HttpStatus.OK).body(taskService.searchTasks(currentClient, deadline, name));
    }

    @GetMapping("/search/by-category")
    public ResponseEntity<Page<Task>> searchTasks(
            @RequestParam String category,
            Principal principal,
            Pageable pageable
    ) {
        String currentClient = principal.getName();
        return ResponseEntity.status(HttpStatus.OK).body(taskService.searchTasksByUsernameAndCategory(currentClient, category, pageable));
    }

    //ADMIN ROLE
    @Operation(summary = "Get all tasks", description = "Retrieves a list of all tasks")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getAllTasks());
    }

    @GetMapping("/search/by-username")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Task>> searchTasksByUsername(
            @RequestParam String username,
            @RequestParam String name,
            @RequestParam LocalDate deadline) {
        return ResponseEntity.status(HttpStatus.OK).body(taskService.searchTasks(username, deadline, name));
    }

}
