package ch.cern.todo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String taskName;
    private String taskDescription;
    private LocalDate deadline;


    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

    @ManyToOne
    @JoinColumn(name = "taskCategory_id", nullable = false)
    @JsonIgnore
    private TaskCategory taskCategory;

    public Task (String taskName, String taskDescription, LocalDate localDateTime){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.deadline = localDateTime;
    }

}
