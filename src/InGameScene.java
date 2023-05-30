import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class InGameScene extends JPanel {
    public static final int EASY = 0;
    public static final int NORMAL = 1;
    public static final int HARD = 2;

    private static final int ROWS[] = {4, 4, 4};
    private static final int COLUMNS[] = {4, 5, 6};

    private static final int TOTAL_IMAGES = 18;
    private static final int IMAGES_PER_PAIR = 2;

    private int row;
    private int column;
    private int totalPairs;

    private int totalMatches;
    private CardButton selectedCard;
    private boolean isChecking;
    private Timer timer;
    private long startTime;
    private long elapsedTime;

    private Main main;
    private int difficulty;

    public InGameScene(Main main, int difficulty) {
        this.main = main;
        this.difficulty = difficulty;

        this.row = this.ROWS[difficulty];
        this.column = this.COLUMNS[difficulty];
        this.totalPairs = (this.row * this.column) / 2;

        List<Integer> selectedImages = selectRandomImages();

        List<Integer> allCards = createCardPairs(selectedImages);
        Collections.shuffle(allCards);

        CardButton[][] cards = createCardArray(allCards);

        setLayout(new BorderLayout());

        // 카드 패널 생성
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(row, column, 10, 10));

        // 각 카드 버튼에 액션 리스너 추가
        for (CardButton[] row : cards) {
            for (CardButton cardButton : row) {
                cardButton.addActionListener(new CardButtonListener());
                cardPanel.add(cardButton);
            }
        }

        add(cardPanel, BorderLayout.CENTER);

        // 타이머 레이블 생성
        JLabel timerLabel = new JLabel("Shown for 3 seconds and START!");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(timerLabel, BorderLayout.NORTH);

        // 모든 카드를 앞면으로 보이도록 설정
        for (CardButton[] row : cards) {
            for (CardButton cardButton : row) {
                cardButton.showImage();
            }
        }

        // 3초 후에 카드를 뒷면으로 다시 뒤집음
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (CardButton[] row : cards) {
                    for (CardButton cardButton : row) {
                        cardButton.hideImage();
                    }
                }

                // 게임 시작 시간 기록
                startTime = System.currentTimeMillis();
                // 타이머 시작
                startTimer(timerLabel);
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private List<Integer> selectRandomImages() {
        List<Integer> allImages = new ArrayList<>();
        for (int i = 1; i <= TOTAL_IMAGES; i++) {
            allImages.add(i);
        }

        List<Integer> selectedImages = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < totalPairs; i++) {
            int randomIndex = random.nextInt(allImages.size());
            int image = allImages.remove(randomIndex);
            selectedImages.add(image);
        }

        return selectedImages;
    }

    private List<Integer> createCardPairs(List<Integer> selectedImages) {
        List<Integer> cardPairs = new ArrayList<>();
        for (int i = 0; i < IMAGES_PER_PAIR; i++) {
            cardPairs.addAll(selectedImages);
        }
        return cardPairs;
    }

    private CardButton[][] createCardArray(List<Integer> allCards) {
        List<CardButton> cardButtons = new ArrayList<>();
        for (int card : allCards) {
            CardButton cardButton = new CardButton(card);
            cardButtons.add(cardButton);
        }

        Collections.shuffle(cardButtons);

        CardButton[][] cards = new CardButton[row][column];
        int index = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                cards[i][j] = cardButtons.get(index);
                index++;
            }
        }

        return cards;
    }

    private void startTimer(JLabel timerLabel) {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedTime / 1000);
                String time = String.format("%d", seconds);
                timerLabel.setText(time);
            }
        });
        timer.start();
    }

    public void stopTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
    }

    private class CardButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (isChecking) {
                // 이미 짝을 체크 중인 경우 클릭 무시
                return;
            }

            CardButton clickedCard = (CardButton) e.getSource();
            if (clickedCard.isMatched()) {
                // 이미 짝이 맞춰진 카드 클릭 무시
                return;
            }

            if (selectedCard == null) {
                // 첫 번째 카드 선택
                selectedCard = clickedCard;
                selectedCard.showImage();
            } else {
                // 두 번째 카드 선택
                if (selectedCard == clickedCard) {
                    // 같은 카드를 클릭한 경우 무시
                    return;
                }
                clickedCard.showImage();
                isChecking = true;

                if (selectedCard.getImageId() == clickedCard.getImageId()) {
                    // 짝이 맞는 경우
                    selectedCard.setMatched(true);
                    clickedCard.setMatched(true);
                    totalMatches++;

                    if (totalMatches == totalPairs) {
                        // 모든 짝을 다 맞춤
                        stopTimer();
                        int seconds = (int) (elapsedTime / 1000);
                        JOptionPane.showMessageDialog(null, "축하합니다!");
                        InGameScene.this.main.setGameOverScene(InGameScene.this.difficulty, seconds);
                    }

                    selectedCard = null;
                    isChecking = false;
                } else {
                    // 짝이 맞지 않는 경우
                    Timer timer = new Timer(1000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            selectedCard.hideImage();
                            clickedCard.hideImage();
                            selectedCard = null;
                            isChecking = false;
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
        }
    }
}

class CardButton extends JButton {
    private static final int CARD_WIDTH = 70;
    private static final int CARD_HEIGHT = 70;

    private int imageId;
    private boolean isMatched;
    private ImageIcon backImage;

    public CardButton(int imageId) {
        this.imageId = imageId;
        this.isMatched = false;

        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        setBackImage(new ImageIcon("buttonImages/back.png"));
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public int getImageId() {
        return imageId;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }

    public void showImage() {
        ImageIcon imageIcon = new ImageIcon("buttonImages/" + imageId + ".png");
        Image image = imageIcon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(image));
    }

    public void hideImage() {
        setIcon(backImage);
    }

    public void setBackImage(ImageIcon backImage) {
        this.backImage = resizeImage(backImage);
        setIcon(this.backImage);
    }

    private ImageIcon resizeImage(ImageIcon imageIcon) {
        Image image = imageIcon.getImage();
        int newWidth = (int) (CARD_WIDTH * 0.7);
        int newHeight = (int) (CARD_HEIGHT * 0.7);
        Image resizedImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }
}
