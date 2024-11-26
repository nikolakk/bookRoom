CREATE TABLE MEETING_ROOM (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(255) NOT NULL
);

CREATE TABLE ROOM_BOOKING (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              date DATE NOT NULL,
                              time_from TIME(6) NOT NULL,
                              time_to TIME(6) NOT NULL,
                              room_id BIGINT NOT NULL,
                              employee_email VARCHAR(255) NOT NULL,
                              CONSTRAINT fk_room FOREIGN KEY (room_id) REFERENCES MEETING_ROOM(id)
);
