package com.example.crud.db.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Calendar;

import javax.persistence.Embeddable;
import javax.persistence.Temporal;

/**
 * Represents the period of time an employee has worked for the company. A null
 * endDate indicates that the employee is current.
 */
public @Data @Embeddable class EmploymentPeriod {

    @Temporal(TIMESTAMP) private Calendar startDate;
    @Temporal(TIMESTAMP) private Calendar endDate;

    public void setStartDate(int year, int month, int date) {
        getStartDate().set(year, month, date);
    }

    public void setEndDate(int year, int month, int date) {
        getEndDate().set(year, month, date);
    }
}
