package ru.hogwarts.school.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;

@RestController
@RequestMapping("avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping
    public Page<Avatar> getAllAvatars(
            @RequestParam int page,
            @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return avatarService.getAllAvatars(pageable);
    }

    @PostMapping(value = "/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long uploadAvatar(@PathVariable Long studentId,
                             @RequestParam("file") MultipartFile file) throws IOException {
        return avatarService.uploadAvatar(studentId, file);
    }

    @GetMapping("/{studentId}/from-db")
    public ResponseEntity<byte[]> downloadAvatarFromDb(@PathVariable Long studentId) {
        Avatar avatar = avatarService.getAvatarFromDb(studentId);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, avatar.getMediaType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(avatar.getFileSize()))
                .body(avatar.getData());
    }

    @GetMapping("/{studentId}/file")
    public ResponseEntity<byte[]> downloadAvatarFromFile(@PathVariable Long studentId) throws IOException {
        Avatar avatar = avatarService.getAvatarFromDb(studentId);
        byte[] data = avatarService.getAvatarFromFile(studentId);

        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_TYPE, avatar.getMediaType())
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(data.length))
                .body(data);
    }
}