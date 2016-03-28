import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.*;


public class Starter {
    protected static final String FILE_HISTORY = "SBER-3-2.txt";
//    protected static final String FILE_HISTORY = "test.txt";


     public static void main(String[] args) throws FileNotFoundException {

         List<Tik> history = loadHistory();
         BigDecimal minPips = getMinPips(history);
         new KrukTester(history, minPips);
    }

    private static BigDecimal getMinPips(List<Tik> history) {

        Set<BigDecimal> allPrices = getAllPrices(history);

        BigDecimal pips = new BigDecimal(Integer.MAX_VALUE);
        BigDecimal prevPrice = new BigDecimal(0);
        for (BigDecimal currentPrice : allPrices) {
            if (currentPrice.subtract(prevPrice).compareTo(pips) == -1) {
                pips = currentPrice.subtract(prevPrice);
            }
            prevPrice=currentPrice;

        }
        return pips;
    }

    private static Set<BigDecimal> getAllPrices(List<Tik> history) {
        Set<BigDecimal> allPrices = new TreeSet<>();
        for (Tik tik : history) {
            allPrices.add(tik.open);
            allPrices.add(tik.high);
            allPrices.add(tik.low);
            allPrices.add(tik.close);
        }
        return allPrices;
    }

    private static List<Tik> loadHistory() throws FileNotFoundException {
        List<Tik> history = new ArrayList<>();
        Scanner fileHistoryScanner = new Scanner(new FileInputStream(FILE_HISTORY));

        while (fileHistoryScanner.hasNext()) {
          history.add(new Tik(fileHistoryScanner.next()));
        }
        return history;

    }


}
