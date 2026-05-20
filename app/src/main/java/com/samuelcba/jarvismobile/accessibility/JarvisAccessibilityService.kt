package com.samuelcba.jarvismobile.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class JarvisAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        active = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        active = false
        super.onDestroy()
    }

    companion object {
        var active: Boolean = false
            private set
    }
}
