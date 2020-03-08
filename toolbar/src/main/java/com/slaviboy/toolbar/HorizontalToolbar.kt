package com.slaviboy.toolbar

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.DragEvent
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat

/**
 * HorizontalToolbar toolbar for arranging elements horizontally.
 */

class HorizontalToolbar : Toolbar {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {

        // global const for the Toolbar class
        const val ELEMENTS_WIDTH = 0
        const val ELEMENTS_HEIGHT = 70
    }

    /**
     * Update the position for the elements, used when elements are rearranged or
     * elements is added or removed. Uses the elements array in order to rearrange them.
     */
    override fun updateSet() {

        val set = ConstraintSet()
        set.clone(this)
        for (i in elements.indices) {

            val element = elements[i]

            if (i == 0) {

                // layout_constraintLeft_toLeftOf for first element with the parent
                set.connect(
                    element.id,
                    ConstraintSet.LEFT,
                    this.id,
                    ConstraintSet.LEFT,
                    marginParentToElement.left
                )
            } else {

                // layout_constraintLeft_toRightOf with the element on left side of this one
                set.connect(
                    element.id,
                    ConstraintSet.LEFT,
                    elements[i - 1].id,
                    ConstraintSet.RIGHT,
                    marginElementToElement.left
                )
            }

            if (i == elements.size - 1) {

                // layout_constraintRight_toRightOf for last element with parent
                set.connect(
                    element.id,
                    ConstraintSet.RIGHT,
                    this.id,
                    ConstraintSet.RIGHT,
                    marginParentToElement.right
                )
            } else {

                // layout_constraintRight_toLeftOf with the element on right side of this one
                set.connect(
                    element.id,
                    ConstraintSet.RIGHT,
                    elements[i + 1].id,
                    ConstraintSet.LEFT,
                    0  // marginElementToElement.right
                )
            }

            // layout_constraintTop_toTopOf with parent
            set.connect(
                element.id,
                ConstraintSet.TOP,
                this.id,
                ConstraintSet.TOP,
                marginParentToElement.top
            )

            // layout_constraintBottom_toBottomOf with parent
            set.connect(
                element.id,
                ConstraintSet.BOTTOM,
                this.id,
                ConstraintSet.BOTTOM,
                marginParentToElement.bottom
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