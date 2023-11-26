package computer.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HexFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import net.miginfocom.swing.MigLayout;

public class Cell extends JPanel {

	private static HexFormat hexFormat = HexFormat.of().withUpperCase();

	private static final long serialVersionUID = 1L;
	private JTextField[] bits;
	private JLabel lblHex;
	private JLabel lblDec;
	private JPanel bitPanel;
	private Component rigidArea;
	private Component rigidArea_1;

	/**
	 * Create the panel.
	 */
	public Cell(int index, CellNav cellNav) {
		setBorder(null);
		setLayout(new MigLayout("gap 0, insets 0",
				"[30px:30px:30px][][100px:100px:100px][][30px:30px:30px][30px:30px:30px]", "[]"));

		JLabel lblIndex = new JLabel(pad(index));
		lblIndex.setBorder(null);
		lblIndex.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblIndex.setPreferredSize(new Dimension(30, 20));
		lblIndex.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblIndex, "cell 0 0,alignx right");

		rigidArea = Box.createRigidArea(new Dimension(5, 10));
		add(rigidArea, "cell 1 0");
		bitPanel = new JPanel();
		bitPanel.setBorder(UIManager.getBorder("TextField.border"));
		bitPanel.setBackground(UIManager.getColor("TextField.background"));
		add(bitPanel, "cell 2 0,grow");
		bitPanel.setLayout(new BoxLayout(bitPanel, BoxLayout.X_AXIS));

		bitPanel.add(Box.createRigidArea(new Dimension(5, 10)));
		bits = new JTextField[8];
		for (int i = 0; i < bits.length; i++) {
			final int idx = i;
			JTextField bit = new JTextField("0", 1);
			bit.setBorder(null);
			bit.setPreferredSize(new Dimension(10, 20));
			bit.setHorizontalAlignment(SwingConstants.CENTER);
			bit.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					System.out.println("Key press: " + e);
					if (e.getKeyChar() == '0' || e.getKeyChar() == '1') {
						bit.setText(Character.toString(e.getKeyChar()));
						if (idx < bits.length - 1) {
							bits[idx + 1].requestFocusInWindow();
						} else {
							cellNav.nextCell(idx);
						}
						updateValue();
					} else {
//						bit.setText("0");
						bit.setCaretPosition(0);
					}
					e.consume();
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getExtendedKeyCode() == KeyEvent.VK_LEFT && idx > 0) {
						bits[idx - 1].requestFocusInWindow();
					}
					if (e.getExtendedKeyCode() == KeyEvent.VK_RIGHT && idx < bits.length - 1) {
						bits[idx + 1].requestFocusInWindow();
					}
					if (e.getExtendedKeyCode() == KeyEvent.VK_UP) {
						cellNav.prevCell(idx);
					}
					if (e.getExtendedKeyCode() == KeyEvent.VK_DOWN) {
						cellNav.nextCell(idx);
					}
					if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
						cellNav.nextCell(0);
					}
				}
			});
			bit.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					bit.setCaretPosition(0);
				}
			});
			bitPanel.add(bit, String.format("cell %d 0,growx", i + 1));
			bits[i] = bit;
			if (i == bits.length / 2 - 1) {
				bitPanel.add(Box.createRigidArea(new Dimension(5, 10)));
			}
		}
		bitPanel.add(Box.createRigidArea(new Dimension(5, 10)));

		rigidArea_1 = Box.createRigidArea(new Dimension(5, 10));
		add(rigidArea_1, "cell 3 0");

		lblHex = new JLabel(hex(0));
		lblHex.setBorder(null);
		lblHex.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblHex.setHorizontalAlignment(SwingConstants.RIGHT);
		lblHex.setPreferredSize(new Dimension(30, 20));
		add(lblHex, "cell 4 0,alignx right");

		lblDec = new JLabel(pad(0));
		lblDec.setBorder(null);
		lblDec.setHorizontalTextPosition(SwingConstants.RIGHT);
		lblDec.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDec.setPreferredSize(new Dimension(30, 20));
		add(lblDec, "cell 5 0,alignx right");

	}

	private void updateValue() {
		int value = 0;
		for (int i = 0; i < bits.length; i++) {
			int b = bits[i].getText().equals("1") ? 1 : 0;
			value |= b << (bits.length - i - 1);
		}
		lblHex.setText(hex(value));
		lblDec.setText(pad(value));
	}

	private static String hex(int value) {
		return hex(value, true);
	}

	private static String hex(int value, boolean prefix) {
		String hexDigits = hexFormat.toHexDigits(value);
		if (hexDigits.length() == 1) {
			hexDigits = "0" + hexDigits;
		} else if (hexDigits.length() > 2) {
			hexDigits = hexDigits.substring(hexDigits.length() - 2);
		}
		return prefix ? "0x" + hexDigits : hexDigits;
	}

	private static String pad(int value) {
		return String.format("%02d", value);
	}

	public void focus(int xpos) {
		bits[xpos].requestFocusInWindow();
		bits[xpos].setCaretPosition(0);
	}

	public void setValue(int value) {
		for (int i = 0; i < 8; i++) {
			bits[7 - i].setText(Integer.toString(((value >> i) & 1)));
		}
	}

	public int getValue() {
		StringBuilder sb = new StringBuilder();
		for (JTextField bit : bits) {
			sb.append(bit.getText());
		}
		return Integer.parseInt(sb.toString(), 2);
	}

}
