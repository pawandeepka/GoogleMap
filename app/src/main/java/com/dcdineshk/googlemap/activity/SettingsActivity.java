package com.dcdineshk.googlemap.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dcdineshk.googlemap.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.textView_rightArrow)
    TextView mTextView_rightArrow;

    @BindView(R.id.seekBar2)
    SeekBar mSeekBar_limit;
    @BindView(R.id.view) View mView;
    @BindView(R.id.viewSeek) View mViewSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextView_rightArrow.setOnClickListener(this);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_rightArrow:

                mSeekBar_limit.setVisibility(View.VISIBLE);
                mView.setVisibility(View.VISIBLE);
                mViewSeek.setVisibility(View.GONE);

                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                        }
                        handler.post(new Runnable() {
                            public void run() {
                                mSeekBar_limit.setVisibility(View.GONE);
                                mViewSeek.setVisibility(View.VISIBLE);
                                mView.setVisibility(View.GONE);
//                                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                            }
                        });
                    }
                }).start();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
