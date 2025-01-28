package ch.cern.todo.service;

import ch.cern.todo.exceptions.BadRequestException;
import ch.cern.todo.model.Client;
import ch.cern.todo.model.Role;
import ch.cern.todo.repository.RoleRepository;
import ch.cern.todo.util.HelperUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public void createRole(Role role) {
        HelperUtils.validateNotEmpty(role.getRoleName());
        HelperUtils.validateNotEmpty(role.getRoleDescription());

        roleRepository.save(role);
    }

    public Role updateRole(String roleName, Role role) {
        HelperUtils.validateNotEmpty(roleName);

        Role existingRole = roleRepository.findByRoleName(role.getRoleName()).orElseThrow(() -> new BadRequestException("Role doesn't exist", "Role doesn't exist"));
        existingRole.setRoleDescription(role.getRoleDescription());

        return roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(String roleName) {
        HelperUtils.validateNotEmpty(roleName);

        Role existingRole = roleRepository.findByRoleName(roleName).orElseThrow(() -> new BadRequestException("Role doesn't exist", "Role doesn't exist"));

        // Remove the association from all clients
        Set<Client> clients = existingRole.getClients();

        if (clients != null && !clients.isEmpty()) {
            // Remove the role from all associated clients
            for (Client client : clients) {
                client.getRoles().remove(existingRole); // Remove the role from the client's roles
            }
        }

        // Now, clear the clients set in the role (remove the many-to-many relation)
        existingRole.getClients().clear();

        roleRepository.delete(existingRole);
    }
}
