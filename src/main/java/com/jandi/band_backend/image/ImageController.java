package com.jandi.band_backend.image;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Image API", description = "이미지 관리 API")
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final S3Service s3Service;

    @Operation(summary = "이미지 업로드")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("dirName") String dirName) throws IOException {
        String imageUrl = s3Service.uploadImage(file, dirName);
        return ResponseEntity.ok(imageUrl);
    }

    @Operation(summary = "이미지 삭제")
    @DeleteMapping
    public ResponseEntity<Void> deleteImage(@RequestParam("fileUrl") String fileUrl) {
        s3Service.deleteImage(fileUrl);
        return ResponseEntity.ok().build();
    }
} 