package com.example.studentmanagement;

import com.example.studentmanagement.controller.StudentController;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {
    @Mock private StudentService service;
    @InjectMocks private StudentController controller;

    @Test
    void creationReturns201AndGeneratedId() {
        Student input = new Student();
        Student saved = new Student();
        saved.setId(7);
        saved.setMatricule("ETU-007");
        when(service.save(input)).thenReturn(saved);
        var response = controller.save(input);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(7, response.getBody().getId());
        assertEquals("ETU-007", response.getBody().getMatricule());
    }

    @Test
    void updateReturns200() {
        Student student = new Student();
        student.setId(7);
        when(service.save(student)).thenReturn(student);
        assertEquals(HttpStatus.OK, controller.save(student).getStatusCode());
    }

    @Test
    void deletionReturns204WithoutBody() {
        when(service.delete(7)).thenReturn(true);
        var response = controller.delete(7);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void missingDeletionReturns404() {
        when(service.delete(7)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, controller.delete(7).getStatusCode());
    }

    @Test
    void listsStudents() {
        var students = List.of(new Student(), new Student());
        when(service.findAll()).thenReturn(students);
        var response = controller.findAll();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(students, response.getBody());
    }

    @Test
    void returnsCount() {
        when(service.countStudents()).thenReturn(3L);
        var response = controller.countStudent();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3L, response.getBody());
    }

    @Test
    void returnsYearStatistics() {
        List<Object[]> stats = List.<Object[]>of(new Object[]{2003, 2L}, new Object[]{2004, 1L});
        when(service.findNbrStudentByYear()).thenReturn(stats);
        var response = controller.findByYear();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(stats, response.getBody());
    }

    @Test
    void returnsStudentById() {
        Student student = new Student();
        student.setId(7);
        when(service.findById(7)).thenReturn(student);
        assertSame(student, controller.findById(7).getBody());
    }

    @Test
    void filtersByFiliere() {
        var students = List.of(new Student());
        when(service.findByFiliere("Informatique")).thenReturn(students);
        assertEquals(students, controller.findByFiliere("Informatique").getBody());
    }
}
