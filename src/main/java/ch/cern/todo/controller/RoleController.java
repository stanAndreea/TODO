package ch.cern.todo.controller;


import ch.cern.todo.model.Role;
import ch.cern.todo.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Role Management", description = "API for managing roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Get all roles", description = "Retrieves a list of all roles")
    @GetMapping
    public ResponseEntity<List<Role>> getAllClients() {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.getAllRoles());
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<Role>> getAllUserByRoleName(@RequestParam String roleName){
//
//
//    }

    @Operation(summary = "Add a new role", description = "Creates a new role in the system")
    @PostMapping
    public ResponseEntity<String> addNewRole(@RequestBody Role role) {
        roleService.createRole(role);
        return ResponseEntity.status(HttpStatus.CREATED).body("Role created successfully");
    }

    @Operation(summary = "Update a role", description = "Updates an existing role's information")
    @PutMapping("/{roleName}")
    public ResponseEntity<Role> updateRole(@PathVariable String roleName, @RequestBody Role role) {
        return ResponseEntity.status(HttpStatus.OK).body(roleService.updateRole(roleName, role));
    }

    @Operation(summary = "Delete a role", description = "Deletes a role by their username")
    @DeleteMapping("/{roleName}")
    public ResponseEntity<String> deleteRole(@PathVariable String roleName) {
        roleService.deleteRole(roleName);
        return ResponseEntity.status(HttpStatus.OK).body("Role deleted successfully");
    }

}
