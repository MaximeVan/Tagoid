package com.example.vanbossm.tagoid;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.vanbossm.tagoid.data.Arret;
import com.example.vanbossm.tagoid.data.Ligne;
import com.example.vanbossm.tagoid.persistence.Favori;
import com.example.vanbossm.tagoid.persistence.Stockage;
import com.example.vanbossm.tagoid.persistence.StockageService;

import java.util.ArrayList;
import java.util.List;

public class FavorisActivity extends AppCompatActivity {

    private List<Favori> favoris;
    private StockageService stockage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoris);
        this.stockage = new Stockage();

        this.favoris = stockage.restore(getApplicationContext());
        fillListViewFavoris();

        final SwipeMenuListView smlv = (SwipeMenuListView) findViewById(R.id.listViewFavoris);
        smlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nomLigne = smlv.getItemAtPosition(position).toString().split(" : ")[0].split(" ")[1];
                String nomArret = smlv.getItemAtPosition(position).toString().split(" : ")[1];

                for(Favori favori : favoris) {
                    if(favori.getLigne().getShortName().equals(nomLigne)
                            && favori.getArret().getName().equals(nomArret)) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("ligneChoisie", nomLigne);
                        returnIntent.putExtra("arretChoisi", nomArret);
                        setResult(RESULT_OK,returnIntent);
                        finish();
                    }
                }
            }
        });
    }

    private void fillListViewFavoris() {
        final SwipeMenuListView smlv = (SwipeMenuListView) findViewById(R.id.listViewFavoris);
        List<String> nomsFavoris = new ArrayList<>();

        for (Favori favori : this.favoris) {
            nomsFavoris.add("Ligne " + favori.getLigne().getShortName() + " : " + favori.getArret().getName());
        }

        final ArrayAdapter adapteur = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, nomsFavoris);
        smlv.setAdapter(adapteur);

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                deleteItem.setWidth(170);
                deleteItem.setIcon(R.drawable.delete);

                menu.addMenuItem(deleteItem);
            }
        };

        smlv.setMenuCreator(creator);
        smlv.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        smlv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        stockage.remove(getApplicationContext(), smlv.getItemAtPosition(position).toString());
                        adapteur.remove(smlv.getItemAtPosition(position));
                        break;
                }
                return false;
            }
        });
    }
}
