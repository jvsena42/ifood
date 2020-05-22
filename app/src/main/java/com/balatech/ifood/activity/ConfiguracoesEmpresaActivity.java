package com.balatech.ifood.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.balatech.ifood.R;
import com.balatech.ifood.helper.ConfiguracaoFirebase;
import com.balatech.ifood.helper.UsuarioFirebase;
import com.balatech.ifood.model.Empresa;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaTempo,
                        editEmpresaTaxa;
    private CircleImageView imagePerfilEmpresa;

    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        //Configuraçõesiniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i,SELECAO_GALERIA);
                }
            }
        });

        //Recuperar dados da empresa
        recuperarDadosEmpresa();
    }

    private void recuperarDadosEmpresa(){
        DatabaseReference empresaRef = firebaseRef.child("empresas")
                .child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    editEmpresaNome.setText(empresa.getNome());
                    editEmpresaCategoria.setText(empresa.getCategoria());
                    editEmpresaTaxa.setText(empresa.getPrecoEntrega().toString());
                    editEmpresaTempo.setText(empresa.getTempo());

                    urlImagemSelecionada = empresa.getUrlImagem();
                    if (urlImagemSelecionada != null){
                        Picasso.get().load(urlImagemSelecionada).into(imagePerfilEmpresa);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {
                switch (requestCode){
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if (imagem != null){
                    imagePerfilEmpresa.setImageBitmap(imagem);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG,70,baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + ".jpeg");

                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this,"Erro ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            if (taskSnapshot.getMetadata() != null){
                                if (taskSnapshot.getMetadata().getReference() != null){
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            urlImagemSelecionada = uri.toString();
                                            exibirMensagem("Dados salvos com sucesso!");
                                        }
                                    });
                                }
                            }
                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void validarDadosEmpresa(View view){

        String nome = editEmpresaNome.getText().toString();
        String taxa = editEmpresaTaxa.getText().toString();
        String categoria = editEmpresaCategoria.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();

        if (!nome.isEmpty() && !taxa.isEmpty() && !categoria.isEmpty() && !tempo.isEmpty()){
            Empresa empresa = new Empresa();
            empresa.setIdUsuario(idUsuarioLogado);
            empresa.setNome(nome);
            empresa.setPrecoEntrega(Double.parseDouble(taxa));
            empresa.setCategoria(categoria);
            empresa.setTempo(tempo);
            empresa.setUrlImagem(urlImagemSelecionada);
            empresa.salvar();
            exibirMensagem("Dados salvos com sucesso!");
            finish();
        }else {
            exibirMensagem("Preencha todos os campos!");
        }

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializarComponentes(){
        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaCategoria = findViewById(R.id.editEmpresaCategoria);
        editEmpresaTaxa = findViewById(R.id.editEmpresaTaxa);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempo);
        imagePerfilEmpresa = findViewById(R.id.imagePerfilEmpresa);
    }
}
