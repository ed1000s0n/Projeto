import java.util.ArrayList;

public class Produto {
    String nome, preco, descricao;
    ArrayList <String> imagem;
    public Produto(String nome, String preco, String descricao, ArrayList <String> imagem){
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.imagem = imagem;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getPreco() {
        return preco;
    }
    public void setPreco(String preco) {
        this.preco = preco;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public ArrayList<String> getImagem() {
        return imagem;
    }
    public void setImagem(ArrayList<String> imagem) {
        this.imagem = imagem;
    }
}
