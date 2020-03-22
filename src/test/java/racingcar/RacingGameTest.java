package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RacingGameTest {

    private static final String BASE_CAR_NAMES = "피카츄,라이츄,파이리";

    @ParameterizedTest
    @DisplayName("우승자 선정 테스트")
    @CsvSource(value = {"5,1,2:피카츄", "1,2,2:라이츄,파이리", "3,3,3:피카츄,라이츄,파이리"}, delimiter = ':')
    void getWinnerTest(String carsMoveCount, String expectedWinnersName) {
        RacingGame racingGame = new RacingGame(BASE_CAR_NAMES);
        String[] splitMoveCount = carsMoveCount.split(",");

        ArrayList<Car> cars = racingGame.getCurrentCars();
        for (int i = 0; i < cars.size(); i++) {
            cars.get(i).move(Integer.valueOf(splitMoveCount[i]));
        }

        ArrayList<Car> winners = racingGame.getWinners(cars);

        assertThat(winners.stream()
                .map(Car::getName)
                .collect(Collectors.joining(","))
        ).isEqualTo(expectedWinnersName);
    }

    @ParameterizedTest
    @DisplayName("모든차가 움직임에 성공했을 때 테스트")
    @CsvSource(value = {"5:1", "6:2", "7:3", "8:4", "9:4"}, delimiter = ':')
    void startRacingAllSuccessTest(int randomValue, int runCount) {
        RacingGame racingGame = new RacingGame(BASE_CAR_NAMES, () -> randomValue);
        ArrayList<Car> carsAtInit = racingGame.getCurrentCars();

        racingGame.startRacing(runCount);
        ArrayList<Car> carsAtEnd = racingGame.getCurrentCars();

        for (int i = 0; i < carsAtEnd.size(); i++) {
            assertThat(carsAtEnd.get(i).getPosition()).isEqualTo(carsAtInit.get(i).getPosition() + runCount);
        }
    }

    @ParameterizedTest
    @DisplayName("모든차가 움직임에 실패했을 때 테스트")
    @CsvSource(value = {"0:1", "1:2", "2:3", "3:4", "4:4"}, delimiter = ':')
    void startRacingAllFailTest(int randomValue, int runCount) {
        RacingGame racingGame = new RacingGame(BASE_CAR_NAMES, () -> randomValue);
        ArrayList<Car> carsAtInit = racingGame.getCurrentCars();
        racingGame.startRacing(runCount);
        ArrayList<Car> carsAtEnd = racingGame.getCurrentCars();

        for (int i = 0; i < carsAtEnd.size(); i++) {
            assertThat(carsAtEnd.get(i).getPosition()).isEqualTo(carsAtInit.get(i).getPosition());
        }
    }

    @ParameterizedTest
    @DisplayName("실행수 입력 값이 1이상이 아니면 IllegalArgumentException 테스트")
    @ValueSource(ints = {0, -1, -99})
    void validateRunCountTest(int runCount) {
        assertThatThrownBy(() -> {
                    RacingGame racingGame = new RacingGame(BASE_CAR_NAMES);
                    racingGame.startRacing(runCount);
                }
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("입력한 차의 대수가 2이상이 아니면 IllegalArgumentException 테스트")
    @ValueSource(strings = {"피카츄", "피카츄,", "파이리"})
    void validateCarNamesTest(String carNames) {
        assertThatThrownBy(() -> {
                    RacingGame racingGame = new RacingGame(carNames);
                }
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
