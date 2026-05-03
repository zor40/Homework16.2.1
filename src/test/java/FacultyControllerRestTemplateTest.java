import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.hogwarts.school.model.Faculty;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Gryffindor");
        faculty.setColor("red");

        ResponseEntity<Long> response = restTemplate.postForEntity(
                url("/faculty"),
                faculty,
                Long.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Hufflepuff");
        faculty.setColor("yellow");
        Long id = restTemplate.postForEntity(url("/faculty"), faculty, Long.class).getBody();

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                url("/faculty/" + id),
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Hufflepuff");
    }

    @Test
    void updateFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Ravenclaw");
        faculty.setColor("blue");
        Long id = restTemplate.postForEntity(url("/faculty"), faculty, Long.class).getBody();

        Faculty updated = new Faculty();
        updated.setName("Ravenclaw Updated");
        updated.setColor("blue");

        restTemplate.put(url("/faculty/" + id), updated);

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                url("/faculty/" + id),
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Ravenclaw Updated");
    }

    @Test
    void deleteFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName("Slytherin");
        faculty.setColor("green");
        Long id = restTemplate.postForEntity(url("/faculty"), faculty, Long.class).getBody();

        restTemplate.delete(url("/faculty/" + id));

        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                url("/faculty/" + id),
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void filterFacultiesByColor() {
        Faculty faculty = new Faculty();
        faculty.setName("TestColor");
        faculty.setColor("silver");
        restTemplate.postForEntity(url("/faculty"), faculty, Long.class);

        ResponseEntity<Faculty[]> response = restTemplate.getForEntity(
                url("/faculty/filter?color=silver"),
                Faculty[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void getFacultyNotFound() {
        ResponseEntity<Faculty> response = restTemplate.getForEntity(
                url("/faculty/999999"),
                Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}