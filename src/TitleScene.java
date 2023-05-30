import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class RButton extends JButton {

    public RButton(String text) {
        super(text);
        decorate();
    }

    protected void decorate() {
        setBorderPainted(false);
        setOpaque(false);
    }
    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        Graphics2D graphics = (Graphics2D) g;

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphics.setColor(getBackground());
        
        graphics.fillRoundRect(0, 0, width, height, 10, 10);

      
        super.paintComponent(g);
    }
}
//---------------------------------------------------------------------------------------------------

class Rmenubar extends JMenuBar {

    public Rmenubar() {
        super();
        decorate();
    }

    protected void decorate() {
//        setBorderPainted(false);
//        setOpaque(false);
    }
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        Graphics2D graphics = (Graphics2D) g;

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.setColor(getBackground());
       
        graphics.fillRoundRect(0, 0, width, height, 10, 10);

        
        super.paintComponent(g);
    }
}
//----------------------------------------------------------------

class TitleScene extends JPanel {
    public TitleScene(Main main) {
    	
    	setLayout(null);
    	
    	
    	
    	ImageIcon icon = new ImageIcon("image/title_back.png");

        JPanel background = new JPanel() {
            public void paintComponent(Graphics g) {
                
                g.drawImage(icon.getImage(), 0, -20, null);
                
                setOpaque(false); 
                super.paintComponent(g);
            }
        };
        background.setLayout(null);
        background.setBounds(0, 0, 700, 700);
        add(background);


        
        JMenuItem btn_E = new JMenuItem("EASY (4X4)");
        btn_E.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setInGameScene(InGameScene.EASY);
            }
        });
        btn_E.setBackground(Color.GREEN);

        
        JMenuItem btn = new JMenuItem("NORMAL (5X4)");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setInGameScene(InGameScene.NORMAL);
            }
        });
        btn.setBackground(Color.YELLOW);

        
        JMenuItem btn_H = new JMenuItem("HARD (6X4)");
        btn_H.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.setInGameScene(InGameScene.HARD);
            }
        });
        btn_H.setBackground(Color.RED);

        
        RButton btn_R = new RButton("랭킹");
        btn_R.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                main.showLeaderboard();
            }
        });
        btn_R.setBounds(100, 330, 108, 72);
        btn_R.setFont(new Font("맑은 고딕", Font.PLAIN, 30));
        btn_R.setBackground(Color.CYAN);
        
        
        RButton btn_Q = new RButton("종료");
        btn_Q.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btn_Q.setBounds(500, 330, 108, 72);
        btn_Q.setFont(new Font("맑은 고딕", Font.PLAIN, 30));
        btn_Q.setBackground(Color.CYAN);
        
        
        Rmenubar mb = new Rmenubar();
        JMenu m1 = new JMenu("시작");
        m1.setFont(new Font("맑은 고딕", Font.PLAIN, 30));
        m1.setPreferredSize(new Dimension(108,72));
        //m1.setForeground(Color.WHITE);
//        mb.setBackground(Color.CYAN);
        
        
        m1.add(btn_E);
        m1.addSeparator();
        m1.add(btn);
        m1.addSeparator();
        m1.add(btn_H);
        
        mb.setPreferredSize(new Dimension(108, 72));
        mb.setBounds(300, 330, 108, 72);
        mb.add(m1);
        
        background.add(mb);
        background.add(btn_R);
        background.add(btn_Q);
        
        
    }
}

