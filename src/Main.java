import requstor.Requestor;

import javax.swing.*;


public class Main extends JFrame {
    private JPanel currentScene;
    // 재사용 가능한 패널과 프레임
    private LeaderboardDialog leaderboardDialog;
    private TitleScene titleScene;
    
    private Requestor requestor;

    public Main() {
        setSize(700, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 서버가 열렸는지 확인
        this.requestor = new Requestor("http://3.34.97.153:3000");

        // 재사용가능한 프레임와 패널 생성
        this.titleScene = new TitleScene(this);
        this.leaderboardDialog = new LeaderboardDialog(this, this.requestor);

        // 현재 패널 결정
        currentScene = this.titleScene;
        this.setContentPane(currentScene);

        // 창 가운데 정렬
        setLocationRelativeTo(null);

        setVisible(true);
    }

    public void showLeaderboard() {
        leaderboardDialog.showLeaderboard();
    }

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