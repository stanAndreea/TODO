package ch.cern.todo.service;

import ch.cern.todo.model.Client;
import ch.cern.todo.model.Task;
import ch.cern.todo.model.TaskCategory;
import ch.cern.todo.repository.ClientRepository;
import ch.cern.todo.repository.TaskCategoryRepository;
import ch.cern.todo.repository.TaskRepository;
import ch.cern.todo.exceptions.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TaskCategoryRepository taskCategoryRepository;

    @InjectMocks
    private TaskService taskService;

    private Client mockClient;
    private TaskCategory mockTaskCategory;
    private Task mockTask;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mock client
        mockClient = new Client();
        mockClient.setUsername("testuser");
        mockClient.setId(1L);

        // Set up mock task category
        mockTaskCategory = new TaskCategory();
        mockTaskCategory.setCategoryName("Work");

        // Set up mock task
        mockTask = new Task();
        mockTask.setTaskName("Test Task");
        mockTask.setTaskCategory(mockTaskCategory);
    }

    @Test
    public void testSearchTasks() {
        // Given
        String username = "testuser";
        LocalDate deadline = LocalDate.now();
        String taskName = "Test Task";

        when(clientRepository.findClientByUsername(username)).thenReturn(Optional.of(mockClient));
        when(taskRepository.findTasksByClientIdAndDeadlineAndTaskName(mockClient.getId(), deadline, taskName))
                .thenReturn(List.of(mockTask));

        // When
        List<Task> tasks = taskService.searchTasks(username, deadline, taskName);

        // Then
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTaskName());
    }

    @Test
    public void testSearchTasks_ClientNotFound() {
        // Given
        String username = "nonexistentuser";
        LocalDate deadline = LocalDate.now();
        String taskName = "Test Task";

        when(clientRepository.findClientByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            taskService.searchTasks(username, deadline, taskName);
        });
        assertEquals("Client doesn't exist in our database", exception.getMessage());
    }

    @Test
    public void testSearchTasksByUsernameAndCategory() {
        // Given
        String username = "testuser";
        String categoryName = "Work";
        PageRequest pageable = PageRequest.of(0, 10);

        when(clientRepository.findClientByUsername(username)).thenReturn(Optional.of(mockClient));
        when(taskCategoryRepository.findByCategoryName(categoryName)).thenReturn(Optional.of(mockTaskCategory));
        when(taskRepository.findTasksByUsernameAndCategory(mockClient.getId(), mockTaskCategory.getId(), pageable))
                .thenReturn(new PageImpl<>(List.of(mockTask)));

        // When
        Page<Task> tasksPage = taskService.searchTasksByUsernameAndCategory(username, categoryName, pageable);

        // Then
        assertNotNull(tasksPage);
        assertEquals(1, tasksPage.getTotalElements());
        assertEquals("Test Task", tasksPage.getContent().get(0).getTaskName());
    }

    @Test
    public void testSearchTasksByUsernameAndCategory_CategoryNotFound() {
        // Given
        String username = "testuser";
        String categoryName = "NonexistentCategory";
        PageRequest pageable = PageRequest.of(0, 10);

        when(clientRepository.findClientByUsername(username)).thenReturn(Optional.of(mockClient));
        when(taskCategoryRepository.findByCategoryName(categoryName)).thenReturn(Optional.empty());

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            taskService.searchTasksByUsernameAndCategory(username, categoryName, pageable);
        });
        assertEquals("Category doesn't exist in our database", exception.getMessage());
    }

    @Test
    public void testCreateTask() {
        // Given
        String category = "Work";
        String currentUser = "testuser";

        when(clientRepository.findClientByUsername(currentUser)).thenReturn(Optional.of(mockClient));
        when(taskCategoryRepository.findByCategoryName(category)).thenReturn(Optional.of(mockTaskCategory));

        // When
        taskService.createTask(mockTask, category, currentUser);

        // Then
        verify(taskRepository, times(1)).save(mockTask);
        assertEquals(mockClient, mockTask.getClient());
        assertEquals(mockTaskCategory, mockTask.getTaskCategory());
    }

    @Test
    public void testCreateTask_ClientNotFound() {
        // Given
        String category = "Work";
        String currentUser = "nonexistentuser";

        when(clientRepository.findClientByUsername(currentUser)).thenReturn(Optional.empty());

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            taskService.createTask(mockTask, category, currentUser);
        });
        assertEquals("The client with the given ID doesn't exist", exception.getMessage());
    }

    @Test
    public void testCreateTask_CategoryNotFound() {
        // Given
        String category = "NonexistentCategory";
        String currentUser = "testuser";

        when(clientRepository.findClientByUsername(currentUser)).thenReturn(Optional.of(mockClient));
        when(taskCategoryRepository.findByCategoryName(category)).thenReturn(Optional.empty());

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            taskService.createTask(mockTask, category, currentUser);
        });
        assertEquals("The task category with the given ID doesn't exist", exception.getMessage());
    }
}
