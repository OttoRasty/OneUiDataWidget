package com.example.oneuidatawidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class WidgetConfigActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.config)

        setResult(Activity.RESULT_CANCELED)
        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish()

        val seek = findViewById<SeekBar>(R.id.seekAlpha)
        val txt = findViewById<TextView>(R.id.txtAlpha)
        val day = findViewById<NumberPicker>(R.id.dayPicker)
        val cap = findViewById<EditText>(R.id.editCap)
        val save = findViewById<Button>(R.id.btnSave)
        val animSwitch: Switch = findViewById(R.id.switchAnim)

        val prefs = Prefs(this, appWidgetId)
        day.minValue = 1; day.maxValue = 31
        seek.progress = prefs.alpha
        txt.text = "${prefs.alpha}"
        day.value = prefs.resetDay
        cap.setText(prefs.dataCapGb.toString())
        animSwitch.isChecked = prefs.animations

        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, f: Boolean) { txt.text = "$p" }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        save.setOnClickListener {
            val alpha = seek.progress.coerceIn(30, 255)
            val resetDay = day.value
            val capGb = cap.text.toString().toDoubleOrNull() ?: 12.0
            val animations = animSwitch.isChecked

            prefs.alpha = alpha
            prefs.resetDay = resetDay
            prefs.dataCapGb = capGb
            prefs.animations = animations

            UsageRepository.ensureBaseline(this, resetDay)
            DataWidgetProvider.updateAll(this)

            val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }
}
