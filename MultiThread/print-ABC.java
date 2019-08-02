public class Main {


    static class ThreadABC implements Runnable {
        private char ch;
        private MajorClass m;

        ThreadABC(char c, MajorClass m) {
            ch = c;
            this.m = m;
        }

        @Override
        public void run() {
            int cn = 10;
            int t = 0;
            while (cn > 0) {
                synchronized (m) {
                    if (m.flag == ch) {
                        System.out.print(ch);
                        cn--;
                        switch (m.flag) {
                            case 'A':
                                m.flag = 'B';
                                break;
                            case 'B':
                                m.flag = 'C';
                                break;
                            case 'C':
                                m.flag = 'A';
                                break;
                        }
                    }
                }
                System.out.print("[" + ch + (t++) + "]");
            }
        }
    }

    static class MajorClass {
        MajorClass() {
            flag = 'A';
        }

        char flag;

    }

    public static void main(String[] args) {
        MajorClass maj = new MajorClass();

        Thread th1 = new Thread(new ThreadABC('A', maj));
        Thread th2 = new Thread(new ThreadABC('B', maj));
        Thread th3 = new Thread(new ThreadABC('C', maj));

        th1.start();
        th2.start();
        th3.start();
    }
}
