/*
 * Copyright (c) 2015, GoMint, BlackyPaw and geNAZt
 *
 * This code is licensed under the BSD license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.gomint.server.report;

import net.openhft.koloboke.collect.LongIterator;
import net.openhft.koloboke.collect.map.LongIntMap;
import net.openhft.koloboke.collect.map.ObjLongMap;
import net.openhft.koloboke.collect.map.ObjObjMap;
import net.openhft.koloboke.collect.map.hash.HashLongIntMaps;
import net.openhft.koloboke.collect.map.hash.HashObjLongMaps;
import net.openhft.koloboke.collect.map.hash.HashObjObjMaps;
import net.openhft.koloboke.collect.set.LongSet;
import net.openhft.koloboke.collect.set.hash.HashLongSets;
import net.openhft.koloboke.function.LongIntToIntFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * @author geNAZt
 * @version 1.0
 */
public class PerformanceReport {
    private static final Logger logger = LoggerFactory.getLogger( PerformanceReport.class );
    private final String startDate = new Date().toString();
    private final ReadWriteLock lock = new ReentrantReadWriteLock( true );
    private ObjObjMap<String, LongIntMap> performanceReport;

    private ThreadLocal<ObjLongMap<String>> threadMeasurements;

    public void startTiming( String name ) {
        // Init maps
        if ( performanceReport == null ) {
            performanceReport = HashObjObjMaps.newMutableMap();
        }

        if ( threadMeasurements == null ) {
            threadMeasurements = new ThreadLocal<>();
        }

        ObjLongMap<String> currentMeasurements = this.threadMeasurements.get();
        if ( currentMeasurements == null ) {
            currentMeasurements = HashObjLongMaps.newMutableMap();
            this.threadMeasurements.set( currentMeasurements );
        }

        currentMeasurements.put( name, System.nanoTime() );
    }

    public void stopTiming( String name ) {
        long diff = System.nanoTime() - this.threadMeasurements.get().removeAsLong( name );

        this.lock.writeLock().lock();
        try {
            this.performanceReport.computeIfAbsent( name, new Function<String, LongIntMap>() {
                @Override
                public LongIntMap apply( String s ) {
                    return HashLongIntMaps.newMutableMap();
                }
            } ).compute( diff, new LongIntToIntFunction() {
                @Override
                public int applyAsInt( long key, int value ) {
                    return value + 1;
                }
            } );
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void print() {
        long overallTime = getOverallTime( null );

        logger.info( "Performance report started at " + this.startDate );
        logger.info( fillUp( "Action", 50 ) + " | " + fillUp( "Amount", 6 ) + " | " + fillUp( "Average time (ns)", 18 ) + " | " + fillUp( "Max time (ns)", 18 ) + " | " + fillUp( "Overall time (ms)", 20 ) + " | " + fillUp( "Percentage", 12 ) );
        logger.info( "-----------------------------------------------------------------------------------------------------------------------------------------------" );

        this.lock.readLock().lock();
        try {
            for ( Map.Entry<String, LongIntMap> stringListEntry : performanceReport.entrySet() ) {
                long currentEventOverall = getOverallTime( stringListEntry.getValue() );

                logger.info( fillUp( stringListEntry.getKey(), 50 ) + " | " + fillUp( String.valueOf( getAmount( stringListEntry.getValue() ) ), 6 ) + " | " + fillUp( getAverage( stringListEntry.getValue() ) + " ns", 18 ) + " | " + fillUp( getMax( stringListEntry.getValue() ) + " ns", 18 ) + " | " + fillUp( String.valueOf( TimeUnit.NANOSECONDS.toMillis( currentEventOverall ) ) + " ms", 20 ) + " | " + fillUp( String.valueOf( Math.round( ( currentEventOverall / (double) overallTime ) * 10000 ) / (double) 100 ) + " %", 12 ) );
            }
        } finally {
            this.lock.readLock().unlock();
        }

        logger.info( "-----------------------------------------------------------------------------------------------------------------------------------------------" );
        logger.info( "Amount of Events captured: " + performanceReport.size() + "; Time measured: " + String.valueOf( TimeUnit.NANOSECONDS.toMillis( overallTime ) ) + " ms" );
    }

    private int getAmount( LongIntMap value ) {
        int times = 0;

        for ( Map.Entry<Long, Integer> entry : value.entrySet() ) {
            times += entry.getValue();
        }

        return times;
    }

    private String getMax( LongIntMap value ) {
        long max = 0;

        LongIterator iterator = value.keySet().iterator();
        while ( iterator.hasNext() ) {
            long current = iterator.nextLong();
            if ( current  > max ) {
                max = current;
            }
        }

        return String.valueOf( max );
    }

    private String getAverage( LongIntMap value ) {
        long overall = 0;
        int times = 0;

        for ( Map.Entry<Long, Integer> entry : value.entrySet() ) {
            overall += entry.getKey() * entry.getValue();
            times += entry.getValue();
        }

        return String.valueOf( overall / times );
    }

    private String fillUp( String content, int neededSize ) {
        if ( content.length() >= neededSize ) return content.substring( 0, neededSize );
        else return content + repeatString( " ", neededSize - content.length() );
    }

    private String repeatString( String s, int i ) {
        String returnString = "";

        for ( int i1 = 0; i1 < i; i1++ ) {
            returnString += s;
        }

        return returnString;
    }

    private long getOverallTime( LongIntMap longSet ) {
        long overall = 0;

        if ( longSet == null ) {
            this.lock.readLock().lock();
            try {
                for ( Map.Entry<String, LongIntMap> stringListEntry : this.performanceReport.entrySet() ) {
                    overall += getOverallTime( stringListEntry.getValue() );
                }
            } finally {
                this.lock.readLock().unlock();
            }
        } else {
            for ( Map.Entry<Long, Integer> entry : longSet.entrySet() ) {
                overall += entry.getKey() * entry.getValue();
            }
        }

        return overall;
    }
}
