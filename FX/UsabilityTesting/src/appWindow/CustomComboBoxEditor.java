package appWindow;

import java.awt.Component;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.swingbinding.JTableBinding;

import tools.Baseline;
import tools.TableData;

public class CustomComboBoxEditor extends DefaultCellEditor {

	private DefaultComboBoxModel model;
	private int objectTypeColumnId = 2;
	private int objectSubTypeColumnId = 3;

	private List<String> elementTypeList;
	private HashMap<String, Baseline> baselineList;

	public CustomComboBoxEditor(List<String> elementTypeList, HashMap<String, Baseline> baselineList) {
		super(new JComboBox<String>());
		this.model = (DefaultComboBoxModel)((JComboBox<String>)getComponent()).getModel();
		this.elementTypeList = elementTypeList;
		this.baselineList = baselineList;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

		// Remove previous elements every time.
		// So that we can populate the elements based on the selection.
		model.removeAllElements();
		
		getComponent().setFont(new Font("Segoe UI", Font.PLAIN, 12));
		
		if(column == objectTypeColumnId) {
			// Just show the elements in the JComboBox. 
			for (String elementType : elementTypeList) {
				model.addElement(elementType);
			}
		} 
		else if (column == objectSubTypeColumnId){

			// getValueAt(..) method will give you the selection that is set for column one.
			String selectedItem = (String) table.getValueAt(row, objectTypeColumnId);

			// Using the obtained selected item from the first column JComboBox 
			// selection make a call ans get the list of elements.
			List<String> elementSubTypeList = baselineList.get(selectedItem).getSubTypeList();
			// Say we have list of data from the call we made. 
			// So loop through the list and add them to the model like the following.
			for (String elementSubType : elementSubTypeList) {
				model.addElement(elementSubType);
			}

		} // Close else

		// finally return the component.
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
}
