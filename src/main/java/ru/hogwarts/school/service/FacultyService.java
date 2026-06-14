package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Comparator;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    @Autowired
    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public String getLongestFacultyName() {
        logger.info("Was invoked method for get longest faculty name");
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        logger.debug("Creating faculty: " + faculty);
        return facultyRepository.save(faculty);
    }

    public Faculty getFaculty(Long id) {
        logger.info("Was invoked method for get faculty");
        logger.debug("Getting faculty with id: " + id);
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            logger.error("No faculty with id=" + id);
        }
        return faculty;
    }

    public Faculty updateFaculty(Long id, Faculty faculty) {
        logger.info("Was invoked method for update faculty");
        logger.debug("Updating faculty with id: " + id);
        if (!facultyRepository.existsById(id)) {
            logger.error("No faculty with id=" + id);
            return null;
        }
        faculty.setId(id);
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty");
        logger.debug("Deleting faculty with id: " + id);
        if (!facultyRepository.existsById(id)) {
            logger.error("No faculty with id=" + id);
        }
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> getAllFaculties() {
        logger.info("Was invoked method for get all faculties");
        return facultyRepository.findAll();
    }

    public Collection<Faculty> findFacultiesByColor(String color) {
        logger.info("Was invoked method for find faculties by color");
        logger.debug("Finding faculties by color: " + color);
        return facultyRepository.findByColor(color);
    }

    public Collection<Faculty> findByNameOrColor(String text) {
        logger.info("Was invoked method for find faculty by name or color");
        logger.debug("Finding faculty by name or color containing: " + text);
        return facultyRepository
                .findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(text, text);
    }
}