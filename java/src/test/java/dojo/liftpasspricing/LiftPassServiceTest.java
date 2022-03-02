package dojo.liftpasspricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LiftPassServiceTest {

    private LiftPassService liftPassService;

    @Mock
    private MysqlLiftPassRepository repository;

    @BeforeEach
    void setUp() {
        liftPassService = new LiftPassService(repository);
    }

    @Test
    void should_add_liftpass() {
        var liftPass = new LiftPass(10, "1hour");
        liftPassService.add(liftPass);
        verify(repository, times(1)).add(liftPass);
    }

    @Test
    void should_return_free_when_age_is_under_6() throws InvalidCustomerAgeException{
        var liftPass = new LiftPass(10, "1hour");
        when(repository.findBaseByPrice(anyString())).thenReturn(liftPass);
        LiftPassPrice actualLiftPassPrice = liftPassService.getLiftPassPrice(new CustomerAge(5), "1hour", "2019-02-22");
        assertEquals(new LiftPassPrice(0), actualLiftPassPrice);
    }

    @Test
    void should_return_error_when_age_is_undefined()  {
        InvalidCustomerAgeException exception = assertThrows(InvalidCustomerAgeException.class, () -> {
            liftPassService.getLiftPassPrice(new CustomerAge(null), null, null);
        });

        assertEquals(new InvalidCustomerAgeException("Invalid customer age"), exception);
    }

    @Test
    void should_return_error_when_age_is_invalid()  {
        InvalidCustomerAgeException exception = assertThrows(InvalidCustomerAgeException.class, () -> {
            liftPassService.getLiftPassPrice(new CustomerAge(-20), null, null);
        });

        assertEquals(new InvalidCustomerAgeException("Invalid customer age"), exception);
    }

    @Nested
    @DisplayName("Tests for getting night time lift pass prices")
    class NightTime {
        @Test
        void should_return_without_discount_when_age_is_under_64() throws InvalidCustomerAgeException{
            var liftPass = new LiftPass(100, "night");
            when(repository.findBaseByPrice(anyString())).thenReturn(liftPass);
            LiftPassPrice actualLiftPassPrice = liftPassService.getLiftPassPrice(new CustomerAge(12), "night", "2019-02-22");
            assertEquals(new LiftPassPrice(100), actualLiftPassPrice);
        }

        @Test
        void should_return_with_60_percent_discount_when_age_is_above_64() throws InvalidCustomerAgeException{
            var liftPass = new LiftPass(100, "night");
            when(repository.findBaseByPrice(anyString())).thenReturn(liftPass);
            LiftPassPrice actualLiftPassPrice = liftPassService.getLiftPassPrice(new CustomerAge(67), "night", "2019-02-22");
            assertEquals(new LiftPassPrice(40), actualLiftPassPrice);
        }
    }

    @Nested
    @DisplayName("Tests for getting day time lift pass prices")
    class DayTime {

        @Test
        void should_return_with_30_percent_discount_when_age_is_under_15() throws InvalidCustomerAgeException{
            var liftPass = new LiftPass(100, "1hour");
            when(repository.findBaseByPrice(anyString())).thenReturn(liftPass);
            LiftPassPrice actualLiftPassPrice = liftPassService.getLiftPassPrice(new CustomerAge(12), "1hour", "2019-02-22");
            assertEquals(new LiftPassPrice(70), actualLiftPassPrice);
        }

        @Test
        void should_return_with_25_percent_discount_when_age_is_older_than_64() throws InvalidCustomerAgeException{
            var liftPass = new LiftPass(100, "1hour");
            when(repository.findBaseByPrice(anyString())).thenReturn(liftPass);
            LiftPassPrice actualLiftPassPrice = liftPassService.getLiftPassPrice(new CustomerAge(67), "1hour", "2019-02-22");
            assertEquals(new LiftPassPrice(75), actualLiftPassPrice);
        }

        @Test
        void should_return_with_51_percent_discount_when_age_is_older_than_64_and_is_monday() throws InvalidCustomerAgeException{
            var liftPass = new LiftPass(100, "1hour");
            when(repository.findBaseByPrice(anyString())).thenReturn(liftPass);
            LiftPassPrice actualLiftPassPrice = liftPassService.getLiftPassPrice(new CustomerAge(67), "1hour", "2022-02-21");
            assertEquals(new LiftPassPrice(49), actualLiftPassPrice);
        }

        @Test
        void should_return_with_35_percent_discount_when_no_holiday_but_monday() throws InvalidCustomerAgeException{
            var liftPass = new LiftPass(100, "1hour");
            when(repository.findBaseByPrice(anyString())).thenReturn(liftPass);
            LiftPassPrice actualLiftPassPrice = liftPassService.getLiftPassPrice(new CustomerAge(25), "1hour", "2022-02-28");
            assertEquals(new LiftPassPrice(65), actualLiftPassPrice);
        }

        @Test
        void should_return_without_discount_when_holiday_but_not_monday() throws ParseException, InvalidCustomerAgeException {
            var liftPass = new LiftPass(100, "1hour");
            when(repository.findBaseByPrice(anyString())).thenReturn(liftPass);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2022-02-22"));
            when(repository.findAllHolidaysDates()).thenReturn(Collections.singletonList(calendar.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
            LiftPassPrice actualLiftPassPrice = liftPassService.getLiftPassPrice(new CustomerAge(25), "1hour", "2022-02-22");
            assertEquals(new LiftPassPrice(100), actualLiftPassPrice);
        }

        @Test
        void should_return_regular_price_without_discounts() throws InvalidCustomerAgeException{
            var liftPass = new LiftPass(100, "1hour");
            when(repository.findBaseByPrice(anyString())).thenReturn(liftPass);
            LiftPassPrice actualLiftPassPrice = liftPassService.getLiftPassPrice(new CustomerAge(22), "1hour", "2019-02-22");
            assertEquals(new LiftPassPrice(100), actualLiftPassPrice);
        }
    }
}