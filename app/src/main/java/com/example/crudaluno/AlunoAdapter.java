package com.example.crudaluno;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class AlunoAdapter extends ArrayAdapter<Aluno> {

    public AlunoAdapter(@NonNull Context context, @NonNull List<Aluno> alunos) {
        super(context, 0, alunos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.item_aluno, parent, false);
        }

        Aluno aluno = getItem(position);
        ImageView foto = view.findViewById(R.id.itemFotoAluno);
        TextView nome = view.findViewById(R.id.itemNomeAluno);
        TextView curso = view.findViewById(R.id.itemCursoAluno);

        if (aluno != null) {
            nome.setText(aluno.getNome());
            curso.setText(aluno.getCurso());

            byte[] fotoBytes = aluno.getFoto();
            if (fotoBytes != null && fotoBytes.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                foto.setImageBitmap(bitmap);
            } else {
                foto.setImageResource(R.drawable.ic_launcher_background);
            }
        }

        return view;
    }
}
