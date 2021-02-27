package ru.gafarov.Family.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "humans")
public class Human extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "patronim")
    private String patronim;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "surname_id")
    private Surname surname;

    @Column(name = "birthdate")
    private Date birthdate;

    @Column(name = "deathdate")
    private Date deathdate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "birthplace_id")
    private Birthplace birthplace;

    @Column(name = "gender")
    private char gender;

    @ManyToMany(mappedBy = "children", fetch = FetchType.LAZY)
    private List<Human> parents;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "humans_parents",
            joinColumns = {@JoinColumn(name = "parent_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "human_id", referencedColumnName = "id")})
    private List<Human> children;

}
