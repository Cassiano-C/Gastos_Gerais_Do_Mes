package com.example.gastosgeraisdomes.Telas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gastosgeraisdomes.Banco.AppDataBase;
import com.example.gastosgeraisdomes.R;
import com.example.gastosgeraisdomes.Tabelas.BackupMensal;
import com.example.gastosgeraisdomes.Tabelas.ItenLista;
import com.example.gastosgeraisdomes.Tabelas.ListaItens;
import com.example.gastosgeraisdomes.databinding.ItenBinding;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class Iten extends Fragment {

    private ItenBinding binding;
    private AppDataBase db;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = ItenBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDataBase.getDataBase(getContext());

        Bundle arg = getArguments();
        ItenLista iten;

        if(arg != null) {
            int idIten = arg.getInt("id");
            int i = arg.getInt("idArq");
            if (i != -1) {
                binding.excluir.setVisibility(View.GONE);
                binding.atualiza.setVisibility(View.GONE);
                iten = retornaIten(i, idIten);
            } else {
                iten = db.itenListaDao().Busca(idIten);
            }

            binding.estabelecimento.setText(iten.getEstabelecimento());
            binding.funcao.setText(iten.getFuncao());
            binding.valor.setText(String.valueOf(iten.getValor()) + "$");

            binding.atualiza.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", idIten);

                    NavHostFragment.findNavController(Iten.this)
                            .navigate(R.id.action_iten2_to_criaIten, bundle);
                }
            });

            binding.excluir.setOnClickListener(v -> mostrarDialogoDeConfirmacao(iten));
        }
    }

    private void mostrarDialogoDeConfirmacao(ItenLista iten) {
        List<ListaItens> listaItens = db.listaDao().getALL();
        ListaItens listaItens1 = listaItens.get(0);
        float gastosAntigo = listaItens1.getValorGasto();
        float restoAntigo = listaItens1.getValorRestante();

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Excluir Iten")
                .setMessage("Deseja mesmo excluir esse item?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    listaItens1.setValorGasto(gastosAntigo - iten.getValor());
                    listaItens1.setValorRestante(restoAntigo + iten.getValor());
                    db.listaDao().upgrade(listaItens1);

                    db.itenListaDao().delete(iten);
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    public ItenLista retornaIten(int i,int posicion){
        List<ItenLista> itenListaLista = new ArrayList<>();
        File dir = new File(requireContext().getFilesDir(), "backups");
        File[] arquivos = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        assert arquivos != null;
        File meuArquivo = arquivos[i];
        try {
            FileReader reader = new FileReader(meuArquivo);
            Gson gson = new Gson();
            BackupMensal backupMensal = gson.fromJson(reader,BackupMensal.class);
            reader.close();

            List<String> estab = backupMensal.getEstabelecimento();
            List<String> func = backupMensal.getFuncao();
            List<Float> valor = backupMensal.getValor();

            if (estab.size() == func.size() && func.size() == valor.size()) {
                for (int j = 0; j < estab.size(); j++) {
                    itenListaLista.add(new ItenLista(estab.get(j), func.get(j), valor.get(j)));
                }
            } else {
                Toast.makeText(requireContext(), "Erro nos dados do backup", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return itenListaLista.get(posicion);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
