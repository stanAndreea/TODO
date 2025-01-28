package ch.cern.todo.repository;

import ch.cern.todo.model.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskCategoryRepository extends JpaRepository<TaskCategory, Long> {

//    TaskCategory findByCategoryName(String name);

    Optional<TaskCategory> findByCategoryName(String categoryName);

}
