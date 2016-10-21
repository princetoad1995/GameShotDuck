package shoot_the_duck;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Actual game.
 *
 * @author PRINCE D. TOAD
 */
public class Game {

    /**
     * Tạo 1 số ngẫu nhiên.
     */
    private Random random;

    /**
     * Font chữ viết lên màn hình.
     */
    private Font font;

    /**
     * Mảng chua ảnh vịt.
     */
    private ArrayList<Duck> ducks;

    /**
     * Số vịt ra màn hình còn sống
     */
    private int runawayDucks;

    /**
     * Số vịt bị bắn chết
     */
    private int killedDucks;

    /**
     * Đối với mỗi con vịt chết, người chơi sẽ nhận điểm.
     */
    private int score;

    /**
     * Số lần người chơi bắn
     */
    private int shoots;

    /**
     * Thời gian trôi qua khi bắn.
     */
    private long lastTimeShoot;
    /**
     * Thời gian trôi qua giữa các bức ảnh
     */
    private long timeBetweenShots;

    /**
     * Game background image.
     */
    private BufferedImage backgroundImg;

    /**
     * Ảnh cỏ.
     */
    private BufferedImage grassImg;

    /**
     * ảnh vịt.
     */
    private BufferedImage duckImg;

    /**
     * ảnh nòng súng.
     */
    private BufferedImage sightImg;

    /**
     * chiều rộng giữa các hình ảnh cảnh.
     */
    private int sightImgMiddleWidth;
    /**
     * chiều cao giữa các hình ảnh cảnh.
     */
    private int sightImgMiddleHeight;

    public Game() {
        Framework.gameState = Framework.GameState.GAME_CONTENT_LOADING;

        Thread threadForInitGame = new Thread() {
            @Override
            public void run() {
                // Đặt các biến và các đối tượng cho các trò chơi.
                Initialize();
                // Tải dữ liệu game files (images, sounds, ...)
                LoadContent();

                Framework.gameState = Framework.GameState.PLAYING;
            }
        };
        threadForInitGame.start();
    }

    /**
     * Thiết lập các biến và các đối tượng cho các trò chơi.
     */
    private void Initialize() {
        random = new Random();
        font = new Font("monospaced", Font.BOLD, 18);

        ducks = new ArrayList<Duck>();

        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        shoots = 0;

        lastTimeShoot = 0;
        timeBetweenShots = Framework.secInNanosec / 3;
    }

