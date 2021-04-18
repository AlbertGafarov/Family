package ru.gafarov.Family.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Table(name = "photos")
public class Photo extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Column(name = "date_photo")
    private Date photoDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "photo_humans",
            joinColumns = {@JoinColumn(name = "photo_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "human_id", referencedColumnName = "id")})
    private List<Human> humans;

}
