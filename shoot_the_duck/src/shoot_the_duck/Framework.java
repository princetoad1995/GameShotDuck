package shoot_the_duck;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author PRINCE D. TOAD
 */
public class Framework extends Canvas {

    // độ dài của frame

    public static int frameWidth;
    // độ cao của frame
    public static int frameHeight;

    /**
     * Thời gian của 1 giây trong nano giây. 1 second = 1 000 000 000
     * nanoseconds
     */
    public static final long secInNanosec = 1000000000L;

    /**
     * Thời gian của 1 milli giây trong nano giây. 1 millisecond = 1 000 000
     * nanoseconds
     */
    public static final long milisecInNanosec = 1000000L;

    /**
     * FPS - Khung hình mỗi giây Số thời gian mà hình ảnh trên khung hình nên
     * cập nhật
     */
    private final int GAME_FPS = 60;

    /**
     * Tạm dừng giữa các cập nhật. nó dùng nanoseconds.
     */
    private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;

    /**
     * trạng thái có thể của trò chơi
     */
    public static enum GameState {

        STARTING, VISUALIZING, GAME_CONTENT_LOADING, MAIN_MENU, PLAYING, PAUSE, GAMEOVER, DESTROYED
    }
    /**
     * trạng thái hiện tại của trò chơi
     */
    public static GameState gameState;

    /**
     * thời gian trò chơi trôi qua dùng nanoseconds.
     */
    /**
     * Font chữ viết lên màn hình.
     */
    private Font font;

    private long gameTime;

    // tính toán thời gian đã trôi qua.
    private long lastTime;

    // Trò chơi
    private Game game;

    /**
     * Hình ảnh cho mennu.
     */
    private BufferedImage shootTheDuckMenuImg;

    public Framework() {
        super();

        gameState = GameState.VISUALIZING;

        //Bắt đầu game với thread.
        Thread gameThread = new Thread() {
            @Override
            public void run() {
                GameLoop();
            }
        };
        gameThread.start();
    }

    /**
     * Set biến và các đối tượng. Phương pháp này được dùng để thiết lập các
     * biến và các đối tượng cho lớp học này, các biến và các đối tượng cho các
     * trò chơi thực tế có thể được thiết lập trong Game.java.
     */
    private void Initialize() {
        font = new Font("monospaced", Font.BOLD, 16);
    }

