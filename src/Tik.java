import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class Tik {
    LocalDateTime time;
    BigDecimal open;
    BigDecimal high;
    BigDecimal low;
    BigDecimal close;
    int value;

    @Override
    public String toString() {
        return "time=" + time +
                " open=" + open +
                " high=" + high +
                " low=" + low +
                " close=" + close +
                " value=" + value
                ;
    }

    public Tik(String DTOHLCV) {
        String[] dateTimeOpenLowHighCloseVol = DTOHLCV.split(",");

        int year = Integer.parseInt(dateTimeOpenLowHighCloseVol[0].substring(0,4));
        int month = Integer.parseInt(dateTimeOpenLowHighCloseVol[0].substring(4,6));
        int day = Integer.parseInt(dateTimeOpenLowHighCloseVol[0].substring(6,8));

        LocalDate localDate = LocalDate.of(year,month,day);

        int hour = Integer.parseInt(dateTimeOpenLowHighCloseVol[1].substring(0,2));
        int minute = Integer.parseInt(dateTimeOpenLowHighCloseVol[1].substring(2,4));

        LocalTime localTime = LocalTime.of(hour,minute);

        time = LocalDateTime.of(localDate,localTime);

        open = new BigDecimal(dateTimeOpenLowHighCloseVol[2]);
        high = new BigDecimal(dateTimeOpenLowHighCloseVol[3]);
        low = new BigDecimal(dateTimeOpenLowHighCloseVol[4]);
        close = new BigDecimal(dateTimeOpenLowHighCloseVol[5]);
        value = Integer.parseInt(dateTimeOpenLowHighCloseVol[6]);

    }
}
