package com.slaviboy.toolbar

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Elements for the toolbars, those are ImageButtons that have some custom
 * properties, that are used in the toolbars.
 */
class ToolbarElement : androidx.appcompat.widget.AppCompatImageButton {

    constructor(context: Context?) : super(context) {
        applyAttributes(context, null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        applyAttributes(context, attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        applyAttributes(context, attrs, defStyleAttr)
    }

    /**
     * Constructor for generating toolbar element using Kotlin
     */
    constructor(
        context: Context?,
        id: Int = View.generateViewId(),
        drawable: Int = R.drawable.ic_default,
        tooltip: Int = R.string.default_tooltip,
        padding: Rect = Rect(),
        isSelectable: Boolean = false,
        isSelected: Boolean = false,
        isDisabled: Boolean = false
    ) : super(context) {
        this.isDisabled = isDisabled
        this.isSelectable = isSelectable
        this.setPadding(padding.left, padding.top, padding.right, padding.bottom)
        this.tooltip = resources.getString(tooltip)
        this.id = id
        this.isSelected = isSelected
        setImageResource(drawable)
    }

    companion object {

        // global const for the Toolbar class
        const val IS_SELECTABLE = false
        const val IS_DISABLED = false
    }

    var tooltip: String?            // tooltip text that can be used as hint to show text about current element
    var isSelectable: Boolean       // whether the element is selectable
    var isDisabled: Boolean         // whether the element is disabled
    var isInitInToolbar: Boolean    // whether the element is initialized in its parant toolbar

    init {

        // default attributes and properties
        scaleType = ScaleType.CENTER_INSIDE
        setBackgroundColor(Color.TRANSPARENT)
        adjustViewBounds = true

        isSelectable = IS_SELECTABLE
        isSelected = false
        isInitInToolbar = false
        isDisabled = IS_DISABLED
        tooltip = ""
    }

    fun applyAttributes(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val attributes =
            context!!.obtainStyledAttributes(attrs, R.styleable.ToolbarElement, defStyleAttr, 0)

        isSelectable = attributes.getBoolean(R.styleable.ToolbarElement_isSelectable, IS_SELECTABLE)
        isSelected = attributes.getBoolean(R.styleable.ToolbarElement_isSelected, false)
        isDisabled = attributes.getBoolean(R.styleable.ToolbarElement_isDisabled, IS_DISABLED)
        tooltip = attributes.getString(R.styleable.ToolbarElement_tooltip)

        attributes.recycle()
    }
}