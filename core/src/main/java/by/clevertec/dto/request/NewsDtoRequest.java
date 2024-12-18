package by.clevertec.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static by.clevertec.constants.NewsDtoConstants.NEWS_NOT_SHOULD_BY_EMPTY;
import static by.clevertec.constants.NewsDtoConstants.NEWS_SHOULD_BY;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewsDtoRequest {

    @NotEmpty(message = NEWS_NOT_SHOULD_BY_EMPTY)
    @Size(min = 1, max = 50, message = NEWS_SHOULD_BY + " 1 - 50 characters")
    private String title;

    @NotEmpty(message = NEWS_NOT_SHOULD_BY_EMPTY)
    @Size(min = 1, max = 5000, message = NEWS_SHOULD_BY + "1 - 5000 characters")
    private String text;
}
