import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import requstor.Requestor;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class LeaderboardDialog extends JDialog {
    private Requestor requestor;
    private LeaderboardScene leaderboardScene;

    public LeaderboardDialog(JFrame frame, Requestor requestor) {
        super(frame, "Leaderboard", true);
        this.requestor = requestor;
        setSize(500, 400);
        setLocationRelativeTo(null);
    }
    
    // 리더보드 창을 표시하는 메서드
    public void showLeaderboard() {
        this.leaderboardScene = new LeaderboardScene(this, this.requestor);
        setContentPane(leaderboardScene);
        setVisible(true);
    }

    // 리더보드 창을 닫는 메서드
    public void closeLeaderboard() {
        setVisible(false);
    }
}

// 리더보드 화면을 담당하는 패널 클래스
class LeaderboardScene extends JPanel {
    private String currentDifficulty;
    private int currentDifficultyIndex;
    private String[] difficultyList = {"easy", "normal", "hard"};

    private JTable table;
    private DefaultTableModel tableModel;

    private JLabel difficultyLabel; // 난이도 표시 레이블

    private LeaderboardDialog frame;
    private Requestor requestor;

    public LeaderboardScene(LeaderboardDialog frame, Requestor requestor) {
        this.frame = frame;
        this.requestor = requestor;

        setLayout(new BorderLayout());

        currentDifficultyIndex = 0;
        currentDifficulty = difficultyList[currentDifficultyIndex];

        JButton prevButton = new JButton("<"); // 이전 난이도로 변경하는 버튼
        JButton nextButton = new JButton(">"); // 다음 난이도로 변경하는 버튼

        difficultyLabel = new JLabel(currentDifficulty.toUpperCase()); // 현재 난이도 표시 레이블
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        difficultyLabel.setFont(difficultyLabel.getFont().deriveFont(20f)); // 글꼴 크기

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDifficulty(-1); // 이전 난이도로 변경
                updateDifficultyLabel(); // 난이도 레이블 업데이트
                showExampleRankingForDifficulty(currentDifficulty); // 변경된 난이도에 해당하는 예시 순위 표시
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeDifficulty(1); // 다음 난이도로 변경
                updateDifficultyLabel(); // 난이도 레이블 업데이트
                showExampleRankingForDifficulty(currentDifficulty); // 변경된 난이도에 해당하는 예시 순위 표시
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(difficultyLabel);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.NORTH);

        String[] columnNames = {"Rank", "Player", "Time"};
        Object[][] data = getExampleRankingForDifficulty(currentDifficulty);
        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.closeLeaderboard();
            }
        });
        add(closeButton, BorderLayout.SOUTH);
    }

    private void changeDifficulty(int offset) {
        currentDifficultyIndex += offset;

        if (currentDifficultyIndex < 0) {
            currentDifficultyIndex = difficultyList.length - 1;
        } else if (currentDifficultyIndex >= difficultyList.length) {
            currentDifficultyIndex = 0;
        }

        currentDifficulty = difficultyList[currentDifficultyIndex];
    }

    private Object[][] getExampleRankingForDifficulty(String difficulty) {

        JSONParser jsonParser = new JSONParser();
        String rankString = requestor.get(difficulty);

        // 오류 발생시 임시값 리턴
        if (rankString == null) {
            return new Object[][]{
                    {1, "Player1", 120},
                    {2, "Player2", 150},
                    {3, "Player3", 180}
            };
        }

        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(rankString);
            Object[][] rankArray = new Object[jsonArray.size()][];

            for (int i = 0; i < jsonArray.size(); i++) {
                var player = (JSONObject) jsonArray.get(i);
                rankArray[i] = new Object[]{i + 1, player.get("name"), player.get("sec")};
            }

            return rankArray;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Object[][]{
                {1, "Player1", 120},
                {2, "Player2", 150},
                {3, "Player3", 180}
        };
    }

    private void showExampleRankingForDifficulty(String difficulty) {
        Object[][] data = getExampleRankingForDifficulty(difficulty);
        tableModel.setDataVector(data, new String[]{"Rank", "Player", "Time"});
    }

    private void updateDifficultyLabel() {
        difficultyLabel.setText(currentDifficulty.toUpperCase());
    }
}
