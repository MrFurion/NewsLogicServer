package by.clevertec.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static by.clevertec.constants.NewsDtoConstants.NEWS_SHOULD_BE;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewsDtoRequestUpdate {

    @Size(min = 1, max = 50, message = NEWS_SHOULD_BE + " 1 - 50 characters")
    private String title;


    @Size(min = 1, max = 5000, message = NEWS_SHOULD_BE + "1 - 5000 characters")
    private String text;
}
