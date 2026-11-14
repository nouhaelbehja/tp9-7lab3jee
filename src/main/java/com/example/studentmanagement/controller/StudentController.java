package com.example.studentmanagement.controller;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/students")
@Tag(name = "Étudiants", description = "Gestion des étudiants et statistiques")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) { this.service = service; }

    @Operation(summary = "Créer ou modifier un étudiant")
    @PostMapping("/save")
    public ResponseEntity<Student> save(@Valid @RequestBody Student student) {
        boolean creation = student.getId() == null;
        Student saved = service.save(student);
        return ResponseEntity.status(creation ? HttpStatus.CREATED : HttpStatus.OK).body(saved);
    }

    @Operation(summary = "Supprimer un étudiant")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Lister tous les étudiants")
    @GetMapping("/all")
    public ResponseEntity<List<Student>> findAll() { return ResponseEntity.ok(service.findAll()); }

    @Operation(summary = "Compter les étudiants")
    @GetMapping("/count")
    public ResponseEntity<Long> countStudent() { return ResponseEntity.ok(service.countStudents()); }

    @Operation(summary = "Compter les étudiants par année de naissance")
    @GetMapping("/byYear")
    public ResponseEntity<List<Object[]>> findByYear() {
        return ResponseEntity.ok(service.findNbrStudentByYear());
    }

    @Operation(summary = "Rechercher un étudiant par identifiant")
    @GetMapping("/{id}")
    public ResponseEntity<Student> findById(@PathVariable("id") int id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Filtrer les étudiants par filière")
    @GetMapping("/byFiliere")
    public ResponseEntity<List<Student>> findByFiliere(@RequestParam("filiere") String filiere) {
        return ResponseEntity.ok(service.findByFiliere(filiere));
    }
}
