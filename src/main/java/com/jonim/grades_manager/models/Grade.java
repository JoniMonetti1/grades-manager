package com.jonim.grades_manager.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grades")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Double grade;
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
