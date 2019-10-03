package com.pereira.tiago.desafio.mobile.details.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pereira.tiago.desafio.mobile.R;
import com.pereira.tiago.desafio.mobile.base.Config;
import com.pereira.tiago.desafio.mobile.databasemodels.GenreMovie;
import com.pereira.tiago.desafio.mobile.databasemodels.SendDetails;
import com.pereira.tiago.desafio.mobile.details.Contract;
import com.pereira.tiago.desafio.mobile.details.presenter.DetailsPresenter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import static com.pereira.tiago.desafio.mobile.base.Config.MOVIE_ID;

public class DetailsActivity extends AppCompatActivity implements Contract.DetailsView {

    private CardView cardViewImg;
    private ProgressBar pbBanner;
    private TextView txtTitle, txtDate, txtRuntime, txtGenres, txtDescription;
    private RecyclerView rcvCredits;
    private ImageView imgBanner, imgExpand, imgBannerZoom;
    private ConstraintLayout clZoom;
    private Toolbar toolbar;

    private static Contract.DetailsPresenter presenter;

    public static Intent newInstance(Context context, int movie_id) {
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(MOVIE_ID, movie_id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toolbar = findViewById(R.id.toolbar);
        cardViewImg = findViewById(R.id.cardViewImg);
        pbBanner = findViewById(R.id.pbBanner);
        imgBanner = findViewById(R.id.imgBanner);
        imgExpand = findViewById(R.id.imgExpand);
        txtTitle = findViewById(R.id.txtTitle);
        txtDate = findViewById(R.id.txtDate);
        txtRuntime = findViewById(R.id.txtRuntime);
        txtGenres = findViewById(R.id.txtGenres);
        rcvCredits = findViewById(R.id.rcvCredits);
        txtDescription = findViewById(R.id.txtDescription);
        imgBannerZoom = findViewById(R.id.imgBannerZoom);
        clZoom = findViewById(R.id.clZoom);

        if (presenter == null){
            presenter = new DetailsPresenter();
        }
        presenter.setView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        init();
    }

    private void init() {
        int movieId = (int) getIntent().getSerializableExtra(MOVIE_ID);

        toolbar.setTitle("Desafio Mobile");
        toolbar.setSubtitle("Detalhes do filme");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter.getDetailsMovie(movieId);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return true;
    }

    @Override
    public void showNoResults() {

    }

    @Override
    public void showResults(SendDetails sendDetails) {

        pbBanner.setVisibility(View.VISIBLE);
        imgExpand.setVisibility(View.GONE);
        cardViewImg.setClickable(false);

        txtTitle.setText(sendDetails.getDetails().getTitle());

        String[] aux = sendDetails.getDetails().getRelease_date().split("-");
        txtDate.setText(aux[0]);

        txtRuntime.setText(sendDetails.getDetails().getRuntime() + " min");

        StringBuilder stringBuffer = new StringBuilder();
        int i = 0;
        for (GenreMovie genreMovie: sendDetails.getGenreMovieList()){
            stringBuffer.append(genreMovie.getName());
            if (i < (sendDetails.getGenreMovieList().size() - 1)){
                stringBuffer.append(" - ");
            }
            i++;
        }
        txtGenres.setText(stringBuffer.toString());

        txtDescription.setText(sendDetails.getDetails().getOverview());

        Picasso.get()
                .load(Config.BASE_URL_IMG + sendDetails.getDetails().getPoster_path())
                .error(R.drawable.ic_launcher_background)
                .into(imgBanner, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (pbBanner != null){
                            pbBanner.setVisibility(View.GONE);
                            imgExpand.setVisibility(View.VISIBLE);
                            cardViewImg.setClickable(true);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        if (pbBanner != null){
                            pbBanner.setVisibility(View.GONE);
                        }
                    }
                });

        Picasso.get()
                .load(Config.BASE_URL_IMG + sendDetails.getDetails().getPoster_path())
                .error(R.drawable.ic_launcher_background)
                .into(imgBannerZoom);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rcvCredits.setLayoutManager(linearLayoutManager);
        DetailsAdapter adapter = new DetailsAdapter(sendDetails.getCastMovieList());
        rcvCredits.setAdapter(adapter);

        cardViewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clZoom.getVisibility() == View.VISIBLE){
                    clZoom.setVisibility(View.GONE);
                } else {
                    clZoom.setVisibility(View.VISIBLE);
                }
            }
        });

        clZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clZoom.setVisibility(View.GONE);
            }
        });
    }
}