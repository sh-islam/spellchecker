package demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Document {
    public static String readTextFile(String filePath) {
        StringBuilder content = new StringBuilder();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return content.toString();
    }
}
