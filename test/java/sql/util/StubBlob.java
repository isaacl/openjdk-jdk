/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
package util;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;


public class StubBlob implements Blob {
    public long length() throws SQLException {
        return 0;
    }
    public byte[] getBytes(long pos, int length)
        throws SQLException {
        return null;
    }
    public InputStream getBinaryStream()
        throws SQLException {
        return null;
    }
    public long position(byte[] pattern, long start)
        throws SQLException {
        return 0;
    }
    public long position(Blob pattern, long start)
        throws SQLException {
        return 0;
    }
    public int setBytes(long pos, byte[] bytes)
        throws SQLException {
        return 0;
    }
    public int setBytes(long pos, byte[] bytes, int offset, int len)
        throws SQLException {
        return 0;
    }
    public OutputStream setBinaryStream(long pos)
        throws SQLException {
        return null;
    }
    public void truncate(long len)
        throws SQLException {
    }
    /* 6.0 implementation */

    public void free() throws SQLException {}

    public InputStream getBinaryStream(long pos, long length) throws SQLException {
       return null;
    }
}
