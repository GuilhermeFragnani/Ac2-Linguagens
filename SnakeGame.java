import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGame extends JFrame {
    private JPanel painelJogo;
    private JButton btnIniciar, btnReiniciar, btnPausar;
    private JLabel labelPlacar;
    private int placar = 0;

    private final int LARGURA_JOGO = 400;
    private final int ALTURA_JOGO = 400;
    private final int TAMANHO_BLOCO = 10;
    private ArrayList<Point> cobra;
    private Point maca;
    private String direcao = "DIREITA";
    private boolean rodando = false;

    private Timer timer;

    public SnakeGame() {
        setTitle("Jogo da Cobrinha");
        setSize(LARGURA_JOGO, ALTURA_JOGO + 60);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Configuração do painel de controle
        JPanel painelControle = new JPanel();
        btnIniciar = new JButton("Iniciar");
        btnReiniciar = new JButton("Reiniciar");
        btnPausar = new JButton("Pausar");
        labelPlacar = new JLabel("Placar: 0");

        painelControle.add(btnIniciar);
        painelControle.add(btnReiniciar);
        painelControle.add(btnPausar);
        painelControle.add(labelPlacar);
        add(painelControle, BorderLayout.NORTH);

        // Configuração do painel de jogo
        painelJogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenhar(g);
            }
        };
        painelJogo.setPreferredSize(new Dimension(LARGURA_JOGO, ALTURA_JOGO));
        painelJogo.setBackground(Color.LIGHT_GRAY);
        add(painelJogo, BorderLayout.CENTER);

        // Listeners para botões
        btnIniciar.addActionListener(e -> iniciarJogo());
        btnReiniciar.addActionListener(e -> reiniciarJogo());
        btnPausar.addActionListener(e -> pausarJogo());

        // Listener para teclas de direção
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                mudarDirecao(e);
            }
        });
        setFocusable(true);

        // Configuração do timer do jogo
        timer = new Timer(100, e -> atualizarJogo());

        // Inicia as variáveis do jogo
        resetJogo();
    }

    private void resetJogo() {
        cobra = new ArrayList<>();
        cobra.add(new Point(100, 100));
        direcao = "DIREITA";
        rodando = false;
        placar = 0;
        atualizarPlacar();
        gerarNovaMaca();
    }

    private void iniciarJogo() {
        if (!rodando) {
            rodando = true;
            timer.start();
            setFocusable(true); // Garante o foco para capturar teclas
            requestFocusInWindow();
        }
    }

    private void pausarJogo() {
        if (rodando) {
            rodando = false;
            timer.stop();
        }
    }

    private void reiniciarJogo() {
        resetJogo();
        painelJogo.repaint();
        iniciarJogo();
    }

    private void atualizarPlacar() {
        labelPlacar.setText("Placar: " + placar);
    }

    private void gerarNovaMaca() {
        Random random = new Random();
        int x = random.nextInt(LARGURA_JOGO / TAMANHO_BLOCO) * TAMANHO_BLOCO;
        int y = random.nextInt(ALTURA_JOGO / TAMANHO_BLOCO) * TAMANHO_BLOCO;
        maca = new Point(x, y);
    }

    private void mudarDirecao(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if (!direcao.equals("BAIXO")) direcao = "CIMA";
                break;
            case KeyEvent.VK_DOWN:
                if (!direcao.equals("CIMA")) direcao = "BAIXO";
                break;
            case KeyEvent.VK_LEFT:
                if (!direcao.equals("DIREITA")) direcao = "ESQUERDA";
                break;
            case KeyEvent.VK_RIGHT:
                if (!direcao.equals("ESQUERDA")) direcao = "DIREITA";
                break;
        }
    }

    private void atualizarJogo() {
        if (rodando) {
            Point cabeca = cobra.get(0);
            Point novaCabeca = new Point(cabeca);

            // Movimento da cobra
            switch (direcao) {
                case "CIMA" -> novaCabeca.y -= TAMANHO_BLOCO;
                case "BAIXO" -> novaCabeca.y += TAMANHO_BLOCO;
                case "ESQUERDA" -> novaCabeca.x -= TAMANHO_BLOCO;
                case "DIREITA" -> novaCabeca.x += TAMANHO_BLOCO;
            }

            // Verifica colisão com as bordas ou com o corpo da cobra
            if (novaCabeca.x < 0 || novaCabeca.x >= LARGURA_JOGO || 
                novaCabeca.y < 0 || novaCabeca.y >= ALTURA_JOGO || 
                cobra.contains(novaCabeca)) {
                rodando = false;
                timer.stop();
                return;
            }

            // Adiciona nova posição da cabeça
            cobra.add(0, novaCabeca);

            // Verifica se comeu a maçã
            if (novaCabeca.equals(maca)) {
                placar++;
                atualizarPlacar();
                gerarNovaMaca();
            } else {
                cobra.remove(cobra.size() - 1); // Remove o último segmento se não comeu
            }

            painelJogo.repaint();
        }
    }

    private void desenhar(Graphics g) {
        // Desenhar cobra
        g.setColor(Color.GREEN);
        for (Point ponto : cobra) {
            g.fillRect(ponto.x, ponto.y, TAMANHO_BLOCO, TAMANHO_BLOCO);
        }

        // Desenhar maçã
        g.setColor(Color.RED);
        g.fillRect(maca.x, maca.y, TAMANHO_BLOCO, TAMANHO_BLOCO);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame jogo = new SnakeGame();
            jogo.setVisible(true);
        });
    }
}
