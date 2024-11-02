package com.jonim.grades_manager.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonim.grades_manager.controllers.SubjectController;
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

@WebMvcTest(SubjectController.class)
@Import(TestSecurityConfig.class)
public class SubjectControllerTest {
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

    @BeforeEach
    public void setUp() {
        subject0 = Subject.builder()
                .id(1)
                .name("Maths")
                .build();
    }

    @Test
    public void shouldReturnSubjectList() throws Exception {
        Subject subject1 = Subject.builder()
                .name("Science")
                .id(2)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Subject> page = new PageImpl<>(List.of(subject0, subject1), pageable, 2);

        when(subjectService.getSubjectList(0, 10))
                .thenReturn(ResponseEntity.ok(page));

        mockMvc.perform(get("/subjects")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Maths")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].name", is("Science")))
                .andDo(print());
    }


    @Test
    public void shouldReturnSubjectById() throws Exception {
        when(subjectService.getSubjectById(subject0.getId()))
                .thenReturn(ResponseEntity.ok(subject0));

        mockMvc.perform(get("/subjects/{id}", subject0.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Maths")))
                .andDo(print());
    }


    @Test
    public void shouldCreateSubject() throws Exception {

        when(subjectService.saveSubject(any(Subject.class)))
                .thenReturn(ResponseEntity
                        .created(new URI("/subjects/1"))
                        .body(subject0));

        mockMvc.perform(post("/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subject0)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/subjects/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Maths")))
                .andExpect(jsonPath("$.id", is(1)))
                .andDo(print());
    }

    @Test
    public void shouldUpdateSubject() throws Exception {
        Subject updatedSubject = Subject.builder()
                .id(1)
                .name("Updated Maths")
                .build();

        when(subjectService.modifySubject(eq(updatedSubject.getId()), any(Subject.class)))
                .thenReturn(ResponseEntity.ok(updatedSubject));

        mockMvc.perform(put("/subjects/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subject0)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Maths")))
                .andDo(print());
    }

    @Test
    public void shouldDeleteSubject() throws Exception {
        when(subjectService.deleteSubjectById(eq(subject0.getId())))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/subjects/{id}", subject0.getId()))
                .andExpect(status().isNoContent())
                .andDo(print());
        verify(subjectService, times(1)).deleteSubjectById(subject0.getId());
    }

    @Test
    public void shouldReturnSubjectByIdAndStudentId() throws Exception {
        Student student = Student.builder()
                .studentId(1)
                .name("joni")
                .surname("monetti")
                .subjects(Set.of(subject0))
                .build();

        when(subjectService.findSubjectByIdAndStudentId(eq(subject0.getId()), eq(student.getStudentId())))
                .thenReturn(ResponseEntity.ok(subject0));

        mockMvc.perform(get("/subjects/{id}/students/{studentId}", subject0.getId(), student.getStudentId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Maths")))
                .andDo(print());
    }
}
