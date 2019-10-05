package com.proj.agnus.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.proj.agnus.adaptadores.Adaptador_Agenda;
import com.proj.agnus.comun.Agenda;
import com.proj.agnus.comun.Periodo;
import com.proj.agnus.conexion.check_internet_connection;
import com.proj.agnus.conexion.connect_HttpPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Fragment_Agenda extends Fragment {

    private ArrayList<Periodo> periodos;
    ArrayList<Agenda> lista = new ArrayList<>();
    SharedPreferences prefs;
    int id_escuela, id_opcion = 0;
    View rootView;
    RecyclerView rvListaAgenda;
    Spinner spinnerPeriodosA;
    TextView txtNoEventosAgenda;
    public Adaptador_Agenda adapter;
    public connect_HttpPost conectar;
    private JSONObject jsonObjRecibido;
    boolean textVisible = false;
    final String url_spinner_agenda = "agnus.mx/app/agenda.php";

    public Fragment_Agenda(){}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_agenda, container, false);

        spinnerPeriodosA = (Spinner) rootView.findViewById(R.id.spinnerPeriodosA);
        rvListaAgenda = (RecyclerView) rootView.findViewById(R.id.rvListaAgenda);
        txtNoEventosAgenda = (TextView) rootView.findViewById(R.id.txtNoEventosAgenda);

        conectar = new connect_HttpPost();
        periodos = new ArrayList<>();
        List<String> periodo_lista = new ArrayList<>();

        prefs = getActivity().getSharedPreferences("Agnus_BD", MODE_PRIVATE);
        id_escuela = Integer.parseInt(prefs.getString("id_escuela", "1"));

        HashMap<String, String> parametros_spinner_agenda = new HashMap<>();
        parametros_spinner_agenda.put("fun","1");
        parametros_spinner_agenda.put("esc",""+id_escuela);
        jsonObjRecibido = conectar.connect(url_spinner_agenda, parametros_spinner_agenda, getActivity());

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
        spinnerPeriodosA.setAdapter(dataAdapter);
        spinnerPeriodosA.setSelection(0, true);

        spinnerPeriodosA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.parseColor("#0D3863"));
                ((TextView) adapterView.getChildAt(0)).setTextSize(12);
                id_opcion = periodos.get(i).obtenerId();

                check_internet_connection check = new check_internet_connection();
                if(check.isConnected(getActivity())){
                    TareaAsincrona tarea = new TareaAsincrona();
                    tarea.execute();
                }else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("No se logró conectar al servidor. Verifique su conexión e intente de nuevo");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Aceptar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvListaAgenda.setLayoutManager(layoutManager);

        TareaAsincrona tarea = new TareaAsincrona();
        tarea.execute();

        return rootView;
    }

    public void llenarLista(){

        JSONObject jsonObjRecibido;
        connect_HttpPost conectar = new connect_HttpPost();

        HashMap<String, String> parametros_eventos_agenda = new HashMap<>();
        parametros_eventos_agenda.put("fun","2");
        parametros_eventos_agenda.put("esc",""+id_escuela);
        parametros_eventos_agenda.put("dias",""+id_opcion);

        jsonObjRecibido = conectar.connect(url_spinner_agenda, parametros_eventos_agenda, getActivity());
        lista.clear();

        try {
            if(jsonObjRecibido.get("status").equals("fail")){
                textVisible = true;
            } else {
                textVisible = false;
                for(int i = 0; i < jsonObjRecibido.getJSONArray("actividad").length(); i++){
                    lista.add(new Agenda(i,
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Titulo"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Descripcion"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Lugar"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("fechaini"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("horaini"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("fechafin"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("horafin"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Responsable"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Departamento"),
                            jsonObjRecibido.getJSONArray("actividad").getJSONObject(i).getString("Grupo")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void adaptadorLleno(){
        adapter = new Adaptador_Agenda(lista, getActivity(), new Adaptador_Agenda.OnItemClickListener() {
            @Override
            public void OnItemClick(Agenda evento_agenda) {

                final LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                final AlertDialog.Builder imageDialog = new AlertDialog.Builder(getActivity());
                View layout = inflater.inflate(R.layout.pop_up_agenda, (ViewGroup) getView().findViewById(R.id.layContenedorAgenda));

                final TextView txtActividadPop = (TextView)layout.findViewById(R.id.txtActividadPop);
                final TextView txtRDescripcionPop = (TextView)layout.findViewById(R.id.txtRDescripcionPop);
                final TextView txtRLugarPop = (TextView)layout.findViewById(R.id.txtRLugarPop);
                final TextView txtRResponsablePop = (TextView)layout.findViewById(R.id.txtRResponsablePop);
                final TextView txtRDepartamentoPop = (TextView)layout.findViewById(R.id.txtRDepartamentoPop);
                final TextView txtRGrupoPop = (TextView)layout.findViewById(R.id.txtRGrupoPop);
                final TextView txtFechaInPop = (TextView)layout.findViewById(R.id.txtFechaInPop);
                final TextView txtHoraInPop = (TextView)layout.findViewById(R.id.txtHoraInPop);
                final TextView txtFechaFinPop = (TextView)layout.findViewById(R.id.txtFechaFinPop);
                final TextView txtHoraFinPop = (TextView)layout.findViewById(R.id.txtHoraFinPop);

                txtActividadPop.setText(""+evento_agenda.obtenerNombre());
                txtRDescripcionPop.setText(""+evento_agenda.getDescripcion());
                txtRLugarPop.setText(""+evento_agenda.getLugar());
                txtFechaInPop.setText(""+evento_agenda.getFecha_ini());
                txtHoraInPop.setText(""+evento_agenda.getHora_ini());
                txtFechaFinPop.setText(""+evento_agenda.getFecha_fin().replace(" ",""));
                txtHoraFinPop.setText(""+evento_agenda.getHora_fin());
                txtRResponsablePop.setText(""+evento_agenda.getResponsable());
                txtRDepartamentoPop.setText(""+evento_agenda.getDepartamento());
                txtRGrupoPop.setText(""+evento_agenda.getGrupo());

                imageDialog.setView(layout);
                AlertDialog alert = imageDialog.create();
                alert.show();
            }
        });

        rvListaAgenda.setAdapter(adapter);
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
                txtNoEventosAgenda.setVisibility(View.VISIBLE);
            } else {
                txtNoEventosAgenda.setVisibility(View.GONE);
            }
            if(result) { pDialog.dismiss(); }
        }
    }

}
