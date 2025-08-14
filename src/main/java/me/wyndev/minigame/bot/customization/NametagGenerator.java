package me.wyndev.minigame.bot.customization;

import java.util.Random;

/**
 * Minecraft username generator.
 * @author Semze
 */
public class NametagGenerator {
    private static final Random random = new Random();

    public static String generateName() {
        String name = "";
        int nameLength = (int)Math.round(Math.random() * 4.0D) + 5;
        String vowels = "aeiouy";
        String consonants = "bcdfghklmnprstvwz";
        int usedConsonants = 0;
        int usedVowels = 0;
        String lastLetter = "blah";

        int capitalMode;
        for(capitalMode = 0; capitalMode < nameLength; ++capitalMode) {
            String nextLetter = lastLetter;
            int letterIndex;
            if ((random.nextBoolean() || usedConsonants == 1) && usedVowels < 2) {
                while(nextLetter.equals(lastLetter)) {
                    letterIndex = (int)(Math.random() * (double)vowels.length() - 1.0D);
                    nextLetter = vowels.substring(letterIndex, letterIndex + 1);
                }

                usedConsonants = 0;
                ++usedVowels;
            } else {
                while(nextLetter.equals(lastLetter)) {
                    letterIndex = (int)(Math.random() * (double)consonants.length() - 1.0D);
                    nextLetter = consonants.substring(letterIndex, letterIndex + 1);
                }

                ++usedConsonants;
                usedVowels = 0;
            }

            lastLetter = nextLetter;
            name = name.concat(nextLetter);
        }

        capitalMode = (int)Math.round(Math.random() * 2.0D);
        int numberLength;
        if (capitalMode == 1) {
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
        } else if (capitalMode == 2) {
            for(numberLength = 0; numberLength < nameLength; ++numberLength) {
                if ((int)Math.round(Math.random() * 3.0D) == 1) {
                    name = name.substring(0, numberLength) + name.substring(numberLength, numberLength + 1).toUpperCase() + (numberLength == nameLength ? "" : name.substring(numberLength + 1));
                }
            }
        }

        numberLength = (int)Math.round(Math.random() * 3.0D) + 1;
        int numberMode = (int)Math.round(Math.random() * 3.0D);
        boolean number = random.nextBoolean();
        int i;
        if (number) {
            int nextNumber;
            if (numberLength == 1) {
                nextNumber = (int)Math.round(Math.random() * 9.0D);
                name = name.concat(Integer.toString(nextNumber));
            } else if (numberMode == 0) {
                nextNumber = (int)(Math.round(Math.random() * 8.0D) + 1L);

                for(i = 0; i < numberLength; ++i) {
                    name = name.concat(Integer.toString(nextNumber));
                }
            } else if (numberMode == 1) {
                nextNumber = (int)(Math.round(Math.random() * 8.0D) + 1L);
                name = name.concat(Integer.toString(nextNumber));

                for(i = 1; i < numberLength; ++i) {
                    name = name.concat("0");
                }
            } else if (numberMode == 2) {
                nextNumber = (int)(Math.round(Math.random() * 8.0D) + 1L);
                name = name.concat(Integer.toString(nextNumber));

                for(i = 0; i < numberLength; ++i) {
                    nextNumber = (int)Math.round(Math.random() * 9.0D);
                    name = name.concat(Integer.toString(nextNumber));
                }
            } else if (numberMode == 3) {
                for(nextNumber = 99999; Integer.toString(nextNumber).length() != numberLength; nextNumber = (int)Math.pow(2.0D, (double)nextNumber)) {
                    nextNumber = (int)(Math.round(Math.random() * 12.0D) + 1L);
                }

                name = name.concat(Integer.toString(nextNumber));
            }
        }

        boolean leet = !number && random.nextBoolean();
        if (leet) {
            String oldName = name;

            while(name.equals(oldName)) {
                int leetMode = (int)Math.round(Math.random() * 7.0D);
                if (leetMode == 0) {
                    name = name.replace("a", "4");
                    name = name.replace("A", "4");
                }

                if (leetMode == 1) {
                    name = name.replace("e", "3");
                    name = name.replace("E", "3");
                }

                if (leetMode == 2) {
                    name = name.replace("g", "6");
                    name = name.replace("G", "6");
                }

                if (leetMode == 3) {
                    name = name.replace("h", "4");
                    name = name.replace("H", "4");
                }

                if (leetMode == 4) {
                    name = name.replace("i", "1");
                    name = name.replace("I", "1");
                }

                if (leetMode == 5) {
                    name = name.replace("o", "0");
                    name = name.replace("O", "0");
                }

                if (leetMode == 6) {
                    name = name.replace("s", "5");
                    name = name.replace("S", "5");
                }

                if (leetMode == 7) {
                    name = name.replace("l", "7");
                    name = name.replace("L", "7");
                }
            }
        }

        i = (int)Math.round(random.nextDouble() * 12);
        if (i == 3) {
            name = "xX".concat(name).concat("Xx");
        } else if (i == 4) {
            name = name.concat("LP");
        } else if (i == 5) {
            name = name.concat("HD");
        } else if (i == 6) {
            name = name.concat(random.nextBoolean() ? "YT" : "_YT");
        }

        return name;
    }
}
