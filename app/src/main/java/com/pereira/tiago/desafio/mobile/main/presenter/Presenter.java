package com.pereira.tiago.desafio.mobile.main.presenter;

import android.content.Context;

import com.pereira.tiago.desafio.mobile.base.Config;
import com.pereira.tiago.desafio.mobile.databasemodels.Movie;
import com.pereira.tiago.desafio.mobile.main.Contract;
import com.pereira.tiago.desafio.mobile.main.model.Model;
import com.pereira.tiago.desafio.mobile.utils.ConnectivityInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Presenter implements Contract.MainPresenter {

    private Contract.MainModel model;
    private Contract.MainView view;

    public Presenter(){
        model = new Model(this);
    }

    @Override
    public Context getContext() {
        return (Context)view;
    }

    @Override
    public void setView(Contract.MainView view) {
        this.view = view;
    }

    @Override
    public void getListMoviesPopular(int currentPage) {
        ConnectivityInfo.init(getContext());
        if (ConnectivityInfo.isConnected() && (ConnectivityInfo.is3G() || ConnectivityInfo.isWifi())) {
            model.getMoviesPopularNetwork(currentPage);
        } else {
            model.getMoviesPopularDatabase();
        }
    }

    @Override
    public void getListMoviesUpcoming(int currentPage) {
//        ConnectivityInfo.init(getContext());
//        if (ConnectivityInfo.isConnected() && (ConnectivityInfo.is3G() || ConnectivityInfo.isWifi())) {
//            model.getMoviesUpcomingNetwork(currentPage);
//        } else {
//            model.getMoviesUpcomingDatabase();
//        }
        model.getMoviesUpcomingDatabase();
    }

    @Override
    public void setMovieList(List<Movie> movieList, String option) {
        if (!movieList.isEmpty()) {

            //ordenar primeiro
            if (option.equals(Config.UPCOMING)){

                Collections.sort(movieList, Collections.reverseOrder(new Comparator<Movie>() {
                    @Override
                    public int compare(Movie movie1, Movie movie2) {
                        return movie1.getRelease_date().compareTo(movie2.getRelease_date());
                    }
                }));
            }

            view.setPopulateRecycler(movieList, option);
        } else {
            view.showNoResults();
        }
    }

    @Override
    public void changeOption(String option, int currentPage) {
        if (option.equals(Config.POPULAR)){
            getListMoviesPopular(currentPage);
            view.showButtonPopular();
        } else if (option.equals(Config.UPCOMING)) {
            getListMoviesUpcoming(currentPage);
            view.showButtonUpcoming();
        }
    }
}
