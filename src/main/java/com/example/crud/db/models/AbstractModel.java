package com.example.crud.db.models;

import com.example.crud.util.SerialVersionUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.eclipse.persistence.annotations.*;
import org.eclipse.persistence.descriptors.changetracking.ChangeTracker;
import org.eclipse.persistence.sessions.Session;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Date;

import static org.eclipse.persistence.annotations.ChangeTrackingType.AUTO;

@EqualsAndHashCode
@ChangeTracking(AUTO)                                  // uses the persistence property change listeners
/* NOTE: @Cache is ignored on mapped superclasses (@MappedSuperclass), this is here purely for reference sake.
@Cache(type = CacheType.SOFT_WEAK                      // let the GC remove cached data under memory pressure
      ,expiryTimeOfDay=@TimeOfDay(hour=1)              // cached data will expire after 1hr even if there is enough memory to keep the data around longer
      ,disableHits=false                               // use the cache (when true the cache is only used for identity)
      ,isolation=ISOLATED                              // cache within UnitOfWork (db/txn), or ".SHARED" for Session scope
      ,alwaysRefresh=true                              // update cache on reads...
      ,refreshOnlyIfNewer=true                         // only when read value is newer than one in cache
      ,databaseChangeNotificationType=INVALIDATE       // when DCN is enabled, invalidate cached objects when DB says they've mutated
      ,coordinationType=SEND_NEW_OBJECTS_WITH_CHANGES) //.INVALIDATE_CHANGED_OBJECTS */
@MappedSuperclass
public abstract class AbstractModel<T extends Serializable> implements Model<T>, ChangeTracker {
    private static final long serialVersionUID = SerialVersionUID.compute(AbstractModel.class);

    // Instance variables *not* mapped to database fields must be marked as @Transient
    @Transient private PropertyChangeListener listener;

    @Getter @CreatedDate @Temporal(TemporalType.TIMESTAMP) @Column(name="CREATED")       Date createdDate;
    @Getter @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) @Column(name="MODIFIED") Date modifiedDate;

    // OPTIMISTIC CONCURRENCY CONTROL
    @Version @Column(name="VERSION") private long version;

    // TODO(gburd): what must these two methods do?
    public PropertyChangeListener _persistence_getPropertyChangeListener() {
        return listener;
    }

    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener) {
        this.listener = listener;
    }
}
