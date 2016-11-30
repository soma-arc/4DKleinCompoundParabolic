import java.awt.Dimension;

import javax.swing.*;

public class HorizontalPanel extends JPanel{
	public HorizontalPanel(int sizeX, int sizeY) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setOpaque(false);
		this.setMaximumSize(new Dimension(sizeX - 1, sizeY));
	}
	public HorizontalPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setOpaque(false);
		this.setAlignmentX(LEFT_ALIGNMENT);
	}
}
