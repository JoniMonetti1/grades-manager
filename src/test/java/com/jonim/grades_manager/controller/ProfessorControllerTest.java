package com.jonim.grades_manager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jonim.grades_manager.models.*;
import com.jonim.grades_manager.services.GradeService;
import com.jonim.grades_manager.services.ProfessorService;
import com.jonim.grades_manager.services.StudentService;
import com.jonim.grades_manager.services.SubjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import(TestSecurityConfig.class)
public class ProfessorControllerTest {
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
    private Subject expectedSubject;
    private Grade grade0;
    private ProfessorDTO professorDTO;
    private Professor expectedProfessor;


    @BeforeEach
    public void setUp() {
        student0 = Student.builder()
                .studentId(1)
                .name("Joni")
                .surname("Monetti")
                .build();

        grade0 = Grade.builder()
                .grade(9.0)
                .student(student0)
                .subject(expectedSubject)
                .build();

        expectedSubject = new Subject();
        expectedSubject.setId(1);
        expectedSubject.setName("Mathematics");

        expectedProfessor = new Professor();
        expectedProfessor.setName("Joni");
        expectedProfessor.setSurname("Monetti");
        expectedProfessor.setSubject(expectedSubject);
        expectedProfessor.setId(1);

        professorDTO = new ProfessorDTO(expectedProfessor);
    }

    @Test
    public void shouldReturnProfessorPage() throws Exception {
        Professor expectedProfessor1 = Professor.builder()
                .name("Abel")
                .surname("Monetti")
                .subject(expectedSubject)
                .id(2)
                .build();

        ProfessorDTO professorDTO = new ProfessorDTO(expectedProfessor);
        ProfessorDTO professor1DTO = new ProfessorDTO(expectedProfessor1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ProfessorDTO> page = new PageImpl<>(List.of(professorDTO, professor1DTO), pageable, 2);

        when(professorService.getProfessorList(0, 10))
                .thenReturn(ResponseEntity.ok(page));

        mockMvc.perform(get("/professors")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Joni")))
                .andExpect(jsonPath("$.content[0].surname", is("Monetti")))
                .andExpect(jsonPath("$.content[0].subject.id", is(1)))
                .andExpect(jsonPath("$.content[0].subject.name", is("Mathematics")))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].name", is("Abel")))
                .andExpect(jsonPath("$.content[1].surname", is("Monetti")))
                .andExpect(jsonPath("$.content[1].subject.name", is("Mathematics")))
                .andExpect(jsonPath("$.content[1].subject.id", is(1)));
    }

    @Test
    public void shouldReturnProfessorById() throws Exception{
        when(professorService.getProfessorById(expectedProfessor.getId()))
                .thenReturn(ResponseEntity.ok(professorDTO));

        mockMvc.perform(get("/professors/{id}", expectedProfessor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Joni")))
                .andExpect(jsonPath("$.surname", is("Monetti")))
                .andExpect(jsonPath("$.subject.id", is(1)))
                .andExpect(jsonPath("$.subject.name", is("Mathematics")))
                .andDo(print());
    }

    @Test
    public void shouldSaveProfessor() throws Exception {
        ProfessorCreateUpdateDTO newProfessor = new ProfessorCreateUpdateDTO();
        newProfessor.setName("Joni");
        newProfessor.setSurname("Monetti");
        newProfessor.setSubjectId(expectedSubject.getId());

        when(professorService.saveProfessor(any(ProfessorCreateUpdateDTO.class)))
                .thenReturn(ResponseEntity
                        .created(new URI("/professor/1"))
                        .body(professorDTO));

        mockMvc.perform(post("/professors")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProfessor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Joni")))
                .andExpect(jsonPath("$.surname", is("Monetti")))
                .andDo(print());
    }

    @Test
    public void shouldUpdateProfessor() throws Exception {
        ProfessorCreateUpdateDTO updatedDTO = new ProfessorCreateUpdateDTO();
        updatedDTO.setName("Joni updated");
        updatedDTO.setSurname("Monetti");
        updatedDTO.setSubjectId(expectedSubject.getId());

        ProfessorDTO updatedProfessorDTO = new ProfessorDTO(
                Professor.builder()
                        .id(expectedProfessor.getId())
                        .name(updatedDTO.getName())
                        .surname(updatedDTO.getSurname())
                        .subject(expectedSubject)
                        .build()
        );

        when(professorService.modifyProfessor(expectedProfessor.getId(), updatedDTO))
                .thenReturn(ResponseEntity.ok(updatedProfessorDTO));

        mockMvc.perform(put("/professors/{id}", expectedProfessor.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Joni updated")))
                .andExpect(jsonPath("$.surname", is("Monetti")))
                .andDo(print());
    }

    @Test
    public void shouldDeleteProfessor() throws Exception {
        Integer id = expectedProfessor.getId();

        when(professorService.deleteProfessorById(id))
                .thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(delete("/professors/{id}", id))
                .andExpect(status().isNoContent())
                .andDo(print());
        verify(professorService, times(1)).deleteProfessorById(id);
    }

    @Test
    public void shouldAssignSubjectToProfessor() throws Exception {
        when(professorService.assignSubjectToProfessor(expectedProfessor.getId(), expectedSubject.getId()))
                .thenReturn(ResponseEntity.ok("Subject assigned"));

        mockMvc.perform(put("/professors/{id}/subjects/{subjectId}", expectedProfessor.getId(), expectedSubject.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Subject assigned"))
                .andDo(print());
        verify(professorService).assignSubjectToProfessor(expectedProfessor.getId(), expectedSubject.getId());
    }

    @Test
    public void shouldNotAssignSubjectToProfessorWhenProfessorNotFound() throws Exception {
        when(professorService.assignSubjectToProfessor(99, expectedSubject.getId()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(put("/professors/{id}/subjects/{subjectId}", 99, expectedSubject.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(professorService).assignSubjectToProfessor(99, expectedSubject.getId());
    }

    @Test
    public void shouldNotAssignSubjectToProfessorWhenSubjectNotFound() throws Exception {
        when(professorService.assignSubjectToProfessor(expectedProfessor.getId(), 99))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(put("/professors/{id}/subjects/{subjectId}", expectedProfessor.getId(), 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());

        verify(professorService).assignSubjectToProfessor(expectedProfessor.getId(), 99);
    }

    @Test
    public void shouldNotAssignSubjectToProfessorWhenInvalidIds() throws Exception {
        mockMvc.perform(put("/professors/{id}/subjects/{subjectId}", "invalid", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
        verify(professorService, never()).assignSubjectToProfessor(any(), any());
    }
}
