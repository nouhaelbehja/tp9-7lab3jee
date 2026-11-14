package com.example.studentmanagement;

import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.repository.StudentRepository;
import com.example.studentmanagement.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    @Mock private StudentRepository repository;
    @InjectMocks private StudentService service;

    @Test
    void savesNewStudent() {
        Student student = new Student();
        when(repository.save(student)).thenReturn(student);
        assertSame(student, service.save(student));
        verify(repository, never()).existsById(anyInt());
    }

    @Test
    void rejectsUpdateOfMissingStudent() {
        Student student = new Student();
        student.setId(42);
        when(repository.existsById(42)).thenReturn(false);
        var error = assertThrows(ResponseStatusException.class, () -> service.save(student));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
        verify(repository, never()).save(any());
    }

    @Test
    void updatesExistingStudent() {
        Student student = new Student();
        student.setId(42);
        when(repository.existsById(42)).thenReturn(true);
        when(repository.save(student)).thenReturn(student);
        assertSame(student, service.save(student));
    }

    @Test
    void deletesExistingStudent() {
        Student student = new Student();
        when(repository.findById(42)).thenReturn(Optional.of(student));
        assertTrue(service.delete(42));
        verify(repository).delete(student);
    }

    @Test
    void doesNotDeleteMissingStudent() {
        when(repository.findById(42)).thenReturn(Optional.empty());
        assertFalse(service.delete(42));
        verify(repository, never()).delete(any());
    }

    @Test
    void missingStudentReturns404() {
        when(repository.findById(42)).thenReturn(Optional.empty());
        var error = assertThrows(ResponseStatusException.class, () -> service.findById(42));
        assertEquals(HttpStatus.NOT_FOUND, error.getStatusCode());
    }
}
