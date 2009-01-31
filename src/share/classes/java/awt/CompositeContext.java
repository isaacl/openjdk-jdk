/*
 * Copyright 1997-1998 Sun Microsystems, Inc.  All Rights Reserved.
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

package java.awt;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * The <code>CompositeContext</code> interface defines the encapsulated
 * and optimized environment for a compositing operation.
 * <code>CompositeContext</code> objects maintain state for
 * compositing operations.  In a multi-threaded environment, several
 * contexts can exist simultaneously for a single {@link Composite} 
 * object.
 * @see Composite
 * @version 10 Feb 1997
 */

public interface CompositeContext {
    /**
     * Releases resources allocated for a context.
     */
    public void dispose();

    /**
     * Composes the two source {@link Raster} objects and 
     * places the result in the destination 
     * {@link WritableRaster}.  Note that the destination 
     * can be the same object as either the first or second 
     * source. Note that <code>dstIn</code> and 
     * <code>dstOut</code> must be compatible with the 
     * <code>dstColorModel</code> passed to the 
     * {@link Composite#createContext(java.awt.image.ColorModel, java.awt.image.ColorModel, java.awt.RenderingHints) createContext} 
     * method of the <code>Composite</code> interface.
     * @param src the first source for the compositing operation
     * @param dstIn the second source for the compositing operation
     * @param dstOut the <code>WritableRaster</code> into which the 
     * result of the operation is stored
     * @see Composite
     */
    public void compose(Raster src,
                        Raster dstIn,
			WritableRaster dstOut);


}

