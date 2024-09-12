package com.company.onboarding.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "MEETING", indexes = {
        @Index(name = "IDX_MEETING_USER", columnList = "USER_ID")
})
@Entity
public class Meeting {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @InstanceName
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull
    @Column(name = "START_DATE", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @NotNull
    @JoinColumn(name = "USER_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}