package by.clevertec.util;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class ExtractNews {

    public static UUID newsIdForDelete;

    public static void extractNewsIdFromResponse(ResponseEntity<String> response) {

        String locationHeader = response.getHeaders().getLocation().toString();
        String extractedId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);

        newsIdForDelete = UUID.fromString(extractedId);
    }
}
