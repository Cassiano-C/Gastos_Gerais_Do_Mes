package com.example.gastosgeraisdomes.Telas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gastosgeraisdomes.Banco.AppDataBase;
import com.example.gastosgeraisdomes.R;
import com.example.gastosgeraisdomes.databinding.InicioBinding;

public class Inicio extends Fragment {

    private InicioBinding binding;
    private AppDataBase db;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = InicioBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDataBase.getDataBase(getContext());

        if(db.listaDao().ExisteLista()){
            binding.cria.setText("Atualizar Lista");
            //Se tiver alguma lista criada vai deixar os botoes visiveis e acesivel
            binding.floatingActionButton.setVisibility(View.VISIBLE);
            if(db.itenListaDao().Quantos() > 0) {
                binding.gera.setVisibility(View.VISIBLE);
                binding.listaItens.setVisibility(View.VISIBLE);
            }
            binding.historico.setVisibility(View.VISIBLE);
            //Vai colocar os valores em seus devidos lugares
            binding.valTotal.setText("Total: R$ "+db.listaDao().Total());
            binding.valGasto.setText("Gasto: R$ "+db.listaDao().Gasto());
            binding.valRestante.setText("Restante: R$ "+db.listaDao().Restante());

            binding.cria.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("at",true);
                    NavHostFragment.findNavController(Inicio.this)
                            .navigate(R.id.action_inicio_to_cria2,bundle);
                }
            });
        }else {
            binding.cria.setOnClickListener(v ->
                    NavHostFragment.findNavController(Inicio.this)
                    .navigate(R.id.action_inicio_to_cria2)
            );
        }

        binding.floatingActionButton.setOnClickListener(v->
                NavHostFragment.findNavController(Inicio.this)
                .navigate(R.id.action_inicio_to_criaIten)
        );

        binding.historico.setOnClickListener(v ->
                NavHostFragment.findNavController(Inicio.this)
                .navigate(R.id.action_inicio_to_historico2)
        );

        binding.listaItens.setOnClickListener(v ->
                NavHostFragment.findNavController(Inicio.this)
                        .navigate(R.id.action_inicio_to_lista)
        );

        binding.gera.setOnClickListener(v ->
                NavHostFragment.findNavController(Inicio.this)
                        .navigate(R.id.action_inicio_to_finalizar)
        );

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}