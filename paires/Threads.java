
public class Threads {
        
        public static void main(String[] args) {
                
                TestThread t = new TestThread("A");
                TestThread t2 = new TestThread("  B");
                t.start();
                t2.start();
        }
}
