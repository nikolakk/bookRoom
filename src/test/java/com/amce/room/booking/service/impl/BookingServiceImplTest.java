package com.amce.room.booking.service.impl;

import com.amce.room.booking.controllers.BookingRequest;
import com.amce.room.booking.controllers.BookingResponse;
import com.amce.room.booking.exceptions.BookingNotFoundException;
import com.amce.room.booking.exceptions.GenericBookingException;
import com.amce.room.booking.model.Booking;
import com.amce.room.booking.model.MeetingRoom;
import com.amce.room.booking.repository.BookingRepository;
import com.amce.room.booking.repository.MeetingRoomRepository;
import com.amce.room.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService; // Service under test
    @Mock
    private MeetingRoomRepository meetingRoomRepository; // Mocked dependency
    @Mock
    private BookingRepository bookingRepository; // Mocked dependency

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void createBooking_Successful() {
        // Arrange
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(1L);
        bookingRequest.setEmployeeEmail("test@acme.com");
        bookingRequest.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingRequest.setTimeFrom(LocalTime.of(9, 0));
        bookingRequest.setTimeTo(LocalTime.of(10, 0));

        MeetingRoom meetingRoom = getDefaultMeetingRoom();
        when(meetingRoomRepository.findById(any())).thenReturn(Optional.of(meetingRoom));
        when(bookingRepository.findOverlappingBookings(
                meetingRoom,
                bookingRequest.getBookingDate(),
                bookingRequest.getTimeFrom(),
                bookingRequest.getTimeTo()
        )).thenReturn(Collections.emptyList()); // No overlapping bookings

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setRoom(meetingRoom);
        savedBooking.setEmployeeEmail(bookingRequest.getEmployeeEmail());
        savedBooking.setDate(bookingRequest.getBookingDate());
        savedBooking.setTimeFrom(bookingRequest.getTimeFrom());
        savedBooking.setTimeTo(bookingRequest.getTimeTo());

        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking); // Mock save

        // Act
        BookingResponse result = bookingService.createBooking(bookingRequest);

        // Assert
        assertNotNull(result);
        assertEquals(savedBooking.getDate(), result.getBookingDate());
        assertEquals(savedBooking.getRoom().getName(), result.getRoom());
        assertEquals(savedBooking.getEmployeeEmail(), result.getEmail());
        assertEquals(savedBooking.getTimeFrom(), result.getTimeFrom());
        assertEquals(savedBooking.getTimeTo(), result.getTimeTo());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }


    @Test
    void createBooking_InvalidDuration_ThrowsException() {
        // Arrange
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(1L);
        bookingRequest.setEmployeeEmail("test@acme.com");
        bookingRequest.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingRequest.setTimeFrom(LocalTime.of(9, 0));
        bookingRequest.setTimeTo(LocalTime.of(9, 30)); // Less than 1 hour

        MeetingRoom meetingRoom = getDefaultMeetingRoom();
        when(meetingRoomRepository.findById(any())).thenReturn(Optional.of(meetingRoom));

        // Act & Assert
        GenericBookingException exception = assertThrows(
                GenericBookingException.class,
                () -> bookingService.createBooking(bookingRequest)
        );

        assertEquals("Booking duration must be at least 1 hour and in multiples of 1 hour.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_OverlappingBookings_ThrowsException() {
        // Arrange
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(1L);
        bookingRequest.setEmployeeEmail("test@acme.com");
        bookingRequest.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingRequest.setTimeFrom(LocalTime.of(9, 0));
        bookingRequest.setTimeTo(LocalTime.of(10, 0));

        Booking existingBooking = getDefaultBooking();

        MeetingRoom meetingRoom = getDefaultMeetingRoom();
        when(meetingRoomRepository.findById(any())).thenReturn(Optional.of(meetingRoom));

        when(bookingRepository.findOverlappingBookings(
                meetingRoom,
                bookingRequest.getBookingDate(),
                bookingRequest.getTimeFrom(),
                bookingRequest.getTimeTo()
        )).thenReturn(Collections.singletonList(existingBooking)); // Mock overlapping booking

        // Act & Assert
        GenericBookingException exception = assertThrows(
                GenericBookingException.class,
                () -> bookingService.createBooking(bookingRequest)
        );

        assertEquals("The room is already booked for the given time slot.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_InvalidTimeRange_ThrowsException() {
        // Arrange
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(1L);
        bookingRequest.setEmployeeEmail("test@acme.com");
        bookingRequest.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingRequest.setTimeFrom(LocalTime.of(10, 0));
        bookingRequest.setTimeTo(LocalTime.of(9, 0)); // Invalid time range

        MeetingRoom meetingRoom = getDefaultMeetingRoom();
        when(meetingRoomRepository.findById(any())).thenReturn(Optional.of(meetingRoom));
        when( bookingRepository.findOverlappingBookings(any(), any(), any(), any())).thenReturn(new ArrayList<>());

        // Act & Assert
        GenericBookingException exception = assertThrows(
                GenericBookingException.class,
                () -> bookingService.createBooking(bookingRequest)
        );

        assertEquals("Booking duration must be at least 1 hour and in multiples of 1 hour.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_DurationNotMultipleOfHour_ThrowsException() {
        // Arrange
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(1L);
        bookingRequest.setEmployeeEmail("test@acme.com");
        bookingRequest.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingRequest.setTimeFrom(LocalTime.of(9, 0));
        bookingRequest.setTimeTo(LocalTime.of(9, 45)); // Not a full hour

        MeetingRoom meetingRoom = getDefaultMeetingRoom();
        when(meetingRoomRepository.findById(any())).thenReturn(Optional.of(meetingRoom));

        // Act & Assert
        GenericBookingException exception = assertThrows(
                GenericBookingException.class,
                () -> bookingService.createBooking(bookingRequest)
        );

        assertEquals("Booking duration must be at least 1 hour and in multiples of 1 hour.", exception.getMessage());
    }


    @Test
    void createBooking_RoomUnavailable_ThrowsException() {
        // Arrange
        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setRoomId(2L); // Non-existent room
        bookingRequest.setEmployeeEmail("test@acme.com");
        bookingRequest.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingRequest.setTimeFrom(LocalTime.of(9, 0));
        bookingRequest.setTimeTo(LocalTime.of(10, 0));

        when(meetingRoomRepository.findById(any())).thenReturn(Optional.empty());

        // Act & Assert
        BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.createBooking(bookingRequest)
        );

        assertEquals("Meeting room not found.", exception.getMessage());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getBookings_ReturnsAllBookings() {
        // Arrange
        List<Booking> mockBookings = getBookingList(); // Helper method
        when(bookingRepository.findByRoomAndDate(any(), any())).thenReturn(mockBookings);
        MeetingRoom meetingRoom = getDefaultMeetingRoom();
        when(meetingRoomRepository.findById(any())).thenReturn(Optional.of(meetingRoom));

        LocalDate bookingDate = LocalDate.of(2024, 11, 27);
        Long roomId = 1L;
        // Act
        List<BookingResponse> result = bookingService.getBookings(roomId, bookingDate);

        // Assert
        assertNotNull(result);
        assertEquals(mockBookings.size(), result.size());
        assertEquals(mockBookings.get(0).getEmployeeEmail(), result.get(0).getEmail());
        assertEquals(mockBookings.get(0).getRoom().getName(), result.get(0).getRoom());

        verify(meetingRoomRepository, times(1)).findById(any());

    }

    @Test
    void getBookings_NoBookings_ReturnsEmptyList() {
        // Arrange
        MeetingRoom meetingRoom = getDefaultMeetingRoom();
        when(meetingRoomRepository.findById(any())).thenReturn(Optional.of(meetingRoom));
        when(bookingRepository.findByRoomAndDate(any(), any())).thenReturn(new ArrayList<>());


        LocalDate bookingDate = LocalDate.of(2024, 11, 27);
        Long roomId = 1L;
        // Act
        List<BookingResponse> result = bookingService.getBookings(roomId, bookingDate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(meetingRoomRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).findByRoomAndDate(any(), any());

    }


    @Test
    void cancelBooking_Successful() {
        // Arrange
        Long bookingId = 1L;
        Booking booking =getDefaultBooking();
        booking.setId(bookingId);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(getDefaultBooking()));

        // Act
        bookingService.cancelBooking(bookingId);

        // Assert
        verify(bookingRepository, times(1)).deleteById(any());
    }

    @Test
    void cancelBooking_BookingNotFound_ThrowsException() {
        // Arrange
        Long bookingId = 1L;
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        // Act & Assert
        BookingNotFoundException exception = assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.cancelBooking(bookingId)
        );

        assertEquals("Booking with ID " + bookingId + " not found.", exception.getMessage());
        verify(bookingRepository, never()).delete(any(Booking.class));
    }

    private static MeetingRoom getDefaultMeetingRoom(){
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setId(1L);
        meetingRoom.setName("My meeting room");
        return meetingRoom;
    }


    private List<Booking> getBookingList(){
        List<Booking> bookings = new ArrayList<>();

        Booking booking = getDefaultBooking();

        bookings.add(booking);

        return bookings;
    }

    private static Booking getDefaultBooking() {
        Booking booking = new Booking();

        booking.setRoom(getDefaultMeetingRoom());
        booking.setDate(LocalDate.of(2024, 11, 27));
        booking.setTimeFrom(LocalTime.of(9, 30));
        booking.setTimeTo(LocalTime.of(10, 30));
        return booking;
    }
}