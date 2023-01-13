package com.mhakim.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.icu.text.NumberFormat;
import android.icu.util.Currency;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mhakim.currencyconverter.API.ApiInterface;
import com.mhakim.currencyconverter.API.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String[] some_array, code;
    int from, to;
    private ApiInterface apiInterface;
    private TextView hasil, namauang;
    private EditText etjumlah;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.btn_convert);
        hasil = findViewById(R.id.tvhasil);
        etjumlah = findViewById(R.id.input_jumlah);
        namauang = findViewById(R.id.nama);
        apiInterface = RetrofitClient.getClient().create(ApiInterface.class);

        Spinner spinnerS = (Spinner) findViewById(R.id.spinnerDari);

        some_array = getResources().getStringArray(R.array.Status);
        code = getResources().getStringArray(R.array.code);

        ArrayList<Integer> imgArr = new ArrayList<Integer>();
//        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        for (int i = 0; i < 25; i++) {

            imgArr.add(getResources().getIdentifier(some_array[i], "drawable", "com.mhakim.currencyconverter"));
        }
        System.out.println(getResources().getIdentifier("idr", "drawable", "com.mhakim.currencyconverter"));

        Integer[] list = imgArr.toArray(new Integer[imgArr.size()]);

        Spinner spinnerK = (Spinner) findViewById(R.id.spinnerKe);
        spinnerK.setOnItemSelectedListener(this);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), list, some_array);
        spinnerK.setAdapter(customAdapter);
        CustomAdapter customAdapter2 = new CustomAdapter(getApplicationContext(), list, some_array);
        spinnerS.setAdapter(customAdapter2);

        spinnerS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("dari", "onItemSelected: " + some_array[i]);
                from = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etjumlah.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Harap isi Jumlah", Toast.LENGTH_SHORT).show();
                }else{
                    konversi();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Log.e("ke", "onItemSelected: " + some_array[i]);
        to = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void konversi() {
        Call<Object> call = apiInterface.convert(some_array[from], some_array[to]);

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
//              Log.e("TAG", "response 33: "+new Gson().toJson(response.body()) );
                String jsonInString = new Gson().toJson(response.body());
                JSONObject json = null;
                try {
                    json = new JSONObject(jsonInString);
                    Log.e("TAG", "response 33: " + json.getDouble(some_array[to]));
                    Locale locale = new Locale("", code[to]);
                    Currency currency = null;
                    Double kurs = json.getDouble(some_array[to]);
                    Double final_result = kurs * Integer.parseInt(etjumlah.getText().toString());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        currency = Currency.getInstance(locale);
                        NumberFormat dollarFormat = NumberFormat.getCurrencyInstance(locale);
//                        System.out.println(currency.getDisplayName() + ": " + dollarFormat.format(json.getDouble(some_array[to])));
                        namauang.setText(currency.getDisplayName());
                        hasil.setText(dollarFormat.format(final_result));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.e("TAG", "onFailure: " + t.toString());
            }
        });
    }
}