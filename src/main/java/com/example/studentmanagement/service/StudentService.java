package com.example.studentmanagement.service;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository repository;

    public StudentService(StudentRepository repository) { this.repository = repository; }

    @Transactional
    public Student save(Student student) {
        if (student.getId() != null && !repository.existsById(student.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Étudiant introuvable");
        }
        return repository.save(student);
    }

    @Transactional
    public boolean delete(int id) {
        return repository.findById(id).map(student -> {
            repository.delete(student);
            return true;
        }).orElse(false);
    }

    public Student findById(int id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Étudiant introuvable"));
    }

    public List<Student> findAll() { return repository.findAll(); }
    public long countStudents() { return repository.count(); }
    public List<Object[]> findNbrStudentByYear() { return repository.findNbrStudentByYear(); }
    public List<Student> findByFiliere(String filiere) {
        return repository.findByFiliereIgnoreCaseOrderByNomAsc(filiere);
    }
}
