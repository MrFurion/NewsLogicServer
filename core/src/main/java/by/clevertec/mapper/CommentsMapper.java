package by.clevertec.mapper;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.response.CommentsDtoResponse;
import by.clevertec.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentsMapper {

    CommentsMapper INSTANCE = Mappers.getMapper(CommentsMapper.class);

    CommentsDtoResponse toCommentsDtoResponse(Comment comment);

    Comment toComment(CommentDtoRequest commentDtoRequest);


}
