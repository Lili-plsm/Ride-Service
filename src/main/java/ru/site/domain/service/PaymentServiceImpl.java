package ru.site.domain.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.site.datasource.enums.RideStatus;
import ru.site.datasource.model.Payment;
import ru.site.datasource.service.PaymentRepositotyService;
import ru.site.dto.event.NewRideStatusEvent;
import ru.site.dto.event.PaymentEvent;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepositotyService paymentRepositoryService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentServiceImpl(KafkaTemplate<String, String> kafkaTemplate,
                              PaymentRepositotyService paymentRepositoryService,
                              ObjectMapper objectMapper) {
        this.paymentRepositoryService = paymentRepositoryService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payment", groupId = "driver-service")
    public void processPaymentEvent(String message) throws JsonProcessingException {
        System.out.println("Получено сообщение из топика 'payment': " + message);

        PaymentEvent paymentEvent = objectMapper.readValue(message, PaymentEvent.class);
        Long rideId = paymentEvent.getRideId();
        Long clientId = paymentEvent.getClientId();

        NewRideStatusEvent rideStatusEvent = NewRideStatusEvent.builder()
                                                 .rideId(rideId)
                                                 .rideStatus(RideStatus.IN_PROGRESS)
                                                 .build();

        Payment payment = new Payment();
        payment.setAmount(paymentEvent.getAmount());
        payment.setRideId(rideId);
        payment.setClientId(clientId);
        paymentRepositoryService.savePayment(payment);

        String jsonResponse = objectMapper.writeValueAsString(rideStatusEvent);
        kafkaTemplate.send("ride-status", jsonResponse);
    }
}
