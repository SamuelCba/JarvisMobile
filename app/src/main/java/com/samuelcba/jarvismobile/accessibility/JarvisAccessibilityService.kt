package com.samuelcba.jarvismobile.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class JarvisAccessibilityService : AccessibilityService() {
    override fun onServiceConnected() {
        active = true
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        active = false
        if (instance === this) {
            instance = null
        }
        super.onDestroy()
    }

    fun goBack(): Boolean = performGlobalAction(GLOBAL_ACTION_BACK)

    fun goHome(): Boolean = performGlobalAction(GLOBAL_ACTION_HOME)

    fun openRecents(): Boolean = performGlobalAction(GLOBAL_ACTION_RECENTS)

    fun scrollForward(): Boolean {
        return performOnFirstScrollable(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)
    }

    fun scrollBackward(): Boolean {
        return performOnFirstScrollable(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)
    }

    fun clickByText(label: String): Boolean {
        val root = rootInActiveWindow ?: return false
        return clickMatchingNode(root, label.lowercase())
    }

    private fun clickMatchingNode(node: AccessibilityNodeInfo, label: String): Boolean {
        val nodeText = listOfNotNull(node.text, node.contentDescription)
            .joinToString(" ")
            .lowercase()

        if (nodeText.contains(label) && clickNodeOrParent(node)) {
            return true
        }

        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            if (clickMatchingNode(child, label)) {
                return true
            }
        }

        return false
    }

    private fun clickNodeOrParent(node: AccessibilityNodeInfo?): Boolean {
        var current = node
        while (current != null) {
            if (current.isClickable && current.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                return true
            }
            current = current.parent
        }
        return false
    }

    private fun performOnFirstScrollable(action: Int): Boolean {
        val root = rootInActiveWindow ?: return false
        return performOnFirstScrollable(root, action)
    }

    private fun performOnFirstScrollable(node: AccessibilityNodeInfo, action: Int): Boolean {
        if (node.isScrollable && node.performAction(action)) {
            return true
        }

        for (index in 0 until node.childCount) {
            val child = node.getChild(index) ?: continue
            if (performOnFirstScrollable(child, action)) {
                return true
            }
        }

        return false
    }

    companion object {
        var active: Boolean = false
            private set

        private var instance: JarvisAccessibilityService? = null

        fun current(): JarvisAccessibilityService? = instance
    }
}
