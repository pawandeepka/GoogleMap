package com.dcdineshk.googlemap.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.dcdineshk.googlemap.R;
import com.dcdineshk.googlemap.adapter.MemberAdapter;
import com.dcdineshk.googlemap.model.Movie;

import java.util.ArrayList;
import java.util.List;

public class MemberActivity extends AppCompatActivity {

    private EditText fEt_Address,fEt_AddressssType;
    private Button fBtnSave1;

    private List<Movie> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MemberAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        getSupportActionBar().setTitle("Members");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);

        mAdapter = new MemberAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        prepareMovieData();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_toolbar_addmember, menu);
        MenuItem getItem = menu.findItem(R.id.get_member);

        if (getItem != null) {
            AppCompatButton button = (AppCompatButton) getItem.getActionView();

            ColorDrawable colorDrawable = new ColorDrawable(
                    Color.parseColor("#01C4E6"));
            button.setBackgroundDrawable(colorDrawable);
            button.setText("Add Member");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog fDialog2 = new Dialog(MemberActivity.this);
                    fDialog2.setContentView(R.layout.add_address_fragment);
                    fDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    fEt_Address = (EditText) fDialog2.findViewById(R.id.et_Address_Add);
                    fEt_AddressssType = (EditText) fDialog2.findViewById(R.id.et_AddressType_Add);
                    fBtnSave1 = (Button) fDialog2.findViewById(R.id.btn_save_Address);

                    fEt_Address.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });

                    fBtnSave1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            fDialog2.dismiss();
                        }
                    });

                    ImageView f_Image = (ImageView) fDialog2.findViewById(R.id.image_Address);
                    f_Image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            fDialog2.dismiss();
                        }
                    });
                    fDialog2.show();
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void prepareMovieData() {
        Movie movie = new Movie("Rakesh", "", "");
        movieList.add(movie);

        movie = new Movie("AK Jain", "", "");
        movieList.add(movie);

        mAdapter.notifyDataSetChanged();
    }
}
