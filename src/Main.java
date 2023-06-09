import requstor.Requestor;

import javax.swing.*;


public class Main extends JFrame {
    private static final int width = 700, height = 700;
    private JPanel currentScene;
    // 재사용 가능한 패널과 프레임
    private LeaderboardDialog leaderboardDialog;
    private TitleScene titleScene;
    // 서버 객체 생성
    private Requestor requestor;

    public Main() {
        super("찾아봐요 카드의숲");

        // 서버가 열렸는지 확인
        this.requestor = new Requestor();
        if (requestor.getStatus() == false) {
            JOptionPane.showMessageDialog(null, "서버와 연결되지 않았습니다.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // 재사용가능한 프레임와 패널 생성
        this.titleScene = new TitleScene(this);
        this.leaderboardDialog = new LeaderboardDialog(this, this.requestor);

        // 프레임 타이틀 화면 설정
        setTitleScene();

        // 프레임 상태 결정
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public void showLeaderboard(int diff) {
        leaderboardDialog.showLeaderboard(diff);
    }

    // currentScene 변경시 화면 update
    public void updateScene() {
        setContentPane(currentScene);
        revalidate();
    }

    public void setGameOverScene(int difficulty, double time) {
        currentScene = new GameOverScene(this, this.requestor, difficulty, time);
        updateScene();
    }

    public void setInGameScene(int difficulty) {
        currentScene = new InGameScene(this, difficulty);
        updateScene();
    }

    public void setTitleScene() {
        currentScene = this.titleScene;
        updateScene();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main();
        });
    }

}