    /**
     * tải dữ liệu - images, sounds, ... Phương pháp này được thiết kế để tải
     * các tập tin cho các lớp học này, các file cho các trò chơi thực tế có thể
     * được nạp trong Game.java.
     */
    private void LoadContent() {
        try {
            URL shootTheDuckMenuImgUrl = this.getClass().getResource("/shoot_the_duck/resources/images/menu.jpg");
            shootTheDuckMenuImg = ImageIO.read(shootTheDuckMenuImgUrl);
        } catch (IOException ex) {
            Logger.getLogger(Framework.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Trong khoảng thời gian cụ thể (GAME_UPDATE_PERIOD) trò chơi / logic được
     * cập nhật và sau đó trò chơi được vẽ trên màn hình.
     */
    private void GameLoop() {
        // Ở đây có hai biến được sử dụng trong việc hình dung trạng thái của trò chơi. Chúng tôi sử dụng chúng để chờ một thời gian để chúng ta có được đúng độ phân giải khung hình / cửa sổ.
//        Này có hai biến được sử dụng trong việc hình dung trạng thái của trò chơi. Chúng tôi sử dụng chúng để chờ một thời gian để chúng ta có được đúng độ phân giải khung hình / cửa sổ.
        long visualizingTime = 0, lastVisualizingTime = System.nanoTime();

        // biến này được sử dụng để tính toán thời gian xác định trong bao lâu chúng ta nên đặt mối đe dọa sleep để đáp ứng các Game FPS.
        long beginTime, timeTaken, timeLeft;

        while (true) {
            beginTime = System.nanoTime();

            switch (gameState) {
                case PLAYING:
                    gameTime += System.nanoTime() - lastTime;

                    game.UpdateGame(gameTime, mousePosition());

                    lastTime = System.nanoTime();
                    break;
                case GAMEOVER:
                    //...
                    break;
                case MAIN_MENU:
                    //...
                    break;
                case PAUSE:
                    //...
                    break;
                case GAME_CONTENT_LOADING:
                    //...
                    break;
                case STARTING:
                    // Đặt các biến và các đối tượng.
                    Initialize();
                    // Tải dữ liệu files - images, sounds, ...
                    LoadContent();

                    // Khi tất cả những thứ được gọi là hoàn thành ở trên, chúng tôi thay đổi trạng thái trò chơi để menu chính.
                    gameState = GameState.MAIN_MENU;
                    break;
                case VISUALIZING:
                    // Thiết lập chiều dài của khung hình
                    if (this.getWidth() > 1 && visualizingTime > secInNanosec) {
                        frameWidth = this.getWidth();
                        frameHeight = this.getHeight();

                        // Khi có kích thước thì ta thay đổi trạng thái
                        gameState = GameState.STARTING;
                    } else {
                        visualizingTime += System.nanoTime() - lastVisualizingTime;
                        lastVisualizingTime = System.nanoTime();
                    }
                    break;
            }

            // Vẽ lại màn hình
            repaint();

            // Ở đây chúng ta tính toán thời gian xác định trong bao lâu chúng ta nên đặt mối đe dọa sleep để đáp ứng các Game FPS.
            timeTaken = System.nanoTime() - beginTime;
            timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
            // Nếu thời gian ít hơn 10 giây, sau đó chúng tôi sẽ đưa thread để ngủ trong 10 phần nghìn giây để một số chủ đề khác có thể làm một số công việc.
            if (timeLeft < 10) {
                timeLeft = 10; //set a minimum
            }
            try {
                //Cung cấp sự chậm trễ cần thiết và cũng mang lại quyền kiểm soát để đề khác có thể làm việc.
                Thread.sleep(timeLeft);
            } catch (InterruptedException ex) {
            }
        }
    }

    /**
     * Vẽ các trò chơi để màn hình. Nó được gọi là thông qua phương pháp
     * repaint() GameLoop().
     */
    @Override
    public void Draw(Graphics2D g2d) {
        switch (gameState) {
            case PLAYING:
                game.Draw(g2d, mousePosition());
                break;
            case GAMEOVER:
                game.DrawGameOver(g2d, mousePosition());
                break;
            case MAIN_MENU:
                g2d.drawImage(shootTheDuckMenuImg, 0, 0, frameWidth, frameHeight, null);
                g2d.setFont(font);
                g2d.setColor(Color.PINK);
                g2d.drawString("Ấn chuột trái để bắn.", frameWidth / 2 - 200, (int) (frameHeight * 0.65) + 1);
                g2d.drawString("Ấn chuột trái để bắt đầu game.", frameWidth / 2 - 200, (int) (frameHeight * 0.68) + 1);
                g2d.drawString("Ấn nút ESC để thoát game.", frameWidth / 2 - 200, (int) (frameHeight * 0.71) + 1);
                g2d.drawString("Ấn nút SPACE để tạm dừng.", frameWidth / 2 - 200, (int) (frameHeight * 0.74) + 1);
                g2d.drawString("Nếu 10 con vịt chạy thoát, trờ chơi sẽ kết thúc.", frameWidth / 2 - 200, (int) (frameHeight * 0.77) + 1);
                g2d.setColor(Color.red);
                g2d.drawString("Ấn chuột trái để bắn.", frameWidth / 2 - 200, (int) (frameHeight * 0.65));
                g2d.drawString("Ấn chuột trái để bắt đầu game.", frameWidth / 2 - 200, (int) (frameHeight * 0.68));
                g2d.drawString("Ấn nút ESC để thoát game.", frameWidth / 2 - 200, (int) (frameHeight * 0.71));
                g2d.drawString("Ấn nút SPACE để tạm dừng.", frameWidth / 2 - 200, (int) (frameHeight * 0.74));
                g2d.drawString("Nếu 10 con vịt chạy thoát, trờ chơi sẽ kết thúc.", frameWidth / 2 - 200, (int) (frameHeight * 0.77));
                break;
            case PAUSE:
                game.DrawForPause(g2d, mousePosition());
                //...
                break;
            case GAME_CONTENT_LOADING:
                g2d.setColor(Color.white);
                g2d.drawString("GAME is LOADING", frameWidth / 2 - 50, frameHeight / 2);
                break;
        }
    }

    /**
     * Bắt đầu game mới.
     */
    private void newGame() {
        // Thiết lập gameTime từ 0 đến lastTime tới cuối cùng để thực hiện các tính toán sau này.
        gameTime = 0;
        lastTime = System.nanoTime();

        game = new Game();
    }

    /**
     * Khởi động lại trò chơi - thiết lập lại thời gian chơi và gọi RestartGame
     * () của đối tượng trò chơi để thiết lập lại một số biến.
     */
    private void restartGame() {
        gameTime = 0;
        lastTime = System.nanoTime();

        game.RestartGame();

        // Thay đổi trạng thái trò chơi để các trò chơi có thể bắt đầu.
        gameState = GameState.PLAYING;
    }

    /**
     * Trả về vị trí của con trỏ chuột trong trò chơi khung hình / cửa sổ. Nếu
     * vị trí chuột null thì trả về tọa độ 0;0
     *
     * @return tọa độ chuột.
     */
    private Point mousePosition() {
        try {
            Point mp = this.getMousePosition();

            if (mp != null) {
                return this.getMousePosition();
            } else {
                return new Point(0, 0);
            }
        } catch (Exception e) {
            return new Point(0, 0);
        }
    }

    /**
     * Phương thức này đc gọi khi nhả chuột
     *
     * @param e KeyEvent
     */
    @Override
    public void keyReleasedFramework(KeyEvent e) {
        switch (gameState) {
            case GAMEOVER:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                    restartGame();
                }
                break;
            case PLAYING:
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameState = GameState.PAUSE;
                }
                break;
            case PAUSE: {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameState = GameState.PLAYING;
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                break;
            }

            case MAIN_MENU:
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                break;
        }
    }

    /**
     * Phương thức đc gọi khi click chuột.
     *
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        switch (gameState) {
            case MAIN_MENU:
                if (e.getButton() == MouseEvent.BUTTON1) {
                    newGame();
                }
                break;
        }
    }
}
