package by.clevertec.controllers;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
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
    public ResponseEntity<NewsDtoResponse> findNews(@PathVariable UUID id) {
        NewsDtoResponse newsDtoResponse = newsService.findById(id);
        return ResponseEntity.ok(newsDtoResponse);
    }

    @PostMapping
    public ResponseEntity<String> createNews(@Validated @RequestBody NewsDtoRequest newsDtoRequest) {
        NewsDtoResponse newsDtoResponse = newsService.create(newsDtoRequest);
        URI location = URI.create("/news/" + newsDtoResponse.getId());
        return ResponseEntity.created(location).body("News created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsDtoResponse> updateNews(@Validated @RequestBody NewsDtoRequestUpdate newsDtoRequestUpdate,
                                                      @PathVariable UUID id) {

        NewsDtoResponse newsDtoResponse = newsService.update(newsDtoRequestUpdate, id);
        return ResponseEntity.ok(newsDtoResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNews(@PathVariable UUID id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
