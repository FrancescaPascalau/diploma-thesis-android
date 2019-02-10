package francesca.pascalau.thesis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import francesca.pascalau.thesis.R;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ale mele
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Toast.makeText(getApplicationContext(), "Logged in", Toast.LENGTH_SHORT).show();
            findViewById(R.id.map_button).setVisibility(View.VISIBLE);
            findViewById(R.id.logout_button).setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
            findViewById(R.id.map_button).setVisibility(View.GONE);
            findViewById(R.id.logout_button).setVisibility(View.GONE);
        }

        configureMapButton();
        configureLoginButton();
        configureRegisterButton();
        configureLogoutButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            findViewById(R.id.map_button).setVisibility(View.VISIBLE);
            findViewById(R.id.logout_button).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.map_button).setVisibility(View.GONE);
            findViewById(R.id.logout_button).setVisibility(View.GONE);
        }
    }

    // Făcute de mine
    private void configureMapButton() {
        Button mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                // User is logged in
                startActivity(new Intent(MainActivity.this, MapsActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Login/Register before you try to access Maps", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configureLoginButton() {
        Button mapButton = findViewById(R.id.login_button);
        mapButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignInActivity.class)));
    }

    private void configureLogoutButton() {
        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                auth.signOut();
                recreate();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private void configureRegisterButton() {
        Button mapButton = findViewById(R.id.register_button);
        mapButton.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SignUpActivity.class)));
    }
}
