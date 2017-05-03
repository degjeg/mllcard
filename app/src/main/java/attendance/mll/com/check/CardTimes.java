package attendance.mll.com.check;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/5/2.
 */

public class CardTimes {
    public static class Row {
        String card_date; // ": "2017-05-02",
        String name; // ": "邓军",
        String userid; // ": 13846,
        int times; // ": 2,
        String card_times; // ": "09:40:53,09:40:57,",
        String badgenumber; // ": "000017950",
        String DeptName; // ": "事业六部-微信",
        String id; // ": 13846

        List<Date> cardTimes = new ArrayList<>();

        public String getCard_date() {
            return card_date;
        }

        public void setCard_date(String card_date) {
            this.card_date = card_date;

            setCard_times(card_times);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        public String getCard_times() {
            return card_times;
        }

        public void setCard_times(String card_times) {

            this.card_times = card_times;
            cardTimes.clear();

            if (card_times == null) return;
            if (card_date == null) return;

            SimpleDateFormat dataFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String[] ts = card_times.split(",");
            for (String t : ts) {
                if (t.trim().length() < 6) continue;
                String tmp = card_date + " " + t; // 2017-04-26 09:28:17
                try {
                    Date date = dataFormater.parse(tmp);
                    cardTimes.add(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        public String getBadgenumber() {
            return badgenumber;
        }

        public void setBadgenumber(String badgenumber) {
            this.badgenumber = badgenumber;
        }

        public String getDeptName() {
            return DeptName;
        }

        public void setDeptName(String deptName) {
            DeptName = deptName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<Date> getCardTimes() {
            return cardTimes;
        }
    }

    List<Row> rows;
    int total; // ": 1,
    int page;// ": 1

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
