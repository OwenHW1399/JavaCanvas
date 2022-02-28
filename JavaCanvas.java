
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileNameExtensionFilter;

public class JavaCanvas extends JFrame {

    public static void main(String[] args) {
        new JavaCanvas();
    }

    enum choiceofPainting {
        LINE,
        RECTANAGLE,
        BRUSH,
        ELLPSE,
        ERASER
    }

    enum choiceOfMenu {
        TOP,
        BOTTOM,
    }

    enum exportOrInitialize {
        EXPORT,
        INIT
    }

    enum choiceOfSlider {
        THICKNESS,
        TRANSPARENCY,
    }

    choiceofPainting choiceOfAction;
    Color backgroundColor, strokeLineColor, fillingColor;
    float strokeThicknessVal, transparencyRate;
    JFrame mainFrame;
    Canvas centerBoard;
    PanelWithButton topMenuPanel, bottomMenuPanel;
    JPanel leftPanel, rightPanel;

    public JavaCanvas() {

        backgroundColor = Color.white;
        strokeLineColor = Color.BLACK;
        fillingColor = Color.white;
        strokeThicknessVal = 4;
        transparencyRate = 1.0f;
        // https://stackoverflow.com/questions/16369726/declaring-floats-why-default-type-double
        // must declare float otherwise will generate error

        mainFrame = new JFrame();
        mainFrame.setTitle("Java Canvas");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 500);
        mainFrame.setLayout(new BorderLayout(10, 10));
        mainFrame.setVisible(true);

        topMenuPanel = new PanelWithButton(600, 50, Color.magenta, choiceOfMenu.TOP);
        leftPanel = new PanelWithButtonVertical(100, 400, Color.CYAN, exportOrInitialize.EXPORT);
        rightPanel = new PanelWithButtonVertical(100, 400, Color.CYAN, exportOrInitialize.INIT);
        bottomMenuPanel = new PanelWithButton(600, 50, Color.magenta, choiceOfMenu.BOTTOM);
        centerBoard = new Canvas(400, 400, Color.white);

        // leftPanel.setPreferredSize(new Dimension(50, 400));
        // rightPanel.setPreferredSize(new Dimension(50, 400));
        // leftPanel.setBackground(Color.CYAN);
        // rightPanel.setBackground(Color.CYAN);

