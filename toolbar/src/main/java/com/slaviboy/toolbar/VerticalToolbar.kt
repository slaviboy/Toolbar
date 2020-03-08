package com.slaviboy.toolbar

import android.content.Context
import android.graphics.*
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

class VerticalToolbar : Toolbar {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
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

        val set = ConstraintSet()
        set.clone(this)
        for (i in elements.indices) {

            val element = elements[i]

            if (i == 0) {
                // layout_constraintTop_toTopOf for first element with the parent
                set.connect(
                    element.id,
                    ConstraintSet.TOP,
                    this.id,
                    ConstraintSet.TOP,
                    marginParentToElement.top
                )
            } else {
                // layout_constraintTop_toBottomOf with the element on top of this one
                set.connect(
                    element.id,
                    ConstraintSet.TOP,
                    elements[i - 1].id,
                    ConstraintSet.BOTTOM,
                    marginElementToElement.top
                )
            }

            if (i == elements.size - 1) {
                // layout_constraintBottom_toBottomOf for last element with parent
                set.connect(
                    element.id,
                    ConstraintSet.BOTTOM,
                    this.id,
                    ConstraintSet.BOTTOM,
                    marginParentToElement.bottom
                )
            } else {
                // layout_constraintBottom_toTopOf with the element below this one
                set.connect(
                    element.id,
                    ConstraintSet.BOTTOM,
                    elements[i + 1].id,
                    ConstraintSet.TOP,
                    0  // marginElementToElement.bottom
                )
            }

            // layout_constraintLeft_toLeftOf with parent
            set.connect(
                element.id,
                ConstraintSet.LEFT,
                this.id,
                ConstraintSet.LEFT,
                marginParentToElement.left
            )

            // layout_constraintRight_toRightOf with parent
            set.connect(
                element.id,
                ConstraintSet.RIGHT,
                this.id,
                ConstraintSet.RIGHT,
                marginParentToElement.right
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