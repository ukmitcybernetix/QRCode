package com.zendev.qrcode;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class TicketResultActivity extends AppCompatActivity {

    private static final String TAG = TicketResultActivity.class.getSimpleName();
    private static final String URL = "https://my-json-server.typicode.com/ukmitcybernetix/contact-api/comments/";

    private TextView txtName, txtId, txtJabatan, txtDivisi, txtJurusan, txtJob, txtError;
    private ImageView imgPoster;
    private Button btnDetail;
    private ProgressBar progressBar;
    private TicketView ticketView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_result);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtName = findViewById(R.id.name);
        txtJabatan = findViewById(R.id.jabatan);
        txtId = findViewById(R.id.idcx);
        txtJob = findViewById(R.id.job);
        txtJurusan = findViewById(R.id.jurusan);
        imgPoster = findViewById(R.id.poster);
        txtDivisi = findViewById(R.id.divisi);
        btnDetail = findViewById(R.id.btn_detail);
        imgPoster = findViewById(R.id.poster);
        txtError = findViewById(R.id.txt_error);
        ticketView = findViewById(R.id.layout_ticket);
        progressBar = findViewById(R.id.progressBar);

        String barcode = getIntent().getStringExtra("code");

        if (TextUtils.isEmpty(barcode)) {
            Toast.makeText(getApplicationContext(), "Barcode is empty!", Toast.LENGTH_LONG).show();
            finish();
        }

        searchBarcode(barcode);
    }

    private void searchBarcode(String barcode){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                URL + barcode, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, "Ticket response: " + response.toString());

                        if (!response.has("error")) {
                            renderMovie(response);
                        } else {
                            showNoTicket();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                showNoTicket();
            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);

        }

    private void showNoTicket() {
        txtError.setVisibility(View.VISIBLE);
        ticketView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void renderMovie(JSONObject response) {
        try {

            Anggota anggota = new Gson().fromJson(response.toString(), Anggota.class);

            if (anggota != null) {
                txtName.setText(anggota.getName());
                txtJabatan.setText(anggota.getDirector());
                txtId.setText(anggota.getDuration());
                txtDivisi.setText(anggota.getGenre());
                txtJurusan.setText("" + anggota.getRating());
                txtJob.setText(anggota.getPrice());
                Glide.with(this).load(anggota.getPoster()).into(imgPoster);

                if (anggota.isReleased()) {
                    btnDetail.setText(getString(R.string.btn_detail));
                    btnDetail.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
                } else {
                    btnDetail.setText(getString(R.string.btn_no_data));
                    btnDetail.setTextColor(ContextCompat.getColor(this, R.color.btn_disabled));
                }
                ticketView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                showNoTicket();
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "JSON Exception: " + e.getMessage());
            showNoTicket();
            Toast.makeText(getApplicationContext(), "Error occurred. Check your LogCat for full report", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            showNoTicket();
            Toast.makeText(getApplicationContext(), "Error occurred. Check your LogCat for full report", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class Anggota {
        String name;
        String director;
        String poster;
        String duration;
        String genre;
        String price;
        float rating;

        @SerializedName("released")
        boolean isReleased;

        public String getName() {
            return name;
        }

        public String getDirector() {
            return director;
        }

        public String getPoster() {
            return poster;
        }

        public String getDuration() {
            return duration;
        }

        public String getGenre() {
            return genre;
        }

        public String getPrice() {
            return price;
        }

        public float getRating() {
            return rating;
        }

        public boolean isReleased() {
            return isReleased;
        }
    }

}

