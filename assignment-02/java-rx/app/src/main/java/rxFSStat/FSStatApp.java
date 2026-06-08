package rxFSStat;

public class FSStatApp {
    public static void main(String[] args) {
        FSStatLib.getFSReport("C:/Users/luca/pcd-25/assignment-02", 100, 10);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
