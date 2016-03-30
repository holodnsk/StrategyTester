import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class KrukTester {
    protected static final int MIN_BREACKDOWN_TO_TRADE = 35;
    protected static final int MAX_BREAKDOWN_TO_TRADE = 35;
    protected static final int MIN_PROFIT = 35;
    protected static final int MAX_PROFIT = 36;
    protected static final int MAX_STEPS_TO_TAKE_PROFIT = 180;
    BigDecimal minPips;
    List<Tik> history;
    List<Tik> fails = new ArrayList<>();
    List<Tik> completed = new ArrayList<>();



    public KrukTester(List<Tik> history, BigDecimal minPips) {
        this.history=history;
        this.minPips = minPips;

        // перебираем весь диапазон допустимых пробоев и на каждый из них весь диапазон допустимого профита
        System.out.println(":start");

            for (int intBreakdown = MIN_BREACKDOWN_TO_TRADE;
                 intBreakdown <= MAX_BREAKDOWN_TO_TRADE;
                 intBreakdown++) {
//                System.out.println("breakdown"+intBreakdown);
                BigDecimal breakdown = minPips.multiply(new BigDecimal(intBreakdown));

                BigDecimal prevFactorCompleteProfit = new BigDecimal(0);

                for (int expectedIncomeMultiplier = MIN_PROFIT;
                     expectedIncomeMultiplier < MAX_PROFIT;
                     expectedIncomeMultiplier++) {

                    BigDecimal expectedIncome = minPips.multiply(new BigDecimal(expectedIncomeMultiplier));


                    BigDecimal factorCompleteProfit = getStatsLongs(breakdown, expectedIncome, MAX_STEPS_TO_TAKE_PROFIT);
                    if (prevFactorCompleteProfit.compareTo(factorCompleteProfit)==1) break;

                }

            }
        for (Tik fail : completed) {
            System.out.println(fail);
        }

    }

    private BigDecimal getStatsLongs(BigDecimal breakdown, BigDecimal expectedIncome, int maxSteps) {

        int countComplete=0;
        int countFails=0;
        BigDecimal totalLost = new BigDecimal(0);

        boolean isFirstTik = true;
        Tik prevTik = new Tik("20160322,100000,112.28,112.63,112.1,112.47,1038180");

        for (Tik curTik : history) {
            if (isFirstTik) {
                isFirstTik = false;
                prevTik = curTik;
                continue;
            }
//            if (curTik.time.getHour() == 10 && curTik.time.getMinute() < 2) {
//                prevTik=curTik;
//                continue;
//            }

            BigDecimal priceToOpenLong = getPriceToOpenLong(breakdown, prevTik);
            BigDecimal goalPriceToCloseLong = getGoalPriceToCloseLong(priceToOpenLong, expectedIncome);


            if (isTradeToLongStarted(curTik, priceToOpenLong)) {
                BigDecimal tradeProfit = tradeToLong(curTik, priceToOpenLong, goalPriceToCloseLong, maxSteps);
                if (tradeProfit.compareTo(new BigDecimal(0))==1) {
                    countComplete++;
                    completed.add(curTik);
                } else {
                    countFails++;
                    totalLost= totalLost.add(tradeProfit);
                    fails.add(curTik);


                }

            }


            prevTik=curTik;
        }


        BigDecimal totalProfit = expectedIncome.multiply(new BigDecimal(countComplete));



                System.out.println("maxSteps:"+maxSteps+
                        " breakdown:"+breakdown+
                        " expectesIncome:"+expectedIncome+
                        " comlete:"+countComplete+
                        " fails:"+countFails+
                        " t.profit:"+totalProfit.add(totalLost)/*+
                        " completeFactor:"+countComplete/countFails*/);

        return totalProfit;
    }

    private BigDecimal getGoalPriceToCloseLong(BigDecimal priceToOpenLong, BigDecimal expectedIncome) {
        return priceToOpenLong.add(expectedIncome);
    }



    private BigDecimal tradeToLong(Tik curTik, BigDecimal priceToOpenLong, BigDecimal goalPriceToCloseLong, int maxStepsToTakeProfit) {
        for (int indexOfHistory = history.indexOf(curTik)+1; // skip tik of open trade
             indexOfHistory < history.indexOf(curTik)+maxStepsToTakeProfit && indexOfHistory<history.size();
             indexOfHistory++) {

            BigDecimal high = history.get(indexOfHistory).high;
            if (goalPriceToCloseLong.compareTo(high) == -1) {

                return goalPriceToCloseLong.subtract(priceToOpenLong);
            }

        }
        BigDecimal lost = history.get(maxStepsToTakeProfit).low.subtract(priceToOpenLong);
        return lost;

    }

    private boolean isTradeToLongStarted(Tik curTik, BigDecimal priceToOpenLong) {
        return priceToOpenLong.compareTo(curTik.low) >= 0 ;
    }



    private BigDecimal getPriceToOpenLong(BigDecimal breakdown, Tik prevTik) {
        return prevTik.close.subtract(breakdown);
    }
}
