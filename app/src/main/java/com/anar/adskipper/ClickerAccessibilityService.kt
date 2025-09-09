package com.anar.adskipper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ClickerAccessibilityService : AccessibilityService() {

    companion object { private const val TAG = "AdSkipper" }

    private val targetPackage = "com.google.android.youtube"

    private val targetViewIds = arrayOf(
        "$targetPackage:id/skip_ad_button"
    )

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
            notificationTimeout = 120
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
            packageNames = arrayOf(targetPackage)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.packageName != targetPackage) return

        val types = event.contentChangeTypes
        if (types != 0 &&
            (types and (AccessibilityEvent.CONTENT_CHANGE_TYPE_SUBTREE
                    or AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT
                    or AccessibilityEvent.CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION)) == 0) {
            return
        }

        val root: AccessibilityNodeInfo = event.source ?: rootInActiveWindow ?: return

        for (id in targetViewIds) {
            val nodes = try { root.findAccessibilityNodeInfosByViewId(id) ?: emptyList() }
            catch (_: RuntimeException) { emptyList() }

            for (n in nodes) {
                if (clickNodeOrParent(n)) return
            }
        }

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

//        dumpTreeBrief(rootInActiveWindow)
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

    private fun dumpTreeBrief(root: AccessibilityNodeInfo, maxNodes: Int = 60) {
        var count = 0

        fun walk(n: AccessibilityNodeInfo?, depth: Int) {
            if (n == null || count >= maxNodes) return

            val id = n.viewIdResourceName ?: "-"
            val txt = n.text?.toString()?.take(40) ?: ""
            val cd = n.contentDescription?.toString()?.take(40) ?: ""

            Log.d(TAG, "${" ".repeat(depth*2)}• cls=${n.className} id=$id txt='$txt' cDesc='$cd' clickable=${n.isClickable} enabled=${n.isEnabled} visible=${n.isVisibleToUser}")
            count++
            var i = 0
            val c = n.childCount

            while (i < c) {
                walk(n.getChild(i), depth + 1)
                i++
            }
        }

        Log.d(TAG, "---- snapshot ----")
        walk(root, 0)
    }
}
