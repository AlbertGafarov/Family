package ru.gafarov.Family.dto.photoDto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Builder
public class PhotoCreateDto {

    @Min(value = 1, message = "id cannot be less 1")
    private Long id;

    @Size(min = 2, message = "surname must be more two symbol")
    private String name;

    @PastOrPresent
    private Date photoDate;
    private Long[] humans_id;

    private String status;

    @Min(value = 1, message = "author_id cannot be less 1")
    private Long author_id;

    MultipartFile file;
}
