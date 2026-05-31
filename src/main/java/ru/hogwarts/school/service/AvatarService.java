package ru.hogwarts.school.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    private final Path avatarsDir = Paths.get("avatars");

    public AvatarService(AvatarRepository avatarRepository,
                         StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Page<Avatar> getAllAvatars(Pageable pageable) {
        return avatarRepository.findAll(pageable);
    }

    public Long uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        if (!Files.exists(avatarsDir)) {
            Files.createDirectories(avatarsDir);
        }

        String extension = getExtension(file.getOriginalFilename());
        Path filePath = avatarsDir.resolve("student-" + studentId + extension);

        Files.write(filePath, file.getBytes());

        Avatar avatar = avatarRepository.findByStudent(student)
                .orElse(new Avatar());

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        Avatar saved = avatarRepository.save(avatar);
        return saved.getId();
    }

    public Avatar getAvatarFromDb(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return avatarRepository.findByStudent(student)
                .orElseThrow(() -> new IllegalArgumentException("Avatar not found"));
    }

    public byte[] getAvatarFromFile(Long studentId) throws IOException {
        Avatar avatar = getAvatarFromDb(studentId);
        return Files.readAllBytes(Paths.get(avatar.getFilePath()));
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}