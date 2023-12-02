import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static final int RED_AMOUNT = 12;
    public static final int GREEN_AMOUNT = 13;
    public static final int BLUE_AMOUNT = 14;

    public static final String WRONG_INPUT_FILE_MESSAGE = "Erm... excuse me little elf, but those don't look like cubes.";

    public static void main(String[] args) throws IOException {
        final int result = extractInputLines().stream()
                .map(Main::toObject)
                .filter(Game::isValid)
                .mapToInt(Game::getId)
                .sum();

        System.out.println("A-ha! The answer to your little game is: " + result);
    }

    /**
     * Reads a number of not yet parsed cube withdrawals... This looks fun!
     *
     * @return many, many uninterpreted games!
     */
    private static List<String> extractInputLines() throws IOException {
        try (InputStream resource = Main.class.getResourceAsStream("input")) {
            if (resource == null) {
                throw new RuntimeException(WRONG_INPUT_FILE_MESSAGE);
            }

            return new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8))
                    .lines()
                    .toList();
        }
    }

    /**
     * Turns a raw input line into a {@link Game} object.
     *
     * @param inputLine raw string with game input.
     * @return {@link Game} object with utilities for all your elvish needs.
     */
    private static Game toObject(final String inputLine) {
        final String[] gameInfoAndWithdrawals = inputLine.split(": ");
        final String[] withdrawalCombinations = gameInfoAndWithdrawals[1].split("; ");

        final int gameId = Integer.parseInt(gameInfoAndWithdrawals[0].split(" ")[1]);
        return new Game(gameId,
                        Stream.of(withdrawalCombinations)
                                .map(CubeWithdrawalCombination::of)
                                .toList());
    }

    /**
     * Game object.
     */
    static class Game {
        private final int id;
        private final List<CubeWithdrawalCombination> withdrawalCombinations;

        public Game(int id, List<CubeWithdrawalCombination> withdrawalCombinations) {
            this.id = id;
            this.withdrawalCombinations = withdrawalCombinations;
        }

        public int getId() {
            return id;
        }

        public boolean isValid() {
            return withdrawalCombinations.stream().allMatch(CubeWithdrawalCombination::isValidCombination);
        }
    }

    /**
     * Represents a combination of different colored cube withdrawals from the elf's pocket.
     */
    static class CubeWithdrawalCombination {
        List<CubeWithdrawal> combinationWithdrawals;

        public static CubeWithdrawalCombination of(String combination) {
            return new CubeWithdrawalCombination(Stream.of(combination.split(", "))
                                                       .map(CubeWithdrawal::new)
                                                       .toList());
        }

        private CubeWithdrawalCombination(List<CubeWithdrawal> withdrawals) {
            this.combinationWithdrawals = withdrawals;
        }

        public boolean isValidCombination() {
            return combinationWithdrawals.stream().allMatch(CubeWithdrawal::isValidWithdrawal);
        }
    }

    /**
     * Represents a single withdrawal of a cube's color and the amount of cubes from that color.
     */
    static class CubeWithdrawal {
        CubeColor color;
        int amount;

        public CubeWithdrawal(String inputWithdrawal) {
            String[] splitInfo = inputWithdrawal.split(" ");

            this.color = CubeColor.valueOf(splitInfo[1].toUpperCase());
            this.amount = Integer.parseInt(splitInfo[0]);
        }

        public boolean isValidWithdrawal() {
            return color.isValid(amount);
        }
    }

    enum CubeColor {
        RED {
            @Override
            public boolean isValid(int amount) {
                return amount <= RED_AMOUNT;
            }
        },
        GREEN {
            @Override
            public boolean isValid(int amount) {
                return amount <= GREEN_AMOUNT;
            }
        },
        BLUE {
            @Override
            public boolean isValid(int amount) {
                return amount <= BLUE_AMOUNT;
            }
        };

        public abstract boolean isValid(int amount);
    }
}
