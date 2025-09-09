package com.anar.adskipper

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var am: AccessibilityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        noAnim(this, isOpening = true)
        super.onCreate(savedInstanceState)

        am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager

        if (intent?.action == "com.anar.adskipper.OPEN_SETTINGS_SHORTCUT") {
            openAccessibilitySettings()
            finishNoAnim()
            return
        }

        if (isServiceEnabled()) {
            Toast.makeText(this, getString(R.string.service_already_running), Toast.LENGTH_SHORT).show()
            finishNoAnim()
            return
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.permission_needed_title)
            .setMessage(R.string.permission_needed_message)
            .setPositiveButton(R.string.open_settings) { _, _ ->
                openAccessibilitySettings()
                finishNoAnim()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> finishNoAnim() }
            .setOnCancelListener { finishNoAnim() }
            .show()
    }

    override fun onResume() {
        super.onResume()

        if (isServiceEnabled()) {
            Toast.makeText(this, getString(R.string.service_enabled_thanks), Toast.LENGTH_SHORT).show()
            finishNoAnim()
        }
    }

    private fun finishNoAnim() {
        finish()
        noAnim(this, isOpening = false)
    }

    private fun noAnim(activity: Activity, isOpening: Boolean) {
        if (Build.VERSION.SDK_INT >= 34) {
            val type = if (isOpening) Activity.OVERRIDE_TRANSITION_OPEN
            else Activity.OVERRIDE_TRANSITION_CLOSE
            activity.overrideActivityTransition(type, 0, 0)
        } else {
            @Suppress("DEPRECATION")
            activity.overridePendingTransition(0, 0)
        }
    }

    private fun openAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun isServiceEnabled(): Boolean {
        val enabled = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val myComponent = ComponentName(this, ClickerAccessibilityService::class.java).flattenToString()
        return enabled?.split(':')?.any { it.equals(myComponent, ignoreCase = true) } == true
    }
}
