package ch.cern.todo.service;

import ch.cern.todo.model.TaskCategory;
import ch.cern.todo.repository.TaskCategoryRepository;
import org.springframework.stereotype.Service;

@Service
public class TaskCategoryService {

    private final TaskCategoryRepository taskCategoryRepository;

    public TaskCategoryService(TaskCategoryRepository taskCategoryRepository) {
        this.taskCategoryRepository = taskCategoryRepository;
    }

    public void createCategory(TaskCategory taskCategory) {
        taskCategoryRepository.save(taskCategory);
    }
}
