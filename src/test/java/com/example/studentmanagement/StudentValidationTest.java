package com.example.studentmanagement;

import com.example.studentmanagement.entity.Student;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class StudentValidationTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void close() { factory.close(); }

    private Student validStudent() {
        Student student = new Student();
        student.setNom("AMRANI");
        student.setPrenom("Salma");
        student.setDateNaissance(LocalDate.of(2003, 5, 17));
        student.setMatricule("ETU-001");
        student.setEmail("salma@example.com");
        student.setFiliere("Informatique");
        student.setNiveau(3);
        student.setTelephone("+212612345678");
        return student;
    }

    @Test
    void acceptsValidStudentAndOptionalPhone() {
        Student student = validStudent();
        assertTrue(validator.validate(student).isEmpty());
        student.setTelephone(null);
        assertTrue(validator.validate(student).isEmpty());
    }

    @Test
    void rejectsInvalidFields() {
        Student student = validStudent();
        student.setNom(" ");
        student.setEmail("incorrect");
        student.setNiveau(6);
        student.setDateNaissance(LocalDate.now().plusDays(1));
        student.setTelephone("abc");
        Set<String> fields = validator.validate(student).stream()
                .map(error -> error.getPropertyPath().toString()).collect(Collectors.toSet());
        assertEquals(Set.of("nom", "email", "niveau", "dateNaissance", "telephone"), fields);
    }
}
