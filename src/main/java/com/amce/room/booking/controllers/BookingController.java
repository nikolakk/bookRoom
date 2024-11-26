package com.amce.room.booking.controllers;

import com.amce.room.booking.exceptions.BookingNotFoundException;
import com.amce.room.booking.exceptions.GenericBookingException;
import com.amce.room.booking.model.Booking;
import com.amce.room.booking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/rooms/{roomId}/bookings")
    public List<BookingResponse> getBookings(@PathVariable Long roomId, @RequestParam LocalDate date) {
        return bookingService.getBookings(roomId, date);
    }

    @PostMapping("/bookings")
    public ResponseEntity<Object> createBooking(@RequestBody BookingRequest request) {
        try {
            // Call the service to create a new booking
            BookingResponse createdBooking = bookingService.createBooking(request);

            // Return 201 Created with a success message
            return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
        } catch (BookingNotFoundException e) {
            // Return 400 Bad Request if validation fails
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/booking/{id}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            bookingService.cancelBooking(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (BookingNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (GenericBookingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
