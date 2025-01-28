package ch.cern.todo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryName;
    private String categoryDescription;

    @OneToMany(mappedBy = "taskCategory", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Task> tasks;

    public TaskCategory (String categoryName, String categoryDescription){
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    public TaskCategory (String categoryName){

    }
}
