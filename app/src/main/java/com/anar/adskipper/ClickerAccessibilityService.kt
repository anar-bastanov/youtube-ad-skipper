package com.anar.adskipper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ClickerAccessibilityService : AccessibilityService() {

    private val targetPackage = "com.google.android.youtube"

    private val targetViewId = "$targetPackage:id/skip_ad_button"

    private var lastScanAt = 0L
    private val scanCooldownMs = 200L
    private var lastClickAt = 0L
    private val clickDebounceMs = 3000L

//    private val targetViewIds = arrayOf(
//        "$targetPackage:id/skip_ad_button"
//    )

//    private val targetTexts = arrayOf(
//        "Dismiss",
//        "SKIP TRIAL",
//        "No thanks",
//        "Don't renew"
//    )

//    private val targetContentDescriptions = arrayOf(
//        "Close ad panel",
//        "Dismiss"
//    )

    private val tmpRect = Rect()
    private val tapPath = Path()

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 250
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            packageNames = arrayOf(targetPackage)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.packageName != targetPackage) return

        val types = event.contentChangeTypes
        if (types != 0 &&
            (types and (AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE
//                    or AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT
//                    or AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION
            )) == 0) {
            return
        }

//        val root: AccessibilityNodeInfo = event.source ?: rootInActiveWindow ?: return

        val now = android.os.SystemClock.uptimeMillis()
        if (now - lastClickAt < clickDebounceMs) return
        if (now - lastScanAt < scanCooldownMs) return
        lastScanAt = now

        val root = rootInActiveWindow ?: return
        try {
            val nodes = root.findAccessibilityNodeInfosByViewId(targetViewId)
            if (!nodes.isNullOrEmpty() && clickNodeOrParent(nodes[0])) {
                lastClickAt = now
            }
        }
        catch (_: RuntimeException) {}

//        for (id in targetViewIds) {
//            val nodes = try { root.findAccessibilityNodeInfosByViewId(id) ?: emptyList() }
//            catch (_: RuntimeException) { emptyList() }
//
//            for (n in nodes) {
//                if (clickNodeOrParent(n)) return
//            }
//        }

//        for (t in targetTexts) {
//            val nodes = root.findAccessibilityNodeInfosByText(t) ?: emptyList()
//
//            for (n in nodes) {
//                if (TextUtils.equals(n.text, t) && clickNodeOrParent(n)) return
//            }
//        }

//        for (d in targetContentDescriptions) {
//            val nodes = root.findAccessibilityNodeInfosByText(d) ?: emptyList()
//
//            for (n in nodes) {
//                if (TextUtils.equals(n.contentDescription, d) && clickNodeOrParent(n)) return
//            }
//        }
    }

    override fun onInterrupt() {}

    private fun clickNodeOrParent(node: AccessibilityNodeInfo): Boolean {
        var cur = node
        var hops = 0

        while (true) {
            if (cur.isVisibleToUser && cur.isEnabled && cur.isClickable) {
                if (cur.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
            }

            if (++hops == 6) break
            cur = cur.parent ?: break
        }

        node.getBoundsInScreen(tmpRect)

        if (!tmpRect.isEmpty) {
            tapPath.rewind()
            tapPath.moveTo(tmpRect.exactCenterX(), tmpRect.exactCenterY())
            val stroke = GestureDescription.StrokeDescription(tapPath, 0, 10)
            val gesture = GestureDescription.Builder().addStroke(stroke).build()
            return dispatchGesture(gesture, null, null)
        }

        return false
    }
}
