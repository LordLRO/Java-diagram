package Analysis;

import java.util.ArrayList;
import java.io.File;
import javax.swing.JPanel;

public class ProjectAnalysis {
    private String projectAddress;
    protected ArrayList<PackageContents> packageLists = new ArrayList();

    public ArrayList<PackageContents> getPackageLists() {
        return packageLists;
    }
    
    public ProjectAnalysis(String address) {
        projectAddress = address;
        final File folder = new File(projectAddress); 
        readPackage(folder);
    }
    
    public void readPackage(final File folder) {
        PackageContents pack = new PackageContents();
        boolean folderContaisClass = false;
        for (final File file: folder.listFiles()) {
            if (file.isDirectory()) 
                readPackage(file);
            else {
                pack.readFile(file);
                folderContaisClass = true;
            }                
        }
        if (folderContaisClass) {
            packageLists.add(pack);
        }
    }
    
//    public void showProject(int a) {
//        for (PackageContents pack: packageLists) {
//            System.out.println("* * * * *");
//            System.out.println("Package: " + pack.getPackageName());
//            pack.showClassList();
//        }
//    }
    public JPanel showProject(){
        return new testPanel(this);
    }  
    
//    public static void main(String[] args) {
//        ProjectAnalysis debug = new ProjectAnalysis(".\\src");
////        debug.showProject(1);
//    }

}
