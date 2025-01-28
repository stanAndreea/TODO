package ch.cern.todo.model.dto;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class TaskResponseDTO {

    private Long id;
    private String name;


}
