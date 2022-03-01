package dojo.liftpasspricing;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LiftPassService {

    private static final DateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final double PERCENT_70 = .7;
    private static final double PERCENT_40 = .4;

    public static final double PERCENT_75 = .75;

    private final LiftPassRepository liftPassRepository;

    public LiftPassService() {
        this(new LiftPassRepository());
    }

    public LiftPassService(LiftPassRepository liftPassRepository) {
        this.liftPassRepository = liftPassRepository;
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
        else {
            return calculateDayPrice(customerAge, date, liftPass);
        }
    }

    private void assertValidCustomerAge(CustomerAge customerAge) throws InvalidCustomerAgeException {
        if(customerAge.isUndefined()){
            throw new InvalidCustomerAgeException("Invalid customer age");
        }
    }

    private LiftPassPrice calculateNightPrice(CustomerAge customerAge, LiftPass liftPass) {
        int basePrice = liftPass.getCost();

        if (customerAge.isOlderThanSixtyFour()) {
            return new LiftPassPrice(costToInt(basePrice * PERCENT_40));
        } else {
            return new LiftPassPrice(basePrice);
        }
    }

    private LiftPassPrice calculateDayPrice(CustomerAge customerAge, String date, LiftPass liftPass) {

        int basePrice = liftPass.getCost();

        int reduction = getReduction(date);
        double reductionPrice = (1 - reduction / 100.0);

        int defaultDayPrice = costToInt(basePrice * reductionPrice);

        if (customerAge.isUnderFifteen()) {
            double cost = basePrice * PERCENT_70;
            return new LiftPassPrice(costToInt(cost));
        }

        if (customerAge.isOlderThanSixtyFour()) {
            double cost = basePrice * PERCENT_75 * reductionPrice;
            return new LiftPassPrice(costToInt(cost));
        }

        return new LiftPassPrice(defaultDayPrice);
    }

    private int costToInt(double cost){
        return (int) Math.ceil(cost);
    }

    private int getReduction(String date) {
        int reduction = 0;

        if(date == null) return reduction;

        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(ISO_FORMAT.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean isHoliday = isHoliday(date);

        // No holiday or if date is monday
        if (!isHoliday && calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            reduction = 35;
        }

        return reduction;
    }

    private boolean isHoliday(String date) {
        if (date == null) return false;

        List<Date> holidaysDates = liftPassRepository.findAllHolidaysDates();
        for (Date holiday : holidaysDates) {
            Date d = null;
            try {
                d = ISO_FORMAT.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            if(d.compareTo(holiday) == 0)  return true;

            if (d.getYear() == holiday.getYear() && //
                    d.getMonth() == holiday.getMonth() && //
                    d.getDate() == holiday.getDate()) {
                return true;
            }
        }
        return false;
    }

}
