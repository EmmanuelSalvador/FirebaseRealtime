package mx.edu.ittepic.firebaserealtime;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    EditText texto;
    Button subir, actualizar, eliminar;
    RecyclerView recyclerView;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseRecyclerOptions<Publicar> options;
    FirebaseRecyclerAdapter<Publicar,MyRecyclerViewHolder> adapter;
    Publicar posisionSeleccionada;
    String selectedKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        texto=findViewById(R.id.edittext);
        subir=findViewById(R.id.botonSubir);
        actualizar=findViewById(R.id.botonActualizar);
        eliminar=findViewById(R.id.botonEliminar);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("FIREBASE");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicar();
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(selectedKey).setValue(new Publicar(texto.getText().toString()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Actualizado", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(selectedKey).removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Borrado", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        mostrarcomentario();
    }
    @Override
    protected void onStop(){
        if (adapter!=null){
            adapter.stopListening();
        }
        super.onStop();
    }
    private void publicar() {
        String texto_enviar = texto.getText().toString();
        Publicar publicar = new Publicar(texto_enviar);
        databaseReference.push()
                .setValue(publicar);
        adapter.notifyDataSetChanged();
    }

    private void mostrarcomentario() {
        options= new FirebaseRecyclerOptions.Builder<Publicar>()
                .setQuery(databaseReference,Publicar.class).build();
        adapter = new FirebaseRecyclerAdapter<Publicar, MyRecyclerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyRecyclerViewHolder holder, final int position, @NonNull final Publicar model) {
                holder.txt_Texto.setText(model.getTexto());
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int pos) {
                        posisionSeleccionada=model;
                        selectedKey=getSnapshots().getSnapshot(position).getKey();
                        Log.d("Key item",""+selectedKey);
                        texto.setText(model.getTexto());
                    }
                });
            }

            @NonNull
            @Override
            public MyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(getBaseContext()).inflate(R.layout.publicar_objeto,viewGroup,false);
                return new MyRecyclerViewHolder(itemView);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
