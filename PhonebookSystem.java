import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class PhonebookSystem extends JFrame implements ActionListener {
	
	private JTextField eventField, nameField, phoneField;
	private JTextArea displayArea;
	private String filename = "phonebook.txt", selectedContact = "";
	
	public PhonebookSystem() {
		//===================Main Frame ng GUI=================//
		setTitle("Phonebook System - Dark Mode");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 400);
		setLayout(new BorderLayout());
		
		//===================Colors for Dark Mode=================//
		Color bgColor = new Color(45, 45, 45);
		Color fgColor = Color.WHITE;
		Color buttonColor = new Color(70, 70, 70);
		
		//===================Font Settings=================//
		Font labelFont = new Font("Arial", Font.BOLD, 14);
		Font fieldFont = new Font("Consolas", Font.PLAIN, 12);
		Font buttonFont = new Font("Verdana", Font.BOLD, 12);
		
		//===================Initialize ung mga variables/objects=================//
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
		
		//===================will hold the header and input panels=================//
		JPanel topPanels = new JPanel(new BorderLayout());
		topPanels.setBackground(bgColor);
		add(topPanels, BorderLayout.NORTH);
		
		// para sa display ng text about sa events or changes na nangyayari //
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(bgColor);
		headerPanel.add(eventField);
		topPanels.add(headerPanel, BorderLayout.NORTH);
		
		//===================Name and Phone Number=================//
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
		
		//===================Scroll bars para sa display ng mga name and their corresponding phone numbers=================//
		JScrollPane scrollPane = new JScrollPane(displayArea);
		scrollPane.getViewport().setBackground(bgColor);
		add(scrollPane, BorderLayout.CENTER);
		
		//===================Buttons=================//
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(bgColor);
		String[] buttons = {"Add", "Search", "Delete", "Select", "Update"};
		for (String button : buttons) {
			JButton b = new JButton(button);
			b.setBackground(buttonColor);
			b.setForeground(fgColor);
			b.setFont(buttonFont);
			b.setFocusPainted(false); // Remove focus border for a cleaner look
			b.addActionListener(this);
			buttonPanel.add(b);
		}
		add(buttonPanel, BorderLayout.SOUTH);
		
		setVisible(true);
		loadContacts();
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		// e-clear lang yung nakasulat
		eventField.setText("");
		
		//===================DATA VALIDATION=====================//
		// Kung may empty kahit isa sa name or phone number. Disregard lang yung command
		if (nameField.getText().isEmpty() || phoneField.getText().isEmpty()) {
			eventField.setText("Input field(s) are empty");
			return;
		}
		// Kung kulang ang input sa phone number
		else if (phoneField.getText().length() != 11 || !phoneField.getText().startsWith("09")) {
			eventField.setText("Phone number must be 11 digits long starting from 09");
			return;
		}
		// Check kung lahat ba ng input sa phone number ay digits
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
			//===================Read FROM File======================//
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
		// update yung display area gamit ang variable contacts na nag hohold ng lahat ng data, automatic na ang line sepratation dahil may line separator na dinagdag sa while loop
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
			
			//===================Read FROM File======================//
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");

				// check kung equal ba yung line sa file sa hinahanap idadagdag natin na bagong contact
				if (splitLine[0].equals(name) && splitLine[1].equals(phone)) { // kung true, ibig sabihin may existing contact na tayo na same sa idadagdag natin
					eventField.setText("Contact Already Exist");
					reader.close();
					return; // abort/stop na agad ang method
				}
				contacts += line + System.lineSeparator();
			}

			// kung wala pang existing contact na same sa idadagdag ay idagdag yung bagong contact
			eventField.setText("Contact Added");
			contacts += name + ":" + phone;
			
			//===================Write INTO File======================//
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

		// e clear yung variable na ito
		selectedContact = "";
		
		try {
			File file = new File(filename);
			
			if (!file.exists()) {
			    file.createNewFile();
			}
			
			//===================Read FROM File======================//
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");
				
				// check kung merong existing contact na same sa hinahanap natin
				if (splitLine[0].equals(name) && splitLine[1].equals(phone)) {
					alreadyExists = true;
					continue; // kung meron ay e skip yung line na yun para hindi idagdag sa contacts na variable
				}
				contacts += line + System.lineSeparator();
			}
			
			if (alreadyExists) { // dinagdag yung sinearch na contact sa pinakaunahan para pinakaunang lalabas sa display field
				eventField.setText("Contact Searched");
				contacts = name + ":" + phone + System.lineSeparator() + contacts;
			} // kung wala abort/stop na lang ang method
			else {
				eventField.setText("Contact does not exist");
				reader.close();
				return;
			}
			
			//===================Write INTO File======================//
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
			
			//===================Read FROM File======================//
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");
				
				// check kung merong existing contact na same sa hinahanap natin
				if (splitLine[0].equals(name) && splitLine[1].equals(phone)) {
					contactExist = true;
					continue; // simple skip the line para hindi idagdag sa contacts variable. Deleted na yun agad dito kasi di na natin sinama sa final contacts list.
				}
				contacts += line + System.lineSeparator();
			}
			
			if (contactExist) {
				eventField.setText("Contact deleted");
			}
			else {
				eventField.setText("Contact does not exist");
			}
			
			//===================Write INTO File======================//
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
			
			//===================Read FROM File======================//
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");
				
				// check kung merong existing contact na same sa hinahanap natin
				if (splitLine[0].equals(name) && splitLine[1].equals(phone)) {
					searchContact(name, phone); // cinall yung isang method para e display yung selected contact sa pinaka taas or pinaka una
					selectedContact = name + ":" + phone; // then inassign na yung contact na yun sa selected contact na variable
					eventField.setText("Contact selected: " + name + ":" + phone);
					return;
				}
			}
			// kung di natin mahanap yung contact na yun walang gagawin sa file or di-display na iba aside from "Contact does not exist".
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
			
			//===================Read FROM File======================//
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			while ((line = reader.readLine()) != null) {
				splitLine = line.split(":");
				splitSelectedContact = selectedContact.split(":");
				
				// hahanapin yung existing contact na same sa e a-update natin
				if (splitLine[0].equals(splitSelectedContact[0]) && splitLine[1].equals(splitSelectedContact[1])) {
					// set natin yung text sa event field into "(selected contact) updated to (new contact)"
					eventField.setText(
							"Contact: " + 
							splitSelectedContact[0] + ":" + splitSelectedContact[1] + 
							" updated to " + name + ":" + phone
					);
					// then e assign na yung bagong contact sa selectedContact
					selectedContact = name + ":" + phone;
					// e skip yung line na yun at hindi idadagdag sa final output yung previous na contact kasi binago na natin
					continue;
				}
				
				contacts += line + System.lineSeparator();
			}

			// dito na natin nilagay yung bagong data sa unahan, pinaka unang lalabas siya.
			// So yung naka display kaninang selected na contact ay mapapaltan na nung bagong contact
			contacts = selectedContact + System.lineSeparator() + contacts;
			selectedContact = "";
			
			//===================Write INTO File======================//
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