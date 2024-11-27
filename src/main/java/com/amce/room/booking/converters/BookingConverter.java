package com.amce.room.booking.converters;

import com.amce.room.booking.controllers.BookingRequest;
import com.amce.room.booking.controllers.BookingResponse;
import com.amce.room.booking.model.Booking;
import com.amce.room.booking.model.MeetingRoom;

public class BookingConverter {
    public static BookingResponse covertToBookingResponse(Booking booking) {

        BookingResponse bookingResponse = new BookingResponse();
        if (booking != null) {
            bookingResponse.setEmail(booking.getEmployeeEmail());
            bookingResponse.setBookingDate(booking.getDate());
            bookingResponse.setRoom(booking.getRoom().getName());
            bookingResponse.setTimeFrom(booking.getTimeFrom());
            bookingResponse.setTimeTo(booking.getTimeTo());
            bookingResponse.setBookingId(booking.getId());
        }
        return bookingResponse;
    }


    public static Booking covertToBooking(BookingRequest bookingRequest, MeetingRoom room) {

        Booking booking = new Booking();
        if (bookingRequest != null) {
            booking.setEmployeeEmail(bookingRequest.getEmployeeEmail());
            booking.setDate(bookingRequest.getBookingDate());
            booking.setRoom(room);
            booking.setTimeFrom(bookingRequest.getTimeFrom());
            booking.setTimeTo(bookingRequest.getTimeTo());
        }
        return booking;
    }
}
