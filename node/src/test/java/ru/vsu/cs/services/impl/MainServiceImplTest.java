package ru.vsu.cs.services.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vsu.cs.entities.RawData;
import ru.vsu.cs.repositories.RawDataRepository;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
class MainServiceImplTest {

    @Autowired
    private RawDataRepository rawDataRepository;

    @Test
    public void testSaveRawData() {
        Update update = new Update();
        Message message = new Message();
        message.setText("new message");
        update.setMessage(message);

        RawData rawData = RawData.builder()
                        .event(update)
                        .build();

        Set<RawData> testData = new HashSet<>();
        testData.add(rawData);
        rawDataRepository.save(rawData);

        Assert.isTrue(testData.contains(rawData), "Entity not found in the set");
    }
}