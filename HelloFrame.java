import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelloFrame  extends JFrame {
    public HelloFrame() {
        super("My First Swing App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel greenPanel = new JPanel();
        greenPanel.setBounds(40,150,220,70);
        greenPanel.setLayout(new BorderLayout());

        JLabel lblName= new JLabel();
        lblName.setText("Your Name");
        lblName.setBounds(40,20, 100,30);
        add(lblName);

        JTextField inputName= new JTextField();
        inputName.setBounds(160,20, 100,30);
        add(inputName);

        JLabel welcomeLabel= new JLabel();
        welcomeLabel.setBounds(50,20, 100,30);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setVerticalAlignment(SwingConstants.CENTER);
        Font font = new Font("Courier", Font.BOLD,12);
        welcomeLabel.setFont(font);
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(16f));

        JButton btnAccept=new JButton("Accept");
        btnAccept.setBounds(100,70,100,30);
        btnAccept.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                greenPanel.setBackground(Color.GREEN);
                welcomeLabel.setText("Welcome to SWING");
            }
        });
        greenPanel.add(welcomeLabel);
        add(greenPanel);
        add(btnAccept);

        setLayout(null);
        setVisible(true);
    }

    public static void main(final String[] args) {
        new HelloFrame();
    }
}
