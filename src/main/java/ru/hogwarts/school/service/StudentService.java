package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.List;

@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public long getCountOfStudents() {
        logger.info("Was invoked method for get count of students");
        return studentRepository.getCountOfStudents();
    }

    public Double getAverageAge() {
        logger.info("Was invoked method for get average age");
        return studentRepository.getAverageAge();
    }

    public List<Student> getLastFiveStudents() {
        logger.info("Was invoked method for get last five students");
        return studentRepository.getLastFiveStudents();
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        logger.debug("Creating student: " + student);
        return studentRepository.save(student);
    }

    public Student getStudent(Long id) {
        logger.info("Was invoked method for get student");
        logger.debug("Getting student with id: " + id);
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            logger.error("No student with id=" + id);
        }
        return student;
    }

    public Student updateStudent(Long id, Student student) {
        logger.info("Was invoked method for update student");
        logger.debug("Updating student with id: " + id);
        if (!studentRepository.existsById(id)) {
            logger.error("No student with id=" + id);
            return null;
        }
        student.setId(id);
        return studentRepository.save(student);
    }

    public Collection<Student> findByAgeBetween(int min, int max) {
        logger.info("Was invoked method for find students by age between");
        logger.warn("Finding students by age between min=" + min + " and max=" + max);
        return studentRepository.findByAgeBetween(min, max);
    }

    public void deleteStudent(Long id) {
        logger.info("Was invoked method for delete student");
        logger.debug("Deleting student with id: " + id);
        if (!studentRepository.existsById(id)) {
            logger.error("No student with id=" + id);
        }
        studentRepository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        logger.info("Was invoked method for get all students");
        return studentRepository.findAll();
    }

    public Collection<Student> findStudentsByAge(int age) {
        logger.info("Was invoked method for find students by age");
        logger.debug("Finding students by age: " + age);
        if (age < 0) {
            logger.warn("Age value is negative: " + age);
        }
        return studentRepository.findByAge(age);
    }
}