package com.amce.room.booking.repository.impl;

import com.amce.room.booking.model.Booking;
import com.amce.room.booking.model.MeetingRoom;
import com.amce.room.booking.repository.BookingRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public class BookingRepositoryCustomImpl implements BookingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Booking> findByRoomAndDate(MeetingRoom room, LocalDate date) {
        return entityManager.createQuery(
                        "SELECT b FROM Booking b WHERE b.room = :room AND b.date = :date ", Booking.class)
                .setParameter("room", room)
                .setParameter("date", date)
                .getResultList();
    }

    @Override
    public List<Booking> findOverlappingBookings(MeetingRoom room, LocalDate date, LocalTime timeFrom, LocalTime timeTo) {
        return entityManager.createQuery(
                        "SELECT b FROM Booking b WHERE b.room = :room AND b.date = :date " +
                                "AND ((b.timeFrom < :timeTo AND b.timeTo > :timeFrom))", Booking.class)
                .setParameter("room", room)
                .setParameter("date", date)
                .setParameter("timeTo", timeTo)
                .setParameter("timeFrom", timeFrom)
                .getResultList();
    }

}