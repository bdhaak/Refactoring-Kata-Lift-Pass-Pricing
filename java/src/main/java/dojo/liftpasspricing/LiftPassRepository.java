package dojo.liftpasspricing;

import java.time.LocalDate;
import java.util.List;

public interface LiftPassRepository {

    void add(LiftPass liftPass);

    LiftPass findBaseByPrice(String type);

    List<LocalDate> findAllHolidaysDates();
}
