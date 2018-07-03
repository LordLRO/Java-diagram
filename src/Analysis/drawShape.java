package Analysis;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
interface Paintable{
    public void paint(JComponent parent, Graphics2D g2,double scale);
    public boolean contains(Point p);
    public void moveTo(Point2D p);
    public Rectangle2D getBounds();
}
    
public class drawShape implements Paintable{
    private static final long serialVersionUID = 1L;
    private ClassContents clazz;
    private int x;
    private int y;
    private int width;
    private int height;
    private Rectangle2D bound;
    private double scale ;
    private int stringwidth;
    private int stringHeight;
    public drawShape(ClassContents a,int b,int c){
        clazz = a;
        x = b;
        y = c;
        bound = new Rectangle2D.Double(b,c,findMaxWidth(a.getClassName(), a.getMethods(), a.getVariables()), 18 + findMaxHeight(a.getMethods()) + findMaxHeight(a.getVariables()));
    }
    @Override
    public boolean contains(Point p){
        return bound.contains(p);
    }
    @Override
    public void moveTo(Point2D p){
        bound = new Rectangle2D.Double(p.getX(),p.getY(),width,height);
    }
    @Override
    public Rectangle2D getBounds(){
        return bound;
    }
    @Override
    public void paint(JComponent parent,Graphics2D g,double scale){
        this.scale = scale;
        Font a = new Font("",Font.BOLD,(int)(12 + scale*0.9));
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(bound.getX(), bound.getY());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);  
        g2.setFont(a);
        FontMetrics fm = g.getFontMetrics();
        stringHeight = fm.getHeight();
        stringwidth = fm.stringWidth(findMax(clazz));
        this.width = (int)(stringwidth * (1.4 + scale*0.06));
        this.height = (int)(stringHeight * (1.13 + scale*0.04));
        
        if(clazz.getclassORinterface().equals("interface")){
            g2.setColor(Color.yellow);
            g2.fillRect(0 , 0, width , height * 2 );
            g2.setColor(Color.black);
            g2.drawRect(0 , 0, width , height * 2); 
            g2.drawString("<<inteface>>", (width - fm.stringWidth("<<inteface>>"))/2, (float) ((height - fm.getHeight())/2 + fm.getAscent()*1.3));
            g2.drawString(clazz.getClassName(),(width - fm.stringWidth(clazz.getClassName()))/2, (float) (((int)(height*2.5) - fm.getHeight())/2 + fm.getAscent()*1.3));
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0 ,height*2 , width , height * clazz.getVariables().size());  
            g2.setColor(Color.black);
            g2.drawRect(0 ,height*2 , width , height * clazz.getVariables().size()); 
            for(int i = 0; i < clazz.getVariables().size();i++){
                g2.drawString(clazz.getVariables().get(i).getFeature(), 5,height*2 + (float)(0.5*scale) + (int)(fm.getAscent()*(i + 1 + ((scale > 0)? i*scale*0.1:i*scale*0.05) )));  
            }
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0,height*2 + height * clazz.getVariables().size() , width  ,height * clazz.getMethods().size() );  
            g2.setColor(Color.black);
            g2.drawRect(0,height*2 + height * clazz.getVariables().size(), width ,height * clazz.getMethods().size() ); 
            if(clazz.getMethods().size() == 0){
                g2.drawString("", 5, height*2 + height * clazz.getVariables().size() + (int)(fm.getAscent()) );  
            } else{
                for(int i = 0; i < clazz.getMethods().size();i++){
                    g2.drawString(clazz.getMethods().get(i).getFeature()+ "()", 5, height*2 + (float)(0.5*scale) + height * clazz.getVariables().size() + (int)(fm.getAscent()*(i + 1 + ((scale > 0)? i*scale*0.1:i*scale*0.05))) );  
                }                
            }
            height = height*2 + height * clazz.getVariables().size() + height * clazz.getMethods().size();
        } else{
            g2.setColor(Color.ORANGE);
            g2.fillRect(0 , 0, width , height);
            g2.setColor(Color.black);
            g2.drawRect(0 , 0, width , height); 
            g2.drawString(clazz.getClassName(),(width - fm.stringWidth(clazz.getClassName()))/2, (float) ((height - fm.getHeight())/2 + fm.getAscent()*1.2));
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0 ,height , width , height * clazz.getVariables().size());  
            g2.setColor(Color.black);
            g2.drawRect(0 ,height , width , height * clazz.getVariables().size()); 
            for(int i = 0; i < clazz.getVariables().size();i++){
                g2.drawString(clazz.getVariables().get(i).getFeature(), 5,height + (float)(0.5*scale) +(int)(fm.getAscent()*(i + 1 + ((scale > 0)? i*scale*0.1:i*scale*0.05) )));  
            }
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRect(0,height + height * clazz.getVariables().size() , width  ,height * clazz.getMethods().size() );  
            g2.setColor(Color.black);
            g2.drawRect(0,height + height * clazz.getVariables().size(), width ,height * clazz.getMethods().size() ); 
            if(clazz.getMethods().size() == 0){
                g2.drawString("", 5, height*2 + height * clazz.getVariables().size() + (int)(fm.getAscent()) );  
            } else{
                for(int i = 0; i < clazz.getMethods().size();i++){
                    g2.drawString(clazz.getMethods().get(i).getFeature()+ "()", 5, height + (float)(0.5*scale) + height * clazz.getVariables().size() + (int)(fm.getAscent()*(i + 1 + ((scale > 0)? i*scale*0.1:i*scale*0.05))) );  
                }                
            }

            height = height + height * clazz.getVariables().size() + height * clazz.getMethods().size();            
        }
       
        g2.dispose();
    }
    public int findMaxWidth(String a,ArrayList<Feature> b,ArrayList<Feature> c){
        int max = (a.length() <= Max(b))? Max(b):a.length();
        return (max <= Max(c))? Max(c) * 6 :max * 6 ;
    }
    public int Max(ArrayList<Feature> b){
        int max = b.get(0).getFeature().length();
        for(int i = 1; i < b.size();i++){
            if(max <= b.get(i).getFeature().length()){
                max = b.get(i).getFeature().length();
            }
        }
        return max;
    }
    public int findMaxHeight(ArrayList<Feature> a){ 
        return (int)(a.size() *18) ;
    }
    public String findMax(ClassContents a){
        String max = a.getClassName();
        for(Feature method:a.getMethods()){
            if((method.getFeature() + "()").length() > max.length()){
                max = method.getFeature() + "()";
            }
        }
        for(Feature var:a.getVariables()){
            if(var.getFeature().length() > max.length()){
                max = var.getFeature();
            }
        }
        if(max.length() < ("<<interface>>").length()){
            max = "<<interface>>";
        }
        return max;
    }
    public ClassContents getClazz(){
        return clazz;
    }
}
