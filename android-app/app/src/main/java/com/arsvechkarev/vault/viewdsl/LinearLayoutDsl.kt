package com.arsvechkarev.vault.viewdsl

import android.view.View
import android.widget.LinearLayout

fun LinearLayout.orientation(orientation: Int) {
    this.orientation = orientation
}

fun LinearLayout.gravity(gravity: Int) {
    setGravity(gravity)
}

fun View.weight(weight: Float) {
    (layoutParams as LinearLayout.LayoutParams).weight = weight
}