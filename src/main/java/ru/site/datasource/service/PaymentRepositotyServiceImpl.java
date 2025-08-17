package ru.site.datasource.service;

import org.springframework.stereotype.Service;
import ru.site.datasource.model.Payment;
import ru.site.datasource.repository.PaymentRepository;

@Service
public class PaymentRepositotyServiceImpl implements PaymentRepositotyService {

  private final PaymentRepository paymentRepository;

  public PaymentRepositotyServiceImpl(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
  }

  public Payment savePayment(Payment payment) {
    return paymentRepository.save(payment);
  }
}
