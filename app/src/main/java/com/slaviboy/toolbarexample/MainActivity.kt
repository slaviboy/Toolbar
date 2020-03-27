package com.slaviboy.toolbarexample

import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import com.slaviboy.toolbar.*

class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView
    var isLongPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideSystemUI()

        textView = findViewById(R.id.text)

        // g all three toolbars
        val toolbar1: HorizontalToolbar = findViewById(R.id.checkable_toolbar)
        val toolbar2: HorizontalToolbar = findViewById(R.id.selectable_toolbar)
        val toolbar3: HorizontalToolbar = findViewById(R.id.button_toolbar)
        val toolbar4: HorizontalToolbar = findViewById(R.id.disabled_toolbar)
        val toolbar5: HorizontalToolbar = findViewById(R.id.animatable_toolbar)

        val toolbars: ArrayList<Toolbar> =
            arrayListOf(toolbar1, toolbar2, toolbar3, toolbar4, toolbar5)

        // put toolbars in a group, that way when selecting element, from one toolbar
        // you remove selection from the previously selected element from another toolbar
        val toolbarGroup: ToolbarGroup =
            ToolbarGroup(toolbar1, toolbar2, toolbar3, toolbar4, toolbar5)

        val ids = arrayListOf(R.id.basketball, R.id.volleyball, R.id.rugby, R.id.boxing)
        toolbarGroup.setOnClickListener {
            if (it?.id == R.id.view_elements) {
                val isSelected = (it as ToolbarElement).isChecked
                val visibility = if (isSelected) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                toolbars.forEach {
                    for (i in ids.indices) {
                        val toolbarElement = it.findViewById<ToolbarElement>(ids[i])
                        if (toolbarElement != null) {
                            toolbarElement.visibility = visibility
                        }
                    }
                }
            }
        }

        toolbar5.setOnElementsStateChangeListener(true) { element: ToolbarElement, previousState: Int, currentState: Int ->

            if (currentState == previousState) {
                return@setOnElementsStateChangeListener
            }

            val previousIcon = element.getIcon(previousState)
            val currentIcon = element.getIcon(currentState)

            val transitionDrawable = TransitionDrawable(
                arrayOf<Drawable?>(previousIcon, currentIcon)
            )
            transitionDrawable.isCrossFadeEnabled = true
            element.setImageDrawable(transitionDrawable)
            transitionDrawable.startTransition(500)

            val parent = element.parent as Toolbar
            when (currentState) {
                ToolbarElement.STATE_DISABLED -> {
                    DrawableCompat.setTint(currentIcon, parent.iconColorDisabled)
                    element.background = parent.iconBackgroundDisabled
                }
                ToolbarElement.STATE_SELECTED -> {
                    DrawableCompat.setTint(currentIcon, parent.iconColorSelected)
                    element.background = parent.iconBackgroundSelected
                }
                ToolbarElement.STATE_CHECKED -> {
                    DrawableCompat.setTint(currentIcon, parent.iconColorChecked)
                    element.background = parent.iconBackgroundChecked
                }
                else -> {
                    DrawableCompat.setTint(currentIcon, parent.iconColorNormal)
                    element.background = parent.iconBackgroundNormal
                }
            }
        }

        // show hint text on long click
        toolbarGroup.setOnLongClickListener {
            textView.text = (it as ToolbarElement).tooltip
            isLongPressed = true
            showHint()
            true
        }

        // hide hint on drop
        toolbarGroup.setOnDragListener { v, dragEvent ->

            if (dragEvent?.action == DragEvent.ACTION_DROP || dragEvent?.action == DragEvent.ACTION_DRAG_ENDED) {
                if (isLongPressed) {
                    hideHint()
                    isLongPressed = false
                }
            }
            true
        }
    }

    fun showHint() {
        textView.visibility = View.VISIBLE
        textView.animate()
            .alpha(1.0f)
            .setDuration(300)
    }

    fun hideHint() {
        textView.animate()
            .alpha(0.0f)
            .setDuration(300)
            .withEndAction {
                textView.visibility = View.GONE
            }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        actionBar?.hide()
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }
}