package com.amce.room.booking.controllers;

import com.amce.room.booking.exceptions.BookingNotFoundException;
import com.amce.room.booking.exceptions.GenericBookingException;
import com.amce.room.booking.model.Booking;
import com.amce.room.booking.model.MeetingRoom;
import com.amce.room.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    public void testGetBookings() throws Exception {

        Booking mockBooking = new Booking();
        mockBooking.setId(1L);
        MeetingRoom meetingRoom = new MeetingRoom(101L);
        meetingRoom.setName("meeting room 1");
        mockBooking.setRoom(meetingRoom);
        mockBooking.setDate(LocalDate.of(2024, 11, 27));
        mockBooking.setTimeFrom(LocalTime.of(9, 0));
        mockBooking.setTimeTo(LocalTime.of(11, 0));
        mockBooking.setEmployeeEmail("john.doe@example.com");

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingResponse.setRoom("meeting room 1");
        bookingResponse.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingResponse.setTimeFrom(LocalTime.of(9, 0));
        bookingResponse.setTimeTo(LocalTime.of(11, 0));
        bookingResponse.setEmail("john.doe@example.com");
        bookingResponse.setBookingId(1L);

        when(bookingService.getBookings(anyLong(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(bookingResponse));

        mockMvc.perform(get("/api/rooms/101/bookings?date=2024-11-27"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].room").value("meeting room 1"))
                .andExpect(jsonPath("$[0].bookingDate").value("2024-11-27"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].timeFrom").value("09:00"))
                .andExpect(jsonPath("$[0].timeTo").value("11:00"));

        verify(bookingService, times(1)).getBookings(anyLong(), any(LocalDate.class));
    }


    @Test
    public void testGetBookings_noResult() throws Exception {

        Booking mockBooking = new Booking();
        mockBooking.setId(1L);
        MeetingRoom meetingRoom = new MeetingRoom(101L);
        meetingRoom.setName("meeting room 1");
        mockBooking.setRoom(meetingRoom);
        mockBooking.setDate(LocalDate.of(2024, 11, 27));
        mockBooking.setTimeFrom(LocalTime.of(9, 0));
        mockBooking.setTimeTo(LocalTime.of(11, 0));
        mockBooking.setEmployeeEmail("john.doe@example.com");

        when(bookingService.getBookings(anyLong(), any(LocalDate.class))).thenThrow(BookingNotFoundException.class);

        mockMvc.perform(get("/api/rooms/101/bookings?date=2024-11-27"))
                .andExpect(status().isNoContent());

        verify(bookingService, times(1)).getBookings(anyLong(), any(LocalDate.class));
    }


    @Test
    public void testCreateBooking() throws Exception {
        Booking mockBooking = new Booking();
        mockBooking.setId(1L);
        MeetingRoom meetingRoom = new MeetingRoom(101L);
        mockBooking.setRoom(meetingRoom);
        mockBooking.setDate(LocalDate.of(2024, 11, 27));
        mockBooking.setTimeFrom(LocalTime.of(9, 0));
        mockBooking.setTimeTo(LocalTime.of(11, 0));
        mockBooking.setEmployeeEmail("john.doe@example.com");

        BookingResponse bookingResponse = new BookingResponse();
        bookingResponse.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingResponse.setRoom("meeting room 1");
        bookingResponse.setBookingDate(LocalDate.of(2024, 11, 27));
        bookingResponse.setTimeFrom(LocalTime.of(9, 0));
        bookingResponse.setTimeTo(LocalTime.of(11, 0));
        bookingResponse.setEmail("john.doe@example.com");
        bookingResponse.setBookingId(1L);

        when(bookingService.createBooking(any())).thenReturn(bookingResponse);

        String bookingRequestJson = "{\"roomId\":101,\"date\":\"2024-11-27\"}";

        mockMvc.perform(post("/api/create/booking")
                        .contentType("application/json")
                        .content(bookingRequestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.room").value("meeting room 1"))
                .andExpect(jsonPath("$.bookingDate").value("2024-11-27"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.timeFrom").value("09:00"))
                .andExpect(jsonPath("$.timeTo").value("11:00"));

        verify(bookingService, times(1)).createBooking(any());
    }


    @Test
    public void testCreateBooking_GenericBookingException() throws Exception {
        Booking mockBooking = new Booking();
        mockBooking.setId(1L);
        MeetingRoom meetingRoom = new MeetingRoom(101L);
        mockBooking.setRoom(meetingRoom);
        mockBooking.setDate(LocalDate.of(2024, 11, 27));
        mockBooking.setTimeFrom(LocalTime.of(9, 0));
        mockBooking.setTimeTo(LocalTime.of(11, 0));
        mockBooking.setEmployeeEmail("john.doe@example.com");

        when(bookingService.createBooking(any())).thenThrow(GenericBookingException.class);

        String bookingRequestJson = "{\"roomId\":101,\"date\":\"2024-11-27\"}";

        mockMvc.perform(post("/api/create/booking")
                        .contentType("application/json")
                        .content(bookingRequestJson))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(1)).createBooking(any());
    }

    @Test
    public void testCreateBooking_BookingNotFoundException() throws Exception {
        Booking mockBooking = new Booking();
        mockBooking.setId(1L);
        MeetingRoom meetingRoom = new MeetingRoom(101L);
        mockBooking.setRoom(meetingRoom);
        mockBooking.setDate(LocalDate.of(2024, 11, 27));
        mockBooking.setTimeFrom(LocalTime.of(9, 0));
        mockBooking.setTimeTo(LocalTime.of(11, 0));
        mockBooking.setEmployeeEmail("john.doe@example.com");

        when(bookingService.createBooking(any())).thenThrow(BookingNotFoundException.class);

        String bookingRequestJson = "{\"roomId\":101,\"date\":\"2024-11-27\"}";

        mockMvc.perform(post("/api/create/booking")
                        .contentType("application/json")
                        .content(bookingRequestJson))
                .andExpect(status().isBadRequest());

        verify(bookingService, times(1)).createBooking(any());
    }
    @Test
    public void testCancelBooking_Success() throws Exception {
        doNothing().when(bookingService).cancelBooking(anyLong());

        mockMvc.perform(delete("/api/booking/1"))
                .andExpect(status().isOk());

        verify(bookingService, times(1)).cancelBooking(1L);
    }

    @Test
    public void testCancelBooking_NotFound() throws Exception {
        doThrow(new BookingNotFoundException("Booking not found")).when(bookingService).cancelBooking(anyLong());

        mockMvc.perform(delete("/api/booking/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Booking not found"));

        verify(bookingService).cancelBooking(1L);
    }

    @Test
    public void testCancelBooking_GenericError() throws Exception {
        doThrow(new GenericBookingException("Generic error")).when(bookingService).cancelBooking(anyLong());

        mockMvc.perform(delete("/api/booking/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Generic error"));

        verify(bookingService).cancelBooking(1L);
    }
}
