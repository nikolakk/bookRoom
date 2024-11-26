package com.amce.room.booking.repository;

import com.amce.room.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

}