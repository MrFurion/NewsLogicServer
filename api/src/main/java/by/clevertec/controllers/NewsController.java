package by.clevertec.controllers;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.models.News;
import by.clevertec.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/{id}")
    public ResponseEntity<News> findNews(@PathVariable UUID id) {
        News news = newsService.findById(id);
        return ResponseEntity.ok(news);
    }

    @PostMapping
    public ResponseEntity<String> createNews(@Validated @RequestBody NewsDtoRequest newsDtoRequest) {
        NewsDtoResponse newsDtoResponse = newsService.create(newsDtoRequest);
        return ResponseEntity.created(URI.create("/news")).body("News created successfully with id : "
                + newsDtoResponse.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsDtoResponse> updateNews(@Validated @RequestBody NewsDtoRequest newsDtoRequest,
                                                      @PathVariable UUID id) {

        //TODO нужно создать и использовать NewsDtoRequestUpdate

        NewsDtoResponse  newsDtoResponse = newsService.update(newsDtoRequest, id);
        return ResponseEntity.ok(newsDtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable UUID id) {
        newsService.delete(id);
        return ResponseEntity.ok("News deleted successfully with id : " + id);
    }
}
