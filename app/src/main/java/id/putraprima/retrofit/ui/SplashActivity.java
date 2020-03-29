package id.putraprima.retrofit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.helper.ServiceGenerator;
import id.putraprima.retrofit.api.models.AppVersion;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    TextView lblAppName, lblAppTittle, lblAppVersion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setupLayout();
        if (checkInternetConnection()==true) {
            checkAppVersion();
        }else{
            //Toast.makeText(this, "No connection", Toast.LENGTH_SHORT).show();
            View parentLayout = findViewById(android.R.id.content);
            snackbar(parentLayout);
        }
        setAppInfo();
    }

    private void setupLayout() {
        lblAppName = findViewById(R.id.lblAppName);
        lblAppTittle = findViewById(R.id.lblAppTittle);
        lblAppVersion = findViewById(R.id.lblAppVersion);
        //Sembunyikan lblAppName dan lblAppVersion pada saat awal dibuka
        lblAppVersion.setVisibility(View.INVISIBLE);
        lblAppName.setVisibility(View.INVISIBLE);
    }

    private boolean checkInternetConnection() {
        //TODO : 1. Implementasikan proses pengecekan koneksi internet, berikan informasi ke user jika tidak terdapat koneksi internet

        ConnectivityManager ConnectionManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true ) {
            return true;
        }
        else {
            return false;
        }
    }


    private void setAppInfo() {
        //TODO : 5. Implementasikan proses setting app info, app info pada fungsi ini diambil dari shared preferences
        SharedPreferences sh = getSharedPreferences("setting",MODE_PRIVATE);

        //lblAppVersion dan lblAppName dimunculkan kembali dengan data dari shared preferences
        //lblAppVersion.setVisibility(View.INVISIBLE);
        lblAppVersion.setText(sh.getString("version",""));
        lblAppVersion.setVisibility(View.VISIBLE);

        //lblAppName.setVisibility(View.INVISIBLE);
        lblAppName.setText(sh.getString("app","not updated"));
        lblAppName.setVisibility(View.VISIBLE);
    }

    public void intentMain(String app, String ver){
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("APP_KEY", app);
        intent.putExtra("VER_KEY", ver);
        startActivity(intent);
    }

    public void snackbar(View view){
        Snackbar.make(view, "No Connection", Snackbar.LENGTH_SHORT).show();
    }

    public void saveInfo(){

    }

    private void checkAppVersion() {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<AppVersion> call = service.getAppVersion();
        call.enqueue(new Callback<AppVersion>() {
            @Override
            public void onResponse(Call<AppVersion> call, Response<AppVersion> response) {
                Toast.makeText(SplashActivity.this, response.body().getApp(), Toast.LENGTH_SHORT).show();
                //Todo : 2. Implementasikan Proses Simpan Data Yang didapat dari Server ke SharedPreferences
                // Storing data into SharedPreferences

                SharedPreferences sharedPreferences = getSharedPreferences("setting",MODE_PRIVATE);

                // Creating an Editor object
                // to edit(write to the file)
                SharedPreferences.Editor myEdit = sharedPreferences.edit();

                // Storing the key and its value
                // as the data fetched from edittext
                myEdit.putString("version",response.body().getVersion());
                myEdit.putString("app",response.body().getApp());
                //myEdit.putInt("age",Integer.parseInt(age.getText().toString()));

                // Once the changes have been made,
                // we need to commit to apply those changes made,
                // otherwise, it will throw an error
                myEdit.apply();




                //Todo : 3. Implementasikan Proses Pindah Ke MainActivity Jika Proses getAppVersion() sukses
                if(response.body().getVersion() != null){
                    setAppInfo();
                    intentMain(response.body().getApp(), response.body().getVersion());
                }



            }

            @Override
            public void onFailure(Call<AppVersion> call, Throwable t) {
                Toast.makeText(SplashActivity.this, "Gagal Koneksi Ke Server", Toast.LENGTH_SHORT).show();
                //Todo : 4. Implementasikan Cara Notifikasi Ke user jika terjadi kegagalan koneksi ke server silahkan googling cara yang lain selain menggunakan TOAST
                View parentLayout = findViewById(android.R.id.content);
                snackbar(parentLayout);
            }

        });
    }
}
