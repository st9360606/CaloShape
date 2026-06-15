package com.calai.bitecal.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.calai.bitecal.R

class BiteCalMacroActionsWidgetReceiver : AppWidgetProvider() {
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
            val caloriesProgressColor = if (isDark) BiteCalWidgetColors.DARK_CALORIES_ACCENT else BiteCalWidgetColors.LIGHT_CALORIES_ACCENT
            val ringTrackColor = if (isDark) BiteCalWidgetColors.DARK_RING_TRACK else BiteCalWidgetColors.LIGHT_RING_TRACK
            val proteinColor = if (isDark) BiteCalWidgetColors.DARK_PROTEIN else BiteCalWidgetColors.LIGHT_PROTEIN
            val carbsColor = if (isDark) BiteCalWidgetColors.DARK_CARBS else BiteCalWidgetColors.LIGHT_CARBS
            val fatsColor = if (isDark) BiteCalWidgetColors.DARK_FATS else BiteCalWidgetColors.LIGHT_FATS
            val views = RemoteViews(appContext.packageName, R.layout.widget_macro_actions).apply {
                setInt(
                    R.id.widget_macro_card,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_card_dark_bg else R.drawable.widget_card_bg
                )
                setInt(
                    R.id.widget_macro_divider,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_divider_dark_bg else R.drawable.widget_divider_bg
                )
                setInt(
                    R.id.widget_scan_food_tile,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_action_tile_dark_bg else R.drawable.widget_action_light_bg
                )
                setInt(
                    R.id.widget_barcode_tile,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_action_tile_dark_bg else R.drawable.widget_action_light_bg
                )
                setTextViewText(R.id.widget_macro_calories_value, snapshot.caloriesLeft.toString())
                setTextColor(R.id.widget_macro_calories_value, metricTextColor)
                setTextViewText(R.id.widget_macro_calories_label, localizedContext.getString(R.string.widget_calories_left))
                setTextColor(R.id.widget_macro_calories_label, labelTextColor)
                setImageViewBitmap(
                    R.id.widget_macro_calories_ring,
                    BiteCalWidgetRingRenderer.render(
                        context = appContext,
                        progressPercent = snapshot.calorieProgress,
                        sizeDp = 114,
                        strokeDp = 7.5f,
                        progressColor = caloriesProgressColor,
                        trackColor = ringTrackColor
                    )
                )

                setTextViewText(R.id.widget_protein_value, localizedContext.getString(R.string.widget_grams_format, snapshot.proteinLeftG))
                setTextColor(R.id.widget_protein_value, metricTextColor)
                setTextViewText(R.id.widget_protein_label, localizedContext.getString(R.string.widget_protein_left))
                setTextColor(R.id.widget_protein_label, labelTextColor)
                setImageViewBitmap(
                    R.id.widget_protein_ring,
                    BiteCalWidgetRingRenderer.render(
                        context = appContext,
                        progressPercent = snapshot.proteinProgress,
                        sizeDp = 34,
                        strokeDp = 3.3f,
                        progressColor = proteinColor,
                        trackColor = ringTrackColor,
                        tickRadiusScale = 0.55f
                    )
                )

                setTextViewText(R.id.widget_carbs_value, localizedContext.getString(R.string.widget_grams_format, snapshot.carbsLeftG))
                setTextColor(R.id.widget_carbs_value, metricTextColor)
                setTextViewText(R.id.widget_carbs_label, localizedContext.getString(R.string.widget_carbs_left))
                setTextColor(R.id.widget_carbs_label, labelTextColor)
                setImageViewBitmap(
                    R.id.widget_carbs_ring,
                    BiteCalWidgetRingRenderer.render(
                        context = appContext,
                        progressPercent = snapshot.carbsProgress,
                        sizeDp = 34,
                        strokeDp = 3.3f,
                        progressColor = carbsColor,
                        trackColor = ringTrackColor,
                        tickRadiusScale = 0.55f
                    )
                )

                setTextViewText(R.id.widget_fats_value, localizedContext.getString(R.string.widget_grams_format, snapshot.fatsLeftG))
                setTextColor(R.id.widget_fats_value, metricTextColor)
                setTextViewText(R.id.widget_fats_label, localizedContext.getString(R.string.widget_fats_left))
                setTextColor(R.id.widget_fats_label, labelTextColor)
                setImageViewBitmap(
                    R.id.widget_fats_ring,
                    BiteCalWidgetRingRenderer.render(
                        context = appContext,
                        progressPercent = snapshot.fatsProgress,
                        sizeDp = 34,
                        strokeDp = 3.3f,
                        progressColor = fatsColor,
                        trackColor = ringTrackColor,
                        tickRadiusScale = 0.55f
                    )
                )

                setTextViewText(R.id.widget_scan_food_text, localizedContext.getString(R.string.widget_scan_food))
                setTextColor(R.id.widget_scan_food_text, if (isDark) metricTextColor else BiteCalWidgetColors.LIGHT_LABEL_TEXT)
                setTextViewText(R.id.widget_barcode_text, localizedContext.getString(R.string.widget_barcode))
                setTextColor(R.id.widget_barcode_text, if (isDark) metricTextColor else BiteCalWidgetColors.LIGHT_LABEL_TEXT)

                setOnClickPendingIntent(R.id.widget_macro_root, BiteCalWidgetPendingIntents.openHome(appContext))
                setOnClickPendingIntent(R.id.widget_scan_food_tile, BiteCalWidgetPendingIntents.scanFood(appContext))
                setOnClickPendingIntent(R.id.widget_barcode_tile, BiteCalWidgetPendingIntents.scanBarcode(appContext))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
