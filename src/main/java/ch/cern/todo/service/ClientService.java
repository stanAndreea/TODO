package ch.cern.todo.service;

import ch.cern.todo.exceptions.BadRequestException;
import ch.cern.todo.mapper.ClientMapper;
import ch.cern.todo.model.Client;
import ch.cern.todo.model.Role;
import ch.cern.todo.model.Task;
import ch.cern.todo.model.dto.ClientResponseDTO;
import ch.cern.todo.repository.ClientRepository;
import ch.cern.todo.repository.RoleRepository;
import ch.cern.todo.repository.TaskRepository;
import ch.cern.todo.util.HelperUtils;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final TaskRepository taskRepository;

    public ClientService(ClientRepository clientRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, TaskRepository taskRepository) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.taskRepository = taskRepository;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }


    public void createClient(Client client) {
        HelperUtils.validateNotEmpty(client.getPassword());
        HelperUtils.validateNotEmpty(client.getUsername());

        client.setPassword(passwordEncoder.encode(client.getPassword()));

        Role role = roleRepository.findByRoleName("USER").orElseThrow(() -> new BadRequestException("We have a little problem", "Please contact support for resolving the problem"));

        client.setRoles(Collections.singletonList(role));
        clientRepository.save(client);
    }

    public ClientResponseDTO getClientByUsername(String username, String currentClient) throws AccessDeniedException {
        HelperUtils.validateNotEmpty(username);

        // Check if the current user is admin or is the client being updated
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        if (!isAdmin && !username.equals(currentClient)) {
            throw new AccessDeniedException("You are not authorized to update this profile.");
        }
        Client client = clientRepository.findWithTaskAndRoleByUsername(username)
                .orElseThrow(() -> new BadRequestException("User doesn't exist", "Client doesn't exist"));

        return ClientMapper.toResponseDTO(client);

    }

    public void updateClient(String username, Client client) {
        HelperUtils.validateNotEmpty(username);
        Client existingClient = clientRepository.findClientByUsername(client.getUsername()).orElseThrow(() -> new BadRequestException("User doesn't exist", "Client doesn't exist"));
        existingClient.setPassword(client.getPassword());
        clientRepository.save(existingClient);
    }

    @Transactional
    public void deleteClient(String username) {
        HelperUtils.validateNotEmpty(username);

        Client client = clientRepository.findClientByUsername(username).orElseThrow(() -> new BadRequestException("User doesn't exist", "Client doesn't exist"));

        // Delete the tasks associated with the client (handled manually)
        Set<Task> tasks = client.getTasks();
        if (tasks != null && !tasks.isEmpty()) {
            // Optionally, you can log here that tasks are being deleted
            taskRepository.deleteAll(tasks); // Assuming taskRepository is injected and you have the delete method available
        }

        // Remove the roles from the client (handled manually in the many-to-many relation)
        List<Role> roles = client.getRoles();
        if (roles != null && !roles.isEmpty()) {
            client.getRoles().clear(); // This will break the many-to-many relationship
        }

        clientRepository.delete(client);
    }

    public List<ClientResponseDTO> getClientsByRoleId(String roleName){
        HelperUtils.validateNotEmpty(roleName);
        Role existingRole = roleRepository.findByRoleName(roleName).orElseThrow(() -> new BadRequestException("Role doesn't exist", "Role doesn't exist"));

        List<Client> clients = clientRepository.findClientsByRoleId(existingRole.getId());

        return clients.stream().map(ClientMapper::toResponseDTO).collect(Collectors.toList());

    }



}
