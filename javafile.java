
// Java sample code to get the list of 
// installed Look and Feel themes, here is a sample code: 
import javax.swing.UIManager; 
  
public class LookAndFeelDemo {    public static void main(String[] a) 
    { 
        UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels(); 
        for (UIManager.LookAndFeelInfo look : looks) { 
            System.out.println(look.getClassName()); 
        } 
    } 
}