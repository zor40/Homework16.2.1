package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
public class StudentPrintService {

    private final StudentRepository studentRepository;

    public StudentPrintService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    private synchronized void printName(String name) {
        System.out.println(name);
    }

    public void printParallel() {
        List<Student> students = studentRepository.findAll();
        if (students.size() < 6) {
            System.out.println("Not enough students");
            return;
        }

        System.out.println(students.get(0).getName());
        System.out.println(students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            System.out.println(students.get(2).getName());
            System.out.println(students.get(3).getName());
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            System.out.println(students.get(4).getName());
            System.out.println(students.get(5).getName());
        });
        thread2.start();
    }

    public void printSynchronized() {
        List<Student> students = studentRepository.findAll();
        if (students.size() < 6) {
            System.out.println("Not enough students");
            return;
        }

        printName(students.get(0).getName());
        printName(students.get(1).getName());

        Thread thread1 = new Thread(() -> {
            printName(students.get(2).getName());
            printName(students.get(3).getName());
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            printName(students.get(4).getName());
            printName(students.get(5).getName());
        });
        thread2.start();
    }
}