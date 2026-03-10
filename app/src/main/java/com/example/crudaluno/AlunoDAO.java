package com.example.crudaluno;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AlunoDAO {
    private final Conexao conexao;
    private final SQLiteDatabase banco;

    private static final Pattern TELEFONE_PATTERN = Pattern.compile("^\\(?([1-9]{2})\\)?\\s?9[0-9]{4}-?[0-9]{4}$");

    public AlunoDAO(Context context) {
        conexao = new Conexao(context);
        banco = conexao.getWritableDatabase();
    }

    public long inserir(Aluno aluno) {
        if (!cpfExistente(aluno.getCpf())) {
            ContentValues values = new ContentValues();
            values.put("nome", aluno.getNome());
            values.put("cpf", aluno.getCpf());
            values.put("telefone", aluno.getTelefone());
            values.put("endereco", aluno.getEndereco());
            values.put("curso", aluno.getCurso());
            return banco.insert("aluno", null, values);
        } else {
            return -1;
        }
    }

    public List<Aluno> obterTodos() {
        List<Aluno> alunos = new ArrayList<>();
        Cursor cursor = banco.query(
                "aluno",
                new String[]{"id", "nome", "cpf", "telefone", "endereco", "curso"},
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            Aluno a = new Aluno();
            a.setId(cursor.getInt(0));
            a.setNome(cursor.getString(1));
            a.setCpf(cursor.getString(2));
            a.setTelefone(cursor.getString(3));
            a.setEndereco(cursor.getString(4));
            a.setCurso(cursor.getString(5));
            alunos.add(a);
        }

        cursor.close();
        return alunos;
    }

    public void excluir(Aluno a) {
        banco.delete("aluno", "id = ?", new String[]{a.getId().toString()});
    }

    public void atualizar(Aluno aluno) {
        ContentValues values = new ContentValues();
        values.put("nome", aluno.getNome());
        values.put("cpf", aluno.getCpf());
        values.put("telefone", aluno.getTelefone());
        values.put("endereco", aluno.getEndereco());
        values.put("curso", aluno.getCurso());
        banco.update("aluno", values, "id = ?", new String[]{aluno.getId().toString()});
    }

    public boolean cpfExistente(String cpf) {
        Cursor cursor = banco.query("aluno", new String[]{"id"}, "cpf = ?", new String[]{cpf}, null, null, null);
        boolean cpfExiste = cursor.getCount() > 0;
        cursor.close();
        return cpfExiste;
    }

    public boolean validaTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }

        if (TELEFONE_PATTERN.matcher(telefone).matches()) {
            return true;
        }

        String apenasDigitos = telefone.replaceAll("[^0-9]", "");
        return apenasDigitos.length() == 11 && apenasDigitos.charAt(2) == '9';
    }

    public boolean validaCpf(String CPF) {
        CPF = CPF.replaceAll("[^0-9]", "");

        if (CPF.length() != 11) {
            return false;
        }

        if (CPF.matches("(\\d)\\1{10}")) {
            return false;
        }

        char dig10;
        char dig11;
        int soma;
        int num;
        int peso;
        int resto;

        try {
            soma = 0;
            peso = 10;

            for (int i = 0; i < 9; i++) {
                num = CPF.charAt(i) - '0';
                soma += (num * peso);
                peso--;
            }

            resto = soma % 11;
            dig10 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

            soma = 0;
            peso = 11;

            for (int i = 0; i < 10; i++) {
                num = CPF.charAt(i) - '0';
                soma += (num * peso);
                peso--;
            }

            resto = soma % 11;
            dig11 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

            return dig10 == CPF.charAt(9) && dig11 == CPF.charAt(10);
        } catch (Exception e) {
            return false;
        }
    }
}
