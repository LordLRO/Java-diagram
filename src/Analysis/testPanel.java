
package Analysis;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class testPanel extends JPanel{
    private static final long serialVersionUID = 1L;
    private ArrayList<drawShape> rect ; 
    private Point2D offset;
    private drawShape selectedShape;
    private ArrayList<Relate1>[] relate;
    private double scale = 0;
    private int Width = 2400;
    private int Height = 1200;
    
    public testPanel(ProjectAnalysis a){
        rect = new ArrayList();
        relate = new  ArrayList[a.getPackageLists().size()];
        for(int i = 0; i < a.getPackageLists().size();i++){
            relate[i] = new ArrayList<Relate1>();
        }
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setSize(Width,Height);
        JButton out = new JButton("Xuất ảnh");
        out.setPreferredSize(new Dimension(250, 50));
        out.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                saveComponentAsJPEG();
                JOptionPane.showMessageDialog(null,"File đã được xuất ra dạng jpg!");
            }
        });
        JButton zoomin = new JButton("Phóng to");
        zoomin.setPreferredSize(new Dimension(250, 50));
        zoomin.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                scale = (scale <= 4)? scale + 1: scale;
                repaint();
            }
        });
        JButton zoomout = new JButton("Thu nhỏ");
        zoomout.setPreferredSize(new Dimension(250, 50));
        zoomout.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                scale = (scale >= -4)? scale - 1: scale;
                repaint();
            }
        });
        add(out);
        add(zoomin);
        add(zoomout);
        int size = 0;
        for(PackageContents p: a.getPackageLists()){
            size += p.getClassList().size();
        }
        sortGroup so = new sortGroup(a);
        int width = 10;
        int height = 100;
        int heightMax = 0;
        for(ArrayList<ClassContents> c: so.getRect()){
            for(ClassContents clazz: c){
                RectSize rectSize = new RectSize(clazz);
                if(heightMax < rectSize.getHeight()){
                    heightMax = rectSize.getHeight();
                }
                if(width >= Width - 100 - rectSize.getWidth()){
                    Width += 300 + rectSize.getWidth();
                    drawShape rect1 = new drawShape(clazz,width,height);
                    addShape(rect1);
                    width += 200 + rectSize.getWidth();   
                } else {
                    if(height < Height - rectSize.getHeight()){
                        drawShape rect1 = new drawShape(clazz,width,height);
                        addShape(rect1);
                        width += 200 + rectSize.getWidth(); 
                    } else{
                        Height += rectSize.getHeight() + 150;
                        drawShape rect1 = new drawShape(clazz,width,height);
                        addShape(rect1);
                        width += 200 + rectSize.getWidth(); 
                    }
                    
                }       
            }
            height += heightMax + 150;
            width = 100;
        }
        
        int k = 0;
        for(PackageContents p: a.getPackageLists()){
            for(int i = 0; i < p.getClassList().size();i++){
                for(int j = 0; j < p.getClassList().size();j++){
                    if(rect.get(i).getClazz().isRelate(rect.get(j).getClazz()) != null){
                        Relate1 re = new Relate1(rect.get(j), rect.get(i), rect.get(i).getClazz().isRelate(rect.get(j).getClazz()));
                        relate[k].add(re);
                    }
                }
            }
            k++;
        }
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e){
                
            }
            @Override
            public void mousePressed(MouseEvent e) {
                for (drawShape p : getRect()) {
                    if (p.contains(e.getPoint())) {
                        // Selected
                        selectedShape = p;
                        offset = new Point2D.Double(e.getX() - p.getBounds().getX(), e.getY() - p.getBounds().getY());
                        break;
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                selectedShape = null;
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedShape != null) {

                    Point2D p = new Point2D.Double(e.getX() - offset.getX(), e.getY() - offset.getX());

                    selectedShape.moveTo(p);
                }
                repaint();
            }
            
        });
        
        revalidate();
        setVisible(true);
    }
    @Override
    public Dimension getPreferredSize(){
        if(isPreferredSizeSet()){
            return super.getPreferredSize();
        }
        return new Dimension(Width,Height);
    }
    public void addShape(drawShape a){
        rect.add(a);
        repaint();
    }
    public ArrayList<drawShape> getRect(){
        return rect;
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();  
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); 
        for(int i = 0; i < relate.length;i++){
            for(Relate1 re: relate[i]){
                Point2D p1 = new Point2D.Double(re.getParent().getBounds().getX() + re.getParent().getBounds().getWidth()/2,re.getParent().getBounds().getY() + re.getParent().getBounds().getHeight());
                Point2D p2 = new Point2D.Double(re.getChild().getBounds().getX() + re.getChild().getBounds().getWidth()/2,re.getChild().getBounds().getY());
                drawArrow(g2, p1, p2, re.getInheritType());
            }
        }    
        for(drawShape a: rect){
            a.paint(this, g2, scale);
        }
        
        updateBound();
        g2.dispose();
    }
    
    private void updateBound() {
        for (drawShape shape : rect) {
            shape.moveTo(new Point2D.Double(shape.getBounds().getX(), shape.getBounds().getY()));
        }
    }
    
    private void drawArrow(Graphics2D g2, Point2D p1, Point2D p2, String relation) {
        if (relation.equals("extends")) {
            //draw triangle
            g2.draw(new Line2D.Double(p1.getX(), p1.getY(), p1.getX() - 8, p1.getY() + 10));
            g2.draw(new Line2D.Double(p1.getX(), p1.getY(), p1.getX() + 8, p1.getY() + 10));
            g2.draw(new Line2D.Double(p1.getX() - 8, p1.getY() + 10, p1.getX() + 8, p1.getY() + 10));
            //draw line
            Point2D O = new Point2D.Double(p1.getX() , p1.getY() + 10);
            Point2D G = new Point2D.Double(p2.getX() - 2, p2.getY());
            Point2D A = new Point2D.Double(O.getX() , (O.getY() + G.getY())/2);
            Point2D B = new Point2D.Double(G.getX(), A.getY());
            //draw
            g2.draw(new Line2D.Double(O, A));
            g2.draw(new Line2D.Double(A, B));
            g2.draw(new Line2D.Double(B, G));
        }
        else if (relation.equals("implements")) {
            //draw triangle
            g2.draw(new Line2D.Double(p1.getX(), p1.getY(), p1.getX() - 8, p1.getY() + 10));
            g2.draw(new Line2D.Double(p1.getX(), p1.getY(), p1.getX() + 8, p1.getY() + 10));
            g2.draw(new Line2D.Double(p1.getX() - 8, p1.getY() + 10, p1.getX() + 8, p1.getY() + 10));
            //draw dashed line
            Point2D O = new Point2D.Double(p1.getX() , p1.getY() + 10);
            Point2D G = new Point2D.Double(p2.getX() + 2, p2.getY());
            Point2D A = new Point2D.Double(O.getX() , (O.getY() + G.getY())/2 + 5);
            Point2D B = new Point2D.Double(G.getX(), A.getY());
            //set dashed line mode
            Stroke defaultStroke = g2.getStroke();
            Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            g2.setStroke(dashed);
            //draw
            g2.draw(new Line2D.Double(O, A));
            g2.draw(new Line2D.Double(A, B));
            g2.draw(new Line2D.Double(B, G));
            //set back to default
            g2.setStroke(defaultStroke);
            
        }
        
    }
    
    public void saveComponentAsJPEG() {
            Dimension size = this.getSize();
            BufferedImage myImage = new BufferedImage(size.width, size.height,BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = myImage.createGraphics();
            this.paint(g2);
            try {
                OutputStream out = new FileOutputStream("output.jpg");
                ImageIO.write(myImage,"jpg",out);
                out.flush();
                out.close();
            } catch (Exception e) {
                System.out.println(e); 
            }
    }
    
}
