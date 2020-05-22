package com.balatech.ifood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.balatech.ifood.R;
import com.balatech.ifood.helper.ConfiguracaoFirebase;
import com.balatech.ifood.helper.UsuarioFirebase;
import com.balatech.ifood.model.Empresa;
import com.balatech.ifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreço;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //Configuracoes iniciais
        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void validarDadosProduto(View view){

        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreço.getText().toString();


        if (!nome.isEmpty() && !descricao.isEmpty() && !preco.isEmpty()){

            Produto produto = new Produto();
            produto.setIdUsuario(idUsuarioLogado);
            produto.setNome(nome);
            produto.setDescricao(descricao);
            produto.setPreco(Double.parseDouble(preco));
            produto.salvar();

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
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoPreço = findViewById(R.id.editProdutoPreco);
    }
}
