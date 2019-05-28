package no.lekrot.busstavle

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import StopPlaceDeparturesQuery
import android.os.Handler
import android.os.Looper
import com.apollographql.apollo.ApolloCallback
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.exception.ApolloException
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val callback = ApolloCallback.wrap(object : ApolloCall.Callback<StopPlaceDeparturesQuery.Data>(){
            override fun onFailure(e: ApolloException) {
                e.printStackTrace()
            }

            override fun onResponse(response: Response<StopPlaceDeparturesQuery.Data>) {
                System.out.println(response)
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
    }
}
