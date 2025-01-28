package ch.cern.todo.controller;


import ch.cern.todo.model.Client;
import ch.cern.todo.model.dto.ClientResponseDTO;
import ch.cern.todo.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientController.class)
@RequestMapping("/api/clients")
public class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private Client mockClient;
    private ClientResponseDTO mockClientDTO;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetClientsByRole_withAdminRole_shouldReturnClientList() throws Exception {
        List<ClientResponseDTO> mockClients = List.of(
                ClientResponseDTO.builder()
                        .id(1L)
                        .username("JohnDoe")
                        .roles(List.of("USER"))
                        .tasks(Collections.emptyList())
                        .build(),
                ClientResponseDTO.builder()
                        .id(2L)
                        .username("JaneDoe")
                        .roles(List.of("ADMIN"))
                        .tasks(Collections.emptyList())
                        .build()
        );
        when(clientService.getClientsByRoleId("ADMIN")).thenReturn(mockClients);

        mockMvc.perform(get("/api/clients/by-role/ADMIN")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].username").value("JohnDoe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].username").value("JaneDoe"));
    }


    @Test
    @WithMockUser(roles = "ADMIN") // Simulate an authenticated ADMIN user
    public void testGetAllClients_shouldReturnClientList() throws Exception {

        Client mockClient1 = new Client(1L, "JohnDoe", "password", null, null);
        Client mockClient2 = new Client(2L, "JaneDoe", "password", null, null);

        // Given: mock data for clients
        List<Client> mockClients = Arrays.asList(mockClient1, mockClient2);

        // Mock the service call
        when(clientService.getAllClients()).thenReturn(mockClients);

        // Perform GET request to the /api/clients endpoint
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk()) // Expect HTTP status 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // Verify the length of the list
                .andExpect(jsonPath("$[0].username").value("JohnDoe")) // Verify the username of the first client
                .andExpect(jsonPath("$[1].username").value("JaneDoe")); // Verify the username of the second client
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    public void testGetClientInformation_shouldReturnClientDetails() throws Exception {
        // Mock the response from the clientService
        ClientResponseDTO mockClientResponse = ClientResponseDTO.builder()
                .id(1L)
                .username("testuser")
                .roles(Collections.singletonList("USER"))
                .tasks(Collections.emptyList())
                .build();

        when(clientService.getClientByUsername("testuser", "testuser")).thenReturn(mockClientResponse);

        // Perform the GET request and validate the response
        mockMvc.perform(get("/api/clients/search/{username}", "testuser"))
                .andExpect(status().isOk()) // HTTP 200 OK
                .andExpect(jsonPath("$.username").value("testuser")) // Check the username
                .andExpect(jsonPath("$.id").value(1L)) // Check the ID
                .andExpect(jsonPath("$.roles[0]").value("USER")) // Check the role
                .andExpect(jsonPath("$.tasks.length()").value(0)); // Check tasks are empty
    }

}
