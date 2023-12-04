import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;

interface CountriesService {


  Single<Integer> countCountries(List<Country> countries);

  Observable<Long> listPopulationOfEachCountry(List<Country> countries);

  Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries);

  /**
   * @param countriesFromNetwork an async task which is sometimes very very slow
   * @return the filtered values from the {@link FutureTask} or an {@link Observable#empty()} if there are no values within 1 second
   */
  Observable<Country> listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(FutureTask<List<Country>> countriesFromNetwork);

  Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries);

  Observable<Long> sumPopulationOfCountries(List<Country> countries);

  Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries);
}
