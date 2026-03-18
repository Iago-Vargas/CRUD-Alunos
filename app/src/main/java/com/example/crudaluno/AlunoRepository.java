package com.example.crudaluno;

import android.content.Context;

import java.util.List;
import java.util.regex.Pattern;

public class AlunoRepository {

    private static final Pattern TELEFONE_PATTERN = Pattern.compile("^\\(?([1-9]{2})\\)?\\s?9[0-9]{4}-?[0-9]{4}$");

    private final AlunoDao alunoDao;

    public AlunoRepository(Context context) {
        alunoDao = AppDatabase.getInstance(context).alunoDao();
    }

    public long inserir(Aluno aluno) {
        if (cpfExistente(aluno.getCpf())) {
            return -1;
        }

        return alunoDao.inserir(aluno);
    }

    public List<Aluno> obterTodos() {
        return alunoDao.obterTodos();
    }

    public void atualizar(Aluno aluno) {
        alunoDao.atualizar(aluno);
    }

    public void excluir(Aluno aluno) {
        alunoDao.excluir(aluno);
    }

    public boolean cpfExistente(String cpf) {
        return alunoDao.cpfExistente(cpf);
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

    public boolean validaCpf(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma = 0;
            int peso = 10;

            for (int i = 0; i < 9; i++) {
                int num = cpf.charAt(i) - '0';
                soma += num * peso;
                peso--;
            }

            int resto = soma % 11;
            char digito10 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

            soma = 0;
            peso = 11;

            for (int i = 0; i < 10; i++) {
                int num = cpf.charAt(i) - '0';
                soma += num * peso;
                peso--;
            }

            resto = soma % 11;
            char digito11 = (resto < 2) ? '0' : (char) ((11 - resto) + '0');

            return digito10 == cpf.charAt(9) && digito11 == cpf.charAt(10);
        } catch (Exception e) {
            return false;
        }
    }
}
