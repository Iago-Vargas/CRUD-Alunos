package com.example.crudaluno;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ActivityListarAlunos extends AppCompatActivity {

    private ListView listView;
    private AlunoRepository repository;
    private final List<Aluno> alunos = new ArrayList<>();
    private final List<Aluno> alunosFiltrados = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_alunos);

        listView = findViewById(R.id.lista_alunos);
        repository = new AlunoRepository(this);

        registerForContextMenu(listView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Aluno alunoSelecionado = alunosFiltrados.get(position);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(alunoSelecionado.getNome())
                    .setItems(new CharSequence[]{"Atualizar", "Excluir"}, (dialogInterface, which) -> {
                        if (which == 0) {
                            abrirTelaAtualizacao(alunoSelecionado);
                        } else {
                            confirmarExclusao(alunoSelecionado);
                        }
                    })
                    .create();
            dialog.show();
        });
    }

    public void irCadastro(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contexto, menu);
    }

    public void excluir(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Aluno alunoExcluir = alunosFiltrados.get(menuInfo.position);
        confirmarExclusao(alunoExcluir);
    }

    public void atualizar(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Aluno alunoAtualizar = alunosFiltrados.get(menuInfo.position);
        abrirTelaAtualizacao(alunoAtualizar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        recarregarLista();
    }

    private void abrirTelaAtualizacao(Aluno aluno) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("aluno", aluno);
        startActivity(intent);
    }

    private void confirmarExclusao(Aluno alunoExcluir) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Realmente deseja excluir o aluno?")
                .setNegativeButton("Não", null)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        repository.excluir(alunoExcluir);
                        alunos.remove(alunoExcluir);
                        alunosFiltrados.remove(alunoExcluir);
                        ((AlunoAdapter) listView.getAdapter()).notifyDataSetChanged();
                    }
                })
                .create();
        dialog.show();
    }

    private void recarregarLista() {
        alunos.clear();
        alunos.addAll(repository.obterTodos());

        alunosFiltrados.clear();
        alunosFiltrados.addAll(alunos);

        AlunoAdapter adaptador = new AlunoAdapter(this, alunosFiltrados);
        listView.setAdapter(adaptador);
    }
}
