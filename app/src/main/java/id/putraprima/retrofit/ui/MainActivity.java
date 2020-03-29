package id.putraprima.retrofit.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import id.putraprima.retrofit.R;
import id.putraprima.retrofit.api.helper.ServiceGenerator;
import id.putraprima.retrofit.api.models.LoginRequest;
import id.putraprima.retrofit.api.models.LoginResponse;
import id.putraprima.retrofit.api.services.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    TextView name,version;
    EditText email,pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle extras = getIntent().getExtras();
        name = findViewById(R.id.mainTxtAppName);
        version = findViewById(R.id.mainTxtAppVersion);
        email = findViewById(R.id.edtEmail);
        pass = findViewById(R.id.edtPassword);

        name.setText(extras.getString("APP_KEY"));
        version.setText(extras.getString("VER_KEY"));

    }

    private void login() {
        ApiInterface service = ServiceGenerator.createService(ApiInterface.class);
        Call<LoginResponse> call = service.login(new LoginRequest(email.getText().toString(), pass.getText().toString()));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Toast.makeText(MainActivity.this, response.body().getToken(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);

                intent.putExtra("TOKEN",response.body().token);
                intent.putExtra("TOKEN_TYPE",response.body().token_type);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Gagal Koneksi Ke Server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void handleLogin(View view) {
        login();
    }

    public void registerMain(View view) {
        startActivity(new Intent(this,RegisterActivity.class));
    }
}
