import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class FloatTimer {
    private int time = 0;
    private Timer timer;
    private JLabel timerLabel;

    public FloatTimer(JLabel timerLabel) {
        this.timerLabel = timerLabel;
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                increase();
                timerLabel.setText(FloatTimer.this.toString());
            }
        });
    }

    public void startTimer() {
        this.timer.start();
    }

    public void stopTimer() {
        timer.stop();
    }

    public void increase() {
        time += 1;
    }

    public double getTime() {
        return time / 100.0;
    }

    @Override
    public String toString() {
        int sec = time / 100;
        int milliSec = time % 100;

        return sec + "." + (milliSec < 10 ? "0" + milliSec : milliSec);
    }
}

class StartThread extends Thread {
    private InGameScene scene;
    private JLabel timerLabel;
    private CardButton[][] cards;
    private int count;
    private FloatTimer timer;

    public StartThread(InGameScene scene, JLabel timerLabel, CardButton[][] cards, FloatTimer timer, int count) {
        this.scene = scene;
        this.timerLabel = timerLabel;
        this.cards = cards;
        this.count = count;
        this.timer = timer;
    }

    @Override
    public void run() {
        scene.setIsHinting(true);
        // count초 동안 반복
        while (count > 0) {
            try {
                Thread.sleep(1000);
                count--;
                timerLabel.setText("Shown for " + count + " seconds and START!");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 카드 전부 덮기
        for (CardButton[] row : cards) {
            for (CardButton cardButton : row) {
                cardButton.hideImage();
            }
        }

        // 타이머 시작
        scene.setIsHinting(false);
        timer.startTimer();
    }


}

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

    private boolean isHinting;
    private int totalMatches;
    private CardButton selectedCard;
    private boolean isChecking;
    private Timer timer;
    private long startTime;
    private long elapsedTime;

    private Main main;
    private int difficulty;

    private FloatTimer floatTimer;

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
        JLabel timerLabel = new JLabel("Shown for 5 seconds and START!");
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        add(timerLabel, BorderLayout.NORTH);

        // 타이머 객체 생성
        this.floatTimer = new FloatTimer(timerLabel);

        // 모든 카드를 앞면으로 보이도록 설정
        for (CardButton[] row : cards) {
            for (CardButton cardButton : row) {
                cardButton.showImage();
            }
        }


        // 3초 후에 카드를 뒷면으로 다시 뒤집음
//        Timer hintTimer = new Timer(5000, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                for (CardButton[] row : cards) {
//                    for (CardButton cardButton : row) {
//                        cardButton.hideImage();
//                    }
//                }
//
//                // 타이머 시작
//                floatTimer.startTimer();
//
//            }
//        });
//        hintTimer.setRepeats(false);
//        hintTimer.start();

//         5초동안 카드를 보여준후 카드를 뒤집고 게임 시작
        new StartThread(
                this,
                timerLabel,
                cards,
                floatTimer,
                5
        ).start();
    }

    public void setIsHinting(boolean hint) {
        this.isHinting = hint;
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
            if (isHinting)
                // 게임 시작시 힌트상태인 경우 무시
                return;

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
                        floatTimer.stopTimer();
                        JOptionPane.showMessageDialog(null, "Game Over!");
                        InGameScene.this.main.setGameOverScene(InGameScene.this.difficulty, floatTimer.getTime());
                    }

                    selectedCard = null;
                    isChecking = false;
                } else {
                    // 짝이 맞지 않는 경우
                    Timer timer = new Timer(750, new ActionListener() {
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
