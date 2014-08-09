/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 * 
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 * 
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores
 * CA 94065 USA or visit www.oracle.com if you need additional information or
 * have any questions.
 */

import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.list.ListModel;
import com.sun.lwuit.util.EventDispatcher;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Default implementation of the list model based on a vector of elements
 *
 * @author Chen Fishbein
 */
public class LWListModel implements ListModel
{

    private final Vector items;
    private final EventDispatcher dataListener = new EventDispatcher();
    private final EventDispatcher selectionListener = new EventDispatcher();
    private int selectedIndex = 0;

    /**
     * Creates a new instance of DefaultListModel
     */
    public LWListModel()
    {
        items = new Vector();
    }

    /**
     * Creates a new instance of DefaultListModel
     *
     * @param items the items in the model
     */
    public LWListModel(Vector items)
    {
        this.items = items;
    }

    public void newItemList(Vector _items)
    {
        removeAll();
        final Enumeration e = _items.elements();
        while (e.hasMoreElements())
        {
            items.addElement(e.nextElement());
        }
    }

    /**
     * @inheritDoc
     */
    public Object getItemAt(int index)
    {
        if ((index < getSize()) && (index >= 0))
        {
            return items.elementAt(index);
        }
        return null;
    }

    /**
     * @inheritDoc
     */
    public int getSize()
    {
        return items.size();
    }

    /**
     * @inheritDoc
     */
    public int getSelectedIndex()
    {
        return selectedIndex;
    }

    /**
     * @inheritDoc
     */
    public void addItem(Object item)
    {
        items.addElement(item);
        fireDataChangedEvent(DataChangedListener.ADDED, items.size());
    }

    public boolean isUnique(Object _item)
    {
        final Enumeration e = items.elements();
        while (e.hasMoreElements())
        {
            final Object o = e.nextElement();
            if (_item.equals(o))
            {
                return false;
            }
        }
        return true;
    }

    public void addUnique(Object item)
    {
        if (isUnique(item))
        {
            addItem(item);
        }
    }

    public void addUniqueAtindex(Object item, int index)
    {
        if (isUnique(item))
        {
            addItemAtIndex(item, index);
        }
    }

    /**
     * Change the item at the given index
     *
     * @param index the offset for the item
     * @param item the value to set
     */
    public void setItem(int index, Object item)
    {
        items.setElementAt(item, index);
        fireDataChangedEvent(DataChangedListener.CHANGED, index);
    }

    /**
     * Adding an item to list at given index
     *
     * @param item - the item to add
     * @param index - the index position in the list
     */
    public void addItemAtIndex(Object item, int index)
    {
        if (index <= items.size())
        {
            items.insertElementAt(item, index);
            fireDataChangedEvent(DataChangedListener.ADDED, index);
        }
    }

    /**
     * @inheritDoc
     */
    public void removeItem( int index)
    {
        if ((index < getSize()) && (index >= 0))
        {
            items.removeElementAt(index);
            if (getSelectedIndex() >= index)
            {
                setSelectedIndex(index - 1);
            }
            fireDataChangedEvent(DataChangedListener.REMOVED, index);
        }
    }

    public void removeItem(Object item)
    {
        removeItem(items.indexOf(item));
    }

    /**
     * Removes all elements from the model
     */
    public void removeAll()
    {
        items.removeAllElements();
        selectedIndex = 0;
    }

    /**
     * @inheritDoc
     */
    public void setSelectedIndex(int index)
    {
        int oldIndex = selectedIndex;
        selectedIndex = (Math.max(index, 0));
        selectedIndex = (Math.min(index, Math.max(0, getSize() - 1)));
        selectionListener.fireSelectionEvent(oldIndex, selectedIndex);
    }

    /**
     * @inheritDoc
     */
    public void addDataChangedListener( DataChangedListener l)
    {
        dataListener.addListener(l);
    }

    /**
     * @inheritDoc
     */
    public void removeDataChangedListener(DataChangedListener l)
    {
        dataListener.removeListener(l);
    }

    private void fireDataChangedEvent(int status, int index)
    {
        dataListener.fireDataChangeEvent(index, status);
    }

    /**
     * @inheritDoc
     */
    public void addSelectionListener(SelectionListener l)
    {
        selectionListener.addListener(l);
    }

    /**
     * @inheritDoc
     */
    public void removeAllListeners()
    {
        Vector v = selectionListener.getListenerVector();
        if (v != null)
        {
            v.removeAllElements();
        }
        //
        v = dataListener.getListenerVector();
        if (v != null)
        {
            v.removeAllElements();
        }
    }

    public void removeSelectionListener(SelectionListener l)
    {
        selectionListener.removeListener(l);
    }

    public Vector getItems()
    {
        return items;
    }

    /**
     * @inheritDoc
     */
    public Object getSelectedItem()
    {
        int index = getSelectedIndex();
        if ((index < getSize()) && (index >= 0))
        {
            return items.elementAt(index);
        }
        return null;
    }
}
