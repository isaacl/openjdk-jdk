/*
 * Copyright 1998 Sun Microsystems, Inc.  All Rights Reserved.
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

package com.sun.java.swing.plaf.windows;

import java.awt.*;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;


/**
 * Divider used for Windows split pane.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.2 10/25/97
 * @author Jeff Dinkins
 */
public class WindowsSplitPaneDivider extends BasicSplitPaneDivider
{

    /**
     * Creates a new Windows SplitPaneDivider
     */
    public WindowsSplitPaneDivider(BasicSplitPaneUI ui) {
        super(ui);
    } 

    /**
      * Paints the divider.
      */
    public void paint(Graphics g) {
        Color bgColor = (splitPane.hasFocus()) ?
                            UIManager.getColor("SplitPane.shadow") :
                            getBackground();
        Dimension size = getSize();

        if(bgColor != null) {
            g.setColor(bgColor);
            g.fillRect(0, 0, size.width, size.height);
        }
        super.paint(g);
    }
}
