package by.clevertec.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static by.clevertec.constants.NewsDtoConstants.SHOULD_BE;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CommentDtoRequestUpdate {

    @Size(min = 1, max = 2500, message = SHOULD_BE + "1 - 2500")
    private String text;
}
