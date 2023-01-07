/*
* Copyright (C) 2020 Stanislav Georgiev
* https://github.com/slaviboy
*
*  NOTICE:  All information contained herein is, and remains the property
*  of Stanislav Georgiev and its suppliers, if any. The intellectual and
*  technical concepts contained herein are proprietary to Stanislav Georgiev
*  and its suppliers and may be covered by U.S. and Foreign Patents, patents
*  in process, and are protected by trade secret or copyright law. Dissemination
*  of this information or reproduction of this material is strictly forbidden
*  unless prior written permission is obtained from Stanislav Georgiev.
*/
package com.slaviboy.toolbar

import android.view.DragEvent
import android.view.MotionEvent
import android.view.View

/**
 * ToolbarGroup used to combine toolbars that way, when changing the selected element
 * from one toolbar, if there is selected element from other toolbars, they are deselected.
 */
class ToolbarGroup(var toolbars: ArrayList<Toolbar> = ArrayList()) {

    constructor(vararg toolbars: Toolbar) : this(toolbars.toCollection(ArrayList<Toolbar>()))

    // listeners for the different events
    lateinit var onElementChangeToolbar: ((addToolbar: Toolbar, removeToolbar: Toolbar, toolbarElement: ToolbarElement, newPosition: Int) -> Unit)
    lateinit var elementsOnClickListener: ((v: View) -> Unit)
    lateinit var elementsOnLongClickListener: ((v: View) -> Boolean)
    lateinit var elementsOnTouchListener: ((v: View, event: MotionEvent) -> Boolean)
    lateinit var elementsOnDragListener: ((v: View, dragEvent: DragEvent) -> Boolean)

    var selectedView: ToolbarElement?         // current selected view id
    var allowElementChange: Boolean           // if change of the selected element is allowed

    init {
        allowElementChange = true
        selectedView = null
        toolbars.forEach {
            setListeners(it)
        }
    }

    fun add(vararg toolbars: Toolbar) {
        toolbars.forEach {
            setListeners(it)
            this.toolbars.add(it)
        }
    }

    /**
     * Set all listeners for all events.
     */
    private fun setListeners(toolbar: Toolbar) {
        toolbar.toolbarGroup = this

        // click
        toolbar.setOnClickListener(true) {

            if (allowElementChange) {
                onToolbarElementClick(it)

                if (::elementsOnClickListener.isInitialized) {
                    elementsOnClickListener.invoke(it)
                }
            }
        }

        // long click
        toolbar.setOnLongClickListener(false) {
            if (::elementsOnLongClickListener.isInitialized) {
                elementsOnLongClickListener.invoke(it)
            }
            true
        }

        // touch events
        toolbar.setOnTouchListener(false) { v: View, event: MotionEvent ->

            if (::elementsOnTouchListener.isInitialized) {
                elementsOnTouchListener.invoke(v, event)
            }
            true
        }

        // drag events
        toolbar.setOnDragListener(false) { v: View, dragEvent: DragEvent ->
            if (::elementsOnDragListener.isInitialized) {
                elementsOnDragListener.invoke(v, dragEvent)
            }
            true
        }

        // when element is transferred to another toolbar
        toolbar.onElementChangeToolbar = { addToolbar: Toolbar, removeToolbar: Toolbar, toolbarElement: ToolbarElement, newPosition: Int ->
            if (::onElementChangeToolbar.isInitialized) {
                onElementChangeToolbar.invoke(addToolbar, removeToolbar, toolbarElement, newPosition)
            }
        }

    }

    /**
     * When element is clicked, change the selected element if the element
     * that is clicked isSelectable.
     */
    fun onToolbarElementClick(v: View) {

        val toolbar = v.parent as Toolbar
        val element = v as ToolbarElement
        if (element.isSelectable && !element.isDisabled) {

            // clear selected elements from all
            toolbars.forEach {
                it.selectedView?.isSelected = false
                it.selectedView = null
            }
            toolbar.selectedView = element
            toolbar.selectedView?.isSelected = true
            selectedView = element
        }

        // change icon and background for checkable elements
        if (element.isCheckable && !element.isDisabled) {
            element.isChecked = !element.isChecked
        }
    }

    /**
     * Change the toolbar elements visibility by their ids
     * @param visibility the new visibility for the element
     * @param ids resource ids of the elements whose visibility will be changes
     */
    fun setElementsVisibility(visibility: Int, vararg ids: Int) {
        ids.forEach {
            findElementById(it)?.visibility = visibility
        }
    }

    /**
     * Set the current selected toolbar element
     * @param id resource id of the element whose visibility will be changes
     */
    fun setSelectedElement(id: Int) {
        val toolbarElement = findElementById(id)
        if (toolbarElement != null) {
            onToolbarElementClick(toolbarElement)
        }
    }

    /**
     * Set the current check state for a toolbar element
     * @param id resource id of the element whose visibility will be changes
     * @param isChecked the new state if the element should be checked or not
     */
    fun setCheckedElement(id: Int, isChecked: Boolean) {
        val toolbarElement = findElementById(id)
        if (toolbarElement != null && toolbarElement.isCheckable) {
            toolbarElement.isChecked = isChecked
        }
    }

    /**
     * Find element by id by searching all toolbars inside the group
     * @param id resource id of the element that is being searched
     */
    fun findElementById(id: Int): ToolbarElement? {

        // for each toolbar search for the element, and if found then select it
        for (i in toolbars.indices) {

            val toolbar = toolbars[i]
            val toolbarElement = toolbar.findViewById<ToolbarElement>(id)
            if (toolbarElement != null) {
                return toolbarElement
            }
        }
        return null
    }

    /**
     * Change if the toolbar elements are movable
     */
    fun setAreToolbarElementsMovable(areToolbarElementsMovable: Boolean = true) {

        // for each toolbar search for the element
        toolbars.forEach {
            it.areToolbarElementsMovable = areToolbarElementsMovable
        }
    }

    fun setOnClickListener(listener: ((v: View) -> Unit)) {
        elementsOnClickListener = listener
    }

    fun setOnLongClickListener(listener: ((v: View) -> Boolean)) {
        elementsOnLongClickListener = listener
    }

    fun setOnTouchListener(listener: ((v: View, event: MotionEvent) -> Boolean)) {
        elementsOnTouchListener = listener
    }

    fun setOnDragListener(listener: ((v: View, dragEvent: DragEvent) -> Boolean)) {
        elementsOnDragListener = listener
    }

}