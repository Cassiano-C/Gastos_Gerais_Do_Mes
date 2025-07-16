package com.example.gastosgeraisdomes.Telas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gastosgeraisdomes.Banco.AppDataBase;
import com.example.gastosgeraisdomes.Tabelas.ItenLista;
import com.example.gastosgeraisdomes.Tabelas.ListaItens;
import com.example.gastosgeraisdomes.databinding.CriaItenBinding;

import java.util.List;

public class CriaIten extends Fragment {

    private CriaItenBinding binding;
    private AppDataBase db;

    private int idIten;
    private float valorAntigo;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = CriaItenBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDataBase.getDataBase(getContext());
        List<ListaItens> listaItens = db.listaDao().getALL();
        float[] valores = {listaItens.get(0).getValorTotal(),listaItens.get(0).getValorGasto(),listaItens.get(0).getValorRestante()};

        binding.valor.setText("0");

        Bundle arg = getArguments();
        if(arg != null){
            idIten = arg.getInt("id");
            ItenLista iten = db.itenListaDao().Busca(idIten);

            binding.estabelecimento.setText(iten.getEstabelecimento());
            binding.funcao.setText(iten.getFuncao());
            binding.valor.setText(String.valueOf(iten.getValor()));
            valorAntigo = iten.getValor();
        }



        binding.salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String estabe = binding.estabelecimento.getText().toString().trim();
                String func = binding.funcao.getText().toString().trim();
                try {
                    if(!estabe.isEmpty()){
                        float valor = Float.parseFloat(binding.valor.getText().toString().trim());
                        if(valor > 0.0){
                            if(func.isEmpty()) func = " ";
                            ItenLista itenLista = new ItenLista(estabe,func,valor);

                            if(arg != null){
                                valores[1] -= valorAntigo;
                                valores[2] += valorAntigo;

                                valores[1] += valor;
                                valores[2] -= valor;

                                listaItens.get(0).setValorRestante(valores[2]);
                                listaItens.get(0).setValorGasto(valores[1]);
                                db.listaDao().upgrade(listaItens.get(0));

                                itenLista.setIdIten(idIten);
                                db.itenListaDao().upgrade(itenLista);
                                Toast.makeText(requireContext(), "Atualizado com sucesso", Toast.LENGTH_LONG).show();
                            }else {
                                valores[1] += valor;
                                valores[2] -= valor;

                                listaItens.get(0).setValorRestante(valores[2]);
                                listaItens.get(0).setValorGasto(valores[1]);
                                db.listaDao().upgrade(listaItens.get(0));
                                db.itenListaDao().insertAll(itenLista);
                            }
                            limpa();
                            //requireActivity().getSupportFragmentManager().popBackStack();
                        }else {
                            Toast.makeText(requireContext(), "Incira um valor valido", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(requireContext(), "Incira um Estabelecimento", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void limpa(){
        binding.valor.setText("0");
        binding.funcao.setText("");
        binding.estabelecimento.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
