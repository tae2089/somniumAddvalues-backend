package bin.study.memo.handler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class jsonHandler {
    public static void main(String[] args) {
        Date from = new Date();
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(transFormat.format(from));
    }
}
