package com.example.myapplication.librarycalendar.utils

import android.view.View
import com.example.myapplication.librarycalendar.utils.ViewDoubleClick.SPACE_TIME
import com.example.myapplication.librarycalendar.utils.ViewDoubleClick.hash
import com.example.myapplication.librarycalendar.utils.ViewDoubleClick.lastClickTime

object ViewDoubleClick {
    var hash: Int = 0
    var lastClickTime: Long = 0
    var SPACE_TIME: Long = 1200
}

infix fun View.setNoDoubleClickListener(clickAction: () -> Unit) {
    this.setOnClickListener {
        if (this.hashCode() != hash) {
            hash = this.hashCode()
            lastClickTime = System.currentTimeMillis()
            clickAction()
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > SPACE_TIME) {
                lastClickTime = System.currentTimeMillis()
                clickAction()
            }
        }
    }
}