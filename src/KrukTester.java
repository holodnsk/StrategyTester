import java.math.BigDecimal;
import java.util.List;


public class KrukTester {
    protected static final int MIN_BREACKDOWN_TO_TRADE = 6;
    protected static final int MAX_BREAKDOWN_TO_TRADE = 7;
    protected static final int MIN_PROFIT = 5;
    protected static final int MAX_PROFIT = 100;
    protected static final int MAX_STEPS_TO_TAKE_PROFIT = 100;
    BigDecimal minPips;
    List<Tik> history;



    public KrukTester(List<Tik> history, BigDecimal minPips) {
        this.history=history;
        this.minPips = minPips;

        // перебираем весь диапазон допустимых пробоев и на каждый из них весь диапазон опустимого профита
        System.out.println(":start");
        for (int intBreakdown = MIN_BREACKDOWN_TO_TRADE;
             intBreakdown < MAX_BREAKDOWN_TO_TRADE;
             intBreakdown++) {
            System.out.println("breakdown"+intBreakdown);
            BigDecimal breakdown = minPips.multiply(new BigDecimal(intBreakdown));

                for (int expectedIncomeMultiplier = MIN_PROFIT;
                     expectedIncomeMultiplier < intBreakdown;
                     expectedIncomeMultiplier++) {
                    BigDecimal expectedIncome = minPips.multiply(new BigDecimal(expectedIncomeMultiplier));
                    getFailsLongsThisBreackdownsAndExpectedIncome(breakdown, expectedIncome);

                }

        }
    }

    private void getFailsLongsThisBreackdownsAndExpectedIncome(BigDecimal breakdown, BigDecimal expectedIncome) {

        int countComplete=0;
        int countFails=0;

        boolean isFirstTik = true;
        Tik prevTik = new Tik("20160322,100000,112.28,112.63,112.1,112.47,1038180");

        for (Tik curTik : history) {
            if (isFirstTik) {
                isFirstTik = false;
                prevTik = curTik;
                continue;
            }

            BigDecimal priceToOpenLong = getPriceToOpenLong(breakdown, prevTik);
            BigDecimal goalPriceToCloseLong = getGoalPriceToCloseLong(priceToOpenLong, expectedIncome);


            if (isTradeToLongStarted(curTik, priceToOpenLong)) {
                if (tradeToLong(curTik, priceToOpenLong, goalPriceToCloseLong)) {
                    countComplete++;
                } else countFails++;

            }


            prevTik=curTik;
        }

        if (countComplete > 0) {

            System.out.println("breakdown:"+breakdown+" expectesIncome:"+expectedIncome+" comlete:"+countComplete+" fails:"+countFails);
        }
    }

    private BigDecimal getGoalPriceToCloseLong(BigDecimal priceToOpenLong, BigDecimal expectedIncome) {
        return priceToOpenLong.add(expectedIncome);
    }



    private boolean tradeToLong(Tik curTik, BigDecimal priceToOpenLong, BigDecimal goalPriceToCloseLong) {
        for (int indexOfHistory = history.indexOf(curTik)+1; // skip tik of open trade
             indexOfHistory < history.indexOf(curTik)+MAX_STEPS_TO_TAKE_PROFIT && indexOfHistory<history.size();
             indexOfHistory++) {

            BigDecimal high = history.get(indexOfHistory).high;
            if (goalPriceToCloseLong.compareTo(high) == -1) {

                return true;
            }

        }
        return false;

    }

    private boolean isTradeToLongStarted(Tik curTik, BigDecimal priceToOpenLong) {
        return priceToOpenLong.compareTo(curTik.low) >= 0 ;
    }



    private BigDecimal getPriceToOpenLong(BigDecimal breakdown, Tik prevTik) {
        return prevTik.close.subtract(breakdown);
    }
}