        mainFrame.add(topMenuPanel, BorderLayout.NORTH);
        mainFrame.add(leftPanel, BorderLayout.WEST);
        mainFrame.add(rightPanel, BorderLayout.EAST);
        mainFrame.add(bottomMenuPanel, BorderLayout.SOUTH);
        mainFrame.add(centerBoard, BorderLayout.CENTER);
    }

    public void reset() {
        centerBoard.shapeMap = new HashMap<Shape, ArrayList<Object>>();
        centerBoard.repaint();
    }

    private class PanelWithButtonVertical extends JPanel implements ActionListener {

        exportOrInitialize choice;
        JButton exportButton, initButton;

        public PanelWithButtonVertical(int width, int height, Color color, exportOrInitialize choice) {
            // First set up two sliders
            this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            this.setPreferredSize(new Dimension(width, height));
            this.setBackground(color);
            this.choice = choice;
            if (this.choice == exportOrInitialize.EXPORT) {
                exportButton = new JButton();
                exportButton.setText("Export");
                exportButton.addActionListener(this);
                this.add(exportButton);
            } else if (this.choice == exportOrInitialize.INIT) {
                initButton = new JButton();
                initButton.setText("Initialize");
                initButton.addActionListener(this);
                this.add(initButton);

            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == exportButton) {
                /*
                 * Open a file chooser and save file, referenced from stackoverflow, however
                 * can't find the link anymore
                 */
                BufferedImage image = new BufferedImage(centerBoard.getWidth(),
                        centerBoard.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setPaint(backgroundColor);
                g.fillRect(0, 0, image.getWidth(), image.getHeight());
                centerBoard.paint(g);
                g.dispose();

                JFileChooser fileChooser = new JFileChooser();
                File theDirectory = new File("imageDir/");
                fileChooser.setCurrentDirectory(theDirectory);
                FileNameExtensionFilter pngFilter = new FileNameExtensionFilter(
                        "PNG file (*.png)", "png");

                // Insipiration from
                // https://stackoverflow.com/questions/1440750/set-bufferedimage-to-be-a-color-in-java
                fileChooser.addChoosableFileFilter(pngFilter);
                fileChooser.setFileFilter(pngFilter);

                int status = fileChooser.showSaveDialog(JavaCanvas.this);

                if (status == JFileChooser.APPROVE_OPTION) {
                    try {
                        ImageIO.write(image, "png",
                                fileChooser.getSelectedFile());
                        JOptionPane.showMessageDialog(null, "Image saved to "
                                + fileChooser.getSelectedFile().getName());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (e.getSource() == initButton) {
                // Inspiration from
                // https://www.edureka.co/community/358/how-to-execute-a-python-file-with-few-arguments-in-java#:~:text=You%20can%20use%20Java%20Runtime,and%20then%20set%20it%20executable.
                String[] cmd = {
                        "python",
                        "frontEndGui.py",
                };
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }

    private class PanelWithButton extends JPanel implements ActionListener, ChangeListener {

        // Add two sliders
        JSlider thicknessSlider, transparencySlider;
        JLabel thicknessLabel, transparencyLabel;
        choiceOfMenu choice;
        JButton lineButton, rectangleButton, ellipseButton, brushButton, eraserButton, resetButton,
                changeBackgroundColorButton, changeStrokeColorButton, changeFillColorButton;

        public PanelWithButton(int width, int height, Color color, choiceOfMenu choice) {
            // First set up two sliders
            this.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            this.setPreferredSize(new Dimension(width, height));
            this.setBackground(color);
            this.choice = choice;

            if (choice == choiceOfMenu.TOP) {
                lineButton = createChoiceButton("./free_icons/line.png", true);
                rectangleButton = createChoiceButton("./free_icons/rectangle.png", true);
                ellipseButton = createChoiceButton("./free_icons/round.png", true);
                brushButton = createChoiceButton("Brush", false);
                eraserButton = createChoiceButton("Eraser", false);
                thicknessLabel = new JLabel();
                thicknessSlider = new JSlider(0, 100, 10);
                thicknessSlider.addChangeListener(this);
                thicknessSlider.setPreferredSize(new Dimension(300, 30));
                thicknessLabel.setText("Thickness: " + thicknessSlider.getValue() + "%");
                this.add(thicknessSlider);
                this.add(thicknessLabel);
            } else if (choice == choiceOfMenu.BOTTOM) {
                resetButton = createChoiceButton("Reset", false);
                changeBackgroundColorButton = createChoiceButton("Background Color", false);
                changeStrokeColorButton = createChoiceButton("Stroke Color", false);
                changeFillColorButton = createChoiceButton("Fill Color", false);
                transparencyLabel = new JLabel();
                transparencySlider = new JSlider(0, 100, 100);
                transparencySlider.addChangeListener(this);
                transparencySlider.setPreferredSize(new Dimension(300, 30));
                transparencyLabel.setText("Transparency: " + transparencySlider.getValue() + "%");
                transparencyRate = transparencySlider.getValue();
                this.add(transparencySlider);
                this.add(transparencyLabel);
            }
        }

        public JButton createChoiceButton(String iconString, boolean usePath) {
            // If usePath is true, then iconString is the path of the image we are going to
            // paste on localButton
            // else, we are just put the text of iconString onto the button.
            JButton localButton = new JButton();
            if (usePath) {
                // Resizing image, a neat trick from https://stackoverflow.com/a/18335435
                ImageIcon imageIcon = new ImageIcon(iconString); // load the image to a imageIcon
                Image image = imageIcon.getImage(); // transform it
                Image newimg = image.getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
                imageIcon = new ImageIcon(newimg); // transform it back
                localButton.setIcon(imageIcon);
            } else {
                localButton.setText(iconString);
            }
            localButton.addActionListener(this);
            this.add(localButton);
            return localButton;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (this.choice == choiceOfMenu.TOP) {
                if (e.getSource() == lineButton) {
                    choiceOfAction = choiceofPainting.LINE;
                } else if (e.getSource() == rectangleButton) {
                    choiceOfAction = choiceofPainting.RECTANAGLE;
                } else if (e.getSource() == ellipseButton) {
                    choiceOfAction = choiceofPainting.ELLPSE;
                } else if (e.getSource() == brushButton) {
                    choiceOfAction = choiceofPainting.BRUSH;
                } else if (e.getSource() == eraserButton) {
                    choiceOfAction = choiceofPainting.ERASER;
                } else {
                    choiceOfAction = choiceofPainting.LINE;
                }
            } else if (this.choice == choiceOfMenu.BOTTOM) {
                if (e.getSource() == resetButton) {
                    reset();
                } else if (e.getSource() == changeBackgroundColorButton) {
                    backgroundColor = JColorChooser.showDialog(null, "Pick a color for the background",
                            backgroundColor);
                    mainFrame.getContentPane().setBackground(backgroundColor);
                } else if (e.getSource() == changeStrokeColorButton) {
                    strokeLineColor = JColorChooser.showDialog(null, "Pick a color for the stroke line",
                            backgroundColor);
                } else if (e.getSource() == changeFillColorButton) {
                    fillingColor = JColorChooser.showDialog(null, "Pick a color for the filling of shapes or the brush",
                            backgroundColor);
                }
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() == transparencySlider) {
                transparencyLabel.setText("Transparency: " + transparencySlider.getValue() + "%");
                transparencyRate = transparencySlider.getValue();
            } else if (e.getSource() == thicknessSlider) {
                thicknessLabel.setText("Thickness: " + thicknessSlider.getValue() + "%");
                strokeThicknessVal = thicknessSlider.getValue();
            }
        }
    }

    private class Canvas extends JComponent {

        Point startPoint, currentPoint;
        HashMap<Shape, ArrayList<Object>> shapeMap; // The format will be graphics,
                                                    // list<fillColor,strokeColor,strokeWidth,transparencyRate>
        Graphics2D graphBeingPainted;
        Shape shapeBeingPainted;
        boolean finishedDrawing; // We use this boolean to mark whether we are drawing stuff or just finished
                                 // drawing (i.e. just released mouse)

        public Canvas(int width, int height, Color color) {
            this.setPreferredSize(new Dimension(width, height));
            this.setBackground(color);
            this.shapeMap = new HashMap<Shape, ArrayList<Object>>();
            this.startPoint = new Point(0, 0);
            this.currentPoint = new Point(0, 0);
            this.addMouseListener(new MousePressListner());
            this.addMouseMotionListener(new MouseMoveListener());
            this.finishedDrawing = true;
        }

        public void paintComponent(Graphics g) {
            Graphics2D tempGraphBeingRecycled = (Graphics2D) g;
            Set<Entry<Shape, ArrayList<Object>>> entrySet = shapeMap.entrySet();
            for (Entry<Shape, ArrayList<Object>> entry : entrySet) {
                // shapeMap's value if of format
                // list<fillColor,strokeColor,strokeWidth,transparencyRate>
                Color tempFillColor = (Color) entry.getValue().get(0);
                Color tempStrokeColor = (Color) entry.getValue().get(1);
                float tempStrokeWidth = (float) entry.getValue().get(2);
                float tempTransparencyRate = (float) entry.getValue().get(3);

                // Set up Transparency, refer to
                // https://www.informit.com/articles/article.aspx?p=26349&seqNum=5#:~:text=You%20set%20a%20transparency%20by,or%20%22alpha%22)%20value.
                float alpha = (float) tempTransparencyRate * 0.01f;
                int type = AlphaComposite.SRC_OVER;
                AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
                tempGraphBeingRecycled.setComposite(composite);

                // Set up the stroke line width
                tempGraphBeingRecycled.setStroke(new BasicStroke(tempStrokeWidth));

                // Set up the Paint attribute for the Graphics2D context first since we only
                // want to paint the border with tempStrokeColor.
                tempGraphBeingRecycled.setPaint(tempStrokeColor);
                tempGraphBeingRecycled.draw(entry.getKey());

                // Set up the Paint attribute for the Graphics2D context again since we need
                // tempFillColor to use fill().
                tempGraphBeingRecycled.setPaint(tempFillColor);
                tempGraphBeingRecycled.fill(entry.getKey());

            }
            if (!finishedDrawing) {
                graphBeingPainted = (Graphics2D) g;
                graphBeingPainted.setComposite(
                        AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) transparencyRate * 0.01f));
                graphBeingPainted.setStroke(new BasicStroke(strokeThicknessVal));
                graphBeingPainted.setPaint(strokeLineColor);
                if (choiceOfAction == choiceofPainting.LINE) {
                    shapeBeingPainted = new Line2D.Float((int) startPoint.getX(), (int) startPoint.getY(),
                            (int) currentPoint.getX(), (int) currentPoint.getY());
                    graphBeingPainted.draw(shapeBeingPainted);
                } else if (choiceOfAction == choiceofPainting.RECTANAGLE) {
                    int smallestX = Math.min((int) startPoint.getX(), (int) currentPoint.getX());
                    int smallestY = Math.min((int) startPoint.getY(), (int) currentPoint.getY());

                    shapeBeingPainted = new Rectangle2D.Float(smallestX, smallestY,
                            Math.abs((int) currentPoint.getX() - (int) startPoint.getX()),
                            Math.abs((int) currentPoint.getY() - (int) startPoint.getY()));
                    graphBeingPainted.draw(shapeBeingPainted);
                    graphBeingPainted.setPaint(fillingColor);
                    graphBeingPainted.fill(shapeBeingPainted);
                } else if (choiceOfAction == choiceofPainting.ELLPSE) {
                    int smallestX = Math.min((int) startPoint.getX(), (int) currentPoint.getX());
                    int smallestY = Math.min((int) startPoint.getY(), (int) currentPoint.getY());

                    shapeBeingPainted = new Ellipse2D.Float(smallestX, smallestY,
                            Math.abs((int) currentPoint.getX() - (int) startPoint.getX()),
                            Math.abs((int) currentPoint.getY() - (int) startPoint.getY()));
                    graphBeingPainted.draw(shapeBeingPainted);
                    graphBeingPainted.setPaint(fillingColor);
                    graphBeingPainted.fill(shapeBeingPainted);
                }

            }
        }

        private class MousePressListner extends MouseAdapter {

            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                finishedDrawing = false;
            }

            public void mouseReleased(MouseEvent e) {
                if (choiceOfAction != choiceofPainting.BRUSH && choiceOfAction != choiceofPainting.ERASER) {
                    ArrayList<Object> featureList = new ArrayList<Object>();
                    featureList.add(fillingColor);
                    featureList.add(strokeLineColor);
                    featureList.add(strokeThicknessVal);
                    featureList.add(transparencyRate);
                    shapeMap.put(shapeBeingPainted, featureList);
                    finishedDrawing = true;
                }
            }
        }

        private class MouseMoveListener extends MouseMotionAdapter {

            public void mouseDragged(MouseEvent e) {
                if (choiceOfAction == choiceofPainting.BRUSH || choiceOfAction == choiceofPainting.ERASER) {
                    if (choiceOfAction == choiceofPainting.ERASER) {
                        strokeLineColor = backgroundColor;
                        fillingColor = backgroundColor;
                    }
                    ArrayList<Object> featureList = new ArrayList<Object>();
                    featureList.add(fillingColor);
                    featureList.add(strokeLineColor);
                    featureList.add(strokeThicknessVal);
                    featureList.add(transparencyRate);
                    Shape newBrushSpot = new Ellipse2D.Float(e.getX(), e.getY(), 10, 10);
                    shapeMap.put(newBrushSpot, featureList);
                }
                currentPoint = e.getPoint();
                repaint();
            }
        }
    }
}
