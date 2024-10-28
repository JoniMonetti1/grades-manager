package com.jonim.grades_manager.services;

import com.jonim.grades_manager.exceptions.ResourceNotFoundException;
import com.jonim.grades_manager.models.Subject;
import com.jonim.grades_manager.repositories.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public ResponseEntity<Page<Subject>> getSubjectList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Subject> subjectPage = subjectRepository.findAll(pageable);
        return ResponseEntity.ok(subjectPage);
    }

    @Override
    public ResponseEntity<Subject> getSubjectById(Integer id) {
        Optional<Subject> optionalSubject = subjectRepository.findById(id);
        return optionalSubject.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Subject> saveSubject(Subject subject) {
        if (subjectRepository.findById(subject.getId()).isPresent()) {
            throw new ResourceNotFoundException("Subject already exists with id: " + subject.getId());
        }

        Subject savedSubject = subjectRepository.save(subject);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(subject.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedSubject);
    }

    @Override
    public ResponseEntity<Subject> modifySubject(Integer id, Subject subject) {
        Optional<Subject> optionalSubject = subjectRepository.findById(id);

        if (optionalSubject.isPresent()) {
            subject.setId(id);
            Subject modifiedSubject = subjectRepository.save(subject);
            return ResponseEntity.ok(modifiedSubject);
        }

        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteSubjectById(Integer id) {
        Optional<Subject> optionalSubject = subjectRepository.findById(id);

        if (optionalSubject.isPresent()) {
            subjectRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Subject> findSubjectByIdAndStudentId(Integer subjectId, Integer studentId) {
        Optional<Subject> optionalSubject = subjectRepository.findSubjectByIdAndStudentId(subjectId, studentId);
        return optionalSubject.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
