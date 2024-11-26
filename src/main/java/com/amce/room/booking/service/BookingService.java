package com.amce.room.booking.service;

import com.amce.room.booking.controllers.BookingRequest;
import com.amce.room.booking.controllers.BookingResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface BookingService {

    List<BookingResponse> getBookings(Long roomId, LocalDate date);

    BookingResponse createBooking(BookingRequest bookingRequest);

    void cancelBooking(Long bookingId);
}
