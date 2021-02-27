package ru.gafarov.Family.dto.humanDto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

@Data
@JsonIgnoreProperties
public class HumanDto implements Comparable<HumanDto>{

    private Long id;

    private String name;

    private String patronim;

    private String surname;

    private Date birthdate;

    private Date deathdate;

    private String birthplace;

    private char gender;

    @Override
    public int compareTo(HumanDto o) {
        return this.id.compareTo(o.id);
    }
}
