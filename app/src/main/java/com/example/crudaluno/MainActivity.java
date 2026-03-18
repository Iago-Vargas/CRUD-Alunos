package com.example.crudaluno;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private EditText nome;
    private EditText cpf;
    private EditText telefone;
    private EditText endereco;
    private EditText curso;
    private ImageView fotoAluno;

    private AlunoRepository repository;
    private Aluno aluno = null;
    private ActivityResultLauncher<Void> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nome = findViewById(R.id.texto1);
        cpf = findViewById(R.id.texto2);
        telefone = findViewById(R.id.texto3);
        endereco = findViewById(R.id.texto4);
        curso = findViewById(R.id.texto5);
        fotoAluno = findViewById(R.id.imagemAluno);

        repository = new AlunoRepository(this);
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bitmap -> {
            if (bitmap == null) {
                return;
            }

            if (aluno == null) {
                aluno = new Aluno();
            }

            fotoAluno.setImageBitmap(bitmap);
            aluno.setFoto(bitmapParaBytes(bitmap));
        });

        Intent it = getIntent();
        if (it.hasExtra("aluno")) {
            aluno = it.getSerializableExtra("aluno", Aluno.class);
            if (aluno != null) {
                nome.setText(aluno.getNome());
                cpf.setText(aluno.getCpf());
                telefone.setText(aluno.getTelefone());
                endereco.setText(aluno.getEndereco());
                curso.setText(aluno.getCurso());
                exibirFoto(aluno.getFoto());
            }
        }
    }

    public void salvar(View view) {
        String cpfDigitado = cpf.getText().toString();
        String telefoneDigitado = telefone.getText().toString();

        if (!repository.validaCpf(cpfDigitado)) {
            Toast.makeText(this, "CPF inválido!", Toast.LENGTH_LONG).show();
            cpf.requestFocus();
            return;
        }

        if (!repository.validaTelefone(telefoneDigitado)) {
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
            long id = repository.inserir(aluno);

            if (id == -1) {
                Toast.makeText(this, "Erro: CPF já cadastrado!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Aluno inserido com id: " + id, Toast.LENGTH_SHORT).show();
                limparFormulario();
                aluno = null;
            }
        } else {
            repository.atualizar(aluno);
            Toast.makeText(this, "Aluno atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void irLista(View view) {
        Intent intent = new Intent(this, ActivityListarAlunos.class);
        startActivity(intent);
    }

    public void abrirCamera(View view) {
        cameraLauncher.launch(null);
    }

    public void removerFoto(View view) {
        if (aluno == null) {
            aluno = new Aluno();
        }

        aluno.setFoto(null);
        fotoAluno.setImageResource(R.drawable.foto_placeholder);
    }

    private void limparFormulario() {
        nome.setText("");
        cpf.setText("");
        telefone.setText("");
        endereco.setText("");
        curso.setText("");
        fotoAluno.setImageResource(R.drawable.foto_placeholder);
    }

    private void exibirFoto(byte[] foto) {
        if (foto == null || foto.length == 0) {
            fotoAluno.setImageResource(R.drawable.foto_placeholder);
            return;
        }

        Bitmap bitmap = BitmapFactory.decodeByteArray(foto, 0, foto.length);
        fotoAluno.setImageBitmap(bitmap);
    }

    private byte[] bitmapParaBytes(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
        return outputStream.toByteArray();
    }
}
