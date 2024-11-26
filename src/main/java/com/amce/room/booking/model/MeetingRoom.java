package com.amce.room.booking.model;

import jakarta.persistence.*;

@Entity
@Table(name = "MEETING_ROOM")
public class MeetingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary key

    @Column(nullable = false, unique = true)
    private String name; // Name of the meeting room

    public MeetingRoom(Long id) {
        this.id = id;
    }

    public MeetingRoom() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}