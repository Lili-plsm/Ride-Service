package ru.site.datasource.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.site.datasource.enums.DriverStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String carModel;
    private String carNumber;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    private Double rating;

    @Column()
    private Double latitude;

    @Column()
    private Double longitude;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
