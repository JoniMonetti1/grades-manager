package com.jonim.grades_manager.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @OneToMany(mappedBy = "subject")
    private Set<Grade> grades = new HashSet<>();

    @ManyToMany(mappedBy = "subjects")
    private Set<Student> students = new HashSet<>();
}

