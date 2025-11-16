package com.carrefour.infrastructure.persistence.entity;

import com.carrefour.domain.model.DeliveryMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("time_slot")
public class TimeSlotEntity {

    @Id
    private Long id;

    @Column("delivery_mode")
    private DeliveryMode mode;
    @Column("date")
    private LocalDate date;
    @Column("start_time")
    private LocalTime startTime;
    @Column("end_time")
    private LocalTime endTime;
    @Column("capacity")
    private int capacity;
}
