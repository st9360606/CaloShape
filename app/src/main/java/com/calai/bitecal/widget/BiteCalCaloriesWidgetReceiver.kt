package com.calai.bitecal.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.calai.bitecal.R

class BiteCalCaloriesWidgetReceiver : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val appContext = context.applicationContext
            val localizedContext = BiteCalWidgetLocaleContext.resolve(appContext)
            val snapshot = BiteCalWidgetSnapshotStore.load(appContext)
            val isDark = snapshot.isDarkAppearance
            val metricTextColor = if (isDark) BiteCalWidgetColors.DARK_METRIC_TEXT else BiteCalWidgetColors.LIGHT_METRIC_TEXT
            val labelTextColor = if (isDark) BiteCalWidgetColors.DARK_LABEL_TEXT else BiteCalWidgetColors.LIGHT_LABEL_TEXT
            val progressColor = if (isDark) BiteCalWidgetColors.DARK_CALORIES_ACCENT else BiteCalWidgetColors.LIGHT_CALORIES_ACCENT
            val trackColor = if (isDark) BiteCalWidgetColors.DARK_RING_TRACK else BiteCalWidgetColors.LIGHT_RING_TRACK
            val views = RemoteViews(appContext.packageName, R.layout.widget_calories).apply {
                setInt(
                    R.id.widget_calories_card,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_card_dark_bg else R.drawable.widget_card_bg
                )
                setTextViewText(R.id.widget_calories_value, snapshot.caloriesLeft.toString())
                setTextColor(R.id.widget_calories_value, metricTextColor)
                setTextViewText(R.id.widget_calories_label, localizedContext.getString(R.string.widget_calories_left))
                setTextColor(R.id.widget_calories_label, labelTextColor)
                setTextViewText(R.id.widget_calories_action_text, localizedContext.getString(R.string.widget_log_your_food))
                setImageViewBitmap(
                    R.id.widget_calories_ring,
                    BiteCalWidgetRingRenderer.render(
                        context = appContext,
                        progressPercent = snapshot.calorieProgress,
                        sizeDp = 112,
                        strokeDp = 7.5f,
                        progressColor = progressColor,
                        trackColor = trackColor
                    )
                )
                setOnClickPendingIntent(R.id.widget_calories_ring_area, BiteCalWidgetPendingIntents.openHome(appContext))
                setOnClickPendingIntent(R.id.widget_calories_action, BiteCalWidgetPendingIntents.scanFood(appContext))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
