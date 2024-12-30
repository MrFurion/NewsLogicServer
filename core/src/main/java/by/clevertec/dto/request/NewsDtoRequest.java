package by.clevertec.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static by.clevertec.constants.NewsDtoConstants.NEWS;
import static by.clevertec.constants.NewsDtoConstants.SHOULD_BE;
import static by.clevertec.constants.NewsDtoConstants.SHOULD_NOT_BE_EMPTY;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewsDtoRequest {

    @NotEmpty(message = NEWS + SHOULD_NOT_BE_EMPTY)
    @Size(min = 1, max = 50, message = NEWS + SHOULD_BE + " 1 - 50 characters")
    private String title;

    @NotEmpty(message = NEWS + SHOULD_NOT_BE_EMPTY)
    @Size(min = 1, max = 5000, message = NEWS + SHOULD_BE + "1 - 5000 characters")
    private String text;
}
