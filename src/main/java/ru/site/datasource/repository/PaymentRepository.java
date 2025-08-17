package ru.site.datasource.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.site.datasource.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {}
