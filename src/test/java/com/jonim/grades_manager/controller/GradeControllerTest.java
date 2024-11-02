package com.jonim.grades_manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonim.grades_manager.models.Grade;
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
public class GradeControllerTest {

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

    private Subject subject0;
    private Student student0;
    private Grade grade0;


    @BeforeEach
    public void setUp() {
        subject0 = Subject.builder()
                .name("Science")
                .id(1)
                .build();

        student0 = Student.builder()
                .studentId(1)
                .name("joni")
                .surname("monetti")
                .subjects(Set.of(subject0))
                .build();

        grade0 = Grade.builder()
                .id(1)
                .grade(9.20)
                .student(student0)
                .subject(subject0)
                .build();
    }

    @Test
    public void testSaveGrade() throws Exception {
        when(gradeService.saveGrade(any(Grade.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                        .location(URI.create("/grades/1"))
                        .body(grade0));

        // do the request
        mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(grade0)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.grade", is(9.20)))
                .andExpect(jsonPath("$.id", is(1)))
                .andDo(print());
    }

    @Test
    public void shouldReturnGradesPage() throws Exception {
        //given
        Subject subject1 = Subject.builder()
                .name("Math")
                .id(2)
                .build();

        Student student1 = Student.builder()
                .studentId(2)
                .name("abel")
                .surname("monetti")
                .subjects(Set.of(subject1))
                .build();

        Grade grade1 = Grade.builder()
                .id(2)
                .grade(8.50)
                .student(student1)
                .subject(subject1)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Grade> page = new PageImpl<>(List.of(grade0, grade1), pageable, 2);

        when(gradeService.getGradesList(0, 10))
                .thenReturn(ResponseEntity.ok(page));

        mockMvc.perform(get("/grades")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].grade", is(9.20)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].grade", is(8.50)))
                .andDo(print());
    }

    @Test
    public void shouldReturnGradeById() throws Exception {
        when(gradeService.getGradeById(1))
                .thenReturn(ResponseEntity.ok(grade0));

        mockMvc.perform(get("/grades/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.grade", is(9.20)))
                .andDo(print());

        when(gradeService.getGradeById(999))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/grades/{id}", 999))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void shouldModifyGrade() throws Exception {
        Grade updatedGrade = Grade.builder()
                .id(1)
                .grade(10.00)
                .student(student0)
                .subject(subject0)
                .build();

        when(gradeService.modifyGrade(eq(updatedGrade.getId()), any(Grade.class)))
                .thenReturn(ResponseEntity.ok(updatedGrade));

        mockMvc.perform(put("/grades/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedGrade)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.grade", is(10.00)))
                .andDo(print());
    }

    @Test
    public void shouldDeleteGradeById() throws Exception {
        when(gradeService.deleteGradeById(eq(grade0.getId())))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/grades/{id}", 1))
                .andExpect(status().isNoContent())
                .andDo(print());
        verify(gradeService, times(1)).deleteGradeById(grade0.getId());
    }
}
