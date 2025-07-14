package com.example.gastosgeraisdomes.Telas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gastosgeraisdomes.Banco.AppDataBase;
import com.example.gastosgeraisdomes.Tabelas.ItenLista;
import com.example.gastosgeraisdomes.Tabelas.ListaItens;
import com.example.gastosgeraisdomes.databinding.FinalizarBinding;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Gastos");

        // Cria a linha 0
        Row titulo = sheet.createRow(0);

        Cell cellTitulo = titulo.createCell(0);
        cellTitulo.setCellValue(listaItens.get(0).getTitulo());

        sheet.addMergedRegion(new CellRangeAddress(
                0, // primeira linha
                0, // última linha (mesma linha)
                0, // primeira coluna
                2  // última coluna (até coluna 2)
        ));

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle moedaStyle = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        moedaStyle.setDataFormat(format.getFormat("R$ #,##0.00"));

        cellTitulo.setCellStyle(style);

        Row caixa = sheet.createRow(1);
        caixa.createCell(0).setCellValue("Caixa");
        caixa.createCell(1).setCellValue(listaItens.get(0).getDia());
        Cell cellValor = caixa.createCell(2);
        cellValor.setCellValue(listaItens.get(0).getValorTotal());
        cellValor.setCellStyle(moedaStyle);

        Row header = sheet.createRow(2);
        header.createCell(0).setCellValue("Estabelecimento");
        header.createCell(1).setCellValue("Funcionalidade");
        header.createCell(2).setCellValue("Valor");

        int fim = 0;

        for (int i = 0; i < listas.size(); i++) {
            ItenLista iten = listas.get(i);
            Row row = sheet.createRow(i + 3);
            row.createCell(0).setCellValue(iten.getEstabelecimento());
            row.createCell(1).setCellValue(iten.getFuncao());
            cellValor = row.createCell(2);
            cellValor.setCellValue(iten.getValor());
            cellValor.setCellStyle(moedaStyle);
            fim = i;
        }
        Row row = sheet.createRow(fim+1);
        row.createCell(1).setCellValue("Sobra do mês");
        cellValor = row.createCell(2);
        cellValor.setCellValue(listaItens.get(0).getValorRestante());
        cellValor.setCellStyle(moedaStyle);

        File file = new File(requireContext().getExternalFilesDir(null), "gastos.xlsx");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}