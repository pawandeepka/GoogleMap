package com.dcdineshk.googlemap.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.dcdineshk.googlemap.R;
import com.dcdineshk.googlemap.adapter.MoviesAdapter;
import com.dcdineshk.googlemap.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class TrackHistory extends AppCompatActivity {

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoviesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_history);

        getSupportActionBar().setTitle("Track History");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new MoviesAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMovieData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareMovieData() {
        Movie movie = new Movie("Chandigarh Club", "Sector 1,Near CM House, Chandigarh, 160001", "8AM-11PM");
        movieList.add(movie);

        movie = new Movie("Quarkcity India Pvt Ltd.", "A-40 A, Industrial Focal Point, Phase 8 B Extension, Mohali, Sahibzada Ajit Singh Nagar, Punjab 160059", "Open 24 hours");
        movieList.add(movie);

        movie = new Movie("Vibhuti Technologies", "F-264, Airport Road, Industrial Area Mohali, Sector 74, Sahibzada Ajit Singh Nagar, Punjab 160062", "Open 24 hours");
        movieList.add(movie);

        movie = new Movie("Vr Punjab", "N Country Mall Rd, Sector 117, Sahibzada Ajit Singh Nagar, Punjab 140307", "8AM-11PM");
        movieList.add(movie);

        movie = new Movie("Cafe Coffee Day", "SCF 84, Near Sacha Dhan Gurudwara, Mohali Stadium Rd, Phase 3B-2, Sector 60, Sahibzada Ajit Singh Nagar, Punjab 160055", "10AM-11PM");
        movieList.add(movie);

        movie = new Movie("Elante", "SCO 178A, Industrial Area Phase 1, MW Area, Chandigarh, 160002", "11AM-12AM");
        movieList.add(movie);

        movie = new Movie("Amrit Ice Cream Parlour ", "S.C.O. 82, Phase 5, Sector 59, Sahibzada Ajit Singh Nagar, Punjab 160059", "10AM-10PM");
        movieList.add(movie);

        movie = new Movie("HDFC Bank", " SCF 55,57, Phz 7, Sahibzada Ajit Singh Nagar, Punjab 160062", "10AM-4PM");
        movieList.add(movie);

        movie = new Movie("State Bank of India", "Cellulosics Road, Phase 7, Industrial Area, Sector 73, Sahibzada Ajit Singh Nagar, Punjab 160071", "10AM-4PM");
        movieList.add(movie);

        movie = new Movie("Ivy Hospital", "3376, Lakhnaur Pind Road, Sector 71, Sahibzada Ajit Singh Nagar, Punjab 160071", "Open 24 Hours");
        movieList.add(movie);

        movie = new Movie("Max Super Speciality Hospital, Mohali", "Near Civil Hospital, Phase-6, Sahibzada Ajit Singh Nagar, Punjab 160055", "Open 24 Hours");
        movieList.add(movie);

        movie = new Movie("Pgi Hospital", " PGI Road, Sector 12, Chandigarh, 160014", "OPEN 24 Hours");
        movieList.add(movie);

        movie = new Movie("Hawk travels ( club mahindra )", "sco148-149,level 2,cabin no.202,sec 34a, Chandigarh, 160022", "9:30AM to 6PM");
        movieList.add(movie);

        movie = new Movie("Café Coffee Day", "Sco No. 469-470, Sector 35C, Chandigarh, 160022", "11AM-1AM");
        movieList.add(movie);

        movie = new Movie("Pizza Hut", "SCO 345, Sector 35B, Chandigarh, 160035", "11AM-11PM");
        movieList.add(movie);

        movie = new Movie("Yadavindra Public School, Mohali", "Sahibzada Ajit Singh Nagar, Sector 51, Chandigarh, 160062", "9:00 AM to 5PM");
        movieList.add(movie);

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}