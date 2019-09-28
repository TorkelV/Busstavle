package no.lekrot.busstavle

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import StopPlaceDeparturesQuery
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ListView
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.exception.ApolloException
import org.jetbrains.annotations.NotNull
import java.time.LocalDateTime
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    internal var blekenbergOnClickListener: View.OnClickListener = View.OnClickListener{
        updateList("NSR:StopPlace:29725")
    }

    internal var oasenOnClickListener: View.OnClickListener = View.OnClickListener{
        updateList("NSR:StopPlace:31798")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_blekenberg.setOnClickListener(blekenbergOnClickListener)
        btn_oasen.setOnClickListener(oasenOnClickListener)
        updateList("NSR:StopPlace:31798")


    }

    fun updateList(stopPlace: String){
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
                    ?.groupBy { it.destinationDisplay()?.frontText() + it.serviceJourney()?.journeyPattern()?.line()?.publicCode() }
                    ?.map { (_, v) ->
                        BusRide(
                            v.map { it.expectedDepartureTime() },
                            v.first().destinationDisplay()?.frontText(),
                            v.first().serviceJourney()?.journeyPattern()?.line()?.publicCode()
                        )
                    }
                val adapter = BusAdapter(applicationContext,grouped)
                busrides_list.adapter = adapter
            }
        }, Handler(Looper.getMainLooper()))


        val query = StopPlaceDeparturesQuery.builder()
            .ids(Arrays.asList(stopPlace))
            .start(LocalDateTime.now())
            .range(72000)
            .departures(50)
            .omitNonBoarding(true)
            .build()

        EnturAPI.getAPI()
            .query(query).httpCachePolicy(HttpCachePolicy.CACHE_FIRST).enqueue(callback)
    }
}
