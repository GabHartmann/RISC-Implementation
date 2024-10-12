import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InstructionMemory {
    List<String> instructions;

    public InstructionMemory(File file) {
        instructions = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                // String[] result = line.split(" ");
                // String opcode = result[0];

                // if(result.length == 4) {
                //     int oper1 = Integer.parseInt(result[1].replaceAll("\\D", ""));
                //     int oper2 = Integer.parseInt(result[2].replaceAll("\\D", ""));
                //     int oper3 = Integer.parseInt(result[3].replaceAll("\\D", ""));

                //     instructions.add(new Instruction(opcode, oper1, oper2, oper3)); 
                // } else {
                //     instructions.add(new Instruction(opcode, 0, 0, 0));
                // }

                instructions.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInstruction(int index) {
        return instructions.get(index);
    }

    public void printInstructions() {
        for (String instruction : instructions) {
            System.out.println(instruction);
        }
    }

    public static void main(String[] args) {
        InstructionMemory instructionMemory = new InstructionMemory(new File("src/input.txt"));
        instructionMemory.printInstructions();
    }
}
