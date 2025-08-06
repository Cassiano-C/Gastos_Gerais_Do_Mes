package com.example.gastosgeraisdomes.Telas;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gastosgeraisdomes.Banco.AppDataBase;
import com.example.gastosgeraisdomes.Tabelas.ListaItens;
import com.example.gastosgeraisdomes.databinding.CriaBinding;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class Cria  extends Fragment {

    private CriaBinding binding;
    private AppDataBase db;
    private int id;

    private float gastoAntigo;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = CriaBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDataBase.getDataBase(getContext());
        String dataAtual;

        Bundle arg = getArguments();
        if(arg != null && arg.getBoolean("at")){
            List<ListaItens> listaItens = db.listaDao().getALL();
            ListaItens listaItens1 = listaItens.get(0);
            id = listaItens1.getIdLista();
            dataAtual = listaItens1.getDia();
            binding.data.setText(listaItens1.getDia());
            binding.titulo.setText(listaItens1.getTitulo());
            binding.valor.setText(String.valueOf(listaItens1.getValorTotal()));
            gastoAntigo = listaItens1.getValorGasto();
        }else {
            LocalDate data = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataAtual = data.format(formatter);

            binding.data.setText(dataAtual);
        }

        binding.definirData.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (datePickerView, year1, month1, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        binding.data.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        });

        binding.criar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titulo = binding.titulo.getText().toString().trim();
                String dataM = binding.data.getText().toString().trim();
                float valor = Float.parseFloat(binding.valor.getText().toString().trim());
                try {
                    if(valor > 0.0){
                        ListaItens lista = new ListaItens(titulo,dataM,valor);
                        if(arg != null && arg.getBoolean("at")){
                            lista.setValorGasto(gastoAntigo);
                            lista.setValorRestante(valor-gastoAntigo);
                            lista.setIdLista(id);
                            db.listaDao().upgrade(lista);
                            Toast.makeText(requireContext(), "Atualizado com sucesso", Toast.LENGTH_LONG).show();
                        }else {
                            lista.setValorGasto(0);
                            lista.setValorRestante(valor);
                            db.listaDao().insertAll(lista);
                        }
                    }else{
                        Toast.makeText(requireContext(), "Incira um valor valido", Toast.LENGTH_LONG).show();
                    }
                    requireActivity().getSupportFragmentManager().popBackStack();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
