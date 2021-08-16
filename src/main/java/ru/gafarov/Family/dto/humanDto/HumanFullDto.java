package ru.gafarov.Family.dto.humanDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties
@SuperBuilder
public class HumanFullDto extends HumanDto {

    protected List<HumanShortDto> parents;

    protected List<HumanShortDto> children;
}
