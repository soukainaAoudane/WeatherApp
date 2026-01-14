package com.example.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextVille;
    private ListView listViewMeteo;
    private List<MeteoItem> data = new ArrayList<>();
    private MeteoListModel model;
    private ImageButton buttonOK;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextVille = findViewById(R.id.editTextVille);
        listViewMeteo = findViewById(R.id.listViewMeteo);
        buttonOK = findViewById(R.id.buttonOK);

        // Initialisation du modèle (adaptateur) et liaison avec la ListView
        model = new MeteoListModel(this, R.layout.list_item_layout, data);
        listViewMeteo.setAdapter(model);

        // Initialisation de la file de requêtes Volley
        queue = Volley.newRequestQueue(getApplicationContext());

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("MyLog", "Bouton cliqué");

                String ville = editTextVille.getText().toString().trim();
                if (ville.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Veuillez entrer une ville", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Vider la liste pour la nouvelle recherche
                data.clear();
                model.notifyDataSetChanged();

                Log.i("MyLog", "Recherche pour la ville : " + ville);

                // IMPORTANT: Remplacez par votre clé API valide
                String url = "https://api.openweathermap.org/data/2.5/forecast?q="
                        + ville + "&appid=a4578e39643716894ec78b28a71c7110&units=metric";

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.i("MyLog", "Réponse reçue de l'API.");

                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("list");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        MeteoItem meteoItem = new MeteoItem();
                                        JSONObject d = jsonArray.getJSONObject(i);

                                        // Formatage de la date
                                        Date date = new Date(d.getLong("dt") * 1000);
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.FRENCH);
                                        String dateString = sdf.format(date);

                                        JSONObject main = d.getJSONObject("main");
                                        JSONArray weather = d.getJSONArray("weather");

                                        // Pas besoin de conversion Kelvin si on utilise `units=metric`
                                        int tempMin = (int) main.getDouble("temp_min");
                                        int tempMax = (int) main.getDouble("temp_max");
                                        int pression = main.getInt("pressure");
                                        int humidity = main.getInt("humidity");

                                        meteoItem.tempMax = tempMax;
                                        meteoItem.tempMin = tempMin;
                                        meteoItem.pression = pression;
                                        meteoItem.humidite = humidity;
                                        meteoItem.date = dateString;
                                        meteoItem.image = weather.getJSONObject(0).getString("main");

                                        data.add(meteoItem);
                                    }

                                    // Notifier l'adaptateur que les données ont changé
                                    model.notifyDataSetChanged();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("MyLog", "Erreur de parsing JSON", e);
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("MyLog", "Erreur de connexion ! ", error);
                                Toast.makeText(MainActivity.this, "Erreur réseau ou ville non trouvée", Toast.LENGTH_LONG).show();
                            }
                        });

                // Ajouter la requête à la file
                queue.add(stringRequest);
            }
        });
    }

    // --- PARTIE 10 : CYCLE DE VIE DE L'ACTIVITY ---

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Application devient visible (onStart)", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Application prête à être utilisée (onResume)", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "Application perd le focus (onPause)", Toast.LENGTH_SHORT).show();
        Log.i("MyLog", "L'application est mise en pause.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        new AlertDialog.Builder(this)
                .setTitle("Information")
                .setMessage("L'application est désormais en arrière-plan.")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new AlertDialog.Builder(this)
                .setTitle("Re-bienvenue !")
                .setMessage("Continuer l'utilisation de l'application ?")
                .setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Reprise...", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish(); // Ferme l'application
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MyLog", "Fermeture définitive de l'application (onDestroy).");
        // Libérer les ressources
        if (queue != null) {
            queue.cancelAll(this);
        }
    }
}
