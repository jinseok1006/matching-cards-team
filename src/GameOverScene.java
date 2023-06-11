import requstor.Requestor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameOverScene extends JPanel {
    private static class ImagePanel extends JPanel {
        private Image image;
        private float alpha = 0.0f;

        public ImagePanel(Image image) {
            this.image = image;
            setLayout(null);
        }

        public void setAlpha(float alpha) {
            this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }

    private ImagePanel backgroundPanel;
    private Timer fadeInTimer;
    private float alpha = 0.0f;

    public Main main; // Main 클래스의 인스턴스 변수
    public double time; // int 값의 변수
    private String difficulty;
    private Requestor requestor;

    private static String DIFFICULTIES[] = {"easy", "normal", "hard"};

    public GameOverScene(Main main, Requestor requestor, int difficulty, double time) {
        this.main = main;
        this.requestor = requestor;
        this.time = time;
        this.difficulty = DIFFICULTIES[difficulty];

        setLayout(new BorderLayout());

        // 배경 이미지 패널
        backgroundPanel = new ImagePanel(new ImageIcon("image/gameover_back.png").getImage());
        backgroundPanel.setLayout(new BorderLayout());

        add(backgroundPanel);

        // 이미지 페이드 인 효과 타이머
        fadeInTimer = new Timer(20, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (alpha < 1.0f) {
                    alpha += 0.03f;
                    backgroundPanel.setAlpha(alpha);
                    backgroundPanel.repaint();
                } else {
                    fadeInTimer.stop();
                }
            }
        });
        fadeInTimer.start();

        // 닉네임 입력 패널
        JPanel nicknamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        nicknamePanel.setBackground(new Color(255, 251, 240));
        nicknamePanel.setOpaque(true);

        JLabel rankLabel = new JLabel("당신의 기록은 " + time + "초입니다.");
        rankLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 36));
        rankLabel.setForeground(Color.BLACK);

        // 이미지 하단 가운데에 위치하기 위한 추가 코드
        backgroundPanel.setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        centerPanel.add(rankLabel);
        backgroundPanel.add(centerPanel, BorderLayout.PAGE_END);

        add(backgroundPanel);

        // 닉네임 레이블// 이미지 하단 가운데에 위치하기 위한 추가 코드
        backgroundPanel.setLayout(new BorderLayout());

        // 중앙 패널
        JPanel centerPanel2 = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new GridBagLayout());

        // 축하 메시지
        JLabel congratulationsLabel = new JLabel("축하합니다!");
        congratulationsLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 36));
        congratulationsLabel.setForeground(Color.BLACK);

        // 중앙 패널에 컴포넌트를 추가하고 위치 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.PAGE_END; // 정중앙 아래로 정렬
        gbc.insets = new Insets(300, 0, 0, 0); // 위쪽 여백 추가 (100px)

        centerPanel.add(congratulationsLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 0, 0); // 위쪽 여백 추가 (10px)
        centerPanel.add(rankLabel, gbc);


        // 중앙 패널을 backgroundPanel의 중앙에 추가
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        JLabel nicknameLabel = new JLabel("Enter Nickname:");
        nicknameLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 27));
        nicknameLabel.setForeground(Color.BLACK);
        nicknamePanel.add(nicknameLabel);

        // 닉네임 입력 필드
        JTextField nicknameField = new JTextField(15);
        nicknameField.setFont(new Font("맑은 고딕", Font.PLAIN, 24));
        Color Fieldcolor = new Color(255, 251, 240);
        nicknameField.setBackground(Fieldcolor);
        nicknameField.setForeground(Color.BLACK);
        nicknameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Requestor requestor = GameOverScene.this.requestor;
                String nickname = nicknameField.getText();
                requestor.register(GameOverScene.this.difficulty, nickname, GameOverScene.this.time);


                // 순위표를 표시하는 새로운 창 열음
                GameOverScene.this.main.showLeaderboard();
                GameOverScene.this.main.setTitleScene();
            }
        });
        nicknamePanel.add(nicknameField);

        add(nicknamePanel, BorderLayout.SOUTH);
    }

}
