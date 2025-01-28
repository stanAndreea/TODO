package ch.cern.todo.controller;

import ch.cern.todo.exceptions.BadRequestException;
import ch.cern.todo.model.Client;
import ch.cern.todo.model.dto.ClientResponseDTO;
import ch.cern.todo.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Client Management", description = "API for managing clients")
public class ClientController {

    private final ClientService clientService;


    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Add a new client", description = "Creates a new client in the system")
    @PostMapping("/newClient")
    public ResponseEntity<String> addClient(@RequestBody @Valid Client client) throws BadRequestException {
        clientService.createClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body("Client created successfully");
    }

    @Operation(summary = "Get a client by username", description = "Retrieves client details by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("search/{username}")
    public ResponseEntity<ClientResponseDTO> getClientInformation(@PathVariable String username, Principal principal) throws AccessDeniedException {

        String currentClient = principal.getName();
        return ResponseEntity.status(HttpStatus.OK).body(clientService.getClientByUsername(username, currentClient));
    }

    @Operation(summary = "Update a client", description = "Updates an existing client's information")
    @PutMapping("/{username}")
    public ResponseEntity<String> updateClient(@PathVariable String username, @RequestBody Client client) {
        clientService.updateClient(username, client);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @Operation(summary = "Delete a client", description = "Deletes a client by their username")
    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteClient(@PathVariable String username) {
        clientService.deleteClient(username);
        return ResponseEntity.status(HttpStatus.OK).body("Client deleted successfully");
    }

    //ADMIN ROLE
    @Operation(summary = "Get all clients", description = "Retrieves a list of all clients")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.status(HttpStatus.OK).body(clientService.getAllClients());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-role/{roleName}")
    public ResponseEntity<List<ClientResponseDTO>> getClientsByRole(@PathVariable String roleName) {
        List<ClientResponseDTO> clients = clientService.getClientsByRoleId(roleName);
        return ResponseEntity.status(HttpStatus.OK).body(clients);
    }

}
