package com.proj.agnus.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.Window;
import android.view.WindowManager;

import com.proj.agnus.R;
import com.proj.agnus.conexion.check_internet_connection;
import com.proj.agnus.seguridad.Login1;

public class Splash_Screen extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 800;
    private String nombre_activity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && getIntent().getExtras() != null) {
            nombre_activity = getIntent().getExtras().getString("activity_name");

            if(nombre_activity != null){
                if(nombre_activity.equals("Notificacion_Local")){
                    ActivityCompat.finishAffinity(Splash_Screen.this);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("activity_name","Notificacion_Local");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    Splash_Screen.this.finish();
                }
            }
        }

        check_internet_connection check = new check_internet_connection();

        if(check.isConnected(this)){
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    SharedPreferences prefs = getSharedPreferences("Agnus_BD", MODE_PRIVATE);
                    Boolean recordar_contrasena = prefs.getBoolean("recordar_contrasena", false);

                    if (recordar_contrasena) {
                        Intent ir_inicio = new Intent(Splash_Screen.this, MainActivity.class);
                        ir_inicio.putExtra("activity_name","Splash");
                        Splash_Screen.this.startActivity(ir_inicio);
                    } else {
                        Intent ir_login = new Intent(Splash_Screen.this, Login1.class);
                        Splash_Screen.this.startActivity(ir_login);
                    }

                    Splash_Screen.this.finish();
                    overridePendingTransition(R.anim.fade_in1, R.anim.fade_out1);
                }
            }, SPLASH_DISPLAY_LENGTH);
        } else {
            check.MsgInternet(this);
        }
    }
}
