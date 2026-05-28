# 📊 Controle de Gastos Mensais

<p align="center">
  <img src="https://img.shields.io/badge/Vers%C3%A3o-2.0.1-blue?style=for-the-badge&logo=android" alt="Versão 2.0.1">
  <img src="https://img.shields.io/badge/Autor-Cassiano%20Carvalho-orange?style=for-the-badge" alt="Autor Cassiano">
  <img src="https://img.shields.io/badge/Status-Conclu%C3%ADdo-brightgreen?style=for-the-badge" alt="Status Concluído">
</p>

## 📝 O que é o Aplicativo?

O **Controle de Gastos Mensais** é um aplicativo mobile prático e eficiente desenvolvido para ajudar você a organizar sua vida financeira de maneira simples e sem burocracia. 

Diferente de planilhas complexas, o app foca no que realmente importa: permitir que você crie uma lista dedicada para cada mês e registre todas as suas despesas em tempo real. Com uma interface limpa, você sabe exatamente para onde o seu dinheiro está indo e se o seu orçamento terminou **no azul** ou **no vermelho**.

---

## 🚀 Principais Funcionalidades

### 🗓️ Organização por Listas Mensais
O aplicativo funciona em torno do conceito de competência mensal. Você cria uma lista específica para o mês atual (ex: *Janeiro/2026*) e gerencia todas as movimentações dentro desse período isolado.

### 🔄 Gerenciamento Completo de Itens (CRUD)
Dentro de cada lista mensal, você pode gerenciar os seus gastos livremente através de um sistema completo de **CRUD** (Criar, Ler, Atualizar e Deletar):
* **Adicionar Gasto:** Insira novas despesas rapidamente.
* **Visualizar Detalhes:** Veja a listagem limpa de tudo o que foi lançado.
* **Editar Item:** Corrija valores ou nomes de estabelecimentos errados sem precisar apagar o registro.
* **Excluir Item:** Remova gastos duplicados ou cancelados instantaneamente.

### 📌 Parâmetros Fixos por Despesa
Para manter a consistência e a facilidade de leitura, cada item adicionado possui três parâmetros obrigatórios e objetivos:
1. **Estabelecimento:** Onde a despesa foi realizada (ex: *Supermercado Target, Posto Shell, Farmácia*).
2. **Funcionalidade:** A categoria ou o motivo do gasto (ex: *Alimentação, Combustível, Saúde, Lazer*).
3. **Valor:** O custo real do item em reais (R$).

### 📄 Exportação e Emissão de Relatório em PDF
Ao finalizar os lançamentos do mês, o aplicativo conta com um módulo nativo gerador de arquivos. Com apenas um clique, o app compila todos os dados inseridos e **gera um relatório em PDF** elegante, formatado e pronto para impressão ou compartilhamento. 
* O PDF traz um resumo matemático do período, destacando de forma visual o veredito financeiro do seu mês (se você fechou com saldo positivo ou se ficou no vermelho).

---

## 📱 Visual do Aplicativo (Screenshots)

Aqui você pode conferir a interface do usuário e o modelo de relatório gerado pelo sistema:

<table align="center">
  <tr>
    <td align="center"><b>Tela Inicial</b></td>
    <td align="center"><b>Cria Listas</b></td>
    <td align="center"><b>Tela Inicial Com Lista Criada</b></td>
  </tr>
  <tr>
    <td><img src="Imagens/home.jpg" width="220" alt="Tela Inicial"></td>
    <td><img src="Imagens/cria_lista.jpg" width="220" alt="Cria Listas"></td>
    <td><img src="Imagens/home_com_lista.jpg" width="220" alt="Tela Inicial Com Lista Criada"></td>
  </tr>
</table>

---

<table align="center">
    <tr>
        <td align="center"><b>Cria itens</b></td>
        <td align="center"><b>Tela Inicial Com Itens na Lista</b></td>
        <td align="center"><b>Lista dos Itens</b></td>
    </tr>
     <tr>
        <td><img src="Imagens/cira_item.jpg" width="220" alt="Cria itens"></td>
        <td><img src="Imagens/home_com_itens.jpg" width="220" alt="Tela Inicial Com Itens na Lista"></td>
        <td><img src="Imagens/lista_itens.jpg" width="220" alt="Lista dos Itens"></td>
    </tr>
</table>


---

## 🛠️ Tecnologias Utilizadas

* **Plataforma:** Android
* **Formato de Distribuição:** APK (Disponível na aba de Releases)
* **Mecanismo de PDF:** Biblioteca nativa de renderização e estruturação de documentos para impressão.

---

## 👨‍💻 Desenvolvedor

Este projeto foi idealizado e construído de ponta a ponta por:

* **Criador:** Cassiano Carvalho de Souza

---

## 📦 Como instalar o app via GitHub?

Como este aplicativo está hospedado aqui no GitHub, siga estes passos para instalar no seu celular:

1. Acesse a seção de **Releases** no lado direito desta página.
2. Baixe o arquivo `.apk` da versão mais recente (`2.0.1`).
3. No seu celular Android, abra o arquivo baixado.
4. Se o sistema solicitar, ative a permissão para **"Instalar aplicativos de fontes desconhecidas"**.
5. Siga as instruções na tela e abra o app!