package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);
    Collection<Student> findByAgeBetween(int min, int max);

    @Query("SELECT COUNT(*) FROM student")
    long getCountOfStudents();

    @Query("SELECT AVG(age) FROM student")
    Double getAverageAge();

    @Query("SELECT * FROM students ORDER BY id DESC LIMIT 5")
    List<Student> getLastFiveStudents();
}