import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Student;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void createStudent() {
        Student student = new Student();
        student.setName("Harry");
        student.setAge(15);

        ResponseEntity<Long> response = restTemplate.postForEntity(
                url("/student"),
                student,
                Long.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getStudent() {
        Student student = new Student();
        student.setName("Hermione");
        student.setAge(15);
        Long id = restTemplate.postForEntity(url("/student"), student, Long.class).getBody();

        ResponseEntity<Student> response = restTemplate.getForEntity(
                url("/student/" + id),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hermione");
    }

    @Test
    void updateStudent() {
        Student student = new Student();
        student.setName("Ron");
        student.setAge(14);
        Long id = restTemplate.postForEntity(url("/student"), student, Long.class).getBody();

        Student updated = new Student();
        updated.setName("Ron Weasley");
        updated.setAge(15);

        restTemplate.put(url("/student/" + id), updated);

        ResponseEntity<Student> response = restTemplate.getForEntity(
                url("/student/" + id),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Ron Weasley");
    }

    @Test
    void deleteStudent() {
        Student student = new Student();
        student.setName("Draco");
        student.setAge(16);
        Long id = restTemplate.postForEntity(url("/student"), student, Long.class).getBody();

        restTemplate.delete(url("/student/" + id));

        ResponseEntity<Student> response = restTemplate.getForEntity(
                url("/student/" + id),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void filterStudentsByAge() {
        Student student = new Student();
        student.setName("Neville");
        student.setAge(13);
        restTemplate.postForEntity(url("/student"), student, Long.class);

        ResponseEntity<Student[]> response = restTemplate.getForEntity(
                url("/student/filter?age=13"),
                Student[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void getStudentNotFound() {
        ResponseEntity<Student> response = restTemplate.getForEntity(
                url("/student/999999"),
                Student.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}