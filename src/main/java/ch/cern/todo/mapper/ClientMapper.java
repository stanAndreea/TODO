package ch.cern.todo.mapper;

import ch.cern.todo.model.Client;
import ch.cern.todo.model.Role;
import ch.cern.todo.model.dto.ClientResponseDTO;
import ch.cern.todo.model.dto.TaskResponseDTO;


import java.util.stream.Collectors;

public class ClientMapper {

    public static ClientResponseDTO toResponseDTO(Client client) {
        return ClientResponseDTO.builder()
                .id(client.getId())
                .username(client.getUsername())
                .roles(client.getRoles().stream()
                        .map(Role::getRoleName)
                        .collect(Collectors.toList()))
                .tasks(client.getTasks().stream()
                        .map(task -> TaskResponseDTO.builder()
                                .id(task.getId())
                                .name(task.getTaskName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
