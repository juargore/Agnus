package com.proj.agnus.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.proj.agnus.R;
import com.proj.agnus.adaptadores.Adaptador_Calendario;
import com.proj.agnus.comun.Calendario;
import com.proj.agnus.comun.Periodo;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Calendario extends Fragment {

    private ArrayList<Periodo> periodos;
    ArrayList<Calendario> lista = new ArrayList<>();
    SharedPreferences prefs;
    int id_escuela, id_opcion = 0;
    View rootView;
    RecyclerView rvListaCalendario;
    Spinner spinnerPeriodosC;
    TextView txtNoEventosCalendario;
    public Adaptador_Calendario adapter;
    public connect_HttpPost conectar;
    private JSONObject jsonObjRecibido;
    boolean textVisible = false;
    final String url_spinner_calendario = "agnus.mx/app/calendario.php";

    public Fragment_Calendario(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_calendario, container, false);

        spinnerPeriodosC = (Spinner) rootView.findViewById(R.id.spinnerPeriodosC);
        rvListaCalendario = (RecyclerView) rootView.findViewById(R.id.rvListaCalendario);
        txtNoEventosCalendario = (TextView) rootView.findViewById(R.id.txtNoEventosCalendario);

        conectar = new connect_HttpPost();
        periodos = new ArrayList<>();
        List<String> periodo_lista = new ArrayList<>();

        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));

        HashMap<String, String> parametros_spinner_calendario = new HashMap<>();
        parametros_spinner_calendario.put("fun","1");
        parametros_spinner_calendario.put("esc",""+id_escuela);
        jsonObjRecibido = conectar.connect(url_spinner_calendario, parametros_spinner_calendario, getActivity());

        try {
            for(int i = 0; i < jsonObjRecibido.getJSONArray("select").length(); i++){
                periodo_lista.add(jsonObjRecibido.getJSONArray("select").getJSONObject(i).getString("opcion"));
                periodos.add(new Periodo(
                        Integer.parseInt(jsonObjRecibido.getJSONArray("select").getJSONObject(i).getString("id")),
                        jsonObjRecibido.getJSONArray("select").getJSONObject(i).getString("opcion")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, periodo_lista);
        spinnerPeriodosC.setAdapter(dataAdapter);
        spinnerPeriodosC.setSelection(0, true);

        spinnerPeriodosC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#0D3863"));
                ((TextView) adapterView.getChildAt(0)).setTextSize(12);
                id_opcion = periodos.get(i).obtenerId();

                TareaAsincrona tarea = new TareaAsincrona();
                tarea.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvListaCalendario.setLayoutManager(layoutManager);

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        return rootView;
    }

    public void llenarLista(){

        JSONObject jsonObjRecibido;
        connect_HttpPost conectar = new connect_HttpPost();

        HashMap<String, String> parametros_eventos_calendario = new HashMap<>();
        parametros_eventos_calendario.put("fun","2");
        parametros_eventos_calendario.put("esc",""+id_escuela);
        parametros_eventos_calendario.put("dias",""+id_opcion);

        jsonObjRecibido = conectar.connect(url_spinner_calendario, parametros_eventos_calendario, getActivity());
        lista.clear();

        try {
            if(jsonObjRecibido.get("status").equals("fail")){
                textVisible = true;
            } else {
                textVisible = false;
                for(int i = 0; i < jsonObjRecibido.getJSONArray("actividad").length(); i++){
                    lista.add(new Calendario(i,
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Titulo"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Descripcion"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Producto"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Entrega"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Recibe"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Tipo"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("fechaini"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("horaini"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("fechafin"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("horafin"))
                    );
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adaptadorLleno(){
        adapter = new Adaptador_Calendario(lista, getActivity(), new Adaptador_Calendario.OnItemClickListener() {
            @Override
            public void OnItemClick(Calendario evento_calendario) {

                final LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final AlertDialog.Builder imageDialog = new AlertDialog.Builder(getActivity());
                View layout = inflater.inflate(R.layout.pop_up_calendario, (ViewGroup) getView().findViewById(R.id.layContenedorCalendario));

                final TextView txtActividadPopC = (TextView)layout.findViewById(R.id.txtActividadPopC);
                final TextView txtRDescripcionPopC = (TextView)layout.findViewById(R.id.txtRDescripcionPopC);
                final TextView txtRProductoPopC = (TextView)layout.findViewById(R.id.txtRProductoPopC);
                final TextView txtREntregaPopC = (TextView)layout.findViewById(R.id.txtREntregaPopC);
                final TextView txtRRecibePopC = (TextView)layout.findViewById(R.id.txtRRecibePopC);
                final TextView txtRTipoPopC = (TextView)layout.findViewById(R.id.txtRTipoPopC);
                final TextView txtFechaInPopC = (TextView)layout.findViewById(R.id.txtFechaInPopC);
                final TextView txtHoraInPopC = (TextView)layout.findViewById(R.id.txtHoraInPopC);
                final TextView txtFechaFinPopC = (TextView)layout.findViewById(R.id.txtFechaFinPopC);
                final TextView txtHoraFinPopC = (TextView)layout.findViewById(R.id.txtHoraFinPopC);

                txtActividadPopC.setText(""+evento_calendario.obtenerNombre());
                txtRDescripcionPopC.setText(""+evento_calendario.getDescripcion());
                txtRProductoPopC.setText(""+evento_calendario.getProducto());
                txtREntregaPopC.setText(""+evento_calendario.getEntrega());
                txtRRecibePopC.setText(""+evento_calendario.getRecibe());
                txtRTipoPopC.setText(""+evento_calendario.getTipo());
                txtFechaInPopC.setText(""+evento_calendario.getFecha_ini());
                txtHoraInPopC.setText(""+evento_calendario.getHora_ini());
                txtFechaFinPopC.setText(""+evento_calendario.getFecha_fin());
                txtHoraFinPopC.setText(""+evento_calendario.getHora_fin());

                imageDialog.setView(layout);
                AlertDialog alert = imageDialog.create();
                alert.show();
            }
        });

        rvListaCalendario.setAdapter(adapter);
    }

    private class TareaAsincrona extends AsyncTask<Void, Integer, Boolean> {

        ProgressDialog pDialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Espere un momento por favor\n\nDescargando contenido...");
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            llenarLista();
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            pDialog.setProgress(progreso);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            adaptadorLleno();
            if(textVisible){
                txtNoEventosCalendario.setVisibility(View.VISIBLE);
            } else {
                txtNoEventosCalendario.setVisibility(View.GONE);
            }
            if(result) { pDialog.dismiss(); }
        }
    }

}
