package ch.cern.todo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    private String categoryName;
    private String categoryDescription;

    @OneToMany(mappedBy = "taskCategory", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Task> tasks;

    public TaskCategory(String categoryName, String categoryDescription) {
        this.categoryName = categoryName;
        this.categoryDescription = categoryDescription;
    }

    public TaskCategory(String categoryName) {

    }
}
