/*
 * Copyright 2004 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * %W% %E%
 */

import static java.lang.management.ManagementFactory.*;
import java.lang.management.*;
import javax.management.*;
import java.io.*;
import java.util.*;

/**
 * Example of using the java.lang.management API to monitor 
 * the memory usage and garbage collection statistics.
 *
 * @author  Mandy Chung
 * @version %% %G%
 */
public class PrintGCStat {
    private RuntimeMXBean rmbean;
    private MemoryMXBean mmbean;
    private List<MemoryPoolMXBean> pools;
    private List<GarbageCollectorMXBean> gcmbeans;
    
    /**
     * Constructs a PrintGCStat object to monitor a remote JVM.
     */
    public PrintGCStat(MBeanServerConnection server) throws IOException {
        // Create the platform mxbean proxies
        this.rmbean = newPlatformMXBeanProxy(server,
                                             RUNTIME_MXBEAN_NAME,
                                             RuntimeMXBean.class);
        this.mmbean = newPlatformMXBeanProxy(server,
                                             MEMORY_MXBEAN_NAME,
                                             MemoryMXBean.class);
        ObjectName poolName = null;
        ObjectName gcName = null;
        try {
            poolName = new ObjectName(MEMORY_POOL_MXBEAN_DOMAIN_TYPE+",*");
            gcName = new ObjectName(GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE+",*");
        } catch (MalformedObjectNameException e) {
            // should not reach here
            assert(false);
        }

        Set mbeans = server.queryNames(poolName, null);
        if (mbeans != null) {
            pools = new ArrayList<MemoryPoolMXBean>();
            Iterator iterator = mbeans.iterator();
            while (iterator.hasNext()) {
                ObjectName objName = (ObjectName) iterator.next();
                MemoryPoolMXBean p = 
                    newPlatformMXBeanProxy(server,
                                           objName.getCanonicalName(),
                                           MemoryPoolMXBean.class);
                pools.add(p);
            }
        }

        mbeans = server.queryNames(gcName, null);
        if (mbeans != null) {
            gcmbeans = new ArrayList<GarbageCollectorMXBean>();
            Iterator iterator = mbeans.iterator();
            while (iterator.hasNext()) {
                ObjectName objName = (ObjectName) iterator.next();
                GarbageCollectorMXBean gc = 
                    newPlatformMXBeanProxy(server,
                                           objName.getCanonicalName(),
                                           GarbageCollectorMXBean.class);
                gcmbeans.add(gc);
            }
        }
    }

    /**
     * Constructs a PrintGCStat object to monitor the local JVM.
     */
    public PrintGCStat() {
        // Obtain the platform mxbean instances for the running JVM.
        this.rmbean = getRuntimeMXBean();
        this.mmbean = getMemoryMXBean();
        this.pools = getMemoryPoolMXBeans();
        this.gcmbeans = getGarbageCollectorMXBeans();
    }

    /**
     * Prints the verbose GC log to System.out to list the memory usage
     * of all memory pools as well as the GC statistics. 
     */
    public void printVerboseGc() {
        System.out.print("Uptime: " + formatMillis(rmbean.getUptime()));
        for (GarbageCollectorMXBean gc : gcmbeans) {
            System.out.print(" [" + gc.getName() + ": ");
            System.out.print("Count=" + gc.getCollectionCount());
            System.out.print(" GCTime=" + formatMillis(gc.getCollectionTime()));
            System.out.print("]");
        }
        System.out.println();
        for (MemoryPoolMXBean p : pools) {
            System.out.print("  [" + p.getName() + ":");
            MemoryUsage u = p.getUsage();
            System.out.print(" Used=" + formatBytes(u.getUsed()));
            System.out.print(" Committed=" + formatBytes(u.getCommitted()));
            System.out.println("]");
        }
    }

    private String formatMillis(long ms) {
        return String.format("%.4fsec", ms / (double) 1000);
    }
    private String formatBytes(long bytes) {
        long kb = bytes;
        if (bytes > 0) {
            kb = bytes / 1024;
        }
        return kb + "K"; 
    }
}
