import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void getStudent() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setName("Harry");
        student.setAge(15);

        Mockito.when(studentService.getStudent(1L)).thenReturn(student);

        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Harry")))
                .andExpect(jsonPath("$.age", is(15)));
    }

    @Test
    void getStudentNotFound() throws Exception {
        Mockito.when(studentService.getStudent(999L)).thenReturn(null);

        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllStudents() throws Exception {
        Student s1 = new Student();
        s1.setId(1L);
        s1.setName("Harry");
        s1.setAge(15);

        Student s2 = new Student();
        s2.setId(2L);
        s2.setName("Hermione");
        s2.setAge(15);

        Mockito.when(studentService.getAllStudents()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/student"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void filterStudentsByAge() throws Exception {
        Student s = new Student();
        s.setId(1L);
        s.setName("Neville");
        s.setAge(13);

        Mockito.when(studentService.findStudentsByAge(13))
                .thenReturn(List.of(s));

        mockMvc.perform(get("/student/filter").param("age", "13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Neville")));
    }

    @Test
    void filterStudentsByAgeBetween() throws Exception {
        Student s = new Student();
        s.setId(1L);
        s.setName("Luna");
        s.setAge(14);

        Mockito.when(studentService.findByAgeBetween(13, 15))
                .thenReturn(List.of(s));

        mockMvc.perform(get("/student/filter-between")
                        .param("min", "13")
                        .param("max", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Luna")));
    }

    @Test
    void createStudent() throws Exception {
        Student s = new Student();
        s.setId(1L);
        s.setName("Harry");
        s.setAge(15);

        Mockito.when(studentService.createStudent(Mockito.any(Student.class)))
                .thenReturn(s);

        String json = """
                {
                  "name": "Harry",
                  "age": 15
                }
                """;

        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void updateStudent() throws Exception {
        Student s = new Student();
        s.setId(1L);
        s.setName("Harry");
        s.setAge(15);

        Mockito.when(studentService.updateStudent(Mockito.eq(1L), Mockito.any(Student.class)))
                .thenReturn(s);

        String json = """
                {
                  "name": "Harry Potter",
                  "age": 16
                }
                """;

        mockMvc.perform(put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteStudent() throws Exception {
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentFaculty() throws Exception {
        Faculty f = new Faculty();
        f.setId(1L);
        f.setName("Gryffindor");
        f.setColor("red");

        Student s = new Student();
        s.setId(1L);
        s.setName("Harry");
        s.setAge(15);
        s.setFaculty(f);

        Mockito.when(studentService.getStudent(1L)).thenReturn(s);

        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Gryffindor")));
    }
}