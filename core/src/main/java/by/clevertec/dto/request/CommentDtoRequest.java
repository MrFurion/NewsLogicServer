package by.clevertec.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static by.clevertec.constants.NewsDtoConstants.COMMENT;
import static by.clevertec.constants.NewsDtoConstants.SHOULD_BE;
import static by.clevertec.constants.NewsDtoConstants.SHOULD_NOT_BY_EMPTY;
import static by.clevertec.constants.NewsDtoConstants.USER_NAME;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommentDtoRequest {

    @NotEmpty(message = COMMENT + SHOULD_NOT_BY_EMPTY)
    @Size(min = 1, max = 2500, message = SHOULD_BE + "1 - 2500")
    private String text;

    @NotEmpty(message = USER_NAME + SHOULD_NOT_BY_EMPTY)
    @Size(min = 4, max = 25, message = USER_NAME + SHOULD_BE + "4 - 25")
    private String username;
}
