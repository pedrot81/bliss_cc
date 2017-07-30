package com.pdt.blissrecruitment.connector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pdt.blissrecruitment.entities.Error;
import com.pdt.blissrecruitment.connector.entities.Question;
import com.pdt.blissrecruitment.connector.entities.Status;
import com.pdt.blissrecruitment.exception.ConnectorException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by pdt on 27/07/2017.
 */

class ConnectorImpl implements IConnector {
    private static final String API_HOST = "https://private-anon-f957499034-blissrecruitmentapi.apiary-mock.com/";

    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String FILTER = "filter";

    private BlissApiaryApi api;

    public ConnectorImpl() {
        initConnector();
    }

    private void initConnector() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();

        OkHttpClient httpClient = new OkHttpClient.Builder()
                // .addInterceptor(interceptor) // enable for debugging
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_HOST)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();

        api = retrofit.create(BlissApiaryApi.class);
    }

    @Override
    public boolean checkServer() throws ConnectorException {
        try {
            Response<Status> response = api.checkHealth().execute();
            return response.isSuccessful() && response.body().isSuccess();
        } catch (IOException e) {
            Error error = new Error.NoNetworkErrorBuilder().build();
            throw new ConnectorException(error.getErrorMessage(), error);
        }
    }

    @Override
    public boolean shareQuestion(String destinationEmail, String contentUrl) throws ConnectorException {
        try {
            Response<Status> response = api.share(destinationEmail, contentUrl).execute();
            return response.isSuccessful();
        } catch (IOException e) {
            Error error = new Error.NoNetworkErrorBuilder().build();
            throw new ConnectorException(error.getErrorMessage(), error);
        }
    }

    @Override
    public List<Question> questionsList(int limit, int offset) throws ConnectorException {
        Map<String, String> params = new HashMap<>();
        params.put(LIMIT, String.valueOf(limit));
        params.put(OFFSET, String.valueOf(offset));
        return RequestQuestionsList(params);
    }

    @Override
    public List<Question> filteredQuestionsList(int limit, int offset, String filter) throws ConnectorException {
        Map<String, String> params = new HashMap<>();
        params.put(LIMIT, String.valueOf(limit));
        params.put(OFFSET, String.valueOf(offset));
        params.put(FILTER, filter);
        return RequestQuestionsList(params);
    }

    private List<Question> RequestQuestionsList(Map<String, String> params) throws ConnectorException {
        try {
            Response<List<Question>> response = api.questionsList(params).execute();
            return response.body();
        } catch (IOException e) {
            Error error = new Error.NoNetworkErrorBuilder().build();
            throw new ConnectorException(error.getErrorMessage(), error);
        }
    }

    @Override
    public Question retrieveQuestion(String questionId) throws ConnectorException {
        try {
            Response<Question> response = api.retrieveQuestion(questionId).execute();
            return response.body();
        } catch (IOException e) {
            Error error = new Error.NoNetworkErrorBuilder().build();
            throw new ConnectorException(error.getErrorMessage(), error);
        }
    }

    @Override
    public Question updateQuestion(String questionId, String json) throws ConnectorException {
        try {
            Response<Question> response = api.updateQuestion(questionId, json).execute();
            return response.body();
        } catch (IOException e) {
            Error error = new Error.NoNetworkErrorBuilder().build();
            throw new ConnectorException(error.getErrorMessage(), error);
        }
    }

    private interface BlissApiaryApi {

        @GET("health")
        Call<Status> checkHealth();

        @GET("questions")
        Call<List<Question>> questionsList(
                @QueryMap Map<String, String> params
        );

        @FormUrlEncoded
        @POST("share")
        Call<Status> share(
                @Field("destination_email") String destinationEmail,
                @Field("content_url") String contentUrl
        );

        @FormUrlEncoded
        @POST("questions")
        Call<Question> updateQuestion(
                @Query("question_id") String questionId,
                @Field("payload") String payload
        );

        @GET("questions/{question_id}")
        Call<Question> retrieveQuestion(
                @Path("question_id") String questionId
        );
    }
}
