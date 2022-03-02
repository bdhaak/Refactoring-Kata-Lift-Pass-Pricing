package dojo.liftpasspricing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LiftPassService {

    private static final DateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static final double PERCENT_70 = .7;
    private static final double PERCENT_40 = .4;
    private static final double PERCENT_75 = .75;
    private static final int NO_REDUCTION = 0;
    private static final int PERCENT_35_REDUCTION = 35;

    private final LiftPassRepository liftPassRepository;

    public LiftPassService() {
        this(new MysqlLiftPassRepository());
    }

    @Deprecated // Only here for demo purposes. Can be removed.
    public LiftPassService(LiftPassRepository liftPassRepository) {
        this.liftPassRepository = liftPassRepository;
    }

    public static LiftPassService nullable() {
        return new LiftPassService(new InMemoryLiftPassRepository());
    }

    public void add(LiftPass liftPass) {
        liftPassRepository.add(liftPass);
    }

    public LiftPassPrice getLiftPassPrice(CustomerAge customerAge, String type, String date) throws InvalidCustomerAgeException {
        assertValidCustomerAge(customerAge);

        LiftPass liftPass = liftPassRepository.findBaseByPrice(type);

        if (customerAge.isUnderSixYears()) {
            return LiftPassPrice.FREE;
        }

        if (liftPass.isNightType()) {
            return calculateNightPrice(customerAge, liftPass);
        }

        return calculateDayPrice(customerAge, date, liftPass);
    }

    private void assertValidCustomerAge(CustomerAge customerAge) throws InvalidCustomerAgeException {
        if(customerAge.isInvalid()){
            throw new InvalidCustomerAgeException("Invalid customer age");
        }
    }

    private LiftPassPrice calculateNightPrice(CustomerAge customerAge, LiftPass liftPass) {
        int basePrice = liftPass.getCost();

        if (customerAge.isOlderThanSixtyFour()) {
            return new LiftPassPrice(costToInt(basePrice * PERCENT_40));
        }
        return new LiftPassPrice(basePrice);
    }

    private LiftPassPrice calculateDayPrice(CustomerAge customerAge, String date, LiftPass liftPass) {
        int basePrice = liftPass.getCost();
        int reduction = getReduction(date);
        double reductionPrice = (1 - reduction / 100.0);

        if (customerAge.isUnderFifteen()) {
            double cost = basePrice * PERCENT_70;
            return new LiftPassPrice(costToInt(cost));
        }

        if (customerAge.isOlderThanSixtyFour()) {
            double cost = basePrice * PERCENT_75 * reductionPrice;
            return new LiftPassPrice(costToInt(cost));
        }

        int defaultDayPrice = costToInt(basePrice * reductionPrice);
        return new LiftPassPrice(defaultDayPrice);
    }

    private int costToInt(double cost){
        return (int) Math.ceil(cost);
    }

    private int getReduction(String date) {
        if(date == null) return NO_REDUCTION;

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(ISO_FORMAT.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean isHoliday = isHoliday(date);

        // No holiday or if date is monday
        if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            return PERCENT_35_REDUCTION;
        }

        return NO_REDUCTION;
    }

    private boolean isHoliday(String date) {
        if (date == null) return false;

        List<LocalDate> holidaysDates = liftPassRepository.findAllHolidaysDates();
        for (LocalDate holiday : holidaysDates) {
            try {
                Date d = ISO_FORMAT.parse(date);
                LocalDate dataAsLocalDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if(holiday.isEqual(dataAsLocalDate))  return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
