package userstats;
import java.security.SecureRandom;

public class RandomTokenGenerator {
    private static final String characters = "abcdefghijklmnopqrstuvwxyz0123456789";

    private static final SecureRandom random = new SecureRandom();

    public static String generator(int length){
        StringBuilder stringBuild = new StringBuilder(length);

        for(int i = 0; i < length; i++){
            int index = random.nextInt(characters.length());
            stringBuild.append(characters.charAt(index));
        }

        return stringBuild.toString();
    }
}
