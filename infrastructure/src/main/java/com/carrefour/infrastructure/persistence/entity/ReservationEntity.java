package com.carrefour.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("reservation")
public class ReservationEntity {

    @Id
    private Long id;

    @Column("customer_id")
    private String customerId;
    @Column("timeslot_id")
    private Long timeSlotId;
    @Column("created_at")
    private LocalDateTime createdAt;
}
