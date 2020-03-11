package com.slaviboy.toolbar

import android.util.Log
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
    lateinit var elementsOnClickListener: ((v: View?) -> Unit)
    lateinit var elementsOnLongClickListener: ((v: View?) -> Boolean)
    lateinit var elementsOnTouchListener: ((v: View?, event: MotionEvent?) -> Boolean)
    lateinit var elementsOnDragListener: ((v: View?, dragEvent: DragEvent?) -> Boolean)

    var selectedView: ToolbarElement? = null       // current selected view id

    init {
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
            onClick(it)
            if (::elementsOnClickListener.isInitialized) {
                elementsOnClickListener.invoke(it)
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
        toolbar.setOnTouchListener(false) { v: View?, event: MotionEvent? ->

            if (::elementsOnTouchListener.isInitialized) {
                elementsOnTouchListener.invoke(v, event)
            }
            true
        }

        // drag events
        toolbar.setOnDragListener(false) { v: View?, dragEvent: DragEvent? ->
            if (::elementsOnDragListener.isInitialized) {
                elementsOnDragListener.invoke(v, dragEvent)
            }
            true
        }
    }

    /**
     * When element is clicked, change the selected element if the element
     * that is clicked isSelectable.
     */
    private fun onClick(v: View?) {

        val toolbar = v?.parent as Toolbar
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

    fun setOnClickListener(listener: ((v: View?) -> Unit)) {
        elementsOnClickListener = listener
    }

    fun setOnLongClickListener(listener: ((v: View?) -> Boolean)) {
        elementsOnLongClickListener = listener
    }

    fun setOnTouchListener(listener: ((v: View?, event: MotionEvent?) -> Boolean)) {
        elementsOnTouchListener = listener
    }

    fun setOnDragListener(listener: ((v: View?, dragEvent: DragEvent?) -> Boolean)) {
        elementsOnDragListener = listener
    }

}