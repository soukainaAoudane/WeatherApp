package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MeteoListModel extends ArrayAdapter<MeteoItem> {

    private List<MeteoItem> listItems;
    private int resource;

    // Table de correspondance entre la chaîne de l'API ("Clouds", "Rain") et vos images
    public static Map<String, Integer> images = new HashMap<>();
    static {
        // IMPORTANT: Vous devez avoir ces images dans votre dossier res/drawable
        images.put("Clear", R.drawable.clear);
        images.put("Clouds", R.drawable.clouds);
        images.put("Rain", R.drawable.thunderstorm);
        images.put("Thunderstorm", R.drawable.thunderstorm); // Note: le TP a une typo "thunderstormspng"
        // Ajoutez d'autres images si nécessaire (Snow, Mist, etc.)
    }

    public MeteoListModel(Context context, int resource, List<MeteoItem> data) {
        super(context, resource, data);
        this.listItems = data;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }

        ImageView imageView = listItem.findViewById(R.id.imageView);
        TextView textViewTempMax = listItem.findViewById(R.id.textViewTempMAX);
        TextView textViewTempMin = listItem.findViewById(R.id.textViewTempMin);
        TextView textViewPression = listItem.findViewById(R.id.textViewPression);
        TextView textViewHumidite = listItem.findViewById(R.id.textViewHumidite);
        TextView textViewDate = listItem.findViewById(R.id.textViewDate);

        // Récupération de l'item à la position actuelle
        MeteoItem currentItem = listItems.get(position);

        // Récupération de la clé pour l'image (ex: "Clouds")
        String key = currentItem.image;
        if (key != null && images.containsKey(key)) {
            imageView.setImageResource(images.get(key));
        } else {
            // Mettre une image par défaut si la clé n'est pas trouvée
            imageView.setImageResource(R.drawable.clouds); // ou une autre image par défaut
        }

        // Remplissage des TextViews avec les données de l'item
        textViewTempMax.setText(String.valueOf(currentItem.tempMax) + " °C");
        textViewTempMin.setText(String.valueOf(currentItem.tempMin) + " °C");
        textViewPression.setText(String.valueOf(currentItem.pression) + " hPa");
        textViewHumidite.setText(String.valueOf(currentItem.humidite) + " %");
        textViewDate.setText(currentItem.date);

        return listItem;
    }
}
