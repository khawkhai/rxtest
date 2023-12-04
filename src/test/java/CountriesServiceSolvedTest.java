import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CountriesServiceSolvedTest {

    private CountriesService countriesService;
    private List<Country> allCountries;

    @BeforeEach
    public void setUp() {
        countriesService = new CountriesServiceSolved();
        allCountries = CountriesTestProvider.countries();
    }

    @Test
    public void rx_CountAmountOfCountries() {
        Integer expected = CountriesTestProvider.countries().size();
        TestObserver<Integer> testObserver = countriesService
                .countCountries(allCountries)
                .test();
        testObserver.assertNoErrors();
        testObserver.assertValue(expected);
    }

    @Test
    public void rx_ListPopulationOfEachCountry() {
        List<Long> expectedResult = CountriesTestProvider.populationOfCountries();
        TestObserver<Long> testObserver = countriesService
                .listPopulationOfEachCountry(allCountries)
                .test();
        testObserver.assertValueSequence(expectedResult);
        testObserver.assertNoErrors();
    }

    @Test
    public void rx_ListPopulationMoreThanOneMillion() {
        List<Country> expectedResult = CountriesTestProvider.countriesPopulationMoreThanOneMillion();
        TestObserver<Country> testObserver = countriesService
                .listPopulationMoreThanOneMillion(allCountries)
                .test();
        testObserver.assertValueSequence(expectedResult);
        testObserver.assertNoErrors();
    }

    @Test
    public void rx_ListPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty_When_NoTimeout() throws InterruptedException {
        FutureTask<List<Country>> futureTask = new FutureTask<>(() -> {
            TimeUnit.MILLISECONDS.sleep(100);
            return allCountries;
        });
        new Thread(futureTask).start();
        TestObserver<Country> testObserver = countriesService
                .listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(futureTask)
                .test();
        List<Country> expectedResult = CountriesTestProvider.countriesPopulationMoreThanOneMillion();
        testObserver.await();
        testObserver.assertComplete();
        testObserver.assertValueSequence(expectedResult);
        testObserver.assertNoErrors();
    }

    @Test
    public void rx_ListPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty_When_Timeout() throws InterruptedException {
        FutureTask<List<Country>> futureTask = new FutureTask<>(() -> {
            TimeUnit.SECONDS.sleep(6);
            return allCountries;
        });
        new Thread(futureTask).start();
        TestObserver<Country> testObserver = countriesService
                .listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(futureTask)
                .test();
        testObserver.await();
        testObserver.assertComplete();
        testObserver.assertNoValues();
        testObserver.assertNoErrors();
    }

    @Test
    public void rx_GetCurrencyUsdIfNotFound_When_CountryFound() {
        String countryRequested = "Austria";
        String expectedCurrencyValue = "EUR";
        TestObserver<String> testObserver = countriesService
                .getCurrencyUsdIfNotFound(countryRequested, allCountries)
                .test();
        testObserver.assertResult(expectedCurrencyValue);
        testObserver.assertNoErrors();
    }

    @Test
    public void rx_GetCurrencyUsdIfNotFound_When_CountryNotFound() {
        String countryRequested = "Senegal";
        String expectedCurrencyValue = "USD";
        TestObserver<String> testObserver = countriesService
                .getCurrencyUsdIfNotFound(countryRequested, allCountries)
                .test();
        testObserver.assertResult(expectedCurrencyValue);
        testObserver.assertNoErrors();
    }

    @Test
    public void rx_SumPopulationOfCountries() {
        // hint: use "reduce" operator
        TestObserver<Long> testObserver = countriesService
                .sumPopulationOfCountries(allCountries)
                .test();
        testObserver.assertResult(CountriesTestProvider.sumPopulationOfAllCountries());
        testObserver.assertNoErrors();
    }


    @Test
    public void rx_MapCountriesToNamePopulation() {
        TestObserver<Map<String, Long>> values = countriesService.mapCountriesToNamePopulation(allCountries).test();
        Map<String, Long> expected = new HashMap<>();
        for (Country country : allCountries) {
            expected.put(country.name, country.population);
        }
        values.assertResult(expected);
        values.assertNoErrors();
    }

}
