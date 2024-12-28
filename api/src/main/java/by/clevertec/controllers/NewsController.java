package by.clevertec.controllers;

import by.clevertec.dto.request.NewsDtoRequest;
import by.clevertec.dto.request.NewsDtoRequestUpdate;
import by.clevertec.dto.response.NewsDtoResponse;
import by.clevertec.services.NewsService;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.sort.dsl.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
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

    @GetMapping
    public ResponseEntity<Page<NewsDtoResponse>> findAllNews(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<NewsDtoResponse> newsDtoResponses = newsService.findAll(pageable);
        return ResponseEntity.ok(newsDtoResponses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<NewsDtoResponse>> searchNewsByTitleAndText(@RequestBody String query,
                                                                          @RequestParam(defaultValue = "0") int startIndex,
                                                                          @RequestParam(defaultValue = "5") int maxResults,
                                                                          @RequestParam(defaultValue = "title") String fields,
                                                                          @RequestParam(defaultValue = "sort_title") String sortBy,
                                                                          @RequestParam(defaultValue = "ASC") SortOrder sortOrder) {

        List<NewsDtoResponse> newsDtoResponses = newsService.fullTextSearchByTitleAndTextField(query, startIndex, maxResults,
                fields, sortBy, sortOrder);
        return ResponseEntity.ok(newsDtoResponses);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<NewsDtoResponse>> findAllNewsWithComments(@PathVariable UUID id,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "5") int size) {
        Page<NewsDtoResponse> newsDtoResponse = newsService.findByIdWithAllComments(id, page, size);
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
