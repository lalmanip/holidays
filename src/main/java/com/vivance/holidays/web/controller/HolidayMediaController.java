package com.vivance.holidays.web.controller;

import com.vivance.holidays.service.HolidayMediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Holidays Media", description = "Public holiday package images")
@RestController
@RequestMapping("/api/v1/holidays")
public class HolidayMediaController {

    private final HolidayMediaService mediaService;

    public HolidayMediaController(HolidayMediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Operation(summary = "Serve uploaded holiday image")
    @GetMapping("/media/{folder}/{fileName}")
    public ResponseEntity<Resource> serveMedia(
            @PathVariable String folder, @PathVariable String fileName) throws IOException {
        String relativePath = folder + "/" + fileName;
        Resource resource = mediaService.resolveMediaResource(relativePath);
        MediaType mediaType = mediaService.mediaTypeForFileName(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .contentType(mediaType)
                .body(resource);
    }
}
