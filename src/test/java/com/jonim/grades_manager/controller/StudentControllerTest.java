package com.jonim.grades_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonim.grades_manager.models.Grade;
import com.jonim.grades_manager.models.GradeRequest;
import com.jonim.grades_manager.models.Student;
import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.services.GradeService;
import com.jonim.grades_manager.services.ProfessorService;
import com.jonim.grades_manager.services.StudentService;
import com.jonim.grades_manager.services.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(TestSecurityConfig.class)
public class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private ProfessorService professorService;

    @MockBean
    private StudentService studentService;

    @MockBean
    private SubjectService subjectService;

    @Autowired
    private ObjectMapper objectMapper;

    private Student student0;
    private Subject subject0;
    private Grade grade0;

    @BeforeEach
    public void setUp() {
        student0 = Student.builder()
                .studentId(1)
                .name("Joni")
                .surname("Monetti")
                .build();

        subject0 = Subject.builder()
                .id(1)
                .name("Math")
                .students(Set.of(student0))
                .build();

        grade0 = Grade.builder()
                .grade(9.0)
                .student(student0)
                .subject(subject0)
                .build();
    }

    @Test
    public void shouldReturnStudentPage() throws Exception {
        Student student1 = Student.builder()
                .studentId(2)
                .name("Alice")
                .surname("Johnson")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = new PageImpl<>(List.of(student0, student1), pageable, 2);

        when(studentService.getStudentList(0, 10))
                .thenReturn(ResponseEntity.ok(page));

        mockMvc.perform(get("/students")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)))
                .andExpect(jsonPath("$.content[0].studentId", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Joni")))
                .andExpect(jsonPath("$.content[0].surname", is("Monetti")))
                .andExpect(jsonPath("$.content[1].studentId", is(2)))
                .andExpect(jsonPath("$.content[1].name", is("Alice")))
                .andExpect(jsonPath("$.content[1].surname", is("Johnson")))
                .andDo(print());
    }

    @Test
    public void shouldReturnStudentById() throws Exception {
        when(studentService.getStudentById(student0.getStudentId()))
                .thenReturn(ResponseEntity.ok(student0));

        mockMvc.perform(get("/students/{studentId}", student0.getStudentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId", is(1)))
                .andExpect(jsonPath("$.name", is("Joni")))
                .andExpect(jsonPath("$.surname", is("Monetti")))
                .andDo(print());
    }

    @Test
    public void shouldSaveStudent() throws Exception {
        when(studentService.saveStudent(any(Student.class)))
                .thenReturn(ResponseEntity
                        .created(new URI("/students/1"))
                        .body(student0));

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student0)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/students/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.studentId", is(1)))
                .andExpect(jsonPath("$.name", is("Joni")))
                .andExpect(jsonPath("$.surname", is("Monetti")))
                .andDo(print());
    }

    @Test
    public void shouldUpdateStudent() throws Exception {
        Student updatedStudent = Student.builder()
                .studentId(1)
                .name("Joni updated")
                .surname("Monetti")
                .build();

        when(studentService.modifyStudent(eq(student0.getStudentId()), any(Student.class)))
                .thenReturn(ResponseEntity.ok(updatedStudent));

        mockMvc.perform(put("/students/{id}", updatedStudent.getStudentId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId", is(1)))
                .andExpect(jsonPath("$.name", is("Joni updated")))
                .andExpect(jsonPath("$.surname", is("Monetti")))
                .andDo(print());
    }

    @Test
    public void shouldDeleteStudent() throws Exception {
        Integer id = student0.getStudentId();

        when(studentService.deleteStudentById(eq(id)))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/students/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(studentService, times(1)).deleteStudentById(id);
    }

    @Test
    public void shoudlassignSubjectToStudent() throws Exception {
        when(studentService.assignSubjectToStudent(student0.getStudentId(), subject0.getId()))
                .thenReturn(ResponseEntity.ok("Student subject assigned"));

        mockMvc.perform(put("/students/{studentId}/assign-subject/{id}", student0.getStudentId(), subject0.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Student subject assigned"))
                .andDo(print());
    }

    @Test
    public void shouldReturnNotFoundWhenStudentDoesNotExist() throws Exception {
        when(studentService.assignSubjectToStudent(anyInt(), anyInt()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(put("/students/{studentId}/assign-subject/{id}", 999, subject0.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void shouldReturnNotFoundWhenSubjectDoesNotExist() throws Exception {
        // Given
        when(studentService.assignSubjectToStudent(anyInt(), anyInt()))
                .thenReturn(ResponseEntity.notFound().build());

        // When & Then
        mockMvc.perform(put("/students/{studentId}/assign-subject/{id}", student0.getStudentId(), 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void shouldAssignGradeToStudent() throws Exception {
        GradeRequest gradeRequest = new GradeRequest();
        gradeRequest.setGrade(grade0.getGrade());

        when(studentService.assignGradeToStudent(eq(student0.getStudentId()), eq(subject0.getId()), any(GradeRequest.class)))
                .thenReturn(ResponseEntity.ok("Student grade assigned"));

        mockMvc.perform(post("/students/{studentId}/subjects/{subjectId}/grade", student0.getStudentId(), subject0.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Student grade assigned"))
                .andDo(print());
    }

    @Test
    public void shouldReturnNotFoundWhenTheStudentDoesNotExistInAssignGrade() throws Exception {
        GradeRequest gradeRequest = new GradeRequest();
        gradeRequest.setGrade(grade0.getGrade());

        when(studentService.assignGradeToStudent(anyInt(), anyInt(), any(GradeRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Student not found"));

        // When & Then
        mockMvc.perform(post("/students/{studentId}/subjects/{subjectId}/grade", 999, subject0.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Student not found"))
                .andDo(print());
    }

    @Test
    public void shouldReturnNotFoundWhenSubjectNotFoundForStudent() throws Exception {
        // Given
        GradeRequest gradeRequest = new GradeRequest();
        gradeRequest.setGrade(grade0.getGrade());

        when(studentService.assignGradeToStudent(anyInt(), anyInt(), any(GradeRequest.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("The subject was not found for the specified student."));

        // When & Then
        mockMvc.perform(post("/students/{studentId}/subjects/{subjectId}/grade", student0.getStudentId(), 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gradeRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The subject was not found for the specified student."))
                .andDo(print());
    }
}
