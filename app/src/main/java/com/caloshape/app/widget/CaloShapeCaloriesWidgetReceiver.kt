package com.caloshape.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.caloshape.app.R

class CaloShapeCaloriesWidgetReceiver : AppWidgetProvider() {
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
            val localizedContext = CaloShapeWidgetLocaleContext.resolve(appContext)
            val snapshot = CaloShapeWidgetSnapshotStore.load(appContext)
            val isDark = snapshot.isDarkAppearance
            val metricTextColor = if (isDark) CaloShapeWidgetColors.DARK_METRIC_TEXT else CaloShapeWidgetColors.LIGHT_METRIC_TEXT
            val labelTextColor = if (isDark) CaloShapeWidgetColors.DARK_LABEL_TEXT else CaloShapeWidgetColors.LIGHT_LABEL_TEXT
            val labelEmphasisTextColor = if (isDark) {
                CaloShapeWidgetColors.DARK_LABEL_EMPHASIS_TEXT
            } else {
                CaloShapeWidgetColors.LIGHT_LABEL_EMPHASIS_TEXT
            }
            val progressColor = if (isDark) CaloShapeWidgetColors.DARK_CALORIES_ACCENT else CaloShapeWidgetColors.LIGHT_CALORIES_ACCENT
            val trackColor = if (isDark) CaloShapeWidgetColors.DARK_RING_TRACK else CaloShapeWidgetColors.LIGHT_RING_TRACK
            val views = RemoteViews(appContext.packageName, R.layout.widget_calories).apply {
                setInt(
                    R.id.widget_calories_card,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_card_dark_bg else R.drawable.widget_card_bg
                )
                setTextViewText(R.id.widget_calories_value, snapshot.caloriesLeft.toString())
                setTextColor(R.id.widget_calories_value, metricTextColor)
                setTextViewText(
                    R.id.widget_calories_label,
                    CaloShapeWidgetText.remainingLabel(
                        text = localizedContext.getString(R.string.widget_calories_left),
                        labelColor = labelTextColor,
                        emphasisColor = labelEmphasisTextColor
                    )
                )
                setTextViewText(R.id.widget_calories_action_text, localizedContext.getString(R.string.widget_log_your_food))
                setTextColor(
                    R.id.widget_calories_action_text,
                    if (isDark) CaloShapeWidgetColors.DARK_ACTION_ICON else CaloShapeWidgetColors.DARK_METRIC_TEXT
                )
                setInt(
                    R.id.widget_calories_action,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_log_food_dark_bg else R.drawable.widget_action_dark_bg
                )
                setInt(
                    R.id.widget_calories_plus,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_circle_black_bg else R.drawable.widget_circle_white_bg
                )
                setInt(
                    R.id.widget_calories_plus_icon,
                    "setColorFilter",
                    if (isDark) CaloShapeWidgetColors.DARK_ACTION_ICON else CaloShapeWidgetColors.LIGHT_ACTION_ICON
                )
                setImageViewBitmap(
                    R.id.widget_calories_ring,
                    CaloShapeWidgetRingRenderer.render(
                        context = appContext,
                        progressPercent = snapshot.calorieProgress,
                        sizeDp = 112,
                        strokeDp = 7.5f,
                        progressColor = progressColor,
                        trackColor = trackColor
                    )
                )
                setOnClickPendingIntent(R.id.widget_calories_ring_area, CaloShapeWidgetPendingIntents.openHome(appContext))
                setOnClickPendingIntent(R.id.widget_calories_action, CaloShapeWidgetPendingIntents.scanFood(appContext))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
