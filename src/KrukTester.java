import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * Created by user1 on 25.03.2016.
 */
public class KrukTester {
    protected static final int MIN_BREACKDOWN_TO_TRADE = 50;
    protected static final int MAX_BREAKDOWN_TO_TRADE = 51;
    protected static final int MIN_PROFIT = 2;
    protected static final int MAX_PROFIT = 150;
    BigDecimal minPips;
    List<Tik> history;


    public KrukTester(List<Tik> history, BigDecimal minPips) {
        this.history=history;
        this.minPips = minPips;

        for (int breackdown = MIN_BREACKDOWN_TO_TRADE; breackdown < MAX_BREAKDOWN_TO_TRADE; breackdown++) {
            for (int profit = MIN_PROFIT; profit < MAX_PROFIT; profit++) {
                Iterator<Tik> iterator = history.iterator();
                boolean isFirstTik = true;

                // just any tik for init new Tik
                Tik prevTik = new Tik("20160322,100000,112.28,112.63,112.1,112.47,1038180");

                while (iterator.hasNext()) {
                    if (isFirstTik) {
                        isFirstTik = false;
                        prevTik = iterator.next();
                        continue;
                    }

                    Tik curTik = iterator.next();

                    BigDecimal priceToOpenLong = getPriceToLong(minPips, breackdown, prevTik);
                    BigDecimal priceToOpenShort = getPriceToShort(minPips, breackdown, prevTik);


                    if (isTradeToLongStarted(curTik, priceToOpenLong)) {

                        System.out.println("long"+minPips.multiply(new BigDecimal(breackdown))+"\n prev:"+prevTik+"\n cur:"+curTik);
                    }


                    if (isTradeToShortStarted(curTik, priceToOpenShort)) {

                        System.out.println("short"+minPips.multiply(new BigDecimal(breackdown))+"\n prev:"+prevTik+"\n cur:"+curTik);
                    }
                    prevTik=curTik;

                }
            }

        }
    }

    private boolean isTradeToLongStarted(Tik curTik, BigDecimal priceToOpenLong) {
        return priceToOpenLong.compareTo(curTik.low) == 1 || priceToOpenLong.compareTo(curTik.low) == 0;
    }

    private boolean isTradeToShortStarted(Tik curTik, BigDecimal priceToOpenShort) {
        return priceToOpenShort.compareTo(curTik.high) == -1 || priceToOpenShort.compareTo(curTik.high) == 0;
    }

    private BigDecimal getPriceToShort(BigDecimal minPips, int breackdown, Tik prevTik) {
        return prevTik.close.add(minPips.multiply(new BigDecimal(breackdown)));
    }

    private BigDecimal getPriceToLong(BigDecimal minPips, int breackdown, Tik prevTik) {
        return prevTik.close.subtract(minPips.multiply(new BigDecimal(breackdown)));
    }
}
