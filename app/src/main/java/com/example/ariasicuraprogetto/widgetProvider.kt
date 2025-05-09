package com.example.ariasicuraprogetto

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import android.widget.RemoteViews

class widgetProvider : AppWidgetProvider() {

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Log.d("WidgetProvider", "Widget creato")
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        Log.d("WidgetProvider", "onUpdate chiamato")

        if (context == null || appWidgetManager == null || appWidgetIds == null) return

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget)
            views.setTextViewText(R.id.widget_cityText, "Caricamento...")
            views.setTextViewText(R.id.widget_regionText, "")
            views.setTextViewText(R.id.widget_nationText, "")
            views.setTextViewText(R.id.widget_qualityText, "-")
            views.setTextViewText(R.id.widget_AQI, "-")

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Log.d("WidgetProvider", "Widget eliminato")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Log.d("WidgetProvider", "Ultimo widget rimosso")
    }

    companion object {
        @JvmStatic
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            dataDTO: PollutionInfo.DataDTO
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget)

            views.setTextViewText(R.id.widget_cityText, dataDTO.city)
            views.setTextViewText(R.id.widget_regionText, dataDTO.state)
            views.setTextViewText(R.id.widget_nationText, dataDTO.country)

            val aqi = dataDTO.current.pollution.aqius
            views.setTextViewText(R.id.widget_AQI, aqi.toString())

            val quality = when {
                aqi <= 50 -> "Good"
                aqi <= 100 -> "Moderate"
                else -> "Unhealthy"
            }
            views.setTextViewText(R.id.widget_qualityText, quality)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}