package no.lekrot.busstavle;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.CustomTypeValue;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import type.CustomType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EnturAPI {

    private static final String BASE_URL = "https://api.entur.io/journey-planner/v2/graphql";

    private static ApolloClient apolloClient;

    public static ApolloClient getAPI() {


        if (apolloClient == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
            apolloClient = ApolloClient.builder()
                    .addCustomTypeAdapter(CustomType.DATETIME, getDateTimeAdapter())
                    .addCustomTypeAdapter(CustomType.DATE, getDateAdapter())
                    .serverUrl(BASE_URL)
                    .okHttpClient(okHttpClient)
                    .build();
        }
        return apolloClient;
    }


    private static CustomTypeAdapter getDateTimeAdapter() {
        return new CustomTypeAdapter<LocalDateTime>() {

            @Override
            public LocalDateTime decode(@NotNull CustomTypeValue value) {
                return LocalDateTime.parse(value.value.toString().replaceAll(".\\d\\d\\d\\d$",""));
            }

            @NotNull
            @Override
            public CustomTypeValue encode(LocalDateTime value) {
                return CustomTypeValue.fromRawValue(value.toString());
            }
        };
    }

    private static CustomTypeAdapter getDateAdapter() {
        return new CustomTypeAdapter<LocalDate>() {

            @Override
            public LocalDate decode(@NotNull CustomTypeValue value) {
                return LocalDate.parse(value.value.toString());
            }

            @NotNull
            @Override
            public CustomTypeValue encode(LocalDate value) {
                return CustomTypeValue.fromRawValue(value.toString());
            }
        };
    }

}
