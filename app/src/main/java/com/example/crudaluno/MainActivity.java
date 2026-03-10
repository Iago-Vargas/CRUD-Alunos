package com.example.crudaluno;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText nome;
    private EditText cpf;
    private EditText telefone;
    private EditText endereco;
    private EditText curso;

    private AlunoDAO dao;
    private Aluno aluno = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.texto1);
        cpf = findViewById(R.id.texto2);
        telefone = findViewById(R.id.texto3);
        endereco = findViewById(R.id.texto4);
        curso = findViewById(R.id.texto5);

        dao = new AlunoDAO(this);

        Intent it = getIntent();
        if (it.hasExtra("aluno")) {
            aluno = (Aluno) it.getSerializableExtra("aluno");
            nome.setText(aluno.getNome());
            cpf.setText(aluno.getCpf());
            telefone.setText(aluno.getTelefone());
            endereco.setText(aluno.getEndereco());
            curso.setText(aluno.getCurso());
        }
    }

    public void salvar(View view) {
        String cpfDigitado = cpf.getText().toString();
        String telefoneDigitado = telefone.getText().toString();

        if (!dao.validaCpf(cpfDigitado)) {
            Toast.makeText(this, "CPF inválido!", Toast.LENGTH_LONG).show();
            cpf.requestFocus();
            return;
        }

        if (!dao.validaTelefone(telefoneDigitado)) {
            Toast.makeText(this, "Telefone inválido! Use o formato (XX) 9XXXX-XXXX.", Toast.LENGTH_LONG).show();
            telefone.requestFocus();
            return;
        }

        if (aluno == null) {
            aluno = new Aluno();
        }

        aluno.setNome(nome.getText().toString());
        aluno.setCpf(cpf.getText().toString());
        aluno.setTelefone(telefone.getText().toString());
        aluno.setEndereco(endereco.getText().toString());
        aluno.setCurso(curso.getText().toString());

        if (aluno.getId() == null) {
            long id = dao.inserir(aluno);

            if (id == -1) {
                Toast.makeText(this, "Erro: CPF já cadastrado!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Aluno inserido com id: " + id, Toast.LENGTH_SHORT).show();
                limparFormulario();
                aluno = null;
            }
        } else {
            dao.atualizar(aluno);
            Toast.makeText(this, "Aluno atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void irLista(View view) {
        Intent intent = new Intent(this, ActivityListarAlunos.class);
        startActivity(intent);
    }

    private void limparFormulario() {
        nome.setText("");
        cpf.setText("");
        telefone.setText("");
        endereco.setText("");
        curso.setText("");
    }
}
