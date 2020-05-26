package com.balatech.ifood.model;

public class ItemPedido {

    private String idProdutoo;
    private String nomeProduto;
    private int quantidade;
    private Double preco;

    public ItemPedido() {
    }

    public String getIdProdutoo() {
        return idProdutoo;
    }

    public void setIdProdutoo(String idProdutoo) {
        this.idProdutoo = idProdutoo;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
