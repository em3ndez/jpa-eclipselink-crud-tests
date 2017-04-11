package com.example.crud.db.sequence;

import com.example.crud.crypto.Randomness;
import com.github.rholder.fauxflake.DefaultIdGenerator;
import com.github.rholder.fauxflake.api.IdGenerator;
import com.github.rholder.fauxflake.provider.SystemTimeProvider;
import com.github.rholder.fauxflake.provider.twitter.SnowflakeEncodingProvider;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;

import java.util.Vector;

/**
 * FlakeSequence is a decentralized, k-ordered unique ID generator that produces 64bit integers (Long).
 *
 * This is configured to mimic Twitter's Snowflake pattern.  "k-ordered" means that they sort in timestamp
 * order based on when they were created within the level of precision available and of course with all the
 * standard NTP-based cavaets.
 */

public class FlakeSequence extends Sequence {
    IdGenerator idGenerator;

    public FlakeSequence(String name) {
        super(name);
        long mid = Randomness.randomIntSecure(1024); // TODO(gburd): use a hash of the hostname?
        idGenerator = new DefaultIdGenerator(new SystemTimeProvider(), new SnowflakeEncodingProvider(mid));
    }

    @Override
    public Object getGeneratedValue(Accessor accessor, AbstractSession writeSession, String seqName) {
        while (true) {
            try {
                return idGenerator.generateId(10).asLong();
            }
            catch (InterruptedException e) {
                // We waited more than 10ms to generate an Id, try again.  This could be due to NTP
                // drift, leap seconds, GC pause, who knows.
            }
        }
    }

    @Override
    public Vector getGeneratedVector(Accessor accessor, AbstractSession writeSession, String seqName, int size) {
        return null;
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void onDisconnect() {
    }

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
        // NOTE: never pre-allocate, that would defeat the time-ordered nature of these IDs
        return false;
    }

}
