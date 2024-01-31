package petstore.util;

import java.io.File;

public class FileProvider {

    public static File getFile(String fileName) {
        return new File("C:/Users/lower/IdeaProjects/petstore-tested/src/main/resources/" + fileName);
    }
}
