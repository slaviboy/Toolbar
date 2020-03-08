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
import androidx.core.graphics.drawable.DrawableCompat

/**
 * Abstract class for creating toolbar - Vertical, Horizontal, Diagonal...
 */
open abstract class Toolbar : ConstraintLayout, View.OnClickListener, View.OnLongClickListener,
        View.OnDragListener, View.OnTouchListener {

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

    companion object {

        // global const for the Toolbar class
        const val ELEMENTS_WIDTH = 70
        const val ELEMENTS_HEIGHT = 70
        const val ICON_COLOR_NORMAL: Int = Color.WHITE
        const val ICON_COLOR_DISABLED: Int = Color.GRAY
        const val ICON_COLOR_SELECTED: Int = Color.BLUE
        const val MARGIN_ELEMENT_TO_ELEMENT: Int = 5
        const val MARGIN_PARENT_TO_ELEMENT: Int = 0

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

        iconColorNormal =
                attributes.getColor(R.styleable.Toolbar_iconColorNormal, ICON_COLOR_NORMAL)
        iconColorDisabled =
                attributes.getColor(R.styleable.Toolbar_iconColorDisabled, ICON_COLOR_DISABLED)
        iconColorSelected =
                attributes.getColor(R.styleable.Toolbar_iconColorSelected, ICON_COLOR_SELECTED)

        val WIDTH =
                if (this is VerticalToolbar) VerticalToolbar.ELEMENTS_WIDTH else HorizontalToolbar.ELEMENTS_WIDTH
        val HEIGHT =
                if (this is VerticalToolbar) VerticalToolbar.ELEMENTS_HEIGHT else HorizontalToolbar.ELEMENTS_HEIGHT
        elementsWidth =
                attributes.getDimensionPixelSize(R.styleable.Toolbar_elementsWidth, WIDTH)
        elementsHeight =
                attributes.getDimensionPixelSize(R.styleable.Toolbar_elementsHeight, HEIGHT)


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

        selectedElementsBackground = attributes.getDrawable(R.styleable.Toolbar_selectedElementsBackground)
        dropTopBackground = attributes.getDrawable(R.styleable.Toolbar_dropTopBackground)
        dropLeftBackground = attributes.getDrawable(R.styleable.Toolbar_dropLeftBackground)
        dropRightBackground = attributes.getDrawable(R.styleable.Toolbar_dropRightBackground)
        dropBottomBackground = attributes.getDrawable(R.styleable.Toolbar_dropBottomBackground)
        setBackgrounds()

        attributes.recycle()
    }

    val elements = ArrayList<ToolbarElement>()            // element that are in this constrain layout
    var marginElementToElement: Rect                      // margin between two ImageButton elements
    var marginParentToElement: Rect                       // margin between the parent container and all ImageButton elements
    var elementsWidth: Int                                // width of all ImageButton elements
    var elementsHeight: Int                               // height of all ImageButton elements
    var iconColorNormal: Int                              // icon color for the ImageButton when element is in normal state
    var iconColorDisabled: Int                            // icon color for the ImageButton when element is in disabled state
    var iconColorSelected: Int                            // icon color for the ImageButton when element is in disabled state
    var selectedViewId: Int = -1                          // current selected ImageButton view id
    var toolbarGroup: ToolbarGroup? = null                // the group containing this toolbar if such exist, used to transfer selectable elements
    var dropBottomBackground: Drawable? = null            // background for drag and drop, when user hovers the bottom part of the view
    var dropLeftBackground: Drawable? = null              // background for drag and drop, when user hovers the left part of the view
    var dropTopBackground: Drawable? = null               // background for drag and drop, when user hovers the top part of the view
    var dropRightBackground: Drawable? = null             // background for drag and drop, when user hovers the right part of the view
    var selectedElementsBackground: Drawable? = null      // background for selected elements

    // listeners and whether to override the existing one in this class and stop them from executing
    lateinit var elementsOnClickListener: ((v: View?) -> Unit)
    lateinit var elementsOnLongClickListener: ((v: View?) -> Boolean)
    lateinit var elementsOnTouchListener: ((v: View?, event: MotionEvent?) -> Boolean)
    lateinit var elementsOnDragListener: ((v: View?, dragEvent: DragEvent?) -> Boolean)
    var elementsOnClickOverride: Boolean = false
    var elementsOnLongClickOverride: Boolean = false
    var elementsOnTouchOverride: Boolean = false
    var elementsOnDragOverride: Boolean = false

    init {

        // set default values
        iconColorNormal = ICON_COLOR_NORMAL
        iconColorDisabled = ICON_COLOR_DISABLED
        iconColorSelected = ICON_COLOR_SELECTED
        elementsWidth = ELEMENTS_WIDTH
        elementsHeight = ELEMENTS_HEIGHT
        marginElementToElement = Rect(MARGIN_ELEMENT_TO_ELEMENT, MARGIN_ELEMENT_TO_ELEMENT, MARGIN_ELEMENT_TO_ELEMENT, MARGIN_ELEMENT_TO_ELEMENT)
        marginParentToElement = Rect(MARGIN_PARENT_TO_ELEMENT, MARGIN_PARENT_TO_ELEMENT, MARGIN_PARENT_TO_ELEMENT, MARGIN_PARENT_TO_ELEMENT)

        setBackgrounds()

        this.afterMeasured {
            val elements: Array<ToolbarElement> =
                    Array(childCount) { i -> this@Toolbar.getChildAt(i) as ToolbarElement }
            add(0, *elements)
        }
    }

    fun setBackgrounds() {

        // set default drop backgrounds
        if (dropTopBackground == null) {
            dropTopBackground = ContextCompat.getDrawable(context, R.drawable.default_drop_top_background)
        }
        if (dropLeftBackground == null) {
            dropLeftBackground = ContextCompat.getDrawable(context, R.drawable.default_drop_left_background)
        }
        if (dropRightBackground == null) {
            dropRightBackground = ContextCompat.getDrawable(context, R.drawable.default_drop_right_background)
        }
        if (dropBottomBackground == null) {
            dropBottomBackground = ContextCompat.getDrawable(context, R.drawable.default_drop_bottom_background)
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

    fun setOnTouchListener(override: Boolean, listener: ((v: View?, event: MotionEvent?) -> Boolean)) {
        elementsOnTouchListener = listener
        elementsOnTouchOverride = override
    }

    fun setOnDragListener(override: Boolean, listener: ((v: View?, dragEvent: DragEvent?) -> Boolean)) {
        elementsOnDragListener = listener
        elementsOnDragOverride = override
    }

    /**
     * Set the background for the hovered view when, the dragView exist the current
     * view it was hovering.
     * @param view the view that was hovered
     */
    fun dragExitBackground(view: View) {
        val parent = view.parent as Toolbar
        if (parent.selectedElementsBackground != null && view.id == selectedViewId) {
            view.background = selectedElementsBackground
        } else {
            view.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun onDrag(v: View?, dragEvent: DragEvent?): Boolean {

        if (::elementsOnDragListener.isInitialized) {
            val returnValue = elementsOnDragListener.invoke(v, dragEvent)

            // return so the code bellow, does not get executed
            if (elementsOnDragOverride) {
                return returnValue
            }
        }

        val view = v as ToolbarElement
        when (dragEvent?.action) {
            DragEvent.ACTION_DRAG_ENDED -> {
                return true
            }
            DragEvent.ACTION_DRAG_EXITED -> {
                dragExitBackground(view)
                return true
            }
            DragEvent.ACTION_DRAG_ENTERED -> {
                return true
            }
            DragEvent.ACTION_DRAG_STARTED -> {
                return true
            }
            DragEvent.ACTION_DROP -> {

                dragExitBackground(view)

                val dragView = dragEvent.localState as ToolbarElement
                if (dragView.id == view.id) return true

                // remove the ImageButton from its existing toolbar
                val dragViewParent = dragView.parent as Toolbar
                val viewParent = view.parent as Toolbar

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
                    if (viewParent.elements[i].id == view.id) {

                        val ifCheck =
                                if (view.parent is HorizontalToolbar) dragEvent.x < view.width / 2 else dragEvent.y < view.height / 2
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
                            if (dragView.id == dragViewParent.selectedViewId && dragView.isSelectable) {
                                dragViewParent.clearSelection()
                                viewParent.setSelection(dragView)
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
                if (view.id == dragView.id) {
                    return true
                }

                if (view.parent is VerticalToolbar) {

                    // change the background to show where the ImageButton will be dropped
                    if (dragEvent.y < view.height / 2) {
                        view.background = dropTopBackground
                    } else {
                        view.background = dropBottomBackground
                    }

                } else if (view.parent is HorizontalToolbar) {

                    // change the background to show where the ImageButton will be dropped
                    if (dragEvent.x < view.width / 2) {
                        view.background = dropLeftBackground
                    } else {
                        view.background = dropRightBackground
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

        // find whether the clicked view is selectable
        val currentToolbarElement = v as ToolbarElement
        if (currentToolbarElement.isSelectable && !currentToolbarElement.isDisabled) {

            // change color, set current and remove previous selection
            clearSelection()
            setSelection(v)
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
        val toolbarElement = v as ToolbarElement
        val previousIconColor = when {
            v.id == selectedViewId -> {
                iconColorSelected
            }
            toolbarElement.isDisabled -> {
                iconColorDisabled
            }
            else -> {
                iconColorNormal
            }
        }
        val myShadow = DragShadowBuilder(
                v, Color.BLACK, Color.WHITE, previousIconColor, 12f, 0.6f
        )

        // starts the drag
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            v.startDragAndDrop(null, myShadow, v, 0)
        } else {
            v.startDrag(null, myShadow, v, 0)
        }

        return true
    }


    /**
     * Set the current selected element for this constrain layout(toolbar)
     */
    fun setSelection(v: View) {
        val element = v as ToolbarElement
        DrawableCompat.setTint(element.drawable, iconColorSelected)
        element.background = selectedElementsBackground
        selectedViewId = element.id
    }

    /**
     * Clear the current selected element if such exist, set selected
     * element to none.
     */
    fun clearSelection() {

        if (selectedViewId != -1) {
            val element = elements.find { s -> s.id == selectedViewId }
            if (element != null) {
                DrawableCompat.setTint(element.drawable, iconColorNormal)
                element.background = null
            }
            selectedViewId = -1
        }
    }


    /**
     * Add new ImageButton element to this constrain layout and add it to the elements and
     * toolbar elements array with start index given as argument.
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
            if (element.isSelected) {
                setSelection(element)
                element.isSelected = false  // remove it, used only on initialization
            }
            if (element.isDisabled) {
                DrawableCompat.setTint(element.drawable, iconColorDisabled)
            }

            if (indexOfChild(element) == -1) addView(element, 0)

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
     * Update the constrain set, in order to update the position of the ImageButtons
     * in this constrain layout.
     */
    open abstract fun updateSet()

    /**
     * Remove ImageButton element, from this constrain layout and return
     * the toolbar element corresponding to this element
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
}