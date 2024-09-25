import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstructionMemory {
    private String[] instructions;

    public InstructionMemory(File file) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        instructions = lines.toArray(new String[0]);
    }

    public void getInstructions() {
        for (String instruction : instructions) {
            System.out.println(instruction);
        }
    }

    public static void main(String[] args) {

    }
}
