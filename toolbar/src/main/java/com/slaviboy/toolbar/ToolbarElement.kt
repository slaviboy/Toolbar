package com.slaviboy.toolbar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.core.content.ContextCompat
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
        iconNormalId: Int = R.drawable.ic_default,
        iconSelectedId: Int = iconNormalId,
        iconDisabledId: Int = iconNormalId,
        iconCheckedId: Int = iconNormalId,
        tooltip: Int = R.string.default_tooltip,
        padding: Rect = Rect(),
        isSelectable: Boolean = false,
        isSelected: Boolean = false,
        isDisabled: Boolean = false,
        isCheckable: Boolean = false,
        isChecked: Boolean = false
    ) : super(context) {
        this.isDisabled = isDisabled
        this.isSelectable = isSelectable
        this.isCheckable = isCheckable
        this.setPadding(padding.left, padding.top, padding.right, padding.bottom)
        this.tooltip = resources.getString(tooltip)
        this.id = id
        this.isSelected = isSelected
        this.isChecked = isChecked
        this.iconNormal = resources.getDrawable(iconNormalId, null)
        this.iconSelected = resources.getDrawable(iconSelectedId, null)
        this.iconDisabled = resources.getDrawable(iconDisabledId, null)
        this.iconChecked = resources.getDrawable(iconCheckedId, null)
        changeState()
    }

    companion object {

        // global const for the ToolbarElement class
        const val IS_SELECTABLE = false
        const val IS_DISABLED = false
        const val IS_CHECKED = false
        const val STATE_NONE = 0
        const val STATE_NORMAL = 1
        const val STATE_DISABLED = 2
        const val STATE_CHECKED = 3
        const val STATE_SELECTED = 4
    }

    var tooltip: String?                    // tooltip text that can be used as hint to show text about current element
    var isSelectable: Boolean               // whether the element is selectable
    var isDisabled: Boolean                 // whether the element is disabled
    var isInitInToolbar: Boolean            // whether the element is initialized in its parant toolbar
    var isCheckable: Boolean                // whether the element is checkable as checkbox element
    var isChecked: Boolean                  // if current element is checked
        set(value) {
            field = value
            changeState()
        }
    var iconNormal: Drawable                // icon for normal state
    var iconSelected: Drawable              // icon for selected state
    var iconDisabled: Drawable              // icon for disabled state
    var iconChecked: Drawable               // icon for checked state
    var previousState: Int = STATE_NONE     // last recorded state
    var visibilityListener: ((visibility: Int) -> Unit)? = null

    init {

        // default attributes and properties
        scaleType = ScaleType.CENTER_INSIDE
        setBackgroundColor(Color.TRANSPARENT)
        adjustViewBounds = true
        tooltip = ""

        iconNormal = resources.getDrawable(R.drawable.ic_default, null)
        iconSelected = iconNormal
        iconDisabled = iconNormal
        iconChecked = iconNormal
        isSelectable = IS_SELECTABLE
        isSelected = false
        isInitInToolbar = false
        isCheckable = false
        isDisabled = IS_DISABLED
        isChecked = IS_CHECKED
    }

    fun applyAttributes(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val attributes =
            context!!.obtainStyledAttributes(attrs, R.styleable.ToolbarElement, defStyleAttr, 0)

        isSelectable = attributes.getBoolean(R.styleable.ToolbarElement_isSelectable, IS_SELECTABLE)
        isSelected = attributes.getBoolean(R.styleable.ToolbarElement_isSelected, false)
        isDisabled = attributes.getBoolean(R.styleable.ToolbarElement_isDisabled, IS_DISABLED)
        isCheckable = attributes.getBoolean(R.styleable.ToolbarElement_isCheckable, false)
        isChecked = attributes.getBoolean(R.styleable.ToolbarElement_isChecked, false)
        tooltip = attributes.getString(R.styleable.ToolbarElement_tooltip)

        val iconNormalTemp = attributes.getDrawable(R.styleable.ToolbarElement_iconNormal)
        if (iconNormalTemp != null) {
            iconNormal = iconNormalTemp
            iconSelected = iconNormalTemp
            iconDisabled = iconNormalTemp
            iconChecked = iconNormalTemp
        }

        val iconSelectedTemp = attributes.getDrawable(R.styleable.ToolbarElement_iconSelected)
        if (iconSelectedTemp != null) {
            iconSelected = iconSelectedTemp
        }

        val iconDisabledTemp = attributes.getDrawable(R.styleable.ToolbarElement_iconDisabled)
        if (iconDisabledTemp != null) {
            iconDisabled = iconDisabledTemp
        }

        val iconCheckedTemp = attributes.getDrawable(R.styleable.ToolbarElement_iconChecked)
        if (iconCheckedTemp != null) {
            iconChecked = iconCheckedTemp
        }

        changeState()
        attributes.recycle()
    }

    /**
     * Override the isSelected property setter method, to also change the state
     */
    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        changeState()
    }

    override fun setVisibility(visibility: Int) {
        val previousVisibility = this.visibility
        super.setVisibility(visibility)
        if (visibility != previousVisibility) {
            visibilityListener?.invoke(visibility)
        }
    }

    fun OnVisibilityListener(callback: (visibility: Int) -> Unit) {
        visibilityListener = callback
    }

    /**
     * When state is changed - disable, selected, checked, or normal(enabled)
     * update the icon, the background and the icon color, depending on the
     * global values from the parent view.
     */
    fun changeState() {

        if (parent == null) {
            return
        }

        val p = parent as Toolbar

        val currentState = getState()
        if (::onStateChangeListener.isInitialized) {
            onStateChangeListener.invoke(this, previousState, currentState)

            if (onStateChangeListenerOverride) {
                previousState = currentState
                return
            }
        }

        // change icon, icon color and background
        when {
            isDisabled -> {

                setImageDrawable(iconDisabled)
                DrawableCompat.setTint(iconDisabled, p.iconColorDisabled)
                background = p.iconBackgroundDisabled
            }
            isSelected -> {
                setImageDrawable(iconSelected)
                DrawableCompat.setTint(iconSelected, p.iconColorSelected)
                background = p.iconBackgroundSelected
            }
            isChecked -> {
                setImageDrawable(iconChecked)
                DrawableCompat.setTint(iconChecked, p.iconColorChecked)
                background = p.iconBackgroundChecked
            }
            else -> {
                setImageDrawable(iconNormal)
                DrawableCompat.setTint(iconNormal, p.iconColorNormal)
                background = p.iconBackgroundNormal
            }
        }
        previousState = currentState
    }

    /**
     * Get current state, depending on
     */
    fun getState(): Int {
        return when {
            isDisabled -> {
                STATE_DISABLED
            }
            isSelected -> {
                STATE_SELECTED
            }
            isChecked -> {
                STATE_CHECKED
            }
            else -> {
                STATE_NORMAL
            }
        }
    }

    /**
     * Get current icon, depending on given state
     */
    fun getIcon(state: Int): Drawable {
        return when (state) {
            STATE_DISABLED -> {
                iconDisabled
            }
            STATE_SELECTED -> {
                iconSelected
            }
            STATE_CHECKED -> {
                iconChecked
            }
            else -> {
                iconNormal
            }
        }
    }

    /**
     * Get current icon, depending on the current state
     */
    fun getIcon(): Drawable {
        return when {
            isDisabled -> {
                iconDisabled
            }
            isSelected -> {
                iconSelected
            }
            isChecked -> {
                iconChecked
            }
            else -> {
                iconNormal
            }
        }
    }

    fun getBackground(state: Int): Drawable? {
        val parent = this.parent as Toolbar
        return when (state) {
            STATE_DISABLED -> {
                parent.iconBackgroundDisabled
            }
            STATE_SELECTED -> {
                parent.iconBackgroundSelected
            }
            STATE_CHECKED -> {
                parent.iconBackgroundChecked
            }
            else -> {
                parent.iconBackgroundNormal
            }
        }
    }

    lateinit var onStateChangeListener: ((element: ToolbarElement, previousState: Int, currentState: Int) -> Unit)
    var onStateChangeListenerOverride: Boolean = false

    /**
     * Set hte listener for state changes
     */
    fun setOnStateChangeListener(
        override: Boolean,
        listener: ((element: ToolbarElement, previousState: Int, currentState: Int) -> Unit)
    ) {
        onStateChangeListener = listener
        onStateChangeListenerOverride = override
    }
}