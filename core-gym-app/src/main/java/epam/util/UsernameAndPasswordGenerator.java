package epam.util;

import java.security.SecureRandom;

public class UsernameAndPasswordGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final int LENGTH = 10;

    public static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }

    public static String createUsername(String firstName, String lastName) {
        return firstName + '.' + lastName;
    }
}
