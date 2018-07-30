package org.hovjyc.scrapad.ui;

import java.awt.Component;

import javax.swing.*;
import javax.swing.table.*;

/**
 * The renderer that permits to display a button in a cell. 
 */
public class JButtonCellRenderer extends JButton implements TableCellRenderer {
	
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -2580508990877967419L;

	/**
	 * Constructor
	 */
	public JButtonCellRenderer() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (value instanceof JButton) {
			return (JButton) value;
		}
		return this;
	}
}