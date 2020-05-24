package com.balatech.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.balatech.ifood.R;
import com.balatech.ifood.helper.ConfiguracaoFirebase;
import com.balatech.ifood.helper.UsuarioFirebase;
import com.balatech.ifood.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editNome, editEndereco;
    private DatabaseReference firebaseRef;
    private String idUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        //Configuracoes iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuario = UsuarioFirebase.getIdUsuario();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recuperar dados usuário
        recuperarDadosUsuario();
    }

    private void recuperarDadosUsuario(){

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){

                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editNome.setText(usuario.getNome());
                    editEndereco.setText(usuario.getEndereco());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void validarDadosUsuario(View view){
        String nome = editNome.getText().toString();
        String endereco = editEndereco.getText().toString();

        if (!nome.isEmpty() && !endereco.isEmpty()){
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(idUsuario);
            usuario.setNome(nome);
            usuario.setEndereco(endereco);
            usuario.salvar();

            Toast.makeText(this, "Dados alterados com sucesso!", Toast.LENGTH_SHORT).show();
            finish();


        }else {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
        }
    }

    private void inicializarComponentes(){
        editNome = findViewById(R.id.editUsuarioNome);
        editEndereco = findViewById(R.id.editUsuarioEndereço);
    }
}
