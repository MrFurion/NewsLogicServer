package by.clevertec.mapper;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.models.News;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NewsMapper {

    NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);


    News toNews(NewsDtoRequest newsDtoRequest);

    NewsDtoResponse toNewsDtoResponse(News news);
}
