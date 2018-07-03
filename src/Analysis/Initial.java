package Analysis;

import javax.swing.*;
import java.awt.*;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class Initial extends JFrame{
    public Initial() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

                try {
                    BackgroundPane background = new BackgroundPane();
                    background.setBackground(ImageIO.read(new File("background1.png")));
                    background.setPreferredSize(new Dimension(1600, 900));
                    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    setLocation(100,0);
                    setContentPane(background);
                    background.setLayout(new BorderLayout());
                    JLabel name = new JLabel("CLASS DIAGRAM",(int)CENTER_ALIGNMENT);
                    name.setPreferredSize(new Dimension(1600,300));
                    name.setFont(new Font("Times New Roman",Font.BOLD,64));
                    name.setForeground(Color.green);
                    background.add(name,BorderLayout.NORTH);
                    JLabel right = new JLabel();
                    right.setPreferredSize(new Dimension(500, 900));
                    background.add(right,BorderLayout.LINE_START);
                    Button start = new Button("START");
                    start.addMouseListener(new MouseAdapter(){
                        @Override
                        public void mouseClicked(MouseEvent e){
                            chooseFile();
                        }    
                    });
                    start.setBackground(Color.green);
                    start.setFont(new Font("Times New Roman",Font.BOLD,48));
                    start.setPreferredSize(new Dimension(200,80));
                    background.add(start,BorderLayout.CENTER);
                    JLabel author = new JLabel("Trần Hữu Tuân and Trần Quang Tuấn",(int)CENTER_ALIGNMENT);
                    author.setPreferredSize(new Dimension(1600,300));
                    author.setFont(new Font("Times New Roman",Font.BOLD,30));
                    author.setForeground(Color.white);
                    background.add(author,BorderLayout.SOUTH);
                    JLabel left = new JLabel();
                    left.setPreferredSize(new Dimension(500, 900));
                    background.add(left,BorderLayout.LINE_END);
                    pack();
                    setLocationRelativeTo(null);
                    setVisible(true);
                    
                } catch (IOException exp) {
                    exp.printStackTrace();
                }
            }
        });
    }    
    
    public class BackgroundPane extends JPanel {
        private BufferedImage img;
        private BufferedImage scaled;

        public BackgroundPane() {
        }

        @Override
        public Dimension getPreferredSize() {
            return img == null ? super.getPreferredSize() : new Dimension(img.getWidth(), img.getHeight());
        }

        public void setBackground(BufferedImage value) {
            if (value != img) {
                this.img = value;
                repaint();
            }
        }

        @Override
        public void invalidate() {
            super.invalidate();
            if (getWidth() > img.getWidth() || getHeight() > img.getHeight()) {
                scaled = getScaledInstanceToFill(img, getSize());
            } else {
                scaled = img;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (scaled != null) {
                int x = (getWidth() - scaled.getWidth()) / 2;
                int y = (getHeight() - scaled.getHeight()) / 2;
                g.drawImage(scaled, x, y, this);
            }
        }

    }
    
    public static BufferedImage getScaledInstanceToFill(BufferedImage img, Dimension size) {
        double scaleFactor = getScaleFactorToFill(img, size);
        return getScaledInstance(img, scaleFactor);
    }
    
    public static double getScaleFactorToFill(BufferedImage img, Dimension size) {
        double dScale = 1;
        if (img != null) {
            int imageWidth = img.getWidth();
            int imageHeight = img.getHeight();

            double dScaleWidth = getScaleFactor(imageWidth, size.width);
            double dScaleHeight = getScaleFactor(imageHeight, size.height);

            dScale = Math.max(dScaleHeight, dScaleWidth);
        }

        return dScale;
    }

    public static double getScaleFactor(int iMasterSize, int iTargetSize) {
        double dScale = (double) iTargetSize / (double) iMasterSize;
        return dScale;
    }

    public static BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor) {
        return getScaledInstance(img, dScaleFactor, RenderingHints.VALUE_INTERPOLATION_BILINEAR, true);
    }
    protected static BufferedImage getScaledInstance(BufferedImage img, double dScaleFactor, Object hint, boolean bHighQuality) {
        BufferedImage imgScale = img;

        int iImageWidth = (int) Math.round(img.getWidth() * dScaleFactor);
        int iImageHeight = (int) Math.round(img.getHeight() * dScaleFactor);

//        System.out.println("Scale Size = " + iImageWidth + "x" + iImageHeight);
        if (dScaleFactor <= 1.0d) {
            imgScale = getScaledDownInstance(img, iImageWidth, iImageHeight, hint, bHighQuality);
        } else {
            imgScale = getScaledUpInstance(img, iImageWidth, iImageHeight, hint, bHighQuality);
        }

        return imgScale;
    }

    protected static BufferedImage getScaledDownInstance(BufferedImage img,
            int targetWidth,
            int targetHeight,
            Object hint,
            boolean higherQuality) {

        int type = (img.getTransparency() == Transparency.OPAQUE)
                ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;

        BufferedImage ret = (BufferedImage) img;
        if (targetHeight > 0 || targetWidth > 0) {
            int w, h;
            if (higherQuality) {
                // Use multi-step technique: start with original size, then
                // scale down in multiple passes with drawImage()
                // until the target size is reached
                w = img.getWidth();
                h = img.getHeight();
            } else {
                // Use one-step technique: scale directly from original
                // size to target size with a single drawImage() call
                w = targetWidth;
                h = targetHeight;
            }

            do {
                if (higherQuality && w > targetWidth) {
                    w /= 2;
                    if (w < targetWidth) {
                        w = targetWidth;
                    }
                }

                if (higherQuality && h > targetHeight) {
                    h /= 2;
                    if (h < targetHeight) {
                        h = targetHeight;
                    }
                }

                BufferedImage tmp = new BufferedImage(Math.max(w, 1), Math.max(h, 1), type);
                Graphics2D g2 = tmp.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
                g2.drawImage(ret, 0, 0, w, h, null);
                g2.dispose();

                ret = tmp;
            } while (w != targetWidth || h != targetHeight);
        } else {
            ret = new BufferedImage(1, 1, type);
        }
        return ret;
    }

    protected static BufferedImage getScaledUpInstance(BufferedImage img,
            int targetWidth,
            int targetHeight,
            Object hint,
            boolean higherQuality) {

        int type = BufferedImage.TYPE_INT_ARGB;

        BufferedImage ret = (BufferedImage) img;
        int w, h;
        if (higherQuality) {
            // Use multi-step technique: start with original size, then
            // scale down in multiple passes with drawImage()
            // until the target size is reached
            w = img.getWidth();
            h = img.getHeight();
        } else {
            // Use one-step technique: scale directly from original
            // size to target size with a single drawImage() call
            w = targetWidth;
            h = targetHeight;
        }

        do {
            if (higherQuality && w < targetWidth) {
                w *= 2;
                if (w > targetWidth) {
                    w = targetWidth;
                }
            }

            if (higherQuality && h < targetHeight) {
                h *= 2;
                if (h > targetHeight) {
                    h = targetHeight;
                }
            }

            BufferedImage tmp = new BufferedImage(w, h, type);
            Graphics2D g2 = tmp.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
            g2.drawImage(ret, 0, 0, w, h, null);
            g2.dispose();

            ret = tmp;
            tmp = null;

        } while (w != targetWidth || h != targetHeight);
        return ret;
    }
    public void chooseFile(){
        JFileChooser chooser = new JFileChooser(); 
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);    
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
            ProjectAnalysis a = new ProjectAnalysis(chooser.getSelectedFile().toString());
            setContentPane(new JScrollPane(a.showProject()) {
            @Override
            protected void processMouseWheelEvent(MouseWheelEvent e) {
                Point oldPosition = getViewport().getViewPosition();
                super.processMouseWheelEvent(e);
                if(getViewport().getViewPosition().y == oldPosition.y) {
                    delegateToParent(e);
                }
                getVerticalScrollBar().setUnitIncrement(20);
                }
                private void delegateToParent(MouseWheelEvent e) {
                    JScrollPane ancestor = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, this);
                    if (ancestor != null) {
                        MouseWheelEvent converted = null;
                        for (MouseWheelListener listener : ancestor.getMouseWheelListeners()) {
                            listener.mouseWheelMoved(converted != null ? converted:(converted = (MouseWheelEvent) SwingUtilities.convertMouseEvent(this, e, ancestor)));
                        }
                    }
                }
            });
            validate();
            setExtendedState(JFrame.MAXIMIZED_BOTH); 
        }
    }
}
