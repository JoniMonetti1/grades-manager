package com.jonim.grades_manager.service;

import com.jonim.grades_manager.exceptions.ResourceNotFoundException;
import com.jonim.grades_manager.models.Grade;
import com.jonim.grades_manager.models.GradeRequest;
import com.jonim.grades_manager.models.Student;
import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.GradeRepository;
import com.jonim.grades_manager.repositories.StudentRepository;
import com.jonim.grades_manager.repositories.SubjectRepository;
import com.jonim.grades_manager.services.StudentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student0;

    @BeforeEach
    public void setUp() {
        student0 = Student.builder()
                .studentId(1)
                .name("Joni")
                .surname("Monetti")
                .build();
    }

    @Test
    public void shouldSaveStudent() {
        // Configuramos una solicitud simulada
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        //given
        given(studentRepository.save(student0)).willReturn(student0);

        //when
        ResponseEntity<Student> savedStudent = studentService.saveStudent(student0);

        //then
        assertThat(savedStudent.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(savedStudent).isNotNull();
    }

    @Test
    public void shouldNotSaveThrowException() {
        //given: Simulamos que el sujeto ya existe en la base de datos
        given(studentRepository.findById(student0.getStudentId()))
                .willReturn(Optional.of(student0));

        //when: Esperamos que el método falle al intentar guardar un `student` duplicado
        assertThrows(ResourceNotFoundException.class, () ->
                studentService.saveStudent(student0));

        //then: Verificamos que nunca se haya intentado guardar
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    public void shouldReturnAllStudents() {
        //given
        Student student1 = Student.builder()
                .studentId(1)
                .name("Abel")
                .surname("Monetti")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> page = new PageImpl<>(List.of(student0, student1), pageable, 2);

        given(studentRepository.findAll(pageable)).willReturn(page);

        //when
        ResponseEntity<Page<Student>> students = studentService.getStudentList(0, 10);

        //then
        assertThat(students.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(students.getBody()).hasSize(2);
        assertThat(students.getBody().getContent()).extracting(Student::getName)
                .containsExactlyInAnyOrder("Joni", "Abel");
    }

    @Test
    public void shouldReturnAEmptyPage() {
        //given
        Pageable pageable = PageRequest.of(0, 10);

        // Mockea el método findAll para devolver un Page vacío
        given(studentRepository.findAll(pageable)).willReturn(Page.empty(pageable));

        //when
        ResponseEntity<Page<Student>> studentPage = studentService.getStudentList(0, 10);

        //then
        assertThat(studentPage.getBody()).isEmpty();
        assertThat(studentPage.getBody().getContent()).hasSize(0);
    }

    @Test
    public void shouldReturnStudentWithGradesForEachSubject() {
        // given
        Integer studentId = 1;
        Student student = new Student();
        student.setStudentId(studentId);
        Subject subject = new Subject();
        subject.setId(1);
        student.setSubjects(Set.of(subject));

        Grade grade = new Grade();
        grade.setId(1);
        grade.setGrade(9.20);

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));
        given(gradeRepository.findGradesByStudentIdAndSubjectId(studentId, subject.getId()))
                .willReturn(List.of(grade));

        // when
        ResponseEntity<Student> response = studentService.getStudentById(studentId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSubjects()).hasSize(1);
        assertThat(response.getBody().getSubjects().iterator().next().getGrades()).contains(grade);
    }

    @Test
    public void shouldReturnNotFoundWhenStudentDoesNotExist() {
        // given
        Integer studentId = 1;
        given(studentRepository.findById(studentId)).willReturn(Optional.empty());

        // when
        ResponseEntity<Student> response = studentService.getStudentById(studentId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void shouldReturnStudentWithoutSubjects() {
        // given
        Integer studentId = 1;
        Student student = new Student();
        student.setStudentId(studentId);
        student.setSubjects(new HashSet<>());

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));

        // when
        ResponseEntity<Student> response = studentService.getStudentById(studentId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getSubjects()).isEmpty();
    }

    @Test
    public void shouldUpdateSubject() {
        //given
        given(studentRepository.findById(student0.getStudentId())).willReturn(Optional.of(student0));
        given(studentRepository.save(student0)).willReturn(student0);
        student0.setName("Jonathan");

        //when
        ResponseEntity<Student> updatedStudent = studentService.modifyStudent(student0.getStudentId(), student0);

        //then
        assertThat(updatedStudent.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedStudent.getBody().getName()).isEqualTo("Jonathan");
    }

    @Test
    public void shouldDeleteSubject() {
        //given
        Integer studentId = student0.getStudentId();
        given(studentRepository.findById(studentId)).willReturn(Optional.of(student0));
        willDoNothing().given(studentRepository).deleteById(studentId);

        //when
        studentService.deleteStudentById(studentId);

        //then
        verify(studentRepository, times(1)).deleteById(studentId);
    }

    @Test
    public void shouldAssignSubjectToStudentSuccessfully() {
        // given
        Integer studentId = 1;
        Integer subjectId = 2;
        Student student = new Student();
        student.setStudentId(studentId);
        Subject subject = new Subject();
        subject.setId(subjectId);

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));
        given(subjectRepository.findById(subjectId)).willReturn(Optional.of(subject));

        // when
        ResponseEntity<Void> response = studentService.assignSubjectToStudent(studentId, subjectId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(studentRepository).save(student);
        assertThat(student.getSubjects()).contains(subject);
    }

    @Test
    public void shouldReturnNotFoundWhenStudentDoesNotExistInAssignSubjectToStudent() {
        // given
        Integer studentId = 1;
        Integer subjectId = 2;
        given(studentRepository.findById(studentId)).willReturn(Optional.empty());

        // when
        ResponseEntity<Void> response = studentService.assignSubjectToStudent(studentId, subjectId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    public void shouldReturnNotFoundWhenSubjectDoesNotExist() {
        // given
        Integer studentId = 1;
        Integer subjectId = 2;
        Student student = new Student();
        student.setStudentId(studentId);

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));
        given(subjectRepository.findById(subjectId)).willReturn(Optional.empty());

        // when
        ResponseEntity<Void> response = studentService.assignSubjectToStudent(studentId, subjectId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    public void shouldAssignGradeToStudentSuccessfully() {
        // given
        Integer studentId = 1;
        Integer subjectId = 2;
        GradeRequest gradeRequest = new GradeRequest();
        gradeRequest.setGrade(9.20);

        Student student = new Student();
        student.setStudentId(studentId);
        Subject subject = new Subject();
        subject.setId(subjectId);

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));
        given(subjectRepository.findSubjectByIdAndStudentId(subjectId, studentId)).willReturn(Optional.of(subject));

        // when
        ResponseEntity<String> response = studentService.assignGradeToStudent(studentId, subjectId, gradeRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Student grade assigned");
        verify(gradeRepository).save(any(Grade.class));
    }

    @Test
    public void shouldReturnNotFoundWhenStudentDoesNotExistForGrade() {
        // given
        Integer studentId = 1;
        Integer subjectId = 2;
        GradeRequest gradeRequest = new GradeRequest();
        gradeRequest.setGrade(9.20);

        given(studentRepository.findById(studentId)).willReturn(Optional.empty());

        // when
        ResponseEntity<String> response = studentService.assignGradeToStudent(studentId, subjectId, gradeRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("Student not found");
        verify(gradeRepository, never()).save(any(Grade.class));
    }

    @Test
    public void shouldReturnNotFoundWhenSubjectIsNotAssociatedToStudent() {
        // given
        Integer studentId = 1;
        Integer subjectId = 2;
        GradeRequest gradeRequest = new GradeRequest();
        gradeRequest.setGrade(9.20);

        Student student = new Student();
        student.setStudentId(studentId);

        given(studentRepository.findById(studentId)).willReturn(Optional.of(student));
        given(subjectRepository.findSubjectByIdAndStudentId(subjectId, studentId)).willReturn(Optional.empty());

        // when
        ResponseEntity<String> response = studentService.assignGradeToStudent(studentId, subjectId, gradeRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isEqualTo("The subject was not found for the specified student.");
        verify(gradeRepository, never()).save(any(Grade.class));
    }
}
