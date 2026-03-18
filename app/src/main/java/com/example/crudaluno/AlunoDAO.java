package com.example.crudaluno;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlunoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long inserir(Aluno aluno);

    @Query("SELECT * FROM aluno ORDER BY nome ASC")
    List<Aluno> obterTodos();

    @Update
    void atualizar(Aluno aluno);

    @Delete
    void excluir(Aluno aluno);

    @Query("SELECT COUNT(*) > 0 FROM aluno WHERE cpf = :cpf")
    boolean cpfExistente(String cpf);
}
