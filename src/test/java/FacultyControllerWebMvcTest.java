import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @Test
    void getFaculty() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Gryffindor");
        faculty.setColor("red");

        Mockito.when(facultyService.getFaculty(1L)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Gryffindor")))
                .andExpect(jsonPath("$.color", is("red")));
    }

    @Test
    void getFacultyNotFound() throws Exception {
        Mockito.when(facultyService.getFaculty(999L)).thenReturn(null);

        mockMvc.perform(get("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllFaculties() throws Exception {
        Faculty f1 = new Faculty();
        f1.setId(1L);
        f1.setName("Gryffindor");
        f1.setColor("red");

        Faculty f2 = new Faculty();
        f2.setId(2L);
        f2.setName("Slytherin");
        f2.setColor("green");

        Mockito.when(facultyService.getAllFaculties()).thenReturn(List.of(f1, f2));

        mockMvc.perform(get("/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void filterFacultiesByColor() throws Exception {
        Faculty f = new Faculty();
        f.setId(1L);
        f.setName("SilverFaculty");
        f.setColor("silver");

        Mockito.when(facultyService.findFacultiesByColor("silver"))
                .thenReturn(List.of(f));

        mockMvc.perform(get("/faculty/filter").param("color", "silver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("SilverFaculty")));
    }

    @Test
    void searchByNameOrColor() throws Exception {
        Faculty f = new Faculty();
        f.setId(1L);
        f.setName("Gryffindor");
        f.setColor("red");

        Mockito.when(facultyService.findByNameOrColor("gryff"))
                .thenReturn(List.of(f));

        mockMvc.perform(get("/faculty/search").param("text", "gryff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Gryffindor")));
    }

    @Test
    void createFaculty() throws Exception {
        Faculty f = new Faculty();
        f.setId(1L);
        f.setName("Gryffindor");
        f.setColor("red");

        Mockito.when(facultyService.createFaculty(Mockito.any(Faculty.class)))
                .thenReturn(f);

        String json = """
                {
                  "name": "Gryffindor",
                  "color": "red"
                }
                """;

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void updateFaculty() throws Exception {
        Faculty f = new Faculty();
        f.setId(1L);
        f.setName("Gryffindor");
        f.setColor("red");

        Mockito.when(facultyService.updateFaculty(Mockito.eq(1L), Mockito.any(Faculty.class)))
                .thenReturn(f);

        String json = """
                {
                  "name": "Gryffindor Updated",
                  "color": "red"
                }
                """;

        mockMvc.perform(put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteFaculty() throws Exception {
        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getFacultyStudents() throws Exception {
        Student s1 = new Student();
        s1.setId(1L);
        s1.setName("Harry");
        s1.setAge(15);

        Faculty f = new Faculty();
        f.setId(1L);
        f.setName("Gryffindor");
        f.setColor("red");
        f.setStudents(List.of(s1));

        Mockito.when(facultyService.getFaculty(1L)).thenReturn(f);

        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Harry")));
    }
}