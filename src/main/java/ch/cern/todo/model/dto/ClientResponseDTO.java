package ch.cern.todo.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@Builder

public class ClientResponseDTO {

    private Long id;
    private String username;
    private List<String> roles; // Assuming roles are represented as strings
    private List<TaskResponseDTO> tasks;
}