    /**
     * Load game files - images, sounds, ...
     */
    private void LoadContent() {
        try {
            URL backgroundImgUrl = this.getClass().getResource("/shoot_the_duck/resources/images/background.jpg");
            backgroundImg = ImageIO.read(backgroundImgUrl);

            URL grassImgUrl = this.getClass().getResource("/shoot_the_duck/resources/images/grass.png");
            grassImg = ImageIO.read(grassImgUrl);

            URL duckImgUrl = this.getClass().getResource("/shoot_the_duck/resources/images/duck3.png");
            duckImg = ImageIO.read(duckImgUrl);

            URL sightImgUrl = this.getClass().getResource("/shoot_the_duck/resources/images/sight.png");
            sightImg = ImageIO.read(sightImgUrl);
            sightImgMiddleWidth = sightImg.getWidth() / 2;
            sightImgMiddleHeight = sightImg.getHeight() / 2;
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Khởi động lại trò chơi - thiết lập lại một số biến.
     */
    public void RestartGame() {
        // Loại bỏ tất cả những con vịt từ danh sách này.
        ducks.clear();

        // Thiết lập thời gian cuối = 0
        Duck.lastDuckTime = 0;

        runawayDucks = 0;
        killedDucks = 0;
        score = 0;
        shoots = 0;

        lastTimeShoot = 0;
    }

    /**
     * Cập nhật trò chơi logic.
     *
     * @param gameTime gameTime của trò chơi.
     * @param mousePosition vị trí chuột hiện tại.
     */
    public void UpdateGame(long gameTime, Point mousePosition) {
        // Tạo ra một con vịt mới, nếu đó là thời gian, và thêm nó vào ArrayList.
        if (System.nanoTime() - Duck.lastDuckTime >= Duck.timeBetweenDucks) {
            // Ở đây chúng ta tạo ra vịt mới và thêm nó vào ArrayList.
            ducks.add(new Duck(Duck.duckLines[Duck.nextDuckLines][0] + random.nextInt(200), Duck.duckLines[Duck.nextDuckLines][1], Duck.duckLines[Duck.nextDuckLines][2], Duck.duckLines[Duck.nextDuckLines][3], duckImg));

            // Ở đây chúng ta tăng nextDuckLines để vịt tiếp theo sẽ được tạo ra trong dòng tiếp theo.
            Duck.nextDuckLines++;
            if (Duck.nextDuckLines >= Duck.duckLines.length) {
                Duck.nextDuckLines = 0;
            }

            Duck.lastDuckTime = System.nanoTime();
        }

        // Cập nhật tất cả vịt
        for (int i = 0; i < ducks.size(); i++) {
            // di chuyển vịt
            ducks.get(i).Update();

            // Kiểm tra nếu vịt rời khỏi màn hình và loại bỏ nó nếu nó không.
            if (ducks.get(i).x < 0 - duckImg.getWidth()) {
                ducks.remove(i);
                runawayDucks++;
            }
        }

        // Bắn như thế nào ?
        if (Canvas.mouseButtonState(MouseEvent.BUTTON1)) {
            // Kiểm tra nếu nó có thể bắn một lần nữa.
            if (System.nanoTime() - lastTimeShoot >= timeBetweenShots) {
                shoots++;

                // Để vịt đi qua và mất vịt khi bị bắn
                for (int i = 0; i < ducks.size(); i++) {
                    // Ta kiểm tra, nếu con chuột đã được hơn vịt đầu hoặc cơ thể, khi người chơi đã bắn.
                    if (new Rectangle(ducks.get(i).x + 18, ducks.get(i).y + 5, 45, 27).contains(mousePosition)
                            || new Rectangle(ducks.get(i).x + 28, ducks.get(i).y + 33, 88, 30).contains(mousePosition)) {

                        killedDucks++;
                        score += ducks.get(i).score;

                        // Xóa vịt khỏi mảng
                        ducks.remove(i);

                        break;
                    }
                }

                lastTimeShoot = System.nanoTime();
            }
        }

        // Khi 10 con vịt chạy trốn, trò chơi kết thúc.
        if (runawayDucks >= 10) {
            Framework.gameState = Framework.GameState.GAMEOVER;
        }
    }

    /**
     * Vẽ các trò chơi để màn hình.
     *
     * @param g2d Graphics2D
     * @param mousePosition vị trí của chuột.
     */
    public void Draw(Graphics2D g2d, Point mousePosition) {
        g2d.drawImage(backgroundImg, 0, 0, Framework.frameWidth, Framework.frameHeight, null);

        // Ở đây chúng ta vẽ tất cả những con vịt.
        for (int i = 0; i < ducks.size(); i++) {
            ducks.get(i).Draw(g2d);
            //Hinh chu nhat chua con vit
//            g2d.drawRect(ducks.get(i).x + 18, ducks.get(i).y + 5, 45, 27);
//            g2d.drawRect(ducks.get(i).x + 28, ducks.get(i).y + 33, 88, 30);
        }

        g2d.drawImage(grassImg, 0, Framework.frameHeight - grassImg.getHeight(), Framework.frameWidth, grassImg.getHeight(), null);

        g2d.drawImage(sightImg, mousePosition.x - sightImgMiddleWidth, mousePosition.y - sightImgMiddleHeight, null);

        g2d.setFont(font);
        g2d.setColor(Color.darkGray);

        g2d.drawString("CHẠY THOÁT:" + runawayDucks, 10, 21);
        g2d.drawString("BẮN TRÚNG:" + killedDucks, 160, 21);
        g2d.drawString("SỐ LẦN BẮN:" + shoots, 299, 21);
        g2d.drawString("ĐIỂM:" + score, 460, 21);
    }

    public void DrawForPause(Graphics2D g2d, Point mousePosition) {
        Draw(g2d, mousePosition);
        g2d.setColor(Color.BLUE);
        g2d.drawString("Nhấn SPACE để tiếp tục chơi", Framework.frameWidth / 2 - 150, (int)(Framework.frameHeight * 0.55) + 1);
        g2d.drawString("Nhấn ESC để thoát trò chơi", Framework.frameWidth / 2 - 150, (int)(Framework.frameHeight * 0.60) + 1);
        g2d.setColor(Color.white);
        g2d.drawString("Nhấn SPACE để tiếp tục chơi", Framework.frameWidth / 2 - 150, (int)(Framework.frameHeight * 0.55) );
        g2d.drawString("Nhấn ESC để thoát trò chơi", Framework.frameWidth / 2 - 150, (int)(Framework.frameHeight * 0.60) );
        
    }
    /**
     * Vẽ các trò chơi trên màn hình.
     *
     * @param g2d Graphics2D
     * @param mousePosition vị trí của chuột.
     */
    public void DrawGameOver(Graphics2D g2d, Point mousePosition) {
        Draw(g2d, mousePosition);

        // Chữ hiện ra khi gameover
        g2d.setColor(Color.PINK);
        g2d.drawString("Trò chơi kết thúc", Framework.frameWidth / 2 - 150, (int) (Framework.frameHeight * 0.65) + 1);
        g2d.drawString("Ấn nút cách hoặc enter để chơi lại.", Framework.frameWidth / 2 - 150, (int) (Framework.frameHeight * 0.70) + 1);
        g2d.setColor(Color.red);
        g2d.drawString("Trò chơi kết thúc", Framework.frameWidth / 2 - 150, (int) (Framework.frameHeight * 0.65));
        g2d.drawString("Ấn nút cách hoặc enter để chơi lại.", Framework.frameWidth / 2 - 150, (int) (Framework.frameHeight * 0.70));
    }
}
