package no.lekrot.busstavle

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.exception.ApolloException
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.util.*

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [BussTavleConfigureActivity]
 */
class BussTavle : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            BussTavleConfigureActivity.deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val callback = ApolloCallback.wrap(object : ApolloCall.Callback<StopPlaceDeparturesQuery.Data>() {
                override fun onFailure(e: ApolloException) {
                    e.printStackTrace()
                }

                override fun onResponse(@NotNull response: Response<StopPlaceDeparturesQuery.Data>) {
                    val grouped = (response.data()
                        ?.stopPlaces()
                        ?.first()
                        ?.estimatedCalls()
                        ?.map { it.fragments().estimatedCallFields() })
                        ?.groupBy { it.serviceJourney()?.journeyPattern()?.line()?.publicCode() }
                        ?.map { (k, v) ->
                            BusRide(
                                v.map { it.expectedDepartureTime() },
                                v.first().destinationDisplay()?.frontText(),
                                k
                            )
                        }
                }
            }, Handler(Looper.getMainLooper()))


            val query = StopPlaceDeparturesQuery.builder()
                .ids(Arrays.asList("NSR:StopPlace:31798"))
                .start(LocalDateTime.now())
                .range(72000)
                .departures(50)
                .omitNonBoarding(true)
                .build()

            EnturAPI.getAPI()
                .query(query).httpCachePolicy(HttpCachePolicy.CACHE_FIRST).enqueue(callback)


            val widgetText = BussTavleConfigureActivity.loadTitlePref(context, appWidgetId)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.buss_tavle)
            views.setTextViewText(R.id.appwidget_text, widgetText)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

