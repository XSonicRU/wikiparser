import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

public class Main {
    static final boolean LANG = false; //false = rus, true = eng
    static final int words = 4; //how many words around the main word will be displayed
    static String target;
    private static int amount = 1000;
    private static int threads = 500;

    public static void main(String[] args) {
        target = new Scanner(System.in).nextLine();
        target = target.toLowerCase();
        for (int i = 0; i < threads; i++) {
            new wthr().start();
        }
    }

    static synchronized boolean decideifnextneeded() {
        amount--;
        if (amount % 100 == 0) {
            System.out.println();
            System.out.println(amount + " sites left");
        }
        if (amount >= threads) {
            return true;
        } else {
            if (amount <= 0) {
                System.exit(0);
            }
            return false;
        }
    }
}

class wthr extends Thread {
    @Override
    public void run() {
        do {
            String s1;
            String title = "";
            int readp = 0;
            String s;
            int p1;
            int p2;
            int c = 0;
            String target = Main.target;
            InputStream is = null;
            HttpsURLConnection u = null;
            while (true) {
                try {
                    u = (HttpsURLConnection) new URL(!Main.LANG ? "https://ru.wikipedia.org/wiki/%D0%A1%D0%BB%D1%83%D0%B6%D0%B5%D0%B1%D0%BD%D0%B0%D1%8F:" +
                            "%D0%A1%D0%BB%D1%83%D1%87%D0%B0%D0%B9%D0%BD%D0%B0%D1%8F_%D1%81%D1%82%D1%80%D0%B0%D0%BD%D0%B8%D1%86%D0%B0" :
                            "https://en.wikipedia.org/wiki/Special:Random").openConnection();
                    is = u.getInputStream();
                } catch (IOException e) {
                }
                if (is == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } else {
                    break;
                }
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                while (br.ready()) {
                    s = br.readLine();
                    s1 = s.toLowerCase();
                    c++;
                    if (c < 8) {
                        if (s.contains("<title>")) {
                            title = (s.substring(s.indexOf("<title>") + 7, s.indexOf(" —")));
                        }
                    }
                    int ind = s1.indexOf(target);
                    if (ind != -1 && s1.charAt(ind + target.length() + 1) == ' ' && s1.charAt(ind - 1) == ' ') {
                        p1 = ind;
                        p2 = ind;
                        for (int i = ind; true; i--) {
                            if (s.charAt(i) == ' ') {
                                if (readp <= Main.words) {
                                    readp++;
                                } else {
                                    break;
                                }
                            } else {
                                p1 = i;
                            }
                        }
                        readp = 0;
                        for (int i = ind + target.length(); true; i++) {
                            if (s.charAt(i) == ' ') {
                                if (readp <= Main.words) {
                                    readp++;
                                } else {
                                    break;
                                }
                            } else {
                                p2 = i;
                            }
                        }
                        System.out.println(title + "     " + s.substring(p1, p2 + 1));
                    }
                }
            } catch (Exception e) {
            }
            u.disconnect();
        } while (Main.decideifnextneeded());
    }
}