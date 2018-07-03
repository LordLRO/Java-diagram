package Analysis;

import java.util.*;

public class sortGroup {
    private ArrayList<ArrayList<ClassContents>> rect ;     
    private int Width;
    private int Height;
    public sortGroup(ProjectAnalysis a){
        ArrayList<ClassContents>[] allRect = new ArrayList[a.getPackageLists().size()];
        for(int i = 0; i < a.getPackageLists().size();i++){
            allRect[i] = new ArrayList<ClassContents>();
        }
        ArrayList<ClassContents> parentRect = new ArrayList<ClassContents>();
        rect = new ArrayList<ArrayList<ClassContents>>();
        int k = 0;
        for(PackageContents p : a.getPackageLists()){
            for(ClassContents clazz: p.getClassList()){
                allRect[k].add(clazz);
            }
            k++;
        }
        for(int i = 0; i < allRect.length;i++){
            int j = 0;
            if(allRect[i].size() == 0){
                continue;
            } else{
                do{
                    if( allRect[i].get(j).getExtendsInheritance() == null && allRect[i].get(j).getImplementsInheritance() == null){
                        parentRect.add(allRect[i].get(j));
                        allRect[i].remove(j);
                    } else{
                        j++;
                    }               
                }while(j < allRect[i].size());
            }
            
        }
               
        rect.add(parentRect);
        
       int i = 0;
        do{
            rect.add(findSub(rect.get(i), allRect));
            i++;
        }while(hasChild(rect.get(i),allRect));  

        if(allRect.length != 0){
            ArrayList<ClassContents> div = new ArrayList<ClassContents>();
            for(int j = 0; j < allRect.length;j++){
                if(allRect[j].size() != 0){
                    for(ClassContents clazz: allRect[j]){
                        div.add(clazz);
                    }    
                }
                
            }
            rect.add(div);
        }
       
//        for(ArrayList<ClassContents> c:rect){
//            for(ClassContents clazz: c){
//                System.out.print(clazz.getClassName() + " ");
//            }
//            System.out.println("");
//            
//        }
//        
    }        
    public ArrayList<ClassContents> findSub(ArrayList<ClassContents> father,ArrayList<ClassContents>[] all){
        ArrayList<ClassContents> sub = new ArrayList<ClassContents>();
            for(ClassContents clazz: father){
                for(int i = 0; i< all.length;i++){
                    int j = 0;
                    if(all[i].size() != 0){
                        while(j < all[i].size()){
                            if(all[i].get(j).isRelate(clazz) != null){
                                sub.add(all[i].get(j));
                                all[i].remove(j);
                            } else{
                                j++;
                            }
                        }    
                    } else{
                        continue;
                    }
                    
                }  
            }
            return sub;
        
    }
    public boolean hasChild(ArrayList<ClassContents> father,ArrayList<ClassContents>[] all){
        boolean z =false;
        for(ClassContents clazz: father){
                for(int i = 0; i< all.length;i++){
                    int j = 0;
                    if(all[i].size() != 0){
                        while(j < all[i].size()){
                            if(all[i].get(j).isRelate(clazz) != null){
                                z = true;
                                break;
                            } else{
                                j++;
                            }
                        }    
                    } else{
                        continue;
                    }
                    
                } 
        }        
        return z;
    }
    public boolean isEmpty(ArrayList<ClassContents>[] all){
        boolean z = true;
        for(int i = 0; i < all.length;i++){
            if(all[i].size() != 0){
                z = false;
            }
        }
        return z;
    }
    public ArrayList<ArrayList<ClassContents>> getRect(){
        return rect;
    }
}