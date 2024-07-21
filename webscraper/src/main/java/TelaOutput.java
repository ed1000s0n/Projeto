import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class TelaOutput extends javax.swing.JPanel {
    private javax.swing.JLabel jLabel1;
    JLabel l_titulo = new JLabel();
    private javax.swing.JLabel jLabel4;
    JLabel l_descricao = new JLabel();
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel l_preco;
    private JLabel l_imagem;
    public TelaOutput(){
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        l_titulo = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        l_descricao = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        l_preco = new javax.swing.JLabel();
        l_imagem = new javax.swing.JLabel();

        JFrame frame = new JFrame("Informações do Produto");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(null);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("PRODUTO");
        frame.add(jLabel1);
        jLabel1.setBounds(6, 6, 68, 20);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("DESCRIÇÃO");
        frame.add(jLabel4);
        jLabel4.setBounds(10, 180, 77, 20);

        l_descricao.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // NOI18N
        l_descricao.setText("Teste");
        l_descricao.setVerticalAlignment(SwingConstants.TOP);
        l_descricao.setBounds(10, 210, 690, 300);
        frame.add(l_descricao);

        l_titulo.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        l_titulo.setText("Título");
        l_titulo.setVerticalAlignment(SwingConstants.TOP);
        l_titulo.setBounds(10, 30, 420, 70);
        frame.add(l_titulo);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("PREÇO");
        frame.add(jLabel6);
        jLabel6.setBounds(10, 100, 45, 20);

        l_preco.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        l_preco.setText("PREÇO");
        frame.add(l_preco);
        l_preco.setBounds(10, 120, 90, 30);

        l_imagem.setBounds(550, 10, 200, 200);
        frame.add(l_imagem);

        frame.setVisible(true);
    }

    public void AtualizarDados(Produto p){
        l_titulo.setText("<html>"+p.getNome()+"</html>");
        l_preco.setText(p.getPreco());
        l_descricao.setText("<html>"+p.getDescricao()+"</html>");

        try {
            URL imageUrl = new URL(p.getImagem().get(0)); // Supondo que Produto tem um método getImagemUrl()
            BufferedImage bufferedImage = ImageIO.read(imageUrl);
            Image resizedImage = bufferedImage.getScaledInstance(l_imagem.getWidth(), l_imagem.getHeight(), Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(resizedImage);
            l_imagem.setIcon(imageIcon);
        } catch (IOException e) {
            e.printStackTrace();
            l_imagem.setIcon(null); // Remove a imagem se ocorrer um erro
        }
    }

}