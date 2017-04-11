package com.example.crud.db.models;

import com.example.crud.util.SerialVersionUID;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.TimeOfDay;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.io.Serializable;

import javax.persistence.*;

import static org.eclipse.persistence.annotations.CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES;
import static org.eclipse.persistence.annotations.DatabaseChangeNotificationType.INVALIDATE;
import static org.eclipse.persistence.config.CacheIsolationType.ISOLATED;

@ToString(exclude="owner") @EqualsAndHashCode(exclude="owner", callSuper=true)
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
@Table(name = "PHONE")
@IdClass(PhoneNumber.ID.class)
@Cache(type = CacheType.SOFT_WEAK
      ,expiryTimeOfDay=@TimeOfDay(hour=1)
      ,disableHits=false
      ,isolation=ISOLATED
      ,alwaysRefresh=true
      ,refreshOnlyIfNewer=true
      ,databaseChangeNotificationType=INVALIDATE
      ,coordinationType=SEND_NEW_OBJECTS_WITH_CHANGES)
public @Data @Entity class PhoneNumber extends AbstractModel<Long> {
    private static final long serialVersionUID = SerialVersionUID.compute(PhoneNumber.class);

    @Id @Column(name = "EMP_ID", updatable = false, insertable = false)
    private Long id;
    @Id @Column(updatable = false)
    private String type;

    private String number;

    @ManyToOne @JoinColumn(name = "EMP_ID")
    @JsonBackReference
    private Employee owner;

    public PhoneNumber() {
    }

    public PhoneNumber(String type, String number) {
        this();
        setType(type);
        setNumber(number);
    }

    protected void setOwner(Employee employee) {
        this.owner = employee;
        if (employee != null) {
            this.id = employee.getId();
        }
    }

    /**
     * Inner-class to manage the compound primary key for phone numbers.
     */
    public @Data static class ID implements Serializable {
        private static final long serialVersionUID = SerialVersionUID.compute(PhoneNumber.ID.class);

        public Long id;
        public String type;
    }

}
