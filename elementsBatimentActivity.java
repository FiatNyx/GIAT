package com.giat.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class elementsBatimentActivity extends AppCompatActivity {
    List<Appartement> listeApparts = new ArrayList<>();
    List<ElementsBatiment> listeElements = new ArrayList<>();
    FirebaseFirestore db;
    LinearLayout layoutElements;
    List<Button> listeBoutonsAppart = new ArrayList<>();
    List<Button> listeBoutonsElement  = new ArrayList<>();
    String adresse;
    Button addAppartButton;
    Button addElementButton;
    int nbrAppart = 0;
    ListenerRegistration appartListener;
    ListenerRegistration nbrAppartListener;
    ListenerRegistration elementListener;
    EditText editTextNouvelElement;
    List<View> listeSeparateurs = new ArrayList<>();
    //--------------------------Update layout-------------------------

    protected void updateLayout(){
        if(addAppartButton != null){
            layoutElements.removeView(addAppartButton);
        }
        if(addElementButton != null){
            layoutElements.removeView(addElementButton);
        }
        if(editTextNouvelElement != null){
            layoutElements.removeView(editTextNouvelElement);
        }

        for(View separateurADelete : listeSeparateurs){
            layoutElements.removeView(separateurADelete);
        }
        for(Button button : listeBoutonsAppart){
            layoutElements.removeView(button);
        }
        listeBoutonsAppart.clear();

        for(Button button : listeBoutonsElement){
            layoutElements.removeView(button);
        }
        listeBoutonsElement.clear();


        for(int i = 0; i < listeApparts.size(); i++) {
            Button btnAppart = new Button(this);
            String texte = "Appartement " + listeApparts.get(i).getNuméro();
            btnAppart.setText(texte);
            btnAppart.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            final String nbrAppartement = listeApparts.get(i).getNuméro();
            btnAppart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(elementsBatimentActivity.this, PiecesAppartActivity.class);
                    intent.putExtra("Numéro", nbrAppartement);
                    intent.putExtra("Adresse", adresse);

                    appartListener.remove();
                    nbrAppartListener.remove();
                    elementListener.remove();

                    startActivity(intent);


                    //TODO: Changer la page selon la collection
                }
            });


            // Add Button to LinearLayout
            if (layoutElements != null) {
                layoutElements.addView(btnAppart);
                listeBoutonsAppart.add(btnAppart);
            }
        }

        //-----------------Bouton ajouter appartement----------------------

        addAppartButton = new Button(this);;
        addAppartButton.setText("Ajouter un appartement");
        addAppartButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addAppartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference compteurRef = db.collection("Bâtiments").document(adresse);

                Map<String, String> appartementMap = new HashMap<>();

                nbrAppart++;
                String nbrAppartTemp = Integer.toString(nbrAppart);
                appartementMap.put("Numéro", nbrAppartTemp);
                db.collection("Bâtiments").document(adresse).collection("Appartements").document("Appartement" + nbrAppartTemp)
                        .set(appartementMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Ajouté au serveur avec succès",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Erreur : " + e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                compteurRef.update("NombreAppart", FieldValue.increment(1));


                Map<String, String> pieceMap = new HashMap<>();
                pieceMap.put("NombreDeProblemes", "0");

                List<String> listePiecesAppart = new ArrayList<>();

                listePiecesAppart.add("01. Salle de bain"); listePiecesAppart.add("02. Salle d'eau");
                listePiecesAppart.add("03. Cuisine"); listePiecesAppart.add("04. Coin-repas");
                listePiecesAppart.add("05. Salon"); listePiecesAppart.add("06. Passage");
                listePiecesAppart.add("07. Vestiaire"); listePiecesAppart.add("08. Buanderie");
                listePiecesAppart.add("09. Chambre 1"); listePiecesAppart.add("10. Rangement 1");
                listePiecesAppart.add("11. Chambre 2"); listePiecesAppart.add("12. Rangement 2");
                listePiecesAppart.add("13. Chambre 3"); listePiecesAppart.add("14. Rangement 3");
                listePiecesAppart.add("15. Chambre 4"); listePiecesAppart.add("16. Rangement 4");
                listePiecesAppart.add("17. Autre");

                for (String piece : listePiecesAppart){
                    pieceMap.put("Nom", piece);
                    db.collection("Bâtiments").document(adresse).collection("Appartements").document("Appartement" + nbrAppartTemp).collection("Pièces").document(piece)
                            .set(pieceMap);
                }
            }
        });

        layoutElements.addView(addAppartButton);

        View separateur = new View(this);
        LinearLayout.LayoutParams parametres = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 3);
        parametres.bottomMargin = 40;
        parametres.topMargin = 40;
        separateur.setLayoutParams(parametres);

        separateur.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black));

        if (layoutElements != null) {
            layoutElements.addView(separateur);
            listeSeparateurs.add(separateur);
        }

        for(int i = 0; i < listeElements.size(); i++){
            Button btnElement = new Button(this);
            final String nomElement = listeElements.get(i).getNom();
            btnElement.setText(nomElement);
            btnElement.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            btnElement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(elementsBatimentActivity.this, ProblemeElementActivity.class);
                    intent.putExtra("NomElement", nomElement);
                    intent.putExtra("Adresse", adresse);

                    appartListener.remove();
                    nbrAppartListener.remove();
                    elementListener.remove();

                    startActivity(intent);


                    //TODO: Changer la page selon la collection
                }
            });


            // Add Button to LinearLayout
            if (layoutElements != null) {
                layoutElements.addView(btnElement);
                listeBoutonsElement.add(btnElement);
            }
        }

        editTextNouvelElement = new EditText(this);
        editTextNouvelElement.setHint("Nom du nouvel élément");
        editTextNouvelElement.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        editTextNouvelElement.setPadding(0,100, 0,30);
        layoutElements.addView(editTextNouvelElement);

        addElementButton = new Button(this);;
        addElementButton.setText("Ajouter un élément commun");
        addElementButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addElementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Map<String, String> elementMap = new HashMap<>();

                String nbrAppartTemp = Integer.toString(nbrAppart);
                elementMap.put("Adresse", adresse);
                elementMap.put("Nom", editTextNouvelElement.getText().toString());

                db.collection("Bâtiments").document(adresse).collection("Elements").document(editTextNouvelElement.getText().toString())
                        .set(elementMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Ajouté au serveur avec succès",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Erreur : " + e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });

        layoutElements.addView(addElementButton);

    }






