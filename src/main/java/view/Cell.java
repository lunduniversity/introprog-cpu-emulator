package view;

public class Cell extends AbstractCell {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public Cell(int index, CellListener listener, CellNav cellNav) {
		super(pad(index), listener, cellNav);
	}

}
