package com.amce.room.booking.service.impl;

import com.amce.room.booking.controllers.BookingRequest;
import com.amce.room.booking.controllers.BookingResponse;
import com.amce.room.booking.converters.BookingConverter;
import com.amce.room.booking.exceptions.BookingNotFoundException;
import com.amce.room.booking.exceptions.GenericBookingException;
import com.amce.room.booking.model.Booking;
import com.amce.room.booking.model.MeetingRoom;
import com.amce.room.booking.repository.BookingRepository;
import com.amce.room.booking.repository.MeetingRoomRepository;
import com.amce.room.booking.service.BookingService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;

    private final MeetingRoomRepository meetingRoomRepository;


    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, MeetingRoomRepository meetingRoomRepository) {
        this.bookingRepository = bookingRepository;
        this.meetingRoomRepository = meetingRoomRepository;
    }

    @Override
    public List<BookingResponse> getBookings(Long roomId, LocalDate date) {
        MeetingRoom room = meetingRoomRepository.findById(roomId)
                .orElseThrow(() -> new BookingNotFoundException("Meeting room not found"));
        List<Booking> bookings = bookingRepository.findByRoomAndDate(room, date);

        return bookings.stream().map(BookingConverter::covertToBookingResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {

        MeetingRoom room = meetingRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new BookingNotFoundException("Meeting room not found."));
        Booking booking = BookingConverter.covertToBooking(request, room);

        // Check if the booking overlaps with an existing one
        LocalTime timeTo = booking.getTimeTo();
        LocalTime timeFrom = booking.getTimeFrom();

        LocalDate bookingDate = booking.getDate();
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                room,
                bookingDate,
                timeFrom,
                timeTo
        );
        if (!overlappingBookings.isEmpty()) {
            throw new GenericBookingException("The room is already booked for the given time slot.");
        }

        // Check if booking duration is at least 1 hour
        long bookingDuration = timeTo.toSecondOfDay() - timeFrom.toSecondOfDay();
        if (bookingDuration < 3600) {
            throw new GenericBookingException("Booking duration must be at least 1 hour and in multiples of 1 hour.");
        }

        log.info("Saving booking: {}", booking);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Saved booking: {}", savedBooking);

        return BookingConverter.covertToBookingResponse(savedBooking);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);

        if (!optionalBooking.isPresent()) {
            throw new BookingNotFoundException("Booking with ID " + bookingId + " not found.");
        }

        Booking booking = optionalBooking.get();

        // Ensure the booking is in the future
        if (booking.getDate().isBefore(LocalDate.now())) {
            throw new GenericBookingException("Cannot cancel a past booking.");
        }

        bookingRepository.deleteById(bookingId);
    }
}
