package shoot_the_duck;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * 
 * @author PRINCE D. TOAD
 */

public abstract class Canvas extends JPanel implements KeyListener, MouseListener {
    // Nơi lưu trữ các sự kiện ấn phím
    private static boolean[] keyboardState = new boolean[525];
    // Nơi lưu trữ các sự kiện click chuột
     private static boolean[] mouseState = new boolean[3];
     
    public Canvas(){
        // Sử dụng bộ đệm đôi để vẽ lên màn hình
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.setBackground(Color.black);
        
        // vẽ con trỏ chuột của mình
        if(true)
        {
            BufferedImage blankCursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(blankCursorImg, new Point(0, 0), null);
            this.setCursor(blankCursor);
        }
        
        // Thêm sự kiện lắng nghe bàn phím
        this.addKeyListener(this);
        // Thêm sự kiện lắng nghe click chuột
        this.addMouseListener(this);
    }
     
    // Phương pháp này được ghi đè trong Framework.java và được sử dụng để vẽ lên màn hình.
    public abstract void Draw(Graphics2D g2d); 
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D)g;        
        super.paintComponent(g2d);        
        Draw(g2d);
    }
    
    // trong sự kiện lắng nghe ấn phím
    /**
     * Is keyboard key "key" down?
     * 
     * @param key Number of key for which you want to check the state.
     * @return true if the key is down, false if the key is not down.
     */
    
    public static boolean keyboardKeyState(int key)
    {
        return keyboardState[key];
    }
    
    // Phương thức khi lắng nghe ấn phím
    @Override
    public void keyPressed(KeyEvent e) // Khi ấn phím
    {
        keyboardState[e.getKeyCode()] = true;
    }
    
    @Override
    public void keyReleased(KeyEvent e) // khi nhả phím
    {
        keyboardState[e.getKeyCode()] = false;
        keyReleasedFramework(e);
    }
    
    @Override 
    public void keyTyped(KeyEvent e) { }
    
    public abstract void keyReleasedFramework(KeyEvent e);
    
    // Sự kiện click chuột
    /**
     * Is mouse button "button" down?
     * Parameter "button" can be "MouseEvent.BUTTON1" - Indicates mouse button #1
     * or "MouseEvent.BUTTON2" - Indicates mouse button #2 ...
     * 
     * @param button Number of mouse button for which you want to check the state.
     * @return true if the button is down, false if the button is not down.
     */
    
    public static boolean mouseButtonState(int button)
    {
        return mouseState[button - 1];
    }
    
    // Thiết lập tình trạng click chuột
    private void mouseKeyStatus(MouseEvent e, boolean status)
    {
        if(e.getButton() == MouseEvent.BUTTON1)
            mouseState[0] = status;
        else if(e.getButton() == MouseEvent.BUTTON2)
            mouseState[1] = status;
        else if(e.getButton() == MouseEvent.BUTTON3)
            mouseState[2] = status;
    }
    
    // Phương thức lắng nghe click chuột
    @Override
    public void mousePressed(MouseEvent e) // ấn chuột
    {
        mouseKeyStatus(e, true);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) // thả ấn chuột
    {
        mouseKeyStatus(e, false);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) { }
    
    @Override
    public void mouseEntered(MouseEvent e) { }
    
    @Override
    public void mouseExited(MouseEvent e) { }
    
}
