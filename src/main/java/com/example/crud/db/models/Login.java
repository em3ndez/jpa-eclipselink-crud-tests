package com.example.crud.db.models;

import com.example.crud.crypto.Password;
import com.example.crud.util.SerialVersionUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.CacheType;
import org.eclipse.persistence.annotations.TimeOfDay;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;

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
public @Data @Entity class Login extends AbstractModel<Long> {
    private static final long serialVersionUID = SerialVersionUID.compute(AbstractModel.class);

    // PRIMARY KEY
    @Id @Column @GeneratedValue(generator = "flake-seq")
    private Long id;

    // FIELDS
    private String username;
    private String password;

    public void setPassword(String password) {
        try { password = Password.getSaltedHash(password); }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean check(String password) {
        try { return Password.check(password, this.password); }
        catch (Exception e) {
            return false;
        }
    }
}
