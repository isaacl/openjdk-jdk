/*
 * Copyright 1999 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.sun.tools.example.debug.bdi;

import com.sun.jdi.*;

class SourceNameReferenceTypeSpec implements ReferenceTypeSpec {
    final String sourceName;
    final int linenumber;

    SourceNameReferenceTypeSpec(String sourceName, int linenumber) {
        this.sourceName = sourceName;
        this.linenumber = linenumber;
    }

    /**
     * Does the specified ReferenceType match this spec.
     */
    public boolean matches(ReferenceType refType) {
        try {
            if (refType.sourceName().equals(sourceName)) {
                try {
                    refType.locationsOfLine(linenumber);
                    // if we don't throw an exception then it was found
                    return true;
                } catch(AbsentInformationException exc) {
                } catch(ObjectCollectedException  exc) {
                } catch(InvalidLineNumberException  exc) {
//          } catch(ClassNotPreparedException  exc) {
//               -- should not happen, so don't catch this ---
                }
            }
        } catch(AbsentInformationException exc) {
            // for sourceName(), fall through
        }
        return false;
    }

    public int hashCode() {
        return sourceName.hashCode() + linenumber;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SourceNameReferenceTypeSpec) {
            SourceNameReferenceTypeSpec spec = (SourceNameReferenceTypeSpec)obj;

            return sourceName.equals(spec.sourceName) && 
                              (linenumber == spec.linenumber);
        } else {
            return false;
        }
    }

    public String toString() {
        return sourceName + "@" + linenumber;
    }
}


