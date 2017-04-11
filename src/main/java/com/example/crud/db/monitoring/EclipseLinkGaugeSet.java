package com.example.crud.db.monitoring;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricSet;

import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A set of gauges for operating system settings.
 */
public class EclipseLinkGaugeSet implements MetricSet {

    private final Optional<Method> level1CacheObjectCount;
    private final Optional<Method> level1CacheMemorySize;
    private final Optional<Method> level1CacheHitMissRatio;
    private final Optional<Method> level2CacheObjectCount;
    private final Optional<Method> level2CacheHitMissRatio;

    /**
     * Creates new gauges using the platform OS bean.
     */
    public EclipseLinkGaugeSet() {
        this(ManagementFactory.getEclipseLinkMXBean());
    }

    /**
     * Creates a new gauges using the given OS bean.
     *
     * @param mxBean an {@link EclipseLinkMXBean}
     */
    public EclipseLinkGaugeSet(EclipseLinkMXBean mxBean) {
        this.mxBean = mxBean;

        committedVirtualMemorySize = getMethod("getCommittedVirtualMemorySize");
        totalSwapSpaceSize = getMethod("getTotalSwapSpaceSize");
        freeSwapSpaceSize = getMethod("getFreeSwapSpaceSize");
        processCpuTime = getMethod("getProcessCpuTime");
        freePhysicalMemorySize = getMethod("getFreePhysicalMemorySize");
        totalPhysicalMemorySize = getMethod("getTotalPhysicalMemorySize");
        openFileDescriptorCount = getMethod("getOpenFileDescriptorCount");
        maxFileDescriptorCount = getMethod("getMaxFileDescriptorCount");
        systemCpuLoad = getMethod("getSystemCpuLoad");
        processCpuLoad = getMethod("getProcessCpuLoad");
    }


    @Override
    public Map<String, Metric> getMetrics() {
        final Map<String, Metric> gauges = new HashMap<>();

        gauges.put("committedVirtualMemorySize", (Gauge<Long>) () -> invokeLong(committedVirtualMemorySize));
        gauges.put("totalSwapSpaceSize", (Gauge<Long>) () -> invokeLong(totalSwapSpaceSize));
        gauges.put("freeSwapSpaceSize", (Gauge<Long>) () -> invokeLong(freeSwapSpaceSize));
        gauges.put("processCpuTime", (Gauge<Long>) () -> invokeLong(processCpuTime));
        gauges.put("freePhysicalMemorySize", (Gauge<Long>) () -> invokeLong(freePhysicalMemorySize));
        gauges.put("totalPhysicalMemorySize", (Gauge<Long>) () -> invokeLong(totalPhysicalMemorySize));
        gauges.put("fd.usage", (Gauge<Double>) () -> invokeRatio(openFileDescriptorCount, maxFileDescriptorCount));
        gauges.put("systemCpuLoad", (Gauge<Double>) () -> invokeDouble(systemCpuLoad));
        gauges.put("processCpuLoad", (Gauge<Double>) () -> invokeDouble(processCpuLoad));

        return gauges;
    }

    private Optional<Method> getMethod(String name) {
        try {
            final Method method = mxBean.getClass().getDeclaredMethod(name);
            method.setAccessible(true);
            return Optional.of(method);
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private long invokeLong(Optional<Method> method) {
        if (method.isPresent()) {
            try {
                return (long) method.get().invoke(mxBean);
            } catch (IllegalAccessException | InvocationTargetException ite) {
                return 0L;
            }
        }
        return 0L;
    }

    private double invokeDouble(Optional<Method> method) {
        if (method.isPresent()) {
            try {
                return (double) method.get().invoke(mxBean);
            } catch (IllegalAccessException | InvocationTargetException ite) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private double invokeRatio(Optional<Method> numeratorMethod, Optional<Method> denominatorMethod) {
        if (numeratorMethod.isPresent() && denominatorMethod.isPresent()) {
            try {
                long numerator = (long) numeratorMethod.get().invoke(mxBean);
                long denominator = (long) denominatorMethod.get().invoke(mxBean);
                if (0 ==  denominator) {
                    return Double.NaN;
                }
                return 1.0 * numerator / denominator;
            } catch (IllegalAccessException | InvocationTargetException ite) {
                return Double.NaN;
            }
        }
        return Double.NaN;
    }

}