//-----------------------------Début de onCreate---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elements_batiment);


        //-----------------------Créer appart bouton onCreate----------------------------------
        adresse = getIntent().getExtras().getString("Adresse");
        TextView textViewTitre = (TextView) findViewById(R.id.textViewTitre);
        textViewTitre.setText(adresse);


        layoutElements = findViewById(R.id.layoutElements);

        addAppartButton = new Button(this);;
        addAppartButton.setText("Ajouter un appartement");
        addAppartButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addAppartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DocumentReference compteurRef = db.collection("Bâtiments").document(adresse);

                Map<String, String> appartementMap = new HashMap<>();

                nbrAppart++;
                String nbrAppartTemp = Integer.toString(nbrAppart);
                appartementMap.put("Numéro", nbrAppartTemp);
                db.collection("Bâtiments").document(adresse).collection("Appartements").document("Appartement" + nbrAppartTemp)
                        .set(appartementMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Ajouté au serveur avec succès",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(),"Erreur : " + e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                compteurRef.update("NombreAppart", FieldValue.increment(1));


                Map<String, String> pieceMap = new HashMap<>();
                pieceMap.put("Nombre de problèmes", "0");

                List<String>  listePiecesAppart = new ArrayList<>();

                listePiecesAppart.add("01. Salle de bain"); listePiecesAppart.add("02. Salle d'eau");
                listePiecesAppart.add("03. Cuisine"); listePiecesAppart.add("04. Coin-repas");
                listePiecesAppart.add("05. Salon"); listePiecesAppart.add("06. Passage");
                listePiecesAppart.add("07. Vestiaire"); listePiecesAppart.add("08. Buanderie");
                listePiecesAppart.add("09. Chambre 1"); listePiecesAppart.add("10. Rangement 1");
                listePiecesAppart.add("11. Chambre 2"); listePiecesAppart.add("12. Rangement 2");
                listePiecesAppart.add("13. Chambre 3"); listePiecesAppart.add("14. Rangement 3");
                listePiecesAppart.add("15. Chambre 4"); listePiecesAppart.add("16. Rangement 4");
                listePiecesAppart.add("17. Autre");


                for (String piece : listePiecesAppart){
                    pieceMap.put("Nom", piece);
                    db.collection("Bâtiments").document(adresse).collection("Appartements").document("Appartement" + nbrAppartTemp).collection("Pièces").document(piece)
                            .set(pieceMap);
                }
            }
        });

        layoutElements.addView(addAppartButton);


    //---------------Return Button------------------------------------

        Button returnButton = (Button) findViewById(R.id.retournerButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elementListener.remove();
                appartListener.remove();
                nbrAppartListener.remove();
                finish();
            }
        });


        //-------------------Listener à la base de donnée-----------------------------

        db = FirebaseFirestore.getInstance();

        appartListener = db.collection("Bâtiments").document(adresse).collection("Appartements").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Toast.makeText(getApplicationContext(),"Lecture des données échouée",Toast.LENGTH_LONG).show();
                }

                if (!queryDocumentSnapshots.isEmpty()) {
                    listeApparts.clear();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for(DocumentSnapshot d : list){
                        Appartement appart = d.toObject(Appartement.class);
                        listeApparts.add(appart);
                    }

                    updateLayout();
                }
            }
        });

        nbrAppartListener = db.collection("Bâtiments").document(adresse).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Toast.makeText(getApplicationContext(), "Lecture des données échouée", Toast.LENGTH_LONG).show();
                }

                if(documentSnapshot.exists()){
                    nbrAppart = Integer.parseInt(documentSnapshot.get("NombreAppart").toString());
                }
            }
        });

        elementListener = db.collection("Bâtiments").document(adresse).collection("Elements").
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Toast.makeText(getApplicationContext(),"Lecture des données échouée",Toast.LENGTH_LONG).show();
                        }

                        if (!queryDocumentSnapshots.isEmpty()) {
                            listeElements.clear();
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot d : list){
                                ElementsBatiment element = d.toObject(ElementsBatiment.class);
                                listeElements.add(element);
                            }

                            updateLayout();
                        }
                    }
                });





    }
}
