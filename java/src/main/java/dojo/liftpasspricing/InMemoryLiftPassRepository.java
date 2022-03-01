package dojo.liftpasspricing;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class InMemoryLiftPassRepository implements LiftPassRepository{
    private final List<LiftPass> liftPasses = new ArrayList<>();

    @Override
    public void add(LiftPass liftPass) {
        liftPasses.add(liftPass);
    }

    @Override
    public LiftPass findBaseByPrice(String type) {
        return liftPasses.stream()
                .filter(liftPass -> liftPass.getType().toString().equalsIgnoreCase(type))
                .findFirst().orElseGet(null);
    }

    @Override
    public List<LocalDate> findAllHolidaysDates() {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2022-02-22"));
        } catch (ParseException e) { e.printStackTrace(); }
        return Collections.singletonList(calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    public List<LiftPass> getLastInserted() {
        return liftPasses;
    }
}
