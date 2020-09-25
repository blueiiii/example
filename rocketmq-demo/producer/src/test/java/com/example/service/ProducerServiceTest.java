package com.example.service;

import com.example.App;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class ProducerServiceTest {

    @Autowired
    private ProducerService producerService;

    @Test
    public void contextLoads() {
        boolean result = producerService.send("demo", "TAG-A", "Hello RocketMQ");
        assertTrue(result);
    }

}