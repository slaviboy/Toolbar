package com.slaviboy.toolbar

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import java.security.InvalidParameterException

/**
 * Abstract class for creating toolbar - Vertical, Horizontal, Diagonal...
 */
open abstract class Toolbar : ConstraintLayout, View.OnClickListener, View.OnLongClickListener,
    View.OnDragListener, View.OnTouchListener {

    constructor(context: Context) : super(context) {
        applyAttributes(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        applyAttributes(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        applyAttributes(context, attrs, defStyleAttr)
    }

    companion object {

        // global const for the Toolbar class
        const val ELEMENTS_WIDTH = 70
        const val ELEMENTS_HEIGHT = 70
        const val ICON_CORNER_RADIUS: Float = 0f
        const val ICON_COLOR_NORMAL: Int = Color.WHITE
        const val ICON_COLOR_DISABLED: Int = Color.GRAY
        const val ICON_COLOR_SELECTED: Int = Color.WHITE
        const val ICON_COLOR_CHECKED: Int = Color.WHITE
        const val MARGIN_ELEMENT_TO_ELEMENT: Int = 5
        const val MARGIN_PARENT_TO_ELEMENT: Int = 0
        const val MARGIN_SINGLE_ELEMENT: Int = 0
        const val DRAG_SHADOW_OPACITY: Float = 0.6f

        /**
         * Inline function that is called, when the final measurement is made and
         * the view is about to be draw.
         */
        inline fun View.afterMeasured(crossinline f: View.() -> Unit) {
            viewTreeObserver.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (measuredWidth > 0 && measuredHeight > 0) {
                        viewTreeObserver.removeOnGlobalLayoutListener(this)
                        f()
                    }
                }
            })
        }
    }

    /**
     * Apply the attributes from the xml custom attributes, for the
     * Toolbar class.
     */
    fun applyAttributes(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {

        val attributes =
            context!!.obtainStyledAttributes(attrs, R.styleable.Toolbar, defStyleAttr, 0)

        // size of the elements
        val WIDTH =
            if (this is VerticalToolbar) VerticalToolbar.ELEMENTS_WIDTH else HorizontalToolbar.ELEMENTS_WIDTH
        val HEIGHT =
            if (this is VerticalToolbar) VerticalToolbar.ELEMENTS_HEIGHT else HorizontalToolbar.ELEMENTS_HEIGHT
        elementsWidth =
            attributes.getDimensionPixelSize(R.styleable.Toolbar_elementsWidth, WIDTH)
        elementsHeight =
            attributes.getDimensionPixelSize(R.styleable.Toolbar_elementsHeight, HEIGHT)


        // margin between element and another element
        val marginElementToElementTemp = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginElementToElement,
            MARGIN_ELEMENT_TO_ELEMENT
        )
        val topE2E = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginElementToElementTop,
            marginElementToElementTemp
        )
        val leftE2E = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginElementToElementLeft,
            marginElementToElementTemp
        )
        val rightE2E = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginElementToElementRight,
            marginElementToElementTemp
        )
        val bottomE2E = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginElementToElementBottom,
            marginElementToElementTemp
        )
        marginElementToElement = Rect(leftE2E, topE2E, rightE2E, bottomE2E)


        // margin for single element between it and the parent view
        val marginSingleElementTemp = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginSingleElement,
            MARGIN_SINGLE_ELEMENT
        )
        val topSE = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginSingleElementTop,
            marginSingleElementTemp
        )
        val leftSE = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginSingleElementLeft,
            marginSingleElementTemp
        )
        val rightSE = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginSingleElementRight,
            marginSingleElementTemp
        )
        val bottomSE = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginSingleElementBottom,
            marginSingleElementTemp
        )
        marginSingleElement = Rect(leftSE, topSE, rightSE, bottomSE)


        dragShadowOpacity =
            attributes.getFloat(R.styleable.Toolbar_dragShadowOpacity, DRAG_SHADOW_OPACITY)

        // margin between parent and element
        val marginParentToElementTemp = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginParentToElement,
            MARGIN_PARENT_TO_ELEMENT
        )
        val topP2E = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginParentToElementTop,
            marginParentToElementTemp
        )
        val leftP2E = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginParentToElementLeft,
            marginParentToElementTemp
        )
        val rightP2E = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginParentToElementRight,
            marginParentToElementTemp
        )
        val bottomP2E = attributes.getDimensionPixelSize(
            R.styleable.Toolbar_marginParentToElementBottom,
            marginParentToElementTemp
        )
        marginParentToElement = Rect(leftP2E, topP2E, rightP2E, bottomP2E)

        // corner radii for each corner
        val iconCornerRadius =
            attributes.getDimension(R.styleable.Toolbar_iconCornerRadius, ICON_CORNER_RADIUS)
        iconTopLeftCornerRadius =
            attributes.getDimension(R.styleable.Toolbar_iconTopLeftCornerRadius, iconCornerRadius)
        iconTopRightCornerRadius =
            attributes.getDimension(R.styleable.Toolbar_iconTopRightCornerRadius, iconCornerRadius)
        iconBottomLeftCornerRadius = attributes.getDimension(
            R.styleable.Toolbar_iconBottomLeftCornerRadius,
            iconCornerRadius
        )
        iconBottomRightCornerRadius = attributes.getDimension(
            R.styleable.Toolbar_iconBottomRightCornerRadius,
            iconCornerRadius
        )

        // icon colors
        iconColorNormal =
            attributes.getColor(R.styleable.Toolbar_iconColorNormal, ICON_COLOR_NORMAL)
        iconColorDisabled =
            attributes.getColor(R.styleable.Toolbar_iconColorDisabled, ICON_COLOR_DISABLED)
        iconColorSelected =
            attributes.getColor(R.styleable.Toolbar_iconColorSelected, ICON_COLOR_SELECTED)
        iconColorChecked =
            attributes.getColor(R.styleable.Toolbar_iconColorChecked, ICON_COLOR_CHECKED)

        // elements backgrounds
        iconBackgroundSelected = attributes.getDrawable(R.styleable.Toolbar_iconBackgroundSelected)
        iconBackgroundChecked = attributes.getDrawable(R.styleable.Toolbar_iconBackgroundChecked)
        iconBackgroundNormal = attributes.getDrawable(R.styleable.Toolbar_iconBackgroundNormal)
        iconBackgroundDisabled = attributes.getDrawable(R.styleable.Toolbar_iconBackgroundDisabled)

        // drag and drop backgrounds on hover element
        dropTopBackground = attributes.getDrawable(R.styleable.Toolbar_dropTopBackground)
        dropLeftBackground = attributes.getDrawable(R.styleable.Toolbar_dropLeftBackground)
        dropRightBackground = attributes.getDrawable(R.styleable.Toolbar_dropRightBackground)
        dropBottomBackground = attributes.getDrawable(R.styleable.Toolbar_dropBottomBackground)
        setDefaultBackgrounds()

        attributes.recycle()
    }

    val elements =
        ArrayList<ToolbarElement>()            // element that are in this constrain layout
    var marginSingleElement: Rect                         // margin for single ToolbarElement with the parent
    var marginElementToElement: Rect                      // margin between two ToolbarElement elements
    var marginParentToElement: Rect                       // margin between the parent container and all ToolbarElement elements
    var elementsWidth: Int                                // width of all ImageButton elements
    var elementsHeight: Int                               // height of all ImageButton elements
    var selectedView: ToolbarElement? = null              // current selected ImageButton view id
    var toolbarGroup: ToolbarGroup? =
        null                // the group containing this toolbar if such exist, used to transfer selectable elements
    var dragShadowOpacity: Float                          // the opacity for the drag shadow

    // background for drag and drop, when user hovers top, left, right or bottom side
    var dropBottomBackground: Drawable? = null
    var dropLeftBackground: Drawable? = null
    var dropTopBackground: Drawable? = null
    var dropRightBackground: Drawable? = null

    // icon for different states for the elements of the toolbar
    var iconColorNormal: Int
    var iconColorDisabled: Int
    var iconColorSelected: Int
    var iconColorChecked: Int

    // background behind the icons for different states for the elements of the toolbar
    var iconBackgroundNormal: Drawable? = null
    var iconBackgroundDisabled: Drawable? = null
    var iconBackgroundSelected: Drawable? = null
    var iconBackgroundChecked: Drawable? = null

    // corner radii for each corner
    var iconTopLeftCornerRadius: Float
    var iconTopRightCornerRadius: Float
    var iconBottomLeftCornerRadius: Float
    var iconBottomRightCornerRadius: Float

    // listeners and whether to override the existing one in this class and stop them from executing
    lateinit var elementsOnClickListener: ((v: View?) -> Unit)
    lateinit var elementsOnLongClickListener: ((v: View?) -> Boolean)
    lateinit var elementsOnTouchListener: ((v: View?, event: MotionEvent?) -> Boolean)
    lateinit var elementsOnDragListener: ((v: View?, dragEvent: DragEvent?) -> Boolean)
    var elementsOnClickOverride: Boolean = false
    var elementsOnLongClickOverride: Boolean = false
    var elementsOnTouchOverride: Boolean = false
    var elementsOnDragOverride: Boolean = false

    // listener for state change for all elements - normal(enabled), disabled, selected, checked
    lateinit var onElementsStateChangeListener: ((element: ToolbarElement, previousState: Int, currentState: Int) -> Unit)
    var onElementsStateChangeListenerOverride: Boolean = false

    init {

        // set default values
        iconTopLeftCornerRadius = ICON_CORNER_RADIUS
        iconTopRightCornerRadius = ICON_CORNER_RADIUS
        iconBottomLeftCornerRadius = ICON_CORNER_RADIUS
        iconBottomRightCornerRadius = ICON_CORNER_RADIUS
        iconColorNormal = ICON_COLOR_NORMAL
        iconColorDisabled = ICON_COLOR_DISABLED
        iconColorSelected = ICON_COLOR_SELECTED
        iconColorChecked = ICON_COLOR_CHECKED
        elementsWidth = ELEMENTS_WIDTH
        elementsHeight = ELEMENTS_HEIGHT
        marginElementToElement = Rect(
            MARGIN_ELEMENT_TO_ELEMENT,
            MARGIN_ELEMENT_TO_ELEMENT,
            MARGIN_ELEMENT_TO_ELEMENT,
            MARGIN_ELEMENT_TO_ELEMENT
        )
        marginParentToElement = Rect(
            MARGIN_PARENT_TO_ELEMENT,
            MARGIN_PARENT_TO_ELEMENT,
            MARGIN_PARENT_TO_ELEMENT,
            MARGIN_PARENT_TO_ELEMENT
        )
        marginSingleElement = Rect(
            MARGIN_SINGLE_ELEMENT,
            MARGIN_SINGLE_ELEMENT,
            MARGIN_SINGLE_ELEMENT,
            MARGIN_SINGLE_ELEMENT
        )
        dragShadowOpacity = DRAG_SHADOW_OPACITY

        setDefaultBackgrounds()

        this.afterMeasured {
            val elements: Array<ToolbarElement> =
                Array(childCount) { i -> this@Toolbar.getChildAt(i) as ToolbarElement }
            add(0, *elements)
        }
    }

    fun setDefaultBackgrounds() {

        // set default drop backgrounds
        if (dropTopBackground == null) {
            dropTopBackground =
                ContextCompat.getDrawable(context, R.drawable.default_drop_top_background)
        }
        if (dropLeftBackground == null) {
            dropLeftBackground =
                ContextCompat.getDrawable(context, R.drawable.default_drop_left_background)
        }
        if (dropRightBackground == null) {
            dropRightBackground =
                ContextCompat.getDrawable(context, R.drawable.default_drop_right_background)
        }
        if (dropBottomBackground == null) {
            dropBottomBackground =
                ContextCompat.getDrawable(context, R.drawable.default_drop_bottom_background)
        }

        // set default toolbar background
        if (background == null) {
            background = ContextCompat.getDrawable(context, R.drawable.default_toolbar_background)
        }
    }

    fun setOnClickListener(override: Boolean, listener: ((v: View?) -> Unit)) {
        elementsOnClickListener = listener
        elementsOnClickOverride = override
    }

    fun setOnLongClickListener(override: Boolean, listener: ((v: View?) -> Boolean)) {
        elementsOnLongClickListener = listener
        elementsOnLongClickOverride = override
    }

    fun setOnTouchListener(
        override: Boolean,
        listener: ((v: View?, event: MotionEvent?) -> Boolean)
    ) {
        elementsOnTouchListener = listener
        elementsOnTouchOverride = override
    }

    fun setOnDragListener(
        override: Boolean,
        listener: ((v: View?, dragEvent: DragEvent?) -> Boolean)
    ) {
        elementsOnDragListener = listener
        elementsOnDragOverride = override
    }

    override fun onDrag(v: View?, dragEvent: DragEvent?): Boolean {

        if (::elementsOnDragListener.isInitialized) {
            val returnValue = elementsOnDragListener.invoke(v, dragEvent)

            // return so the code bellow, does not get executed
            if (elementsOnDragOverride) {
                return returnValue
            }
        }

        val element = v as ToolbarElement
        when (dragEvent?.action) {
            DragEvent.ACTION_DRAG_ENDED -> {
                return true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                element.changeState()
                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                return true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                return true
            }
            DragEvent.ACTION_DROP -> {

                element.changeState()

                val dragView = dragEvent.localState as ToolbarElement
                if (dragView.id == element.id) return true

                // remove the ImageButton from its existing toolbar
                val dragViewParent = dragView.parent as Toolbar
                val viewParent = element.parent as Toolbar

                // make sure selectable element are dropped between toolbars in same toolbar group
                if (dragView.isSelectable &&
                    dragViewParent.id != viewParent.id &&
                    (dragViewParent.toolbarGroup == null || viewParent.toolbarGroup == null) ||
                    (dragViewParent.toolbarGroup != null && dragViewParent.toolbarGroup != viewParent.toolbarGroup)
                ) {
                    throw IllegalStateException("You can not transfer selectable object to another toolbar, use ToolbarGroup instead!")
                }

                // find the start index, where the ImageButton will  be placed in the toolbar
                var nextIndex = 0
                for (i in viewParent.elements.indices) {
                    if (viewParent.elements[i].id == element.id) {

                        val ifCheck =
                            if (element.parent is HorizontalToolbar) dragEvent.x < element.width / 2 else dragEvent.y < element.height / 2
                        nextIndex = if (ifCheck) {
                            i
                        } else {
                            i + 1
                        }
                        break
                    }
                }

                val previousIndex = dragViewParent.elements.indexOf(dragView)
                var isFirstCall: Boolean = true

                /**
                 * there is a problem using a animation for the layout transition, the child is still
                 * not removed until the animation finished, so a listener is needed, that way once the
                 * animation is done, the same view is added to its new parent
                 */
                dragViewParent.layoutTransition.addTransitionListener(object :
                    LayoutTransition.TransitionListener {
                    override fun startTransition(
                        transition: LayoutTransition?, container: ViewGroup?,
                        view: View?, transitionType: Int
                    ) {
                    }

                    override fun endTransition(
                        transition: LayoutTransition?, container: ViewGroup?,
                        view: View?, transitionType: Int
                    ) {

                        // remove the listener right after it is executed
                        dragViewParent.layoutTransition.removeTransitionListener(this)

                        if (view?.id == dragView.id && isFirstCall) {

                            // if the element location is changed in the same toolbar
                            if (dragViewParent.id == viewParent.id) {

                                // since it is removed from same toolbar
                                if (nextIndex > previousIndex) {
                                    nextIndex--
                                }
                            }

                            // add the same view to its new parent view
                            viewParent.add(nextIndex, dragView)

                            // change the selected element, for both toolbars
                            if (dragView.isSelected) {
                                dragViewParent.selectedView = null
                                viewParent.selectedView = dragView
                            }

                            isFirstCall = false
                        }
                    }
                })

                // remove view from its parent, and wait for the endTransition to be called
                dragViewParent.remove(dragView)

                return true
            }
            DragEvent.ACTION_DRAG_LOCATION -> {

                val dragView = dragEvent.localState as ToolbarElement
                if (element.id == dragView.id) {
                    return true
                }

                if (element.parent is VerticalToolbar) {

                    // change the background to show where the ImageButton will be dropped
                    if (dragEvent.y < element.height / 2) {
                        element.background = dropTopBackground
                    } else {
                        element.background = dropBottomBackground
                    }

                } else if (element.parent is HorizontalToolbar) {

                    // change the background to show where the ImageButton will be dropped
                    if (dragEvent.x < element.width / 2) {
                        element.background = dropLeftBackground
                    } else {
                        element.background = dropRightBackground
                    }
                }
                return true
            }
            else -> return false
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {

        if (::elementsOnTouchListener.isInitialized) {
            val returnValue = elementsOnTouchListener.invoke(v, event)

            // return so the code bellow, does not get executed
            if (elementsOnTouchOverride) {
                return returnValue
            }
        }

        // write code here in case onTouch event is needed

        return false
    }

    override fun onClick(v: View?) {

        if (::elementsOnClickListener.isInitialized) {
            elementsOnClickListener.invoke(v)

            // return so the code bellow, does not get executed
            if (elementsOnClickOverride) {
                return
            }
        }

        // change icon and background for selectable elements
        val element = v as ToolbarElement
        if (element.isSelectable && !element.isDisabled) {

            // change color, set current and remove previous selection
            selectedView?.isSelected = false
            selectedView = element
            selectedView?.isSelected = true
        }

        // change icon and background for checkable elements
        if (element.isCheckable && !element.isDisabled) {
            element.isChecked = !element.isChecked
        }
    }

    override fun onLongClick(v: View?): Boolean {

        if (::elementsOnLongClickListener.isInitialized) {
            val returnValue = elementsOnLongClickListener.invoke(v)

            // return so the code bellow, does not get executed
            if (elementsOnLongClickOverride) {
                return returnValue
            }
        }

        // instantiates the drag shadow builder
        val element = v as ToolbarElement
        val previousIconColor = when {
            element.isDisabled -> {
                iconColorDisabled
            }
            element.isSelected -> {
                iconColorSelected
            }
            element.isChecked -> {
                iconColorChecked
            }
            else -> {
                iconColorNormal
            }
        }
        val myShadow = DragShadowBuilder(
            element, Color.BLACK, Color.WHITE, previousIconColor,
            floatArrayOf(
                iconTopLeftCornerRadius, iconTopLeftCornerRadius,
                iconTopRightCornerRadius, iconTopRightCornerRadius,
                iconBottomRightCornerRadius, iconBottomRightCornerRadius,
                iconBottomLeftCornerRadius, iconBottomLeftCornerRadius
            ),
            dragShadowOpacity
        )

        // starts the drag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            element.startDragAndDrop(null, myShadow, element, 0)
        } else {
            element.startDrag(null, myShadow, element, 0)
        }

        return true
    }

    /**
     * Add new ToolbarElement element to this toolbar(constrain layout) and add it to the
     * toolbar elements array with start index given as argument. Attach the acquired
     * properties to the new toolbar element and set listeners.
     */
    fun add(startIndex: Int = 0, vararg elements: ToolbarElement) {

        if (elements.isNotEmpty()) {
            visibility = View.VISIBLE
        }

        for (i in elements.indices) {

            val element = elements[i]
            if (element.isInitInToolbar) {
                continue
            }

            element.setOnLongClickListener(this)
            element.setOnClickListener(this)
            element.setOnDragListener(this)
            element.setOnTouchListener(this)
            element.isInitInToolbar = true
            element.OnVisibilityListener {
                updateSet()
            }

            checkException(element)

            if (indexOfChild(element) == -1) {
                addView(element, 0)
            }
            element.changeState()

            // attach listener after the element is added to parent
            if (::onElementsStateChangeListener.isInitialized) {
                element.setOnStateChangeListener(
                    onElementsStateChangeListenerOverride
                ) { e: ToolbarElement, previousState: Int, currentState: Int ->
                    onElementsStateChangeListener.invoke(e, previousState, currentState)
                }
            }

            // for transferring element from another toolbar as last element
            if (startIndex + i >= this.elements.size) {
                this.elements.add(element)
            } else {
                this.elements.add(startIndex + i, element)
            }
        }

        updateSet()
    }

    /**
     * Check the toolbar element for exceptions, when added to the toolbar.
     */
    fun checkException(element: ToolbarElement) {

        if (selectedView == null && element.isSelected) {
            selectedView = element
        } else if (selectedView != null && element.isSelected && selectedView?.id != element.id) {
            throw InvalidParameterException(
                "Cannot have two or more elements selected at the same time: ${getId(selectedView!!)} and ${getId(
                    element
                )}"
            )
        }

        if ((element.isSelectable && element.isCheckable) ||
            (element.isSelectable && element.isChecked) ||
            (element.isSelected && element.isCheckable) ||
            (element.isSelected && element.isChecked)
        ) {
            throw InvalidParameterException(
                "Toolbar elements can`t be selectable and checkable at the same time: ${getId(
                    element
                )}"
            )
        }

        if (!element.isSelectable && element.isSelected) {
            throw InvalidParameterException(
                "Toolbar elements must be selectable, in order to be selected: ${getId(element)}"
            )
        }

        if (!element.isCheckable && element.isChecked) {
            throw InvalidParameterException(
                "Toolbar elements must be checkable, in order to be checked: ${getId(element)}"
            )
        }
    }

    /**
     * Get the id as a string, used when throwing a exception with the view id,
     * where the problem was found.
     */
    fun getId(view: View): String {
        return if (view.id == View.NO_ID) "no-id" else view.resources.getResourceName(view.id)
    }

    /**
     * Update the constrain set, in order to update the position of the ImageButtons
     * in this constrain layout.
     */
    open abstract fun updateSet()

    /**
     * Remove ImageButton element, from current toolbar(constrain layout) and update
     * set, that will change the elements position, margin and padding.
     */
    fun remove(element: ToolbarElement) {

        elements.removeAt(elements.indexOf(element))
        element.isInitInToolbar = false

        if (elements.isEmpty()) {
            visibility = View.GONE
        }

        removeView(element)
        updateSet()
    }


    /**
     * Set the listener for the state change, for all elements added to this
     * toolbar.
     */
    fun setOnElementsStateChangeListener(
        override: Boolean,
        listener: ((element: ToolbarElement, previousState: Int, currentState: Int) -> Unit)
    ) {
        onElementsStateChangeListener = listener
        onElementsStateChangeListenerOverride = override
    }
}