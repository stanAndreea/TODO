package ch.cern.todo.service;


import ch.cern.todo.exceptions.BadRequestException;
import ch.cern.todo.model.Client;
import ch.cern.todo.model.Task;
import ch.cern.todo.model.TaskCategory;
import ch.cern.todo.repository.ClientRepository;
import ch.cern.todo.repository.TaskCategoryRepository;
import ch.cern.todo.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ClientRepository clientRepository;
    private final TaskCategoryRepository taskCategoryRepository;

    public TaskService(TaskRepository taskRepository, ClientRepository clientRepository, TaskCategoryRepository taskCategoryRepository) {
        this.taskRepository = taskRepository;
        this.clientRepository = clientRepository;
        this.taskCategoryRepository = taskCategoryRepository;
    }

    public List<Task> searchTasks(String username, LocalDate localDate, String taskName) {
        Long clientId = clientRepository.findClientByUsername(username).orElseThrow(() -> new BadRequestException("Client doesn't exist", "Client doesn't exist in our database")).getId();

        return taskRepository.findTasksByClientIdAndDeadlineAndTaskName(clientId, localDate, taskName);
    }


    public Page<Task> searchTasksByUsernameAndCategory(String username, String categoryName, Pageable pageable) {
        Long clientId = clientRepository.findClientByUsername(username).orElseThrow(() -> new BadRequestException("Client doesn't exist", "Client doesn't exist in our database")).getId();
        Long categoryId = taskCategoryRepository.findByCategoryName(categoryName).orElseThrow(() -> new BadRequestException("Category doesn't exist", "Category doesn't exist in our database")).getId();

        return taskRepository.findTasksByUsernameAndCategory(clientId, categoryId, pageable);
    }


    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public void createTask(Task task, String category, String currentUser) {

        // Find the client by its ID
        Client client = clientRepository.findClientByUsername(currentUser)
                .orElseThrow(() -> new BadRequestException("Client not found", "The client with the given ID doesn't exist"));

//        // Find the task category by its ID
        TaskCategory taskCategory = taskCategoryRepository.findByCategoryName(category)
                .orElseThrow(() -> new BadRequestException("Task Category not found", "The task category with the given ID doesn't exist"));
        // Create a new Task object and set its properties
        task.setClient(client); // Set the client
        task.setTaskCategory(taskCategory);

        taskRepository.save(task);

    }
}
