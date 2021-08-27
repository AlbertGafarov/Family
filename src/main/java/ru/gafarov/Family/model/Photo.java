package ru.gafarov.Family.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Table(name = "photos")
public class Photo extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Column(name = "date_photo")
    private Date photoDate;

    @Column(name = "height")
    private Integer height;

    @Column(name = "width")
    private Integer width;

    @Column(name = "size")
    private Long size;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "photo_humans",
            joinColumns = {@JoinColumn(name = "photo_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "human_id", referencedColumnName = "id")})
    private List<Human> humansOnPhoto;

}
