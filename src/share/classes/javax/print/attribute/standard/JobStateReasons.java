/*
 * Copyright (c) 2000, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package javax.print.attribute.standard;

import java.util.Collection;
import java.util.HashSet;

import javax.print.attribute.Attribute;
import javax.print.attribute.PrintJobAttribute;

/**
 * Class JobStateReasons is a printing attribute class, a set of enumeration
 * values, that provides additional information about the job's current state,
 * i.e., information that augments the value of the job's {@link JobState
 * JobState} attribute.
 * <P>
 * Instances of {@link JobStateReason JobStateReason} do not appear in a Print
 * Job's attribute set directly. Rather, a JobStateReasons attribute appears in
 * the Print Job's attribute set. The JobStateReasons attribute contains zero,
 * one, or more than one {@link JobStateReason JobStateReason} objects which
 * pertain to the Print Job's status. The printer adds a {@link JobStateReason
 * JobStateReason} object to the Print Job's JobStateReasons attribute when the
 * corresponding condition becomes true of the Print Job, and the printer
 * removes the {@link JobStateReason JobStateReason} object again when the
 * corresponding condition becomes false, regardless of whether the Print Job's
 * overall {@link JobState JobState} also changed.
 * <P>
 * Class JobStateReasons inherits its implementation from class {@link
 * java.util.HashSet java.util.HashSet}. Unlike most printing attributes which
 * are immutable once constructed, class JobStateReasons is designed to be
 * mutable; you can add {@link JobStateReason JobStateReason} objects to an
 * existing JobStateReasons object and remove them again. However, like class
 * {@link java.util.HashSet java.util.HashSet}, class JobStateReasons is not
 * multiple thread safe. If a JobStateReasons object will be used by multiple
 * threads, be sure to synchronize its operations (e.g., using a synchronized
 * set view obtained from class {@link java.util.Collections
 * java.util.Collections}).
 * <P>
 * <B>IPP Compatibility:</B> The string value returned by each individual {@link
 * JobStateReason JobStateReason} object's <CODE>toString()</CODE> method gives
 * the IPP keyword value. The category name returned by <CODE>getName()</CODE>
 * gives the IPP attribute name.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class JobStateReasons
    extends HashSet<JobStateReason> implements PrintJobAttribute {

    private static final long serialVersionUID = 8849088261264331812L;

    /**
     * Construct a new, empty job state reasons attribute; the underlying hash
     * set has the default initial capacity and load factor.
     */
    public JobStateReasons() {
        super();
    }

    /**
     * Construct a new, empty job state reasons attribute; the underlying hash
     * set has the given initial capacity and the default load factor.
     *
     * @param  initialCapacity  Initial capacity.
     * @throws IllegalArgumentException if the initial capacity is less
     *     than zero.
     */
    public JobStateReasons(int initialCapacity) {
        super (initialCapacity);
    }

    /**
     * Construct a new, empty job state reasons attribute; the underlying hash
     * set has the given initial capacity and load factor.
     *
     * @param  initialCapacity  Initial capacity.
     * @param  loadFactor       Load factor.
     * @throws IllegalArgumentException if the initial capacity is less
     *     than zero.
     */
    public JobStateReasons(int initialCapacity, float loadFactor) {
        super (initialCapacity, loadFactor);
    }

    /**
     * Construct a new job state reasons attribute that contains the same
     * {@link JobStateReason JobStateReason} objects as the given collection.
     * The underlying hash set's initial capacity and load factor are as
     * specified in the superclass constructor {@link
     * java.util.HashSet#HashSet(java.util.Collection)
     * HashSet(Collection)}.
     *
     * @param  collection  Collection to copy.
     *
     * @exception  NullPointerException
     *     (unchecked exception) Thrown if <CODE>collection</CODE> is null or
     *     if any element in <CODE>collection</CODE> is null.
     * @throws  ClassCastException
     *     (unchecked exception) Thrown if any element in
     *     <CODE>collection</CODE> is not an instance of class {@link
     *     JobStateReason JobStateReason}.
     */
   public JobStateReasons(Collection<JobStateReason> collection) {
       super (collection);
   }

    /**
     * Adds the specified element to this job state reasons attribute if it is
     * not already present. The element to be added must be an instance of class
     * {@link JobStateReason JobStateReason}. If this job state reasons
     * attribute already contains the specified element, the call leaves this
     * job state reasons attribute unchanged and returns <tt>false</tt>.
     *
     * @param  o  Element to be added to this job state reasons attribute.
     *
     * @return  <tt>true</tt> if this job state reasons attribute did not
     *          already contain the specified element.
     *
     * @throws  NullPointerException
     *     (unchecked exception) Thrown if the specified element is null.
     * @throws  ClassCastException
     *     (unchecked exception) Thrown if the specified element is not an
     *     instance of class {@link JobStateReason JobStateReason}.
     * @since 1.5
     */
    public boolean add(JobStateReason o) {
        if (o == null) {
            throw new NullPointerException();
        }
        return super.add(o);
    }

    /**
     * Get the printing attribute class which is to be used as the "category"
     * for this printing attribute value.
     * <P>
     * For class JobStateReasons, the category is class JobStateReasons itself.
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link java.lang.Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
        return JobStateReasons.class;
    }

    /**
     * Get the name of the category of which this attribute value is an
     * instance.
     * <P>
     * For class JobStateReasons, the category
     * name is <CODE>"job-state-reasons"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
        return "job-state-reasons";
    }

}
