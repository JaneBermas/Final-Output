import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Calculator extends JFrame implements ActionListener {
    
    //===================Text Displays======================//
    private JTextField textDisplay;
    private JTextArea historyDisplay;
    //===================Functionality Variables======================//
    private double input1, input2, resultingValue;
    private String operator;
    private boolean done;
    
    public Calculator() {
        //===================GUI Main Frame======================//
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30)); // Dark background color
        
        //===================GUI Upper Text Display======================//
        textDisplay = new JTextField();
        textDisplay.setEditable(false);
        textDisplay.setFont(new Font("Arial", Font.BOLD, 30));
        textDisplay.setHorizontalAlignment(JTextField.RIGHT);
        textDisplay.setBackground(new Color(50, 50, 50)); // Darker background
        textDisplay.setForeground(Color.white);
        textDisplay.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(textDisplay, BorderLayout.NORTH);
        
        //===================GUI Buttons ======================//
        JPanel buttonGroup = new JPanel();
        buttonGroup.setBackground(new Color(30, 30, 30));
        buttonGroup.setLayout(new GridLayout(5, 4, 10, 10)); // Spacing between buttons
        buttonGroup.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Padding
        
        String[] buttons = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "C"
        };
        
        for (String button : buttons) {
            JButton b = new JButton(button);
            b.setFont(new Font("Arial", Font.BOLD, 24));
            b.setBackground(new Color(70, 70, 70)); // Dark gray background
            b.setForeground(Color.white);
            b.setFocusPainted(false); // Remove focus border
            b.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50), 5));
            b.setPreferredSize(new Dimension(80, 80));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Change cursor to hand on hover
            b.addActionListener(this);
            buttonGroup.add(b);
        }
        
        add(buttonGroup, BorderLayout.CENTER);
        
        //===================GUI Left History Text Area======================//
        JPanel historyPanel = new JPanel();
        historyPanel.setBackground(new Color(30, 30, 30));
        historyPanel.setLayout(new BorderLayout());
        
        historyDisplay = new JTextArea(10, 30);
        historyDisplay.setEditable(false);
        historyDisplay.setFont(new Font("Courier New", Font.PLAIN, 14));
        historyDisplay.setBackground(new Color(50, 50, 50)); // Darker background for the history
        historyDisplay.setForeground(Color.white);
        historyDisplay.setText("History:\n");
        
        JScrollPane scrollPane = new JScrollPane(historyDisplay);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(historyPanel, BorderLayout.WEST);
        
        //===================GUI Frame Visibility======================//
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String calcuInput = e.getActionCommand();
        
        //===================If There is No Input And The First Input is 0======================//
        if (textDisplay.getText().length() <= 0 && calcuInput.equals("0")) {
            return;
        }
        
        //===================Reset Text Area And Values of Input1 And Input2 Variables======================//
        if (calcuInput.equals("C")) {
            input1 = 0;
            input2 = 0;
            textDisplay.setText("");
        }
        //===================If Input is Digit or Dot (.)======================//
        else if (Character.isDigit(calcuInput.charAt(0)) || calcuInput.equals(".")) {
            //===================To Avoid Writing The Next Input Into The Previous Output======================//
            if (done) {
                textDisplay.setText("");
                done = false;
            }
            
            //===================If input is Dot (.) but there is already a Dot======================//
            if (calcuInput.equals(".") && textDisplay.getText().contains(".")) {
                return;
            }
            
            //===================Add the NEW Number or Dot (.) Input Into The Previous======================//
            textDisplay.setText(textDisplay.getText() + calcuInput);
        }
        //===================If Input Is Equal Sign (=)======================//
        else if (calcuInput.equals("=")) {
            input2 = Double.parseDouble(textDisplay.getText());
            calculate();
            textDisplay.setText(String.valueOf(resultingValue));
            
            done = true;
            
            //===================Add to History======================//
            try {
                String history = historyRecorder(
                        String.valueOf(input1) + " " +
                        operator + " " +
                        String.valueOf(input2) + " = " +
                        String.valueOf(resultingValue)
                        );
                historyDisplay.setText("History:\n" + history);
            }
            catch (IOException e1) {
                e1.printStackTrace(); // Handle IOException
            }
        }
        //===================If Input is NOT A Digit, Dot (.) or Equal Sign (=)======================//
        else {
            operator = calcuInput;
            input1 = Double.parseDouble(textDisplay.getText());
            textDisplay.setText("");
            
            done = false;
        }
    }
    
    public void calculate() {
        switch (operator) {
            case "+":
                resultingValue = input1 + input2;
                break;
            case "-":
                resultingValue = input1 - input2;
                break;
            case "*":
                resultingValue = input1 * input2;
                break;
            case "/":
                resultingValue = input1 / input2;
                break;
        }
    }

    public String historyRecorder(String record) throws IOException {
        //===================Reading FROM File======================//
        BufferedReader reader = new BufferedReader(new FileReader("calculator_history.txt"));
        
        String line;
        String history = "";
        
        while ((line = reader.readLine()) != null) {
            history += line + System.lineSeparator();
        }
        
        history += record;
        
        //===================Writing INTO File======================//
        BufferedWriter writer = new BufferedWriter(new FileWriter("calculator_history.txt"));
        writer.write(history);
        
        reader.close();
        writer.close();
        
        return history;
    }
    
    public static void main(String[] args) throws IOException {
        //===================To Clear The Contents of The History File======================//
        BufferedWriter writer = new BufferedWriter(new FileWriter("calculator_history.txt"));
        writer.write("");
        writer.close();
        
        new Calculator();
    }
}
