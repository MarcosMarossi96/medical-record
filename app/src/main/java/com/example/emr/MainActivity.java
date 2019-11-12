package com.example.emr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.emr.Adapter.AdapterCountry;
import com.example.emr.Models.User;
import com.example.emr.Services.DataService;

import java.util.ArrayList;
import java.util.Locale;

import com.example.emr.User.MenuUsrActivity;
import com.example.emr.Doctor.MenuDocActivity;
import com.example.emr.Nurse.MenuNurActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private Spinner idioma;
    private ArrayList<CountryItem> countryList;
    private AdapterCountry adapterCountry;
    Retrofit retrofit;
    private String stringURL = "https://ey7li2szf0.execute-api.us-east-1.amazonaws.com/dev/";
    String Token, getToken, Profile;
    SharedPreferences sharedPreferences;
    private Button btnAux;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mudarIdioma();
        setContentView(R.layout.act_main);

        retrofit = new Retrofit.Builder()
                .baseUrl(stringURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        sharedPreferences = getSharedPreferences("salvarToken", MODE_PRIVATE);
        getToken = sharedPreferences.getString("token", null);

        DataService service = retrofit.create(DataService.class);
        Call<User> call = service.pegarToken(getToken);

        call.enqueue(new Callback<User>() {

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {

                    Profile = response.body().getProfile();
                    if (Profile.equals("patient") && !getToken.equals("")) {
                        menuPaciente();
                    } else if (Profile.equals("medic") && !getToken.equals("")) {
                        menuMedico();
                    } else if (Profile.equals("nurse") && !getToken.equals("")) {
                        menuEnfermeira();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Error", "Ocorreu um erro: " + t);
            }
        });


        initList();
        idioma = findViewById(R.id.spinIdioma);

        adapterCountry = new AdapterCountry(this, countryList);
        idioma.setAdapter(adapterCountry);

        idioma.setSelected(false);
        idioma.setSelection(getLanguage(), true);


        idioma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CountryItem clickedItem = (CountryItem) parent.getItemAtPosition(position);
                String clickedCountryName = clickedItem.getCountryName().toLowerCase();
                // Toast.makeText(MainActivity.this, clickedCountryName + " selected", Toast.LENGTH_LONG).show();
                alteraridioma(clickedCountryName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





    }

    private void initList() {
        countryList = new ArrayList<>();
        countryList.add(new CountryItem("PT-BR", R.drawable.brazil));
        countryList.add(new CountryItem("USA", R.drawable.usa3));
        countryList.add(new CountryItem("ES", R.drawable.spain));
        countryList.add(new CountryItem("FR", R.drawable.french));
        countryList.add(new CountryItem("ALE", R.drawable.germany));
        countryList.add(new CountryItem("HU", R.drawable.hungary));

    }

    public void abrirTelaLogin(View v) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void abrirTelaCadastro(View v) {
        startActivity(new Intent(this, RefactorActivity.class));
    }

    public void mudarIdioma()
    {
        SharedPreferences dados = getSharedPreferences("emr-language", MODE_PRIVATE);
        String lang = dados.getString("idioma", getResources().getConfiguration().locale.getLanguage());

        Locale idioma = new Locale(lang);
        Locale.setDefault(idioma);

        Context context = this;
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        config.setLocale(idioma);
        res.updateConfiguration(config, res.getDisplayMetrics());
        //finish();
        //startActivity(new Intent(this,MainActivity.class));

    }

    public void alteraridioma(String idioma) {

        Locale locale;
        if(idioma.equals("pt-br"))
            locale = new Locale("pt");
        else if(idioma.equals("usa"))
            locale = new Locale("en");
        else if(idioma.equals("ale"))
            locale = new Locale("de");
        else
            locale = new Locale(idioma);



        Locale.setDefault(locale);

        Context context = this;
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());

        SharedPreferences.Editor dados = getSharedPreferences("emr-language", MODE_PRIVATE).edit();
        dados.putString("idioma", getResources().getConfiguration().locale.getLanguage());
        dados.commit();

        finish();
        startActivity(new Intent(this,MainActivity.class));
    }


    public int getLanguage(){
        SharedPreferences dados = getSharedPreferences("emr-language",MODE_PRIVATE);
        String result = dados.getString("idioma","");
        // Toast.makeText(MainActivity.this,result+"",Toast.LENGTH_LONG).show();
        switch(result){
            case "pt":
                return 0;
            case "en":
                return 1;
            case "es":
                return 2;
            case "fr":
                return 3;
            case "de":
                return 4;
            case "hu":
                return 5;

        }
        return 1;
    }

    public void menuPaciente() {
        Intent intent = new Intent(this, MenuUsrActivity.class);
        startActivity(intent);
    }

    public void menuMedico() {
        Intent intent = new Intent(this, MenuDocActivity.class);
        startActivity(intent);
    }

    public void menuEnfermeira() {
        Intent intent = new Intent(this, MenuNurActivity.class);
        startActivity(intent);
    }

}
