package com.amce.room.booking.repository;

import com.amce.room.booking.model.Booking;
import com.amce.room.booking.model.MeetingRoom;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface BookingRepositoryCustom {
    List<Booking> findOverlappingBookings(MeetingRoom room, LocalDate date, LocalTime timeFrom, LocalTime timeTo);

    List<Booking> findByRoomAndDate(MeetingRoom room, LocalDate date);

}