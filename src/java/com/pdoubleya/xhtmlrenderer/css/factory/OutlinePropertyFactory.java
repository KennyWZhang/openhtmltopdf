/*
 * {{{ header & license
 * OutlinePropertyFactory.java
 * Copyright (c) 2004 Patrick Wright
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * }}}
 */
package com.pdoubleya.xhtmlrenderer.css.factory;

import java.util.*;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;

import org.joshy.html.css.RuleNormalizer;

import com.pdoubleya.xhtmlrenderer.css.constants.CSSName;
import com.pdoubleya.xhtmlrenderer.css.impl.XRPropertyImpl;
import com.pdoubleya.xhtmlrenderer.css.impl.XRValueImpl;


/**
 * A PropertyFactory for CSS 2 "margin" shorthand property, instantiating
 * XRProperties; Singleton, use {@link #instance()}.
 *
 * @author   Patrick Wright
 */
public class OutlinePropertyFactory extends AbstractPropertyFactory {
    /** Singleton instance. */
    private static OutlinePropertyFactory _instance;


    /** Constructor for the OutlinePropertyFactory object */
    private OutlinePropertyFactory() { }

    /**
     * If <code>propName</code> describes a shorthand property, explodes it into
     * the specific properties it is a shorthand for, and returns those as an
     * Iterator of {@link com.pdoubleya.xhtmlrenderer.css.XRProperty} instances;
     * or just instantiates a single <code>XRProperty</code> for non-shorthand
     * props.
     *
     * @param style     The CSSStyleDeclaration from the SAC parser.
     * @param propName  The String property name for the property to explode.
     * @param sequence  Sequence in which the declaration was found in the
     *      containing stylesheet.
     * @return          Iterator of one or more XRProperty instances
     *      representing the exploded values.
     */
    public Iterator explodeProperties( CSSStyleDeclaration style, String propName, int sequence ) {
        List list = new ArrayList();
        CSSValue cssValue = style.getPropertyCSSValue( propName );
        String priority = style.getPropertyPriority( propName );

        // CAREFUL: note that with steadyState parser impl, their value class impl
        // both primitive and value list interfaces! use getCssValueType(), not instanceof!!
        if ( cssValue.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE ) {
            addPrimitive(style, (CSSPrimitiveValue)cssValue, priority, sequence, list);
        } else {
            // is a value list
            CSSValueList vList = (CSSValueList)cssValue;

            // background shorthand can have color, image, repeat, 
            // attachment, position in any order; so loop whatever's 
            // provided and sniff for the value-type
            CSSPrimitiveValue primitive = null;
            for ( int i = 0, len = vList.getLength(); i < len; i++ ) {
                primitive = (CSSPrimitiveValue)vList.item( i );
                addPrimitive(style, primitive, priority, sequence, list);
            }
        }// is a value list
        return list.iterator();
    }

    private void addPrimitive(
        CSSStyleDeclaration style,
        CSSPrimitiveValue primitive, 
        String priority,
        int sequence,
        List list) {
            
        String val = primitive.getCssText().trim();
        String propName = "";
        if ( RuleNormalizer.looksLikeAColor( val ) ) {
            propName = CSSName.OUTLINE_COLOR;
        } else if ( RuleNormalizer.looksLikeABorderStyle( val )) {
            propName = CSSName.OUTLINE_STYLE;
        } else {
            // HACK: as with Background, assume it is a length value
            propName = CSSName.OUTLINE_WIDTH;
        }
        list.add( 
            newProperty( 
                propName, 
                primitive, 
                priority, 
                style, 
                sequence 
            ) 
        );
    }

    /**
     * Returns the singleton instance.
     *
     * @return   Returns
     */
    public static synchronized PropertyFactory instance() {
        if ( _instance == null ) {
            _instance = new OutlinePropertyFactory();
        }
        return _instance;
    }
} // end class

