package com.jonim.grades_manager.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer studentId;
    private String name;
    private String surname;

    @ManyToMany(mappedBy = "students")
    private Set<Subject> subjects = new HashSet<>();

    @OneToMany(mappedBy = "student")
    private Set<Grade> grades = new HashSet<>();
}
