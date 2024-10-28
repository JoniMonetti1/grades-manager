package com.jonim.grades_manager.services;

import com.jonim.grades_manager.models.Grade;
import com.jonim.grades_manager.repositories.GradeRepository;
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
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;

    public GradeServiceImpl(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public ResponseEntity<Page<Grade>> getGradesList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Grade> gradesPage = gradeRepository.findAll(pageable);
        return gradesPage.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(gradesPage);
    }

    @Override
    public ResponseEntity<Grade> getGradeById(Integer id) {
        Optional<Grade> grade = gradeRepository.findById(id);
        return grade.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Grade> saveGrade(Grade grade) {
        Grade savedGrade = gradeRepository.save(grade);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedGrade.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedGrade);
    }

    @Override
    public ResponseEntity<Grade> modifyGrade(Integer id, Grade grade) {
        Optional<Grade> optionalGrade = gradeRepository.findById(id);

        if (optionalGrade.isPresent()) {
            grade.setId(id);
            Grade modifiedGrade = gradeRepository.save(grade);
            return ResponseEntity.ok(modifiedGrade);
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteGradeById(Integer id) {
        Optional<Grade> optionalGrade = gradeRepository.findById(id);

        if (optionalGrade.isPresent()) {
            gradeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
