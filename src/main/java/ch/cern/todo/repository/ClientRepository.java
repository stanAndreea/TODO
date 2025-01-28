package ch.cern.todo.repository;

import ch.cern.todo.model.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findClientByUsername(String username);

    boolean existsByUsername(String username);

    @EntityGraph(attributePaths = {"tasks", "roles"})
    Optional<Client> findWithTaskAndRoleByUsername(String username);

    void deleteByUsername(String username);

    @Query("SELECT c FROM Client c JOIN c.roles r WHERE r.id = :roleId")
    List<Client> findClientsByRoleId(int roleId);
}
