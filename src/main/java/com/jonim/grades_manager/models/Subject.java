package com.jonim.grades_manager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Set<Student> students = new HashSet<>();
}

