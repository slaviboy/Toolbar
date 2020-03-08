package com.slaviboy.toolbarexample

import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.slaviboy.toolbar.HorizontalToolbar
import com.slaviboy.toolbar.ToolbarElement
import com.slaviboy.toolbar.ToolbarGroup
import com.slaviboy.toolbar.VerticalToolbar

class MainActivity : AppCompatActivity() {

    lateinit var textView: TextView
    var isLongPressed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideSystemUI()

        textView = findViewById(R.id.text)

        // get all three toolbars
        val toolbar1: VerticalToolbar = findViewById(R.id.vertical_toolbar1)
        val toolbar2: VerticalToolbar = findViewById(R.id.vertical_toolbar2)
        val toolbar3: HorizontalToolbar = findViewById(R.id.horizontal_toolbar1)
        val toolbar4: HorizontalToolbar = findViewById(R.id.horizontal_toolbar2)

        // put toolbars in a group, that way when selecting element, from one toolbar
        // you remove selection from the previously selected element from another toolbar
        val toolbarGroup: ToolbarGroup = ToolbarGroup(toolbar1, toolbar2, toolbar3, toolbar4)

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