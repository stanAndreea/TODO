package ch.cern.todo.util;

import ch.cern.todo.exceptions.BadRequestException;
import ch.cern.todo.model.Client;
import ch.cern.todo.model.Role;
import ch.cern.todo.model.Task;
import ch.cern.todo.model.TaskCategory;
import ch.cern.todo.repository.ClientRepository;
import ch.cern.todo.repository.RoleRepository;
import ch.cern.todo.repository.TaskCategoryRepository;
import ch.cern.todo.repository.TaskRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final ClientRepository clientRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final TaskRepository taskRepository;

    public DataLoader(RoleRepository roleRepository, ClientRepository clientRepository, TaskCategoryRepository taskCategoryRepository, TaskRepository taskRepository) {
        this.roleRepository = roleRepository;
        this.clientRepository = clientRepository;
        this.taskCategoryRepository = taskCategoryRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) throws Exception {
//        loadRoles();
//        loadTaskCategories();
//        loadUsers();
//        loadTasks();
    }

    // PRIVATE METHODS

    private void loadRoles() {
        List<Role> roles = Arrays.asList(
                new Role("ADMIN", "Admin has access to all the tasks from the system"),
                new Role("USER", "User has access only to his task that are create/exist in the system by himsetf")
        );

        for (Role role : roles) {
            roleRepository.save(role);
        }
    }

    private void loadTaskCategories() {
        List<TaskCategory> taskCategories = Arrays.asList(
                new TaskCategory("Administrative", "Tasks related to managing schedules, emails, documents, and routine office operations."),
                new TaskCategory("Creative", "Tasks involving design, writing, brainstorming, or content creation."),
                new TaskCategory("Analytical", "Tasks focused on analyzing data, conducting research, and providing insights."),
                new TaskCategory("Strategic", "Tasks focused on planning, goal setting, and creating long-term strategies."),
                new TaskCategory("Financial", "Tasks involving budgets, expenses, invoices, financial reports, and audits."),
                new TaskCategory("Personal", "Tasks aimed at improving skills, learning, and personal growth.")
        );

        for (TaskCategory taskCategory : taskCategories) {
            taskCategoryRepository.save(taskCategory);
        }
    }

    private void loadUsers() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        List<Client> clients = Arrays.asList(
                new Client("admin", passwordEncoder.encode("password123")),
                new Client("user_admin", passwordEncoder.encode("password123")),
                new Client("user123", passwordEncoder.encode("user123")),
                new Client("user456", passwordEncoder.encode("user456"))
        );

        for (Client client : clients) {
            client = clientRepository.save(client);
            if (client.getUsername().contains("admin"))
                client.setRoles(roleRepository.findAll());
            else
                client.setRoles(Collections.singletonList(roleRepository.findByRoleName("USER").orElse(new Role())));
            clientRepository.save(client);
        }

    }

    private void loadTasks() throws ParseException {
        List<Task> tasks = Arrays.asList(
                new Task("Administrative - Organize and maintain employee files", "Organize and maintain employee files, including personnel records, contracts, and performance reviews.", HelperUtils.formatData("2025-07-18")),
                new Task("Strategic thinking", "Develop and implement long-term strategic plans to achieve organizational goals and objectives.", HelperUtils.formatData("2025-11-23")),
                new Task("Data analysis", " Analyze market trends, competitor activities, and customer data to identify opportunities and risks.", HelperUtils.formatData("2025-04-16")),
                new Task("Graphic design", "Design and create presentations, infographics, and other visual aids for meetings and reports.", HelperUtils.formatData("2025-02-01")),
                new Task("Inventory management", " Manage office supplies and equipment, ensuring adequate inventory levels and timely maintenance.", HelperUtils.formatData("2025-12-18")),
                new Task("Budgeting", "Manage the company's budget, track expenses, and prepare financial reports.", HelperUtils.formatData("2025-05-20"))
        );

        for (Task task : tasks) {
            Client client = clientRepository.findClientByUsername(getUserByTaskName(task.getTaskName())).orElse(new Client());
            task.setClient(client);
            task.setTaskCategory(taskCategoryRepository.findByCategoryName(getTaskCategoryByTaskName(task.getTaskName())).orElse(new TaskCategory()));

            taskRepository.save(task);
        }
    }

    private String getTaskCategoryByTaskName(String taskName) {
        if (taskName.contains("Administrative"))
            return "Administrative";
        else if (taskName.contains("Strategic"))
            return "Strategic";
        else if (taskName.contains("Financial") || taskName.contains("Budgeting"))
            return "Financial";
        else if (taskName.contains("Analytical") || taskName.contains("analysis"))
            return "Analytical";
        return "Personal";
    }

    private String getUserByTaskName(String taskName) {
        if (taskName.contains("Administrative"))
            return "admin";
        else if (taskName.contains("Strategic"))
            return "user_admin";
        else if (taskName.contains("Financial") || taskName.contains("Budgeting"))
            return "user123";
        else if (taskName.contains("Analytical") || taskName.contains("analysis"))
            return "user456";

        return "user123";
    }
}
