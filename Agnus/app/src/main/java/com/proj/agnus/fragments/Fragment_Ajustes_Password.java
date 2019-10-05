package com.proj.agnus.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.proj.agnus.R;
import com.proj.agnus.conexion.connect_HttpsPost;
import com.proj.agnus.seguridad.Login1;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;


public class Fragment_Ajustes_Password extends Fragment {

    View rootView;
    EditText etPassActualAjustes, etPassNuevaAjustes, etPassConfirmarAjustes;
    Button btnActualizarAjustes;
    connect_HttpsPost conectar;
    SharedPreferences prefs;
    String url_pantalla_ajustes = "agnus.mx/app/ajustes.php";
    String url_pantalla_token = "agnus.mx/app/token.php";
    JSONObject jsonObjRecibido;
    int id_escuela;
    String tipo_usuario, codigo_usuario, contrasena;

    public Fragment_Ajustes_Password() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_ajustes_password, container, false);

        etPassActualAjustes = (EditText) rootView.findViewById(R.id.etPassActualAjustes);
        etPassNuevaAjustes = (EditText) rootView.findViewById(R.id.etPassNuevaAjustes);
        etPassConfirmarAjustes = (EditText) rootView.findViewById(R.id.etPassConfirmarAjustes);
        btnActualizarAjustes = (Button) rootView.findViewById(R.id.btnActualizarAjustes);

        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "0"));
        tipo_usuario = (prefs.getString("tipo_usuario", "0"));
        codigo_usuario = (prefs.getString("codigo_usuario", "0"));
        contrasena = (prefs.getString("contrasena_usuario", "0"));

        Log.e("--", ""+contrasena);

        btnActualizarAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCamposLlenos()){
                    if(etPassActualAjustes.getText().toString().equalsIgnoreCase(""+contrasena.replace(" ",""))){
                        if(validarFormatoCampos(etPassNuevaAjustes.getText().toString())){
                            if(etPassNuevaAjustes.getText().toString().length() > 5){
                                if(etPassNuevaAjustes.getText().toString().equals(etPassConfirmarAjustes.getText().toString())){
                                    TareaAsincrona tarea = new TareaAsincrona();
                                    tarea.execute();
                                } else {
                                    Toast.makeText(getActivity(), "Las Contraseñas NO coinciden", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getActivity(), "La Nueva contraseña debe tener al menos 6 dígitos", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Asegúrate que la Nueva Contraseña contenga:\n" +
                                            "\n-Al menos un número " +
                                            "\n-Al menos una letra en mayúscula " +
                                            "\n-Al menos 6 dígitos ",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "La Contraseña Actual no es correcta", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        return rootView;
    }

    public boolean validarCamposLlenos(){
        if(TextUtils.isEmpty(etPassActualAjustes.getText().toString())){
            etPassActualAjustes.setError("Este campo no puede estar vacio");
            return false;
        } else if(TextUtils.isEmpty(etPassNuevaAjustes.getText().toString())){
            etPassNuevaAjustes.setError("Este campo no puede estar vacio");
            return false;
        } else if(TextUtils.isEmpty(etPassConfirmarAjustes.getText().toString())){
            etPassConfirmarAjustes.setError("Este campo no puede estar vacio");
            return false;
        } else {
            return true;
        }
    }

    public boolean validarFormatoCampos(final String password) {
        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{4,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public void enviar_datos_recuperacion(){
        conectar = new connect_HttpsPost();

        HashMap<String, String> parametros_autenticacion = new HashMap<>();
        parametros_autenticacion.put("fun","4");
        parametros_autenticacion.put("esc", ""+id_escuela);
        parametros_autenticacion.put("tipo",""+tipo_usuario);
        parametros_autenticacion.put("codigo",""+codigo_usuario);
        parametros_autenticacion.put("actual", ""+etPassActualAjustes.getText().toString());
        parametros_autenticacion.put("nueva",""+etPassNuevaAjustes.getText().toString());


        jsonObjRecibido = conectar.connect(url_pantalla_ajustes, parametros_autenticacion, getActivity());
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Enviando datos al Servidor...");
            pDialog.setCancelable(true);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            enviar_datos_recuperacion();
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pDialog.setProgress(values[0].intValue());
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result) { pDialog.dismiss(); }
            try {
                String msj = jsonObjRecibido.getString("mensaje");
                final String status = jsonObjRecibido.getString("status");

                new AlertDialog.Builder(getActivity())
                        .setMessage(""+msj)
                        .setNeutralButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(status.equals("success")){
                                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE).edit();
                                    editor.putString("contrasena_usuario", etPassConfirmarAjustes.getText().toString().trim());
                                    editor.commit();

                                    final ProgressDialog dialog = new ProgressDialog(getActivity());
                                    dialog.setMessage("Cerrando sesión en Agnus\n\nPara aplicar nuevos cambios...");
                                    dialog.setCancelable(false);
                                    dialog.show();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {
                                            SharedPreferences.Editor editor = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE).edit();
                                            editor.putBoolean("recordar_contrasena", false);
                                            editor.commit();

                                            //Desligar Token
                                            desligar_token();
                                            dialog.dismiss();

                                            ActivityCompat.finishAffinity(getActivity());
                                            Intent intent = new Intent(getActivity().getApplicationContext(), Login1.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }, 3000);
                                }
                            }
                        })
                        .show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void desligar_token(){
        SharedPreferences prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        HashMap<String, String> parametros_token = new HashMap<>();
        parametros_token.put("fun","3");
        parametros_token.put("esc",""+prefs.getString("id_escuela","0"));
        parametros_token.put("token",""+prefs.getString("token","0"));
        jsonObjRecibido = conectar.connect(url_pantalla_token, parametros_token, getActivity());
    }

}
