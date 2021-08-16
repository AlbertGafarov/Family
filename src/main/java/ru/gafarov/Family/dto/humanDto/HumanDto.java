package ru.gafarov.Family.dto.humanDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties
@SuperBuilder
public class HumanDto extends HumanShortDto implements Comparable<HumanDto>{

    protected String birthdate;

    protected String deathdate;

    protected String birthplace;

    protected char gender;

    @Override
    public int compareTo(HumanDto o) {
        return id.compareTo(o.id);
    }
}
