import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class PhonebookSystem extends JFrame implements ActionListener {
	
	private JTextField eventField, nameField, phoneField;
	private JTextArea displayArea;
	private String filename = "phonebook.txt", selectedContact = "";
	
	public PhonebookSystem() {
		setTitle("Phonebook System - Dark Mode");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 400);
		setLayout(new BorderLayout());
		
		Color bgColor = new Color(45, 45, 45);
		Color fgColor = Color.WHITE;
		Color buttonColor = new Color(70, 70, 70);
		
		Font labelFont = new Font("Arial", Font.BOLD, 14);
		Font fieldFont = new Font("Consolas", Font.PLAIN, 12);
		Font buttonFont = new Font("Verdana", Font.BOLD, 12);
		
		eventField = new JTextField();
		eventField.setEditable(false);
		eventField.setBackground(bgColor);
		eventField.setForeground(fgColor);
		eventField.setFont(fieldFont);
		
		nameField = new JTextField();
		nameField.setBackground(bgColor);
		nameField.setForeground(fgColor);
		nameField.setCaretColor(fgColor);
		nameField.setFont(fieldFont);
		
		phoneField = new JTextField();
		phoneField.setBackground(bgColor);
		phoneField.setForeground(fgColor);
		phoneField.setCaretColor(fgColor);
		phoneField.setFont(fieldFont);
		
		displayArea = new JTextArea();
		displayArea.setEditable(false);
		displayArea.setBackground(bgColor);
		displayArea.setForeground(fgColor);
		displayArea.setCaretColor(fgColor);
		displayArea.setFont(fieldFont);
	
		JPanel topPanels = new JPanel(new BorderLayout());
		topPanels.setBackground(bgColor);
		add(topPanels, BorderLayout.NORTH);
		
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(bgColor);
		headerPanel.add(eventField);
		topPanels.add(headerPanel, BorderLayout.NORTH);
		
		JPanel inputPanel = new JPanel(new GridLayout(2, 2));
		inputPanel.setBackground(bgColor);
		
		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setForeground(fgColor);
		nameLabel.setFont(labelFont);
		inputPanel.add(nameLabel);
		inputPanel.add(nameField);
		
		JLabel phoneLabel = new JLabel("Phone:");
		phoneLabel.setForeground(fgColor);
		phoneLabel.setFont(labelFont);
		inputPanel.add(phoneLabel);
		inputPanel.add(phoneField);
		topPanels.add(inputPanel, BorderLayout.SOUTH);
		
		JScrollPane scrollPane = new JScrollPane(displayArea);
		scrollPane.getViewport().setBackground(bgColor);
		add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(bgColor);
		String[] buttons = {"Add", "Search", "Delete", "Select", "Update"};
		for (String button : buttons) {
			JButton b = new JButton(button);
			b.setBackground(buttonColor);
			b.setForeground(fgColor);
			b.setFont(buttonFont);
			b.setFocusPainted(false); 
			b.addActionListener(this);
			buttonPanel.add(b);
		}
		add(buttonPanel, BorderLayout.SOUTH);
		
		setVisible(true);
		loadContacts();
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		eventField.setText("");
		
		if (nameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
			eventField.setText("Input field(s) are empty");
			return;
		}

		else if (phoneField.getText().length() != 11 || !phoneField.getText().startsWith("09")) {
			eventField.setText("Phone number must be 11 digits long starting from 09");
			return;
		}
		for (char c : phoneField.getText().toCharArray()) {
			if (!Character.isDigit(c)) {
				eventField.setText("Phone number must contain only digits");
				return;
			}
		}
		
		if (command.equals("Add")) {
			addContact(nameField.getText(), phoneField.getText());
		}
		else if (command.equals("Search")) {
			searchContact(nameField.getText(), phoneField.getText());
		}
		else if (command.equals("Delete")) {
			deleteContact(nameField.getText(), phoneField.getText());
		}
		else if (command.equals("Select")) {
			selectContact(nameField.getText(), phoneField.getText());
		}
		else if (command.equals("Update")) {
			updateContact(nameField.getText(), phoneField.getText());
		}
	}
	
	public void loadContacts() {
		String line;
		String contacts = "";
		
		try {
			File file = new File(filename);
			// Check kung nag e-exist na yung file natin, kung indi gagawin.
			if (!file.exists()) {
			    file.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			while ((line = reader.readLine()) != null) {
				// e load lang lahat ng data from the file into a single String variable na separated by line separator (\r\n)
				contacts += line + System.lineSeparator();
			}
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		displayArea.setText(contacts);
	}
	
	public void addContact(String name, String phone) {
		
		String line;
		String contacts = "";
		String[] splitLine;
		
		// e clear yung variable na ito
		selectedContact = "";
		
		try {
			File file = new File(filename);

			if (!file.exists()) {
			    file.createNewFile();
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");

				if (splitLine[0].equals(name) && splitLine[1].equals(phone)) { 
					eventField.setText("Contact Already Exist");
					reader.close();
					return; 
				}
				contacts += line + System.lineSeparator();
			}

			eventField.setText("Contact Added");
			contacts += name + ":" + phone;
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(contacts);
			
			reader.close();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		loadContacts();
	}
	
	public void searchContact(String name, String phone) {
		
		String line;
		String contacts = "";
		String[] splitLine;
		boolean alreadyExists = false;

		selectedContact = "";
		
		try {
			File file = new File(filename);
			
			if (!file.exists()) {
			    file.createNewFile();
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");
				
				if (splitLine[0].equals(name) && splitLine[1].equals(phone)) {
					alreadyExists = true;
					continue;
				}
				contacts += line + System.lineSeparator();
			}
			
			if (alreadyExists) { 
				eventField.setText("Contact Searched");
				contacts = name + ":" + phone + System.lineSeparator() + contacts;
			} 
			else {
				eventField.setText("Contact does not exist");
				reader.close();
				return;
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(contacts);
			
			reader.close();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		loadContacts();
	}

	public void deleteContact(String name, String phone) {
		
		String line;
		String contacts = "";
		String[] splitLine;
		boolean contactExist = false;
		
		// e clear yung variable na ito
		selectedContact = "";
		
		try {
			File file = new File(filename);
			
			if (!file.exists()) {
			    file.createNewFile();
			}
			
			
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");
				
				
				if (splitLine[0].equals(name) && splitLine[1].equals(phone)) {
					contactExist = true;
					continue;
				}
				contacts += line + System.lineSeparator();
			}
			
			if (contactExist) {
				eventField.setText("Contact deleted");
			}
			else {
				eventField.setText("Contact does not exist");
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(contacts);
			
			reader.close();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		loadContacts();
	}
	
	public void selectContact(String name, String phone) {
		
		String line;
		String[] splitLine;

		try {
			File file = new File(filename);
			
			if (!file.exists()) {
			    file.createNewFile();
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");
				
				if (splitLine[0].equals(name) && splitLine[1].equals(phone)) {
					searchContact(name, phone);
					selectedContact = name + ":" + phone; 
					eventField.setText("Contact selected: " + name + ":" + phone);
					return;
				}
			}
			eventField.setText("Contact does not exist");
			reader.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateContact(String name, String phone) {
		
		String line;
		String contacts = "";
		String[] splitLine;
		String[] splitSelectedContact;

		// dapat may selected contact kung mag a update tayo
		if (selectedContact.isEmpty()) {
			eventField.setText("No contact selected - Please select a contact");
			return;
		}
		
		try {
			File file = new File(filename);
			
			if (!file.exists()) {
			    file.createNewFile();
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");
				splitSelectedContact = selectedContact.split(":");
				
			
				if (splitLine[0].equals(splitSelectedContact[0]) && splitLine[1].equals(splitSelectedContact[1])) {
					eventField.setText(
							"Contact: " + 
							splitSelectedContact[0] + ":" + splitSelectedContact[1] + 
							" updated to " + name + ":" + phone
					);
					selectedContact = name + ":" + phone;
					continue;
				}
				
				contacts += line + System.lineSeparator();
			}
			contacts = selectedContact + System.lineSeparator() + contacts;
			selectedContact = "";
		
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(contacts);
			
			reader.close();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		loadContacts();
	}
	
	public static void main(String[] args) {
		new PhonebookSystem();
	}
}
