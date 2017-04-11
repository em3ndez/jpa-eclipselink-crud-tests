package com.example.crud.db.models;

import com.example.crud.util.SerialVersionUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.TimeOfDay;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static org.eclipse.persistence.annotations.CacheCoordinationType.SEND_NEW_OBJECTS_WITH_CHANGES;
import static org.eclipse.persistence.annotations.DatabaseChangeNotificationType.INVALIDATE;
import static org.eclipse.persistence.config.CacheIsolationType.ISOLATED;

@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper=true)
@Cache(type = CacheType.SOFT_WEAK
      ,expiryTimeOfDay=@TimeOfDay(hour=1)
      ,disableHits=false
      ,isolation=ISOLATED
      ,alwaysRefresh=true
      ,refreshOnlyIfNewer=true
      ,databaseChangeNotificationType=INVALIDATE
      ,coordinationType=SEND_NEW_OBJECTS_WITH_CHANGES)
@Table(name = "ADDRESS")
public @Data @Entity class Address extends AbstractModel<Long> {
    private static final long serialVersionUID = SerialVersionUID.compute(Address.class);

    // PRIMARY KEY
    @Id @Column @GeneratedValue(generator = "flake-seq")
    private Long id;

    // FIELDS
    private String city;
    private String country;
    @Basic(fetch=LAZY) private String province;
    private String postalCode;
    private String street;

    public Address() {
    }

    public Address(String city, String country, String province, String postalCode, String street) {
        this.city = city;
        this.country = country;
        this.province = province;
        this.postalCode = postalCode;
        this.street = street;
    }
}
