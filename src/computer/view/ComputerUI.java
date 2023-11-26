package computer.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import computer.model.Memory;
import computer.model.ProgramCounter;
import net.miginfocom.swing.MigLayout;

public class ComputerUI {

	private JFrame frame;
	private final Memory mem;
	private final Memory reg;
	private final ProgramCounter pc;

	public ComputerUI(Memory mem, Memory reg, ProgramCounter pc) {
		super();
		this.mem = mem;
		this.reg = reg;
		this.pc = pc;
		initialize();

		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 725);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[grow][][][][grow]", "[][][][grow][grow][grow]"));

		JLabel lblComputerHeader = new JLabel("Computer");
		lblComputerHeader.setFont(new Font("Tahoma", Font.BOLD, 20));
		frame.getContentPane().add(lblComputerHeader, "cell 0 0 5 1,grow");

		JLabel lblDescription = new JLabel(
				"A simple simulation of a CPU and memory. This computer has an 8-bit processor, with 64 bytes of memory.");
		frame.getContentPane().add(lblDescription, "cell 0 1 5 1");

		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		frame.getContentPane().add(rigidArea, "cell 0 2");

		JPanel memoryPanel = new JPanel();
		frame.getContentPane().add(memoryPanel, "cell 0 3 1 3,grow");
		memoryPanel.setLayout(new MigLayout("",
				"[30px:30px:30px][][100px:100px:100px][][30px:30px:30px][30px:30px:30px]", "[][][][]"));

		JLabel lblMemoryHeader = new JLabel("Memory");
		memoryPanel.add(lblMemoryHeader, "cell 0 0 6 1");
		lblMemoryHeader.setFont(new Font("Tahoma", Font.BOLD, 14));

		Component rigidArea_3 = Box.createRigidArea(new Dimension(20, 10));
		memoryPanel.add(rigidArea_3, "cell 0 1 2 1");

		JLabel lblAddress = new JLabel("Addr.");
		lblAddress.setBorder(null);
		lblAddress.setHorizontalAlignment(SwingConstants.RIGHT);
		memoryPanel.add(lblAddress, "cell 0 2,alignx right");

		Component rigidArea_1 = Box.createRigidArea(new Dimension(5, 10));
		memoryPanel.add(rigidArea_1, "cell 1 2");

		JLabel lblValue = new JLabel("Value");
		lblValue.setHorizontalAlignment(SwingConstants.RIGHT);
		memoryPanel.add(lblValue, "cell 2 2,alignx center");

		Component rigidArea_2 = Box.createRigidArea(new Dimension(5, 10));
		memoryPanel.add(rigidArea_2, "cell 3 2");

		JLabel lblHex = new JLabel("Hex");
		lblHex.setHorizontalAlignment(SwingConstants.RIGHT);
		memoryPanel.add(lblHex, "cell 4 2,alignx right");

		JLabel lblDec = new JLabel("Dec");
		lblDec.setHorizontalAlignment(SwingConstants.RIGHT);
		memoryPanel.add(lblDec, "cell 5 2,alignx right");

		// Memory cells
		{
			JPanel memoryCellsPanel = new JPanel();
			memoryCellsPanel.setBorder(null);
			memoryPanel.add(memoryCellsPanel, "cell 0 3 6 1,alignx left,aligny top");
			memoryCellsPanel.setLayout(new BoxLayout(memoryCellsPanel, BoxLayout.Y_AXIS));

			final Cell[] cells = new Cell[mem.numCells()];
			for (int i = 0; i < mem.numCells(); i++) {
				final int idx = i;
				cells[i] = new Cell(i, new CellNav() {
					@Override
					public void prevCell(int xpos) {
						cells[(idx - 1 + mem.numCells()) % mem.numCells()].focus(xpos);
					}

					@Override
					public void nextCell(int xpos) {
						cells[(idx + 1) % mem.numCells()].focus(xpos);
					}
				});
				memoryCellsPanel.add(cells[i]);
			}

			{
				mem.addListener((idx, value) -> cells[idx].setValue(value));
			}
		}

		// Registers and program counter
		{
			JPanel registerPanel = new JPanel();
			frame.getContentPane().add(registerPanel, "cell 4 3,grow");
			registerPanel.setLayout(new MigLayout("", "[][][]", "[][][][][][]"));

			JLabel lblRegisterHeader = new JLabel("Registers");
			lblRegisterHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
			registerPanel.add(lblRegisterHeader, "cell 0 0 3 1");

			Component rigidArea_4 = Box.createRigidArea(new Dimension(20, 10));
			registerPanel.add(rigidArea_4, "cell 0 1");

			Register[] regs = new Register[reg.numCells() + 1];
			for (int i = 0; i < regs.length - 1; i++) {
				final int idx = i;
				String name = String.format("R%d", idx + 1);
				regs[i] = new Register(name, new CellNav() {
					@Override
					public void prevCell(int xpos) {
						regs[(idx - 1 + reg.numCells()) % (reg.numCells())].focus(xpos);
					}

					@Override
					public void nextCell(int xpos) {
						regs[(idx + 1) % (reg.numCells())].focus(xpos);
					}
				});
				registerPanel.add(regs[i], String.format("cell 0 %d", 2 + idx));
			}

			Component rigidArea_6 = Box.createRigidArea(new Dimension(20, 10));
			registerPanel.add(rigidArea_6, String.format("cell 0 %d", 2 + reg.numCells()));

			Register pc = new Register("PC", null);
			registerPanel.add(pc, String.format("cell 0 %d", 2 + reg.numCells() + 1));

		}

		// Execution controls
		{
			JPanel controlPanel = new JPanel();
			frame.getContentPane().add(controlPanel, "cell 4 5,grow");
			controlPanel.setLayout(new MigLayout("", "[]", "[]"));

			JLabel lblControlHeader = new JLabel("Controls");
			lblControlHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
			controlPanel.add(lblControlHeader, "cell 0 0");
		}
	}

}
