package com.example.gastosgeraisdomes.Telas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gastosgeraisdomes.Banco.AppDataBase;
import com.example.gastosgeraisdomes.R;
import com.example.gastosgeraisdomes.Tabelas.ItenLista;
import com.example.gastosgeraisdomes.databinding.ListaBinding;

import java.util.List;

public class Lista extends Fragment {

    private ListaBinding binding;
    private AppDataBase db;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = ListaBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDataBase.getDataBase(getContext());

        List<ItenLista> itenListaLista = db.itenListaDao().getALL();

        ArrayAdapter<ItenLista> adapter = new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_activated_1,itenListaLista);
        binding.listaItens.setAdapter(adapter);
        binding.gasto.setText("Gasto: R$ "+db.listaDao().Gasto());
        binding.resto.setText("Restante: R$ "+db.listaDao().Restante());

        binding.busca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pesquisa = binding.pesquisa.getText().toString().trim();
                try {
                    if(!pesquisa.isEmpty() && db.itenListaDao().BuscaIten(pesquisa)){
                        List<ItenLista> itenListas = db.itenListaDao().BuscasEstab(pesquisa);
                        ArrayAdapter<ItenLista> adapter2 = new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_activated_1,itenListas);
                        binding.listaItens.setAdapter(adapter2);
                    }else {
                        Toast.makeText(requireContext(), "Estabelecimento invalido", Toast.LENGTH_LONG).show();
                        binding.listaItens.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        binding.listaItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItenLista itenLista = itenListaLista.get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("id",itenLista.getIdIten());

                NavHostFragment.findNavController(Lista.this)
                        .navigate(R.id.action_lista_to_iten2, bundle);
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}