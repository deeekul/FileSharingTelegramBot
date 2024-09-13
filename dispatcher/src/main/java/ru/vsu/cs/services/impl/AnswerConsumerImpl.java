package ru.vsu.cs.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.vsu.cs.controllers.UpdateProcessor;
import ru.vsu.cs.services.AnswerConsumer;

@RequiredArgsConstructor
@Service
public class AnswerConsumerImpl implements AnswerConsumer {

    private final UpdateProcessor updateProcessor;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.answer-message}")
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
