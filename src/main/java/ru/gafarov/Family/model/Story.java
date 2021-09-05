package ru.gafarov.Family.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@Table(name = "stories")
public class Story extends BaseEntity {

    @Column(name = "story")
    private String story;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "stories_humans",
            joinColumns = {@JoinColumn(name = "story_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "human_id", referencedColumnName = "id")})
    private List<Human> heroes;
}
