package com.example.mqtt.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mqtt.data.local.AppDatabase;
import com.example.mqtt.model.News;

import java.util.List;

public class NewsRepository {

    private Application application;

    private static NewsRepository mInstance;

    // private RetrofitClient retrofit = RetrofitClient.getInstance();

    private AppDatabase database;

    private static MutableLiveData<List<News>> allNewsData = new MutableLiveData<>();


    public static synchronized NewsRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new NewsRepository(application);
        }
        return mInstance;
    }

    private NewsRepository(Application application) {
        database = AppDatabase.getDatabase(application);
        this.application = application;
    }


    /*
    public void loadAllPokemon() {
        int offset = application.getResources().getInteger(R.integer.offset);
        int limit = application.getResources().getInteger(R.integer.limit);
        Call<PokemonResponse> pokemonResponseCall = retrofit.getApi().getListaPokemon(limit, offset);

        pokemonResponseCall.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call, @NonNull Response<PokemonResponse> response) {
                if (response.isSuccessful()) {
                    PokemonResponse pokemonResponse = response.body();
                    assert pokemonResponse != null;
                    ArrayList<Pokemon> listPokemon = pokemonResponse.getResults();
                    allPokemonData.setValue(listPokemon);
                    for (Pokemon p : listPokemon) {
                        Call<Pokemon> pokemonCall = retrofit.getApi().getPokemonByName(p.getName());
                        pokemonCall.enqueue(new Callback<Pokemon>() {
                            @Override
                            public void onResponse(@NonNull Call<Pokemon> call, @NonNull Response<Pokemon> response) {
                                assert response.body() != null;
                                Objects.requireNonNull(allPokemonData.getValue()).set(response.body().getId() - 1, response.body());
                                insertPokemon(new MutableLiveData<>(response.body()));
                            }

                            @SuppressWarnings("NullableProblems")
                            @Override
                            public void onFailure(Call<News> call, Throwable t) {

                            }
                        });
                    }
                    allPokemonData.setValue(allPokemonData.getValue());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {}
        });
    }

    */

    public LiveData<List<News>> getAllNews() {
        return database.getDAO().getAll();
    }


    public void insertNews(final LiveData<News> newsItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getDAO().insert(newsItem.getValue()));
    }

    public LiveData<List<News>> searchByDate(String date){
        return database.getDAO().getAllByDate(date);
    }
}

