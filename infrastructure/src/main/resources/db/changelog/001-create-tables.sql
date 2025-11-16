CREATE TABLE time_slot (
                           id BIGSERIAL PRIMARY KEY,

                           delivery_mode VARCHAR(50) NOT NULL,

                           date DATE NOT NULL,
                           start_time TIME NOT NULL,
                           end_time TIME NOT NULL,

                           capacity INT NOT NULL CHECK (capacity >= 0),

    -- Ensure valid time interval
                           CONSTRAINT time_slot_valid_interval CHECK (end_time > start_time)
);

-- Index for queries based on delivery mode and date
CREATE INDEX idx_time_slot_mode_date
    ON time_slot (delivery_mode, date);

CREATE TABLE reservation (
                             id BIGSERIAL PRIMARY KEY,

                             customer_id VARCHAR(100) NOT NULL,

                             timeslot_id BIGINT NOT NULL
                                 REFERENCES time_slot(id)
                                     ON DELETE CASCADE,

                             created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Prevent duplicate reservations for the same slot by same user
                             CONSTRAINT uq_reservation_customer_timeslot UNIQUE (customer_id, timeslot_id)
);

-- Index for queries checking existence: existsByCustomerIdAndTimeSlotId()
CREATE INDEX idx_reservation_customer_timeslot
    ON reservation (customer_id, timeslot_id);

