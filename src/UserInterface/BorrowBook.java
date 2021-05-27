package UserInterface;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import ControlDataBase.ControlAuthor;
import ControlDataBase.ControlBook;
import ControlDataBase.ControlBorrow;
import ControlDataBase.ControlHave;
import ControlDataBase.ControlTag;
import ControlDataBase.MyConnection;
import Entity.Author;
import Entity.Book;
import Entity.Tag;
import Keeptrack.CurrentAccount;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JComboBox;

public class BorrowBook extends JFrame {
	private JPanel contentPane;
	private JTable table;
	private JScrollPane scrollPane1;
	private JComboBox comboBoxBook;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BorrowBook frame = new BorrowBook();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public BorrowBook() {

		this.setSize(720, 480);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.getContentPane().setLayout(null);
                this.setResizable(false);

		contentPane = new JPanel();
		contentPane.setBounds(0, 0, 720, 480);
                contentPane.setBackground(Color.BLACK);
		this.getContentPane().add(contentPane);
		contentPane.setLayout(null);

		JLabel lblBorrowBook = new JLabel("Borrow Book");
		lblBorrowBook.setHorizontalAlignment(SwingConstants.CENTER);
		lblBorrowBook.setBounds(292, 11, 200, 45);
                lblBorrowBook.setForeground(Color.WHITE);
                lblBorrowBook.setFont(new Font("Tahoma", Font.BOLD, 27));
		contentPane.add(lblBorrowBook);

		JButton btnAdd = new JButton("Borrow");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Book book = (Book) comboBoxBook.getSelectedItem();
				Date date = new Date();
				long now = date.getTime();
				long then = date.getTime();
				if (CurrentAccount.getCurrentAccount().getJob()[0].equals("Student")) {
					then += 3 * 24 * 3600 * 1000;
				} else
					then += 5 * 24 * 3600 * 1000;
				java.sql.Date borrowDate = new java.sql.Date(now);
				java.sql.Date returnDate = new java.sql.Date(then);

				int k = ControlBorrow.insert(CurrentAccount.getCurrentAccount().getAccount_ID(), book.getBook_ID(),
						borrowDate, returnDate);
				if (k == 1) {
					JOptionPane.showMessageDialog(null, "insert success", "Message", JOptionPane.WARNING_MESSAGE);

				} else {
					JOptionPane.showMessageDialog(null, "insert not success", "Message", JOptionPane.WARNING_MESSAGE);
				}
				load_Borrow();
			}
		});
		btnAdd.setBounds(44, 275, 170, 35);
                btnAdd.setBackground(Color.WHITE);
		contentPane.add(btnAdd);

		JButton btnReturn = new JButton("Return");
		btnReturn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DefaultTableModel d = (DefaultTableModel) table.getModel();
				int selectedRow = table.getSelectedRow();
				Book book = (Book) comboBoxBook.getSelectedItem();

				int k = ControlBorrow.returnBook(CurrentAccount.getCurrentAccount().getAccount_ID(), book.getBook_ID());
				load_Borrow();
			}
		});
		btnReturn.setBounds(44, 321, 170, 35);
                btnReturn.setBackground(Color.WHITE);
		contentPane.add(btnReturn);

		JButton btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				ChooseFunction chooseFunction = new ChooseFunction();
				chooseFunction.setTitle("Welcome");
				chooseFunction.setVisible(true);// making the frame visible
			}
		});
		btnBack.setBounds(555, 380, 118, 35);
                btnBack.setBackground(Color.WHITE);
		contentPane.add(btnBack);

		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DefaultTableModel d = (DefaultTableModel) table.getModel();
				int selectedRow = table.getSelectedRow();

				try {
					Connection conn = new MyConnection().getConnection();
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(
							"select * from book where Name = '" + d.getValueAt(selectedRow, 0).toString() + "'");
					rs.next();
					for (int i = 0; i < comboBoxBook.getItemCount(); i++) {
						Book temp = (Book) comboBoxBook.getItemAt(i);
						if (temp.getBook_ID().equals(rs.getString("Book_ID"))) {
							comboBoxBook.setSelectedIndex(i);
						}
					}

				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			}
		});
		table.setModel(
				new DefaultTableModel(new Object[][] {}, new String[] { "Book", " Borrow Date", "Return Date" }) {
					Class[] columnTypes = new Class[] { String.class, String.class, String.class };

					public Class getColumnClass(int columnIndex) {
						return columnTypes[columnIndex];
					}
				});

		scrollPane1 = new JScrollPane(table);
		scrollPane1.setBounds(276, 56, 398, 308);
		contentPane.add(scrollPane1);

		JLabel lblBook = new JLabel("Book");
		lblBook.setBounds(44, 82, 46, 14);
                lblBook.setForeground(Color.WHITE);
                lblBook.setFont(new Font("Tahoma", Font.BOLD, 15));
		contentPane.add(lblBook);

		comboBoxBook = new JComboBox();
		comboBoxBook.setBounds(44, 120, 170, 20);
		comboBoxBook.removeAllItems();
		try {
			Connection conn = new MyConnection().getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from book");
			while (rs.next()) {
				comboBoxBook.addItem(new Book(rs.getString("Book_ID"), rs.getString("Name"), -1, null, null));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		contentPane.add(comboBoxBook);
		load_Borrow();
	}

	public void load_Borrow() {
		DefaultTableModel d = (DefaultTableModel) table.getModel();
		d.setRowCount(0);
		Vector v = ControlBorrow.findAll(CurrentAccount.getCurrentAccount().getAccount_ID());
		for (int i = 0; i < v.size(); i++) {
			d.addRow((Vector) v.get(i));

		}

	}
}
