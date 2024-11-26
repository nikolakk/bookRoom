package com.amce.room.booking.controllers;

import com.amce.room.booking.exceptions.BookingNotFoundException;
import com.amce.room.booking.model.Booking;
import com.amce.room.booking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBookings_Success() throws Exception {
        // Arrange
        Long roomId = 1L;
        LocalDate date = LocalDate.of(2024, 11, 27);
        BookingResponse mockResponse = new BookingResponse();
        mockResponse.setRoom("Room 1");
        mockResponse.setBookingDate(date);
        mockResponse.setEmail("test@acme.com");
        mockResponse.setTimeFrom(LocalTime.of(9, 0));
        mockResponse.setTimeTo(LocalTime.of(10, 0));

        when(bookingService.getBookings(roomId, date)).thenReturn(List.of(mockResponse));

        // Act & Assert
        mockMvc.perform(get("/api/rooms/{roomId}/bookings", roomId)
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].room").value("Room 1"))
                .andExpect(jsonPath("$[0].bookingDate").value(date.toString()))
                .andExpect(jsonPath("$[0].email").value("test@acme.com"));

        verify(bookingService, times(1)).getBookings(roomId, date);
    }

    @Test
    void getBookings_NoBookings() throws Exception {
        // Arrange
        Long roomId = 1L;
        LocalDate date = LocalDate.of(2024, 11, 27);
        when(bookingService.getBookings(roomId, date)).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/rooms/{roomId}/bookings", roomId)
                        .param("date", date.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(bookingService, times(1)).getBookings(roomId, date);
    }

    @Test
    void createBooking_Success() throws Exception {
        // Arrange
        BookingRequest request = new BookingRequest();
        request.setRoomId(1L);
        request.setEmployeeEmail("test@acme.com");
        request.setBookingDate(LocalDate.of(2024, 11, 27));
        request.setTimeFrom(LocalTime.of(9, 0));
        request.setTimeTo(LocalTime.of(10, 0));

        BookingResponse mockResponse = new BookingResponse();
        mockResponse.setRoom("Room 1");
        mockResponse.setBookingDate(request.getBookingDate());
        mockResponse.setEmail(request.getEmployeeEmail());
        mockResponse.setTimeFrom(request.getTimeFrom());
        mockResponse.setTimeTo(request.getTimeTo());

        when(bookingService.createBooking(any())).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "roomId": 1,
                      "employeeEmail": "test@acme.com",
                      "bookingDate": "2024-11-27",
                      "timeFrom": "09:00",
                      "timeTo": "10:00"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.room").value("Room 1"))
                .andExpect(jsonPath("$.bookingDate").value("2024-11-27"));

        verify(bookingService, times(1)).createBooking(any());
    }

    @Test
    void createBooking_ValidationFailure() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                      "roomId": 1
                    }
                    """)) // Missing fields
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).createBooking(any());
    }


    @Test
    void cancelBooking_Success() throws Exception {
        // Arrange
        Long bookingId = 1L;
        doNothing().when(bookingService).cancelBooking(bookingId);

        // Act & Assert
        mockMvc.perform(delete("/api/booking/{id}", bookingId))
                .andExpect(status().isNoContent());

        verify(bookingService, times(1)).cancelBooking(bookingId);
    }

    @Test
    void cancelBooking_NotFound() throws Exception {
        // Arrange
        Long bookingId = 1L;
        doThrow(new BookingNotFoundException("Booking not found"))
                .when(bookingService).cancelBooking(bookingId);

        // Act & Assert
        mockMvc.perform(delete("/api/booking/{id}", bookingId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Booking not found"));

        verify(bookingService, times(1)).cancelBooking(bookingId);
    }
}