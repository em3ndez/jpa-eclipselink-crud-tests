package com.example.crud.db.monitoring;

import org.eclipse.persistence.internal.sessions.IdentityMapAccessor;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ServerSession;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EclipseLinkCache {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("default");
    EntityManager em = emf.createEntityManager();
    Session session = em.unwrap(Session.class);
    JpaEntityManager jem = em.unwrap(JpaEntityManager.class);
    UnitOfWorkImpl ouw = jem.unwrap(UnitOfWorkImpl.class);
    ServerSession ss = jem.unwrap(ServerSession.class);
    IdentityMapAccessor ima = (IdentityMapAccessor) ss.getIdentityMapAccessor();


    /**
     * long count = countCachedEntitiesL1(clazz);
     */
    public long countCachedEntitiesL1(Class clazz) {
        return ouw.getCloneMapping().keySet().stream()
                .filter(entity -> entity.getClass().equals(clazz))
                .count();
    }

    /**
     * int count = countCachedEntitiesL2(clazz);
     */
    public int countCachedEntitiesL2(Class clazz) {
        return ima.getIdentityMap(clazz).getSize();
    }

}
