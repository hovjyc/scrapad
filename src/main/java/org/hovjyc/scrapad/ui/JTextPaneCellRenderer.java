package org.hovjyc.scrapad.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.table.TableCellRenderer;

/**
 * The renderer that permits to resize the text pane following its content.
 */
public class JTextPaneCellRenderer extends JViewport implements TableCellRenderer {
	
	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = 1585934956978677340L;
	
	/** The text pane */
	private JTextPane pane;

	/**
	 * Constructor
	 */
	JTextPaneCellRenderer() {
		pane = new JTextPane();
		add(pane);
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		pane.setText(value.toString());
		if (column == 0) {
			int lPreferedSize = (int) pane.getPreferredSize().getHeight();
			table.setRowHeight(row, lPreferedSize);
		}
		return this;
	}
}
