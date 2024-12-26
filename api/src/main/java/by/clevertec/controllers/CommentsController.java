package by.clevertec.controllers;

import by.clevertec.dto.request.CommentDtoRequest;
import by.clevertec.dto.request.CommentDtoRequestUpdate;
import by.clevertec.dto.response.CommentsDtoResponse;
import by.clevertec.services.CommentsService;
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
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsController {

    private final CommentsService commentsService;

    @GetMapping("/{uuid}")
    public ResponseEntity<CommentsDtoResponse> findComment(@PathVariable UUID uuid) {
        CommentsDtoResponse response = commentsService.findById(uuid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{uuid}")
    public ResponseEntity<String> createComment(@Validated @RequestBody CommentDtoRequest commentDtoRequest,
                                                @PathVariable UUID uuid) {
        CommentsDtoResponse commentsDtoResponse = commentsService.create(uuid, commentDtoRequest);
        URI location = URI.create("/comments/" + commentsDtoResponse.getId());
        return ResponseEntity.created(location).body("Comment created successfully");
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<CommentsDtoResponse> updateComment(@PathVariable UUID uuid,
                                                             @RequestBody CommentDtoRequestUpdate commentDtoRequestUpdate) {
        CommentsDtoResponse commentsDtoResponse = commentsService.update(uuid, commentDtoRequestUpdate);
        return ResponseEntity.ok(commentsDtoResponse);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteComment(@PathVariable UUID uuid) {
        commentsService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
