package ch.cern.todo;

import ch.cern.todo.model.Client;
import ch.cern.todo.model.Role;
import ch.cern.todo.repository.ClientRepository;
import ch.cern.todo.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CustomUserDetailsServiceTest {
    @Mock
    private ClientRepository clientRepository;  // Mock the client repository

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService; // The service being tested

    private Client mockClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initializes mocks
        mockClient = new Client();
        mockClient.setUsername("testuser");
        mockClient.setPassword("password123");

        // Mock roles if needed (example)
        Role role = new Role();
        role.setRoleName("USER");
        mockClient.setRoles(List.of(role));
    }

    @Test
    public void testLoadUserByUsername_Success() {
        // Given: clientRepository returns a valid client
        when(clientRepository.findClientByUsername("testuser")).thenReturn(Optional.of(mockClient));

        // When: calling the service method to load user by username
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

        // Then: verify that the UserDetails is returned correctly
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Given: clientRepository returns empty (no client found)
        when(clientRepository.findClientByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // When: calling the service method to load user by username
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistentuser");
        });

        // Then: ensure the exception is thrown with the expected message
        assertEquals("User not found: nonexistentuser", thrown.getMessage());
    }
}
