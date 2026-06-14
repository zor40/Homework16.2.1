package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hogwarts.school.service.SumService;

@RestController
public class InfoController {

    private final SumService sumService;

    public InfoController(SumService sumService) {
        this.sumService = sumService;
    }

    @Value("${server.port}")
    private int port;

    @GetMapping("/port")
    public int getPort() {
        return port;
    }

    @GetMapping("/sum")
    public int getSum() {
        return sumService.calculateSum();
    }
}