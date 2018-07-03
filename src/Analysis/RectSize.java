
package Analysis;


import java.util.*;

public class RectSize {
    private ClassContents clazz;
    private int height;
    private int width;
    public RectSize(ClassContents a){
        clazz = a;
        height = findMaxHeight();
        width = findMaxWidth();
    }
    public int findMaxWidth(){
        int max = (clazz.getClassName().length() <= Max(clazz.getMethods()))? Max(clazz.getMethods()):clazz.getClassName().length();
        return (max <= Max(clazz.getVariables()))? Max(clazz.getVariables())*6:max * 6;
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
    public int findMaxHeight(){
        return 20 + (int)(clazz.getMethods().size() * 17.8) + (int)(clazz.getVariables().size()* 17.8);
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }    
}
