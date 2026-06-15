package rxFSStat;

public class FSStatApp {
    public static void main(String[] args) {
        FSreport report = FSStatLib.getFSReport("C:/Users/luca/Downloads", 100, 10).blockingGet();
        System.out.println(report);
    }
}
