package ru.site.datasource.service;

import org.springframework.stereotype.Service;
import ru.site.datasource.model.Payment;

@Service
public interface PaymentRepositotyService {
  Payment savePayment(Payment payment);
}
