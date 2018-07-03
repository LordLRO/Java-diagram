
package Analysis;

public class Relate1 {
    private drawShape parent;
    private drawShape child;
    private String inheritType;
    
    public Relate1(drawShape a, drawShape b, String inherit){
        parent = a;
        child = b;
        inheritType = inherit;
    }
    
    public drawShape getChild(){
        return child;
    }
    
    public drawShape getParent(){
        return parent;
    }
    
    public String getInheritType() {
        return inheritType;
    }
    
}
