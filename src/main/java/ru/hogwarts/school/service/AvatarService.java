package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AvatarService.class);

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;
    private final Path avatarsDir = Paths.get("avatars");

    public AvatarService(AvatarRepository avatarRepository,
                         StudentRepository studentRepository) {
        logger.info("Was invoked method for create AvatarService");
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Page<Avatar> getAllAvatars(Pageable pageable) {
        logger.info("Was invoked method for get all avatars");
        logger.debug("Getting all avatars with pageable: " + pageable);
        return avatarRepository.findAll(pageable);
    }

    public Long uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar");
        logger.debug("Uploading avatar for studentId: " + studentId);

        Student student = studentRepository.findById(studentId)
                .orElse(null);
        if (student == null) {
            logger.error("No student with id=" + studentId);
            throw new IllegalArgumentException("Student not found");
        }

        if (!Files.exists(avatarsDir)) {
            logger.debug("Creating avatars directory");
            Files.createDirectories(avatarsDir);
        }

        String extension = getExtension(file.getOriginalFilename());
        Path filePath = avatarsDir.resolve("student-" + studentId + extension);

        logger.debug("Writing avatar to file: " + filePath);
        Files.write(filePath, file.getBytes());

        Avatar avatar = avatarRepository.findByStudent(student)
                .orElse(new Avatar());

        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        Avatar saved = avatarRepository.save(avatar);
        logger.info("Avatar uploaded successfully, id=" + saved.getId());
        return saved.getId();
    }

    public Avatar getAvatarFromDb(Long studentId) {
        logger.info("Was invoked method for get avatar from db");
        logger.debug("Getting avatar from db for studentId: " + studentId);

        Student student = studentRepository.findById(studentId)
                .orElse(null);
        if (student == null) {
            logger.error("No student with id=" + studentId);
            throw new IllegalArgumentException("Student not found");
        }

        Avatar avatar = avatarRepository.findByStudent(student)
                .orElse(null);
        if (avatar == null) {
            logger.error("No avatar for student with id=" + studentId);
            throw new IllegalArgumentException("Avatar not found");
        }

        return avatar;
    }

    public byte[] getAvatarFromFile(Long studentId) throws IOException {
        logger.info("Was invoked method for get avatar from file");
        logger.debug("Getting avatar from file for studentId: " + studentId);

        Avatar avatar = getAvatarFromDb(studentId);
        Path path = Paths.get(avatar.getFilePath());

        if (!Files.exists(path)) {
            logger.error("Avatar file not found: " + path);
            throw new IOException("Avatar file not found: " + path);
        }

        logger.debug("Reading avatar from file: " + path);
        return Files.readAllBytes(path);
    }

    private String getExtension(String fileName) {
        logger.debug("Getting extension for file: " + fileName);
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }
}