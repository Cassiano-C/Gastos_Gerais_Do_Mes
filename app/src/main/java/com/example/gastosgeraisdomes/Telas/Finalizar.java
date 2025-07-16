package com.example.gastosgeraisdomes.Telas;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.gastosgeraisdomes.Banco.AppDataBase;
import com.example.gastosgeraisdomes.Tabelas.BackupMensal;
import com.example.gastosgeraisdomes.Tabelas.ItenLista;
import com.example.gastosgeraisdomes.Tabelas.ListaItens;
import com.example.gastosgeraisdomes.databinding.FinalizarBinding;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Finalizar extends Fragment {

    private FinalizarBinding binding;
    private AppDataBase db;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FinalizarBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = AppDataBase.getDataBase(getContext());

        List<ItenLista> listas = db.itenListaDao().getALL();
        List<ListaItens> listaItens = db.listaDao().getALL();
        geraTabela(listas,listaItens);

        binding.bntFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoDeConfirmacao(listas,listaItens);
            }
        });
    }

    public static byte[] gerarPDF(Context context, List<ItenLista> gastos, String data, String totalFormatado,String resto, String titulo) {
        PdfDocument pdfDocument = new PdfDocument();
        int pageWidth = 595;
        int pageHeight = 842;

        int startX = 40;
        int tableWidth = 515;
        int[] columnWidths = {150, 200, 165};
        int rowHeight = 40;

        int maxLinhasPorPagina = 30; // ajuste conforme fonte/altura
        int totalLinhas = gastos.size() + 2; // "Caixa" + cabeçalho + dados

        int paginaAtual = 1;
        int linhaGlobal = 0;

        // Cálculo das posições verticais das linhas (colunas)
        int xCol1 = startX;
        int xCol2 = xCol1 + columnWidths[0];
        int xCol3 = xCol2 + columnWidths[1];
        int xCol4 = xCol3 + columnWidths[2]; // = startX + tableWidth

        while (linhaGlobal < totalLinhas - 2) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, paginaAtual).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            int currentY = 50;

            // Título
            paint.setTextSize(18);
            paint.setFakeBoldText(true);
            float larguraTexto = paint.measureText(titulo);
            canvas.drawText(titulo, (pageWidth - larguraTexto) / 2, currentY, paint);
            currentY += 30;

            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
            paint.setFakeBoldText(true);
            paint.setTextSize(14);

            // --- Linha "Caixa" ---
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(startX, currentY, startX + tableWidth, currentY + rowHeight, paint);

            // Linhas verticais da "Caixa"
            canvas.drawLine(xCol1, currentY, xCol1, currentY + rowHeight, paint);
            canvas.drawLine(xCol2, currentY, xCol2, currentY + rowHeight, paint);
            canvas.drawLine(xCol3, currentY, xCol3, currentY + rowHeight, paint);
            canvas.drawLine(xCol4, currentY, xCol4, currentY + rowHeight, paint);

            paint.setStyle(Paint.Style.FILL);
            String[] caixaTexts = { "Caixa", data, "Total: R$ " + totalFormatado };
            int[] colStarts = { xCol1, xCol2, xCol3 };
            for (int i = 0; i < caixaTexts.length; i++) {
                float textoWidth = paint.measureText(caixaTexts[i]);
                float posX = colStarts[i] + (columnWidths[i] - textoWidth) / 2;
                canvas.drawText(caixaTexts[i], posX, currentY + 25, paint);
            }
            currentY += rowHeight;

            // --- Cabeçalho ---
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(startX, currentY, startX + tableWidth, currentY + rowHeight, paint);

            // Linhas verticais do cabeçalho
            canvas.drawLine(xCol1, currentY, xCol1, currentY + rowHeight, paint);
            canvas.drawLine(xCol2, currentY, xCol2, currentY + rowHeight, paint);
            canvas.drawLine(xCol3, currentY, xCol3, currentY + rowHeight, paint);
            canvas.drawLine(xCol4, currentY, xCol4, currentY + rowHeight, paint);

            paint.setStyle(Paint.Style.FILL);
            // Centraliza e desenha no cabeçalho
            String[] headerTexts = { "ESTABELECIMENTO", "FUNCIONALIDADE", "VALOR" };
            for (int i = 0; i < headerTexts.length; i++) {
                float textoWidth = paint.measureText(headerTexts[i]);
                float posX = colStarts[i] + (columnWidths[i] - textoWidth) / 2;
                canvas.drawText(headerTexts[i], posX, currentY + 25, paint);
            }
            currentY += rowHeight;
            paint.setStyle(Paint.Style.STROKE);

            // --- Linhas dos dados ---
            paint.setFakeBoldText(false);
            int linhasDesenhadas = 0;

            while (linhaGlobal < gastos.size() && linhasDesenhadas < maxLinhasPorPagina - 2) {
                ItenLista gasto = gastos.get(linhaGlobal);

                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(startX, currentY, startX + tableWidth, currentY + rowHeight, paint);

                // Linhas verticais
                canvas.drawLine(xCol1, currentY, xCol1, currentY + rowHeight, paint);
                canvas.drawLine(xCol2, currentY, xCol2, currentY + rowHeight, paint);
                canvas.drawLine(xCol3, currentY, xCol3, currentY + rowHeight, paint);
                canvas.drawLine(xCol4, currentY, xCol4, currentY + rowHeight, paint);

                paint.setStyle(Paint.Style.FILL);
                canvas.drawText(gasto.getEstabelecimento(), xCol1 + 10, currentY + 25, paint);
                canvas.drawText(gasto.getFuncao(), xCol2 + 10, currentY + 25, paint);
                canvas.drawText("R$ " + String.valueOf(gasto.getValor()), xCol3 + 10, currentY + 25, paint);

                currentY += rowHeight;
                linhaGlobal++;
                linhasDesenhadas++;
            }

            // --- Cabeçalho ---
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(startX, currentY, startX + tableWidth, currentY + rowHeight, paint);

            // Linhas verticais do cabeçalho
            canvas.drawLine(xCol1, currentY, xCol1, currentY + rowHeight, paint);
            canvas.drawLine(xCol2, currentY, xCol2, currentY + rowHeight, paint);
            canvas.drawLine(xCol3, currentY, xCol3, currentY + rowHeight, paint);
            canvas.drawLine(xCol4, currentY, xCol4, currentY + rowHeight, paint);

            paint.setStyle(Paint.Style.FILL);
            // Centraliza e desenha no cabeçalho
            String[] fim = { " ", "Sobra do Mês", "R$ " + resto};
            for (int i = 0; i < fim.length; i++) {
                float textoWidth = paint.measureText(fim[i]);
                float posX = colStarts[i] + (columnWidths[i] - textoWidth) / 2;
                canvas.drawText(fim[i], posX, currentY + 25, paint);
            }
            currentY += rowHeight;

            pdfDocument.finishPage(page);
            paginaAtual++;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            pdfDocument.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
        return outputStream.toByteArray();
    }


    public static void salvarPDFnoDownloads(Context context, byte[] pdfData, String nomeArquivo) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, nomeArquivo);
        values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        values.put(MediaStore.Downloads.IS_PENDING, 1);

        ContentResolver resolver = context.getContentResolver();
        Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri itemUri = resolver.insert(collection, values);

        if (itemUri != null) {
            try (OutputStream out = resolver.openOutputStream(itemUri)) {
                out.write(pdfData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Marca como pronto para aparecer
            values.clear();
            values.put(MediaStore.Downloads.IS_PENDING, 0);
            resolver.update(itemUri, values, null, null);
        }
    }

    public static void deletarArquivoDownloadsSeExistir(Context context, String nomeArquivo) {
        Uri collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        ContentResolver resolver = context.getContentResolver();

        String selection = MediaStore.Downloads.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{nomeArquivo};

        Cursor cursor = resolver.query(collection, null, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID));
            Uri uri = ContentUris.withAppendedId(collection, id);
            resolver.delete(uri, null, null);
        }

        if (cursor != null) cursor.close();
    }


    private void mostrarDialogoDeConfirmacao(List<ItenLista> listas, List<ListaItens> listaItens) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Finalizar Lista")
                .setMessage("Deseja finalizar essa lista?" + "\n" + "O app vai gerar um PDF e perguntar se deseja compartilhar.")
                .setPositiveButton("Sim", (dialog, which) -> {

                    // Gera o PDF
                    byte[] pdf = gerarPDF(requireContext(), listas,
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

                                // ⚠️ Só aqui executa o restante das ações:
                                trocarDeMes();
                                geraTabela(listas, listaItens);
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

    void trocarDeMes() {
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

    public void geraTabela(List<ItenLista> listas, List<ListaItens> listaItens){
        TableLayout tabela = binding.tabela;
        for (ItenLista iten : listas) {
            TableRow row = new TableRow(requireContext());

            TextView est = new TextView(requireContext());
            est.setText(iten.getEstabelecimento());
            est.setPadding(8, 8, 8, 8);
            row.addView(est);

            TextView func = new TextView(requireContext());
            func.setText(iten.getFuncao());
            func.setPadding(8, 8, 8, 8);
            row.addView(func);

            TextView val = new TextView(requireContext());
            val.setText(String.format("R$ %.2f", iten.getValor()));
            val.setPadding(8, 8, 8, 8);
            row.addView(val);

            tabela.addView(row);
        }
        binding.sobraMes.setText("Sobra do mês: R$ " + String.valueOf(listaItens.get(0).getValorRestante()));
    }

    public void salvarECompartilharPDF(Context context, byte[] pdfData, String nomeArquivo) {
        File pasta = new File(context.getFilesDir(), "pdfs");
        if (!pasta.exists()) pasta.mkdirs();

        File arquivo = new File(pasta, nomeArquivo);
        try (FileOutputStream fos = new FileOutputStream(arquivo)) {
            fos.write(pdfData);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Erro ao salvar PDF", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", arquivo);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(Intent.createChooser(intent, "Compartilhar PDF"));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}