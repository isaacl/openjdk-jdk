/*
 * Copyright 2000-2004 Sun Microsystems, Inc.  All Rights Reserved.
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
package javax.print.attribute.standard;

import javax.print.attribute.Attribute;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.DocAttribute;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintJobAttribute;

/**
 * Class OrientationRequested is a printing attribute class, an enumeration, 
 * that indicates the desired orientation for printed print-stream pages; it 
 * does not describe the orientation of the client-supplied print-stream
 * pages. 
 * <P>
 * For some document formats (such as <CODE>"application/postscript"</CODE>), 
 * the desired orientation of the print-stream pages is specified within the 
 * document data. This information is generated by a device driver prior to  
 * the submission of the print job. Other document formats (such as 
 * <CODE>"text/plain"</CODE>) do not include the notion of desired orientation 
 * within the document data. In the latter case it is possible for the printer 
 * to bind the desired orientation to the document data after it has been 
 * submitted. It is expected that a printer would only support the 
 * OrientationRequested attribute for some document formats (e.g., 
 * <CODE>"text/plain"</CODE> or <CODE>"text/html"</CODE>) but not others (e.g. 
 * <CODE>"application/postscript"</CODE>). This is no different from any other 
 * job template attribute, since a print job can always impose constraints
 * among the values of different job template attributes.
 *  However, a special mention 
 * is made here since it is very likely that a printer will support the 
 * OrientationRequested attribute for only a subset of the supported document 
 * formats. 
 * <P>
 * <B>IPP Compatibility:</B> The category name returned by 
 * <CODE>getName()</CODE> is the IPP attribute name.  The enumeration's 
 * integer value is the IPP enum value.  The <code>toString()</code> method 
 * returns the IPP string representation of the attribute value.
 * <P>
 *
 * @author  Alan Kaminsky
 */
public final class OrientationRequested extends EnumSyntax
    implements DocAttribute, PrintRequestAttribute, PrintJobAttribute {

    private static final long serialVersionUID = -4447437289862822276L;

    /**
     * The content will be imaged across the short edge of the medium. 
     */
    public static final OrientationRequested
	PORTRAIT = new OrientationRequested(3);

    /**
     * The content will be imaged across the long edge of the medium.
     * Landscape is defined to be a rotation of the print-stream page to be
     * imaged by +90 degrees with respect to the medium
     * (i.e. anti-clockwise) from the 
     * portrait orientation. <I>Note:</I> The +90 direction was chosen because 
     * simple finishing on the long edge is the same edge whether portrait or 
     * landscape. 
     */
    public static final OrientationRequested
	LANDSCAPE = new OrientationRequested(4);

    /**
     * The content will be imaged across the long edge of the medium, but in 
     * the opposite manner from landscape. Reverse-landscape is defined to be 
     * a rotation of the print-stream page to be imaged by -90 degrees with 
     * respect to the medium (i.e. clockwise) from the portrait orientation. 
     * <I>Note:</I> The REVERSE_LANDSCAPE value was added because some 
     * applications rotate landscape -90 degrees from portrait, rather than
     * +90 degrees. 
     */
    public static final OrientationRequested
	REVERSE_LANDSCAPE = new OrientationRequested(5);

    /**
     * The content will be imaged across the short edge of the medium, but in 
     * the opposite manner from portrait. Reverse-portrait is defined to be a 
     * rotation of the print-stream page to be imaged by 180 degrees with 
     * respect to the medium from the portrait orientation. <I>Note:</I> The 
     * REVERSE_PORTRAIT value was added for use with the {@link 
     * Finishings Finishings} attribute in cases where the 
     * opposite edge is desired for finishing a portrait document on simple 
     * finishing devices that have only one finishing position. Thus a 
     * <CODE>"text/plain"</CODE> portrait document can be stapled "on the
     * right" by a simple finishing device as is common use with some 
     * Middle Eastern languages such as Hebrew. 
     */
    public static final OrientationRequested
	REVERSE_PORTRAIT = new OrientationRequested(6);

    /**
     * Construct a new orientation requested enumeration value with the given 
     * integer value. 
     *
     * @param  value  Integer value.
     */
    protected OrientationRequested(int value) {
	super(value);
    }

    private static final String[] myStringTable = {
	"portrait",
	"landscape",
	"reverse-landscape",
	"reverse-portrait"
    };

    private static final OrientationRequested[] myEnumValueTable = {
	PORTRAIT,
	LANDSCAPE,
	REVERSE_LANDSCAPE,
	REVERSE_PORTRAIT
    };

    /**
     * Returns the string table for class OrientationRequested.
     */
    protected String[] getStringTable() {
	return myStringTable;
    }

    /**
     * Returns the enumeration value table for class OrientationRequested.
     */
    protected EnumSyntax[] getEnumValueTable() {
	return myEnumValueTable;
    }

    /**
     * Returns the lowest integer value used by class OrientationRequested.
     */
    protected int getOffset() {
	return 3;
    } 

    /**
     * Get the printing attribute class which is to be used as the "category" 
     * for this printing attribute value.
     * <P>
     * For class OrientationRequested, the 
     * category is class OrientationRequested itself. 
     *
     * @return  Printing attribute class (category), an instance of class
     *          {@link java.lang.Class java.lang.Class}.
     */
    public final Class<? extends Attribute> getCategory() {
	return OrientationRequested.class;
    }

    /**
     * Get the name of the category of which this attribute value is an 
     * instance. 
     * <P>
     * For class OrientationRequested, the 
     * category name is <CODE>"orientation-requested"</CODE>.
     *
     * @return  Attribute category name.
     */
    public final String getName() {
	return "orientation-requested";
    }

}
