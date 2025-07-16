package com.example.gastosgeraisdomes.Telas;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.gastosgeraisdomes.Banco.AppDataBase;
import com.example.gastosgeraisdomes.R;
import com.example.gastosgeraisdomes.Tabelas.BackupMensal;
import com.example.gastosgeraisdomes.Tabelas.ItenLista;
import com.example.gastosgeraisdomes.Tabelas.ListaItens;
import com.example.gastosgeraisdomes.databinding.ListaBinding;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lista extends Fragment {

    private ListaBinding binding;
    private AppDataBase db;
    private int i = -1;

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
        float gasto = 0;
        float rest = 0;
        List<ItenLista> itenListaLista;
        List<ListaItens> listaItens = new ArrayList<>();

        Bundle arg = getArguments();
        if(arg != null){
            itenListaLista = new ArrayList<>();
            i = arg.getInt("idArq");
            Toast.makeText(requireContext(), "qual arquivo" + i, Toast.LENGTH_SHORT).show();
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
                gasto = backupMensal.getValorGasto();
                rest = backupMensal.getValorRestante();

                List<String> estab = backupMensal.getEstabelecimento();
                List<String> func = backupMensal.getFuncao();
                List<Float> valor = backupMensal.getValor();
                listaItens.add(new ListaItens(backupMensal.getTitulo(),backupMensal.getDia(),backupMensal.getValorTotal(),backupMensal.getValorGasto(),backupMensal.getValorRestante()));

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
            binding.finalizar.setVisibility(View.VISIBLE);
        }else {
            itenListaLista = db.itenListaDao().getALL();
            gasto = db.listaDao().Gasto();
            rest = db.listaDao().Restante();
        }

        ArrayAdapter<ItenLista> adapter = new ArrayAdapter<>(requireContext(),android.R.layout.simple_list_item_activated_1,itenListaLista);
        binding.listaItens.setAdapter(adapter);
        binding.gasto.setText("Gasto: R$ "+gasto);
        binding.resto.setText("Restante: R$ "+rest);

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
                if(i != -1) {
                    bundle.putInt("idArq",i);
                    bundle.putInt("id",position);
                }else{
                    bundle.putInt("idArq",-1);
                    bundle.putInt("id",itenLista.getIdIten());
                }

                NavHostFragment.findNavController(Lista.this)
                        .navigate(R.id.action_lista_to_iten2, bundle);
            }
        });

        binding.finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoDeConfirmacao(itenListaLista,listaItens);
            }
        });


    }

    private void mostrarDialogoDeConfirmacao(List<ItenLista> listas, List<ListaItens> listaItens) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Gerar o PDF da Lista")
                .setMessage("Deseja Gerar o PDF da lista?" + "\n" + "O app vai gerar um PDF e perguntar se deseja compartilhar.")
                .setPositiveButton("Sim", (dialog, which) -> {

                    // Gera o PDF
                    byte[] pdf = Finalizar.gerarPDF(requireContext(), listas,
                            listaItens.get(0).getDia(),
                            String.valueOf(listaItens.get(0).getValorTotal()),
                            String.valueOf(listaItens.get(0).getValorRestante()),
                            listaItens.get(0).getTitulo());

                    // Cria o arquivo na pasta interna visível ao FileProvider
                    File pasta = new File(requireContext().getFilesDir(), "pdfs");
                    if (!pasta.exists()) pasta.mkdirs();
                    String nomeArquivo = "relatorio_gastos.pdf";
                    File arquivoPDF = new File(pasta, nomeArquivo);

                    try (FileOutputStream fos = new FileOutputStream(arquivoPDF)) {
                        fos.write(pdf);
                        fos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Erro ao salvar PDF", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Pergunta se quer compartilhar
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Compartilhar PDF")
                            .setMessage("Deseja compartilhar o PDF agora?")
                            .setPositiveButton("Sim", (dialog2, which2) -> {
                                Uri uri = FileProvider.getUriForFile(requireContext(),
                                        requireContext().getPackageName() + ".provider",
                                        arquivoPDF);

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("application/pdf");
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                startActivity(Intent.createChooser(intent, "Compartilhar PDF"));
                                requireActivity().getSupportFragmentManager().popBackStack();
                            })
                            .setNegativeButton("Não", (dialog2, which2) -> {
                                Toast.makeText(requireContext(), "Ação cancelada", Toast.LENGTH_SHORT).show();
                            })
                            .show();

                })
                .setNegativeButton("Não", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}