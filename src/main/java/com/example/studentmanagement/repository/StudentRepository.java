package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

// Les écritures passent par le contrôleur et ses validations.
@RepositoryRestResource(exported = false)
public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Query("SELECT YEAR(s.dateNaissance), COUNT(s) FROM Student s GROUP BY YEAR(s.dateNaissance) ORDER BY YEAR(s.dateNaissance)")
    List<Object[]> findNbrStudentByYear();

    List<Student> findByFiliereIgnoreCaseOrderByNomAsc(String filiere);
}
