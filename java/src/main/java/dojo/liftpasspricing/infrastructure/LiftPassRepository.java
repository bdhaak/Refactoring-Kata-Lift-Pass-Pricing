package dojo.liftpasspricing.infrastructure;

import dojo.liftpasspricing.domain.LiftPass;

import java.time.LocalDate;
import java.util.List;

public interface LiftPassRepository {

    void add(LiftPass liftPass);

    LiftPass findBaseByPrice(String type);

    List<LocalDate> findAllHolidaysDates();
}
