package shoot_the_duck;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * 
 * 
 * @author PRINCE D. TOAD
 */

public class Window extends JFrame{
        
    private Window() // chạy  game
    {
        // tạo tiêu đề game
        this.setTitle("Game bắn vịt");
        // tạo size của khung hình
        if(false){ // toàn màn hình
            // ẩn khung hình và tiêu đề
            this.setUndecorated(true);
            // full màn hình
            this.setExtendedState(this.MAXIMIZED_BOTH);
        } else{ // tao size cho khung hình
            // set size
            this.setSize(800, 600);
            // đặt khung hình vô trung tâm của màn hình
            this.setLocationRelativeTo(null);
            // ngăn người dùng thay đổi kích thước khung hình
            this.setResizable(false);
        }
        
        // tắt ứng dụng khi đóng màn hình
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // đưa đối tượng đồ họa vào
        this.setContentPane(new Framework());
        
        this.setVisible(true);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Window();
            }
        });
    }
}
