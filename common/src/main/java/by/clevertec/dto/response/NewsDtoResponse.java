package by.clevertec.dto.response;



import by.clevertec.models.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewsDtoResponse {
    private UUID id;
    private String title;
    private String text;
    private List<Comment> comments;
}
