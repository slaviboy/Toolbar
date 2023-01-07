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

import android.content.Context
import android.graphics.*
import android.opengl.Visibility
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat

/**
 * VerticalToolbar for arranging elements vertically.
 */

open class VerticalToolbar : Toolbar {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {

        // global const for the Toolbar class
        const val ELEMENTS_WIDTH = 70
        const val ELEMENTS_HEIGHT = 0
    }

    override fun updateSet() {

        // get all elements that have visibility different from GONE
        val notGoneElement = ArrayList<ToolbarElement>()
        elements.forEach {
            if (it.visibility != View.GONE) {
                notGoneElement.add(it)
            }
        }

        val set = ConstraintSet()
        set.clone(this)
        for (i in notGoneElement.indices) {

            val element = notGoneElement[i]

            if (i == 0) {
                val marginTop = if (notGoneElement.size == 1) {
                    marginSingleElement.top
                } else {
                    marginParentToElement.top
                }

                // layout_constraintTop_toTopOf for first element with the parent
                set.connect(
                    element.id,
                    ConstraintSet.TOP,
                    this.id,
                    ConstraintSet.TOP,
                    marginTop
                )
            } else {
                // layout_constraintTop_toBottomOf with the element on top of this one
                set.connect(
                    element.id,
                    ConstraintSet.TOP,
                    notGoneElement[i - 1].id,
                    ConstraintSet.BOTTOM,
                    marginElementToElement.top
                )
            }

            if (i == notGoneElement.size - 1) {

                val marginBottom = if (notGoneElement.size == 1) {
                    marginSingleElement.bottom
                } else {
                    marginParentToElement.bottom
                }

                // layout_constraintBottom_toBottomOf for last element with parent
                set.connect(
                    element.id,
                    ConstraintSet.BOTTOM,
                    this.id,
                    ConstraintSet.BOTTOM,
                    marginBottom
                )
            } else {
                // layout_constraintBottom_toTopOf with the element below this one
                set.connect(
                    element.id,
                    ConstraintSet.BOTTOM,
                    notGoneElement[i + 1].id,
                    ConstraintSet.TOP,
                    0  // marginElementToElement.bottom
                )
            }

            val marginLeft = if (notGoneElement.size == 1) {
                marginSingleElement.left
            } else {
                marginParentToElement.left
            }

            // layout_constraintLeft_toLeftOf with parent
            set.connect(
                element.id,
                ConstraintSet.LEFT,
                this.id,
                ConstraintSet.LEFT,
                marginLeft
            )

            val marginRight = if (notGoneElement.size == 1) {
                marginSingleElement.right
            } else {
                marginParentToElement.right
            }

            // layout_constraintRight_toRightOf with parent
            set.connect(
                element.id,
                ConstraintSet.RIGHT,
                this.id,
                ConstraintSet.RIGHT,
                marginRight
            )

            // set size to the specified one or WRAP_CONTENT
            if (elementsWidth > 0) {
                set.constrainWidth(element.id, elementsWidth)
            } else {
                set.constrainWidth(element.id, ConstraintSet.WRAP_CONTENT)
            }

            if (elementsHeight > 0) {
                set.constrainHeight(element.id, elementsHeight)
            } else {
                set.constrainHeight(element.id, ConstraintSet.WRAP_CONTENT)
            }
        }
        set.applyTo(this)
    }
}