package appWindow;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import tools.TableData;

public class DisplayDetails extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField_Sl;
	private JTextField textField_PageTitle;
	private JTextField textField_PageURL;
	private JTextField textField_ObjectType;
	private JTextField textField_ObjectName;
	private JTextField textField_ObjectXpath;
	private JTextField textField_Status;
	private JTextArea textArea_Comments;
	private JTextField textField_ObjectSubType;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DisplayDetails dialog = new DisplayDetails(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DisplayDetails(TableData tabledata) {
		setTitle("Details");
		getContentPane().setFont(new Font("Segoe UI", Font.PLAIN, 12));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 600, 600);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][][][][][][][190.00,grow,fill][][][]"));
		
		JLabel lblSl = new JLabel("Sl : ");
		lblSl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblSl, "cell 0 0,alignx trailing");
		
		textField_Sl = new JTextField();
		textField_Sl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(textField_Sl, "cell 1 0,growx");
		textField_Sl.setColumns(10);
		textField_Sl.setText(String.valueOf(tabledata.getSl()));
		
		JLabel lblNewLabel = new JLabel("Page Title : ");
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblNewLabel, "cell 0 1,alignx trailing");
		
		textField_PageTitle = new JTextField();
		textField_PageTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(textField_PageTitle, "cell 1 1,growx");
		textField_PageTitle.setColumns(10);
		textField_PageTitle.setText(tabledata.getPageTitle());
		
		JLabel lblNewLabel_1 = new JLabel("Page URL : ");
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblNewLabel_1, "cell 0 2,alignx trailing");
		
		textField_PageURL = new JTextField();
		textField_PageURL.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(textField_PageURL, "cell 1 2,growx");
		textField_PageURL.setColumns(10);
		textField_PageURL.setText(tabledata.getPageURL());
		
		JLabel lblNewLabel_2 = new JLabel("Object Type : ");
		lblNewLabel_2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblNewLabel_2, "cell 0 3,alignx trailing");
		
		textField_ObjectType = new JTextField();
		textField_ObjectType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(textField_ObjectType, "cell 1 3,growx");
		textField_ObjectType.setColumns(10);
		textField_ObjectType.setText(tabledata.getObjectType());
		
		JLabel lblObjectSubtype = new JLabel("Object SubType : ");
		lblObjectSubtype.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblObjectSubtype, "cell 0 4,alignx trailing");
		
		textField_ObjectSubType = new JTextField();
		textField_ObjectSubType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(textField_ObjectSubType, "cell 1 4,growx");
		textField_ObjectSubType.setColumns(10);
		textField_ObjectSubType.setText(tabledata.getObjectSubType());
		
		JLabel lblNewLabel_3 = new JLabel("Object Name : ");
		lblNewLabel_3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblNewLabel_3, "cell 0 5,alignx trailing");
		
		textField_ObjectName = new JTextField();
		textField_ObjectName.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(textField_ObjectName, "cell 1 5,growx");
		textField_ObjectName.setColumns(10);
		textField_ObjectName.setText(tabledata.getObjectName());
		
		JLabel lblObjectXpath = new JLabel("Object Xpath : ");
		lblObjectXpath.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblObjectXpath, "cell 0 6,alignx trailing");
		
		textField_ObjectXpath = new JTextField();
		textField_ObjectXpath.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(textField_ObjectXpath, "cell 1 6,growx");
		textField_ObjectXpath.setColumns(10);
		textField_ObjectXpath.setText(tabledata.getObjectXpath());
		
		JLabel lblActualCss = new JLabel("CSS Comparison : ");
		lblActualCss.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblActualCss, "cell 0 7,alignx trailing");

		JScrollPane scrollPane_ActualCSS = new JScrollPane();
		getContentPane().add(scrollPane_ActualCSS, "cell 1 7,grow");

		table = new JTable();
		String[] columns = new String[] {"CSS Properties", "Actual Value", "Expected Value", "Status"};
		String[][] data = null;
		DefaultTableModel model = new DefaultTableModel(data, columns) {

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.setModel(model);
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(0).setMinWidth(50);
		table.getColumnModel().getColumn(1).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setMinWidth(50);
		table.getColumnModel().getColumn(2).setPreferredWidth(100);
		table.getColumnModel().getColumn(2).setMinWidth(50);
		table.getColumnModel().getColumn(3).setPreferredWidth(75);
		table.getColumnModel().getColumn(3).setMinWidth(50);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER );
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(true);
		table.setRowHeight(20);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		table.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 12));
		scrollPane_ActualCSS.setViewportView(table);
		for (Map.Entry<String, String> kv: tabledata.getActualCSS().entrySet()) {
			String key = kv.getKey().trim();
			String actualValue = kv.getValue().trim();
			String expectedValue = tabledata.getExpectedCSS().get(key).trim();
			String status = actualValue.equalsIgnoreCase(expectedValue) ? "Match" : "No Match";
			model.addRow(new String[]{key, actualValue, expectedValue, status});
		}
		/*table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                String status = (String)table.getModel().getValueAt(row, 3);
                if ("No Match".equals(status)) {
                    setBackground(Color.RED);
                    //setForeground(Color.WHITE);
                } else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }       
                return this;
            }   
        });*/
		
		JLabel lblStatus = new JLabel("Status : ");
		lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblStatus, "cell 0 8,alignx trailing");
		
		textField_Status = new JTextField();
		textField_Status.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(textField_Status, "cell 1 8,growx");
		textField_Status.setColumns(10);
		textField_Status.setText(tabledata.getStatus());
		
		JLabel lblComments = new JLabel("Comments : ");
		lblComments.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(lblComments, "cell 0 9,alignx trailing");
		
		JScrollPane scrollPane_Comments = new JScrollPane();
		scrollPane_Comments.setMinimumSize(new Dimension(25, 75));
		scrollPane_Comments.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(scrollPane_Comments, "cell 1 9,grow");
		
		textArea_Comments = new JTextArea();
		scrollPane_Comments.setViewportView(textArea_Comments);
		textArea_Comments.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		textArea_Comments.setColumns(10);
		textArea_Comments.setText(tabledata.getComments().trim());
		
		JButton btnClose = new JButton("Close");
		btnClose.setIcon(new ImageIcon(DisplayDetails.class.getResource("/icons/close.png")));
		btnClose.setToolTipText("<html>\r\n\t<p>\r\n\t\t<font size=\"4\" face=\"Segoe UI\">\r\n\t\t\tClose the window.\r\n\t\t</font>\r\n\t</p>\r\n</html>");
		btnClose.setMargin(new Insets(2, 2, 2, 2));
		btnClose.setIconTextGap(2);
		btnClose.setMinimumSize(new Dimension(80, 30));
		btnClose.setMaximumSize(new Dimension(180, 30));
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		btnClose.setPreferredSize(new Dimension(80, 30));
		btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		getContentPane().add(btnClose, "cell 1 10,alignx trailing");
		
	}
}
