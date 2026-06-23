package com.caloshape.app.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.caloshape.app.R

class CaloShapeMacroActionsWidgetReceiver : AppWidgetProvider() {
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
            val caloriesProgressColor = if (isDark) CaloShapeWidgetColors.DARK_CALORIES_ACCENT else CaloShapeWidgetColors.LIGHT_CALORIES_ACCENT
            val ringTrackColor = if (isDark) CaloShapeWidgetColors.DARK_RING_TRACK else CaloShapeWidgetColors.LIGHT_RING_TRACK
            val proteinColor = if (isDark) CaloShapeWidgetColors.DARK_PROTEIN else CaloShapeWidgetColors.LIGHT_PROTEIN
            val carbsColor = if (isDark) CaloShapeWidgetColors.DARK_CARBS else CaloShapeWidgetColors.LIGHT_CARBS
            val fatsColor = if (isDark) CaloShapeWidgetColors.DARK_FATS else CaloShapeWidgetColors.LIGHT_FATS
            val actionIconColor = if (isDark) CaloShapeWidgetColors.DARK_ACTION_ICON else CaloShapeWidgetColors.LIGHT_ACTION_ICON
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
                setTextViewText(
                    R.id.widget_macro_calories_label,
                    CaloShapeWidgetText.remainingLabel(
                        text = localizedContext.getString(R.string.widget_calories_left),
                        labelColor = labelTextColor,
                        emphasisColor = labelEmphasisTextColor
                    )
                )
                setImageViewBitmap(
                    R.id.widget_macro_calories_ring,
                    CaloShapeWidgetRingRenderer.render(
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
                setTextViewText(
                    R.id.widget_protein_label,
                    CaloShapeWidgetText.remainingLabel(
                        text = localizedContext.getString(R.string.widget_protein_left),
                        labelColor = labelTextColor,
                        emphasisColor = labelEmphasisTextColor
                    )
                )
                setInt(
                    R.id.widget_protein_icon,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_circle_dark_bg else R.drawable.widget_circle_protein_bg
                )
                setImageViewBitmap(
                    R.id.widget_protein_ring,
                    CaloShapeWidgetRingRenderer.render(
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
                setTextViewText(
                    R.id.widget_carbs_label,
                    CaloShapeWidgetText.remainingLabel(
                        text = localizedContext.getString(R.string.widget_carbs_left),
                        labelColor = labelTextColor,
                        emphasisColor = labelEmphasisTextColor
                    )
                )
                setInt(
                    R.id.widget_carbs_icon,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_circle_dark_bg else R.drawable.widget_circle_carbs_bg
                )
                setImageViewBitmap(
                    R.id.widget_carbs_ring,
                    CaloShapeWidgetRingRenderer.render(
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
                setTextViewText(
                    R.id.widget_fats_label,
                    CaloShapeWidgetText.remainingLabel(
                        text = localizedContext.getString(R.string.widget_fats_left),
                        labelColor = labelTextColor,
                        emphasisColor = labelEmphasisTextColor
                    )
                )
                setInt(
                    R.id.widget_fats_icon,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_circle_dark_bg else R.drawable.widget_circle_fats_bg
                )
                setImageViewBitmap(
                    R.id.widget_fats_ring,
                    CaloShapeWidgetRingRenderer.render(
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
                setTextColor(R.id.widget_scan_food_text, if (isDark) CaloShapeWidgetColors.DARK_LABEL_EMPHASIS_TEXT else CaloShapeWidgetColors.LIGHT_ACTION_ICON)
                setInt(
                    R.id.widget_scan_food_icon_bg,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_action_icon_circle_dark_bg else R.drawable.widget_circle_gray_bg
                )
                setInt(R.id.widget_scan_food_icon, "setColorFilter", actionIconColor)
                setTextViewText(R.id.widget_barcode_text, localizedContext.getString(R.string.widget_barcode))
                setTextColor(R.id.widget_barcode_text, if (isDark) CaloShapeWidgetColors.DARK_LABEL_EMPHASIS_TEXT else CaloShapeWidgetColors.LIGHT_ACTION_ICON)
                setInt(
                    R.id.widget_barcode_icon_bg,
                    "setBackgroundResource",
                    if (isDark) R.drawable.widget_action_icon_circle_dark_bg else R.drawable.widget_circle_gray_bg
                )
                setInt(R.id.widget_barcode_icon, "setColorFilter", actionIconColor)

                setOnClickPendingIntent(R.id.widget_macro_root, CaloShapeWidgetPendingIntents.openHome(appContext))
                setOnClickPendingIntent(R.id.widget_scan_food_tile, CaloShapeWidgetPendingIntents.scanFood(appContext))
                setOnClickPendingIntent(R.id.widget_barcode_tile, CaloShapeWidgetPendingIntents.scanBarcode(appContext))
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
