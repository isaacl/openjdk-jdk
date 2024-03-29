/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
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
package javax.swing.plaf.nimbus;

import javax.swing.Painter;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * TableScrollPaneCorner - A simple component that paints itself using the table
 * header background painter. It is used to fill the top right corner of
 * scrollpane.
 *
 * @author Created by Jasper Potts (Jan 28, 2008)
 */
@SuppressWarnings("serial") // Superclass is not serializable across versions
class TableScrollPaneCorner extends JComponent implements UIResource{

    /**
     * Paint the component using the Nimbus Table Header Background Painter
     */
    @Override protected void paintComponent(Graphics g) {
        Painter painter = (Painter) UIManager.get(
            "TableHeader:\"TableHeader.renderer\"[Enabled].backgroundPainter");
        if (painter != null){
            if (g instanceof Graphics2D){
                painter.paint((Graphics2D)g,this,getWidth()+1,getHeight());
            } else {
                // paint using image to not Graphics2D to support
                // Java 1.1 printing API
                BufferedImage img =  new BufferedImage(getWidth(),getHeight(),
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = (Graphics2D)img.getGraphics();
                painter.paint(g2,this,getWidth()+1,getHeight());
                g2.dispose();
                g.drawImage(img,0,0,null);
                img = null;
            }
        }
    }
}
