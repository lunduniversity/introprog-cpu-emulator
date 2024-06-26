package view;

import static util.LazySwing.inv;

import java.awt.Point;
import java.awt.event.ComponentListener;
import javax.swing.*;
import javax.swing.WindowConstants;
import util.LazySwing;

public abstract class AnchoredFrame extends JDialog {

  public enum AnchorSide {
    LEFT,
    RIGHT
  }

  protected JFrame parentFrame;

  private boolean isAnchored;
  private AnchorSide anchorSide;
  private transient ComponentListener parentListener;

  protected AnchoredFrame(String title, JFrame parent, AnchorSide anchorSide) {
    super(parent, title, false);
    this.parentFrame = parent;
    this.anchorSide = anchorSide;

    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

    this.parentListener =
        new ComponentListener() {

          private long lastInvocation = 0;

          @Override
          public void componentResized(java.awt.event.ComponentEvent e) {
            doIt(true);
          }

          @Override
          public void componentMoved(java.awt.event.ComponentEvent e) {
            doIt(false);
          }

          @Override
          public void componentShown(java.awt.event.ComponentEvent e) {
            // Not needed
          }

          @Override
          public void componentHidden(java.awt.event.ComponentEvent e) {
            // Not needed
          }

          private void doIt(boolean recalculate) {
            if (!isAnchored) {
              return;
            }
            if (recalculate && System.currentTimeMillis() - lastInvocation < 100) {
              return;
            }
            lastInvocation = System.currentTimeMillis();
            if (recalculate) {
              inv(AnchoredFrame.this::fitToParent);
            } else {
              inv(AnchoredFrame.this::followParent);
            }
          }
        };
  }

  public void anchorToParent() {
    if (isAnchored) {
      return;
    }
    this.isAnchored = true;
    parentFrame.addComponentListener(parentListener);
    inv(this::fitToParent);
  }

  public void releaseFromParent() {
    if (!isAnchored) {
      return;
    }
    this.isAnchored = false;
    parentFrame.removeComponentListener(parentListener);
  }

  public void updateGlobalFontSize(int newFontSize) {
    LazySwing.setComponentTreeFontSize(this, newFontSize);
    if (isAnchored) {
      inv(this::fitToParent);
    }
  }

  protected void fitToParent() {
    fitContent();
    setSize(getWidth(), parentFrame.getHeight());
    followParent();
  }

  protected abstract void fitContent();

  protected void followParent() {
    Point parentLocation = parentFrame.getLocation();
    int xCoord;
    int yCoord = parentLocation.y;
    if (anchorSide == AnchorSide.LEFT) {
      xCoord = parentLocation.x - getWidth();
    } else {
      xCoord = parentLocation.x + parentFrame.getWidth();
    }
    setLocation(xCoord, yCoord);

    revalidate();
    repaint();
  }
}
