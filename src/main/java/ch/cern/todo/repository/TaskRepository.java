package ch.cern.todo.repository;


import ch.cern.todo.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

//   @Query("SELECT t FROM Task t WHERE t.client_id = :client_id " +
//            "AND t.deadline = ':deadline' " +
//            "AND t.task_name LIKE '%:task_name%'")
//    List<Task> findTasksByClientIdAndDeadlineAndTaskName(@Param("client_id") Long clientId, @Param("deadline") LocalDate deadline,
//                                                      @Param("task_name") String taskName);



    @Query("SELECT t FROM Task t WHERE t.client.id = :client_id " +
            "AND t.deadline = :deadline " +
            "AND LOWER(t.taskName) LIKE LOWER(CONCAT('%', :task_name, '%'))")
    List<Task> findTasksByClientIdAndDeadlineAndTaskName(@Param("client_id") Long clientId,
                                                         @Param("deadline") LocalDate deadline,
                                                         @Param("task_name") String taskName);


    @Query("SELECT t FROM Task t WHERE t.client.id = :client_id " +
            "AND t.taskCategory.id = :taskCategory_id")
    Page<Task> findTasksByUsernameAndCategory(@Param("client_id") Long clientId,
                                              @Param("taskCategory_id") Long categoryId, Pageable pageable);
}
