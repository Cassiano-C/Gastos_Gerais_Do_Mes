package com.example.gastosgeraisdomes.Telas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.gastosgeraisdomes.databinding.HistoricoBinding;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Historico   extends Fragment {

    private HistoricoBinding binding;
    private AppDataBase db;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = HistoricoBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDataBase.getDataBase(getContext());

        File dir = new File(requireContext().getFilesDir(), "backups");
        File[] arquivos = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });

        List<String> nomesArquivos = new ArrayList<>();
        if (arquivos != null) {
            for (File arq : arquivos) {
                nomesArquivos.add(arq.getName());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, nomesArquivos);
        binding.historico.setAdapter(adapter);

        binding.historico.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Bundle bundle = new Bundle();
                bundle.putInt("idArq",position);

                NavHostFragment.findNavController(Historico.this)
                        .navigate(R.id.action_historico_to_listaItens, bundle);*/
                DialogodeOpcoes(position);
            }
        });

    }


    public void DialogodeOpcoes(int i){
        float gasto = 0;
        float rest = 0;
        List<ItenLista> itenListaLista = new ArrayList<>();
        List<ListaItens> listaItens = new ArrayList<>();

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View caragamento = inflater.inflate(R.layout.botoes, null);
        builder.setView(caragamento);

        final AlertDialog tela_caregamento = builder.create();

        Button comparar = caragamento.findViewById(R.id.compartilhar);
        Button carregar = caragamento.findViewById(R.id.caregar);


        comparar.setOnClickListener(v -> {
            tela_caregamento.dismiss();
            mostrarDialogoDeConfirmacaoCompartilhar(itenListaLista,listaItens);
        });

        carregar.setOnClickListener(v -> {
            tela_caregamento.dismiss();
            if(!db.itenListaDao().getALL().isEmpty())trocarDeMes();
            CaregaBanco(i);
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        tela_caregamento.show();
    }

    private void mostrarDialogoDeConfirmacaoCompartilhar(List<ItenLista> listas, List<ListaItens> listaItens) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Finalizar Lista")
                .setMessage("Deseja finalizar essa lista?" + "\n" + "O app vai gerar um PDF para compartilhar.")
                .setPositiveButton("Sim", (dialog, which) -> {

                    // Gera o PDF
                    byte[] pdf = Finalizar.gerarPDF(requireContext(), listas,
                            listaItens.get(0).getDia(),
                            String.valueOf(listaItens.get(0).getValorTotal()),
                            String.valueOf(listaItens.get(0).getValorRestante()),
                            String.valueOf(listaItens.get(0).getValorGasto()),
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


                    Uri uri = FileProvider.getUriForFile(requireContext(),
                            requireContext().getPackageName() + ".provider",
                            arquivoPDF);

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("application/pdf");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    startActivity(Intent.createChooser(intent, "Compartilhar PDF"));
                })
                .setNegativeButton("Não", (dialog2, which2) -> {
                    Toast.makeText(requireContext(), "Ação cancelada", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    public void trocarDeMes() {
        List<String> esta = db.itenListaDao().getALLEsta();
        List<String> func = db.itenListaDao().getALLFunc();
        List<Float> valor = db.itenListaDao().getALLVAalor();
        List<ListaItens> listaItens = db.listaDao().getALL();
        // 1. Exporta JSON
        //String titulo, String dia, float valorTotal, float valorGasto, float valorRestante, List<String> estabelecimento, List<String> funcao, List<Float> valor
        BackupMensal backup = new BackupMensal(listaItens.get(0).getTitulo(),listaItens.get(0).getDia(),listaItens.get(0).getValorTotal(),listaItens.get(0).getValorGasto(),listaItens.get(0).getValorRestante(),esta,func,valor);
        salvarComoJson(backup, requireContext());

        // 2. Mantém só os últimos 6 backups
        manterSoUltimos6Json(requireContext());

        // 3. Limpa o banco para o novo mês
        db.listaDao().deletarTodos();
        db.itenListaDao().deletarTodos();
    }

    void salvarComoJson(BackupMensal backup, Context context) {
        File dir = new File(context.getFilesDir(), "backups");
        if (!dir.exists()) dir.mkdirs();

        String nomeSanitizado = backup.getDia().replace("/", "-");
        File arquivo = new File(dir, "gastos_" + nomeSanitizado + ".json");

        try (FileWriter writer = new FileWriter(arquivo)) {
            new Gson().toJson(backup, writer);
            Toast.makeText(context, "Backup salvo: " + arquivo.getName(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Erro ao salvar backup", Toast.LENGTH_SHORT).show();
        }
    }

    void manterSoUltimos6Json(Context context) {
        File dir = new File(context.getFilesDir(), "backups");
        File[] arquivos = dir.listFiles((d, name) -> name.endsWith(".json"));

        if (arquivos != null && arquivos.length > 6) {
            // Ordena por data do nome do arquivo
            Arrays.sort(arquivos, Comparator.comparing(File::getName));

            int excessos = arquivos.length - 6;
            for (int i = 0; i < excessos; i++) {
                arquivos[i].delete();
            }
        }
    }

    public void CaregaBanco(int i){
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
            ListaItens listaItens = new ListaItens(backupMensal.getTitulo(),backupMensal.getDia(),backupMensal.getValorTotal(),backupMensal.getValorGasto(),backupMensal.getValorRestante());

            db.listaDao().insertAll(listaItens);
            if (estab.size() == func.size() && func.size() == valor.size()) {
                for (int j = 0; j < estab.size(); j++) {
                    db.itenListaDao().insertAll(new ItenLista(estab.get(j), func.get(j), valor.get(j)));
                }
            } else {
                Toast.makeText(requireContext(), "Erro nos dados do backup", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
