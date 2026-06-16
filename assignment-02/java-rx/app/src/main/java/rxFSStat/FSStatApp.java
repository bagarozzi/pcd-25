package rxFSStat;

public class FSStatApp {
    public static void main(String[] args) {
        FSreport report = FSStatLib.getFSReport("", 100000, 10).blockingGet();
        System.out.println(report);
    }
}
