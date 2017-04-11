package com.example.crud.db.sequence;

import com.example.crud.crypto.Randomness;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;

import java.util.Vector;

public class PSRNSequence extends Sequence {

    public PSRNSequence(String name) {
        super(name);
    }

    @Override
    public Object getGeneratedValue(Accessor accessor, AbstractSession writeSession, String seqName) {
        return Randomness.randomHexStringSecure(36);
    }

    @Override
    public Vector getGeneratedVector(Accessor accessor, AbstractSession writeSession, String seqName, int size) {
        return null;
    }

    @Override
    public void onConnect() { }

    @Override
    public void onDisconnect() { }

    @Override
    public boolean shouldAcquireValueAfterInsert() {
        return false;
    }

    public boolean shouldAlwaysOverrideExistingValue(String seqName, Object existingValue) {
        return ((String) existingValue).isEmpty();
    }

    @Override
    public boolean shouldUseTransaction() {
        return false;
    }

    @Override
    public boolean shouldUsePreallocation() {
        return false;
    }

}
