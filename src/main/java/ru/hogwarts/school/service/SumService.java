package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.stream.IntStream;

@Service
public class SumService {

    private static final Logger logger = LoggerFactory.getLogger(SumService.class);

    public int calculateSum() {
        logger.info("Was invoked method for calculate sum");
        return IntStream.rangeClosed(1, 1_000_000)
                .parallel()               // или parallelStream()
                .reduce(0, (a, b) -> a + b);
    }
}