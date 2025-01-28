package ch.cern.todo.controller;


import ch.cern.todo.exceptions.BadRequestException;
import ch.cern.todo.model.Task;
import ch.cern.todo.model.TaskCategory;
import ch.cern.todo.service.TaskCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Task Category Management", description = "API for managing task categories")
public class TaskCategoryController {

    private final TaskCategoryService taskCategoryService;

    public TaskCategoryController(TaskCategoryService taskCategoryService) {
        this.taskCategoryService = taskCategoryService;
    }

    @Operation(summary = "Add a new category", description = "Creates a new category in the system")
    @PostMapping("/newCategory")
    public ResponseEntity<String> addCategory(@RequestBody @Valid TaskCategory taskCategory) throws BadRequestException {
        taskCategoryService.createCategory(taskCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body("Task created successfully");
    }
}
