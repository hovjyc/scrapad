package org.hovjyc.scrapad.ui;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * THe editor that permits to click on the button in the cell. 
 */
public class JButtonCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener {

	/**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -5321497524544218416L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getCellEditorValue() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCellEditable(EventObject e) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value instanceof JButton) {
			return (JButton) value;
		}
		return null;
	}

}
