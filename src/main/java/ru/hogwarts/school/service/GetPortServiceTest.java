package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Profile("test")
@Primary //Или эта аннотация или бин этого класса в AppConfig. Совсем без того и другого не компилируется! Вместе оба - тоже хорошо. (?)
public class GetPortServiceTest implements GetPortService {
    @Value("${server.port}")
    private String serverPort;
    private Logger logger = LoggerFactory.getLogger(GetPortServiceTest.class);

    @PostConstruct
    public void testGetPort(){
        System.out.println("getPort() = " + getPort());
    }

    @Override
    public String getPort() {
        logger.info("Метод GetPortServiceTest прошёл");
        return serverPort;
    }
}