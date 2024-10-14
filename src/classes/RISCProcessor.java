import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.HashMap;

import enums.Prediction;

public class RISCProcessor {
    Prediction prediction;

    int GHR_SIZE = 8;
    int GHR = 0;
    static final int PRED_TABLE_SIZE = 256;
    int[] predTable = new int[PRED_TABLE_SIZE];
    HashMap<Integer, Integer> phtTable;
    boolean predictedTaken;
    int totalFetchedInstructions, totalInvalidInstructions;

    int numCorrectedPredictions;

    static File PROGRAM_PATH = new File("src/input.txt");
    List<String> instructionMemory;
    int[] dataMemory; 
    int[] registers; 
    int programCounter, readValue;
    Queue<Integer> alu;

    String if_id;
    Instruction id_ex, ex_mem, mem_wb;
    public RISCProcessor(Prediction prediction) {
        this.prediction = prediction;

        if(prediction.equals(Prediction.G_SHARE)) {
            for(int i = 0; i < PRED_TABLE_SIZE; i++) {
                predTable[i] = 0; // TODO changing this value affects the accuracy of the prediction
            }

            phtTable = new HashMap<>();
        }

        programCounter = 1;
        instructionMemory = new ArrayList<>();
        dataMemory = new int[32];
        registers = new int[32];
        alu = new LinkedList<>();
        numCorrectedPredictions = 0;

        loadProgram(PROGRAM_PATH);
    }
    
    private int calculateIndex(int programCounter) {
        int truncatedPC = programCounter & (PRED_TABLE_SIZE - 1);
        int index = truncatedPC ^ (GHR & (PRED_TABLE_SIZE - 1));

        return index;
    }

    public boolean predictBranch(int programCounter) {
        int index = calculateIndex(programCounter);
        int prediction = predTable[index];

        return prediction >= 2; // 2 and 3 are taken predictions
    }

    private void updatePredictor(int programCounter, boolean branchTaken) {
        int index = calculateIndex(programCounter);
        int prediction = predTable[index];

        if(branchTaken) {
            if(prediction < 3) {
                predTable[index]++;
            }
        } else {
            if(prediction > 0) {
                predTable[index]--;
            }
        }

        GHR = (GHR << 1) | (branchTaken ? 1 : 0);
        GHR = GHR & ((1 << GHR_SIZE) - 1);
    }
    
    private void loadProgram(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            instructionMemory.add(null);

            while ((line = reader.readLine()) != null) {
                
                instructionMemory.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (programCounter < instructionMemory.size()) {
            writeBack();
            memoryAccess();
            execution();
            instructionDecode();
            instructionFetch();
        }
    }

    private void writeBack() {
        if (mem_wb != null) {
            String opcode = mem_wb.getOpcode();

            if(opcode.equalsIgnoreCase("ADD") && mem_wb.isValid()) {
                registers[mem_wb.getOper1()] = alu.remove();
            } else if(opcode.equalsIgnoreCase("SUB") && mem_wb.isValid()) {
                registers[mem_wb.getOper1()] = alu.remove();
            } else if(opcode.equalsIgnoreCase("LW") && mem_wb.isValid()) {
                int address = alu.poll();

                registers[address] = dataMemory[address];
            } else if(opcode.equalsIgnoreCase("SW") && mem_wb.isValid()) {
                // TODO SW writeBack
            }

            mem_wb = null;
        }
    }

    private void memoryAccess() {
        if(ex_mem != null) {
            if(ex_mem.getOpcode().equalsIgnoreCase("LW") && ex_mem.isValid()) {
                int address = alu.peek();

                dataMemory[address] = ex_mem.getOper3();
                readValue = dataMemory[address];
            } else if(ex_mem.getOpcode().equalsIgnoreCase("SW") && ex_mem.isValid()) {
                dataMemory[ex_mem.getOper3()] = ex_mem.getOper3();
            }

            mem_wb = ex_mem;
            ex_mem = null;
        }
    }

    private void execution() {
        if(id_ex != null) {
            String opcode = id_ex.getOpcode();

            if(opcode.equalsIgnoreCase("ADD")) {
                alu.add(id_ex.getOper2() + id_ex.getOper3());
            } else if(opcode.equalsIgnoreCase("SUB")) {
                alu.add(id_ex.getOper2() - id_ex.getOper3());
            } else if(opcode.equalsIgnoreCase("BEQ")) {
                if(prediction.equals(Prediction.G_SHARE)) {
                    boolean realBranchTaken = id_ex.getOper1() == id_ex.getOper2();

                    updatePredictor(programCounter - 2, realBranchTaken);

                    if(predictedTaken == realBranchTaken) {
                        numCorrectedPredictions++;
                    }

                    if(predictedTaken != realBranchTaken) {
                        // if_id = null;
                        // totalInvalidInstructions++;

                        if(realBranchTaken) {
                            if(if_id != null) {
                                if_id = null;
                                totalInvalidInstructions++;
                            }

                            programCounter = id_ex.getOper3();
                        }
                    }
                } else {
                    if(id_ex.getOper1() == id_ex.getOper2()) {
                        programCounter = id_ex.getOper3();
                        // if_id = null;

                        // if(if_id != null) {
                        //     totalInvalidInstructions++;
                        // }

                        if(if_id != null) {
                            if_id = null;
                            totalInvalidInstructions++;
                        }
                    } else {
                        numCorrectedPredictions++;
                    }
                }
            } else if (opcode.equalsIgnoreCase("LW")) {
                int offset = id_ex.getOper1();

                alu.add(offset + id_ex.getOper2());
            } else if(opcode.equalsIgnoreCase("SW")) {
                // TODO SW
            } else if(opcode.equalsIgnoreCase("NOOP")) {
                // does nothing
            } else if(opcode.equalsIgnoreCase("HALT")) {
                programCounter = instructionMemory.size();
            } else {
                System.out.println("Invalid instruction");
                programCounter = instructionMemory.size();
            }

            ex_mem = id_ex;
            id_ex = null;
        }
    }
    
    private void instructionDecode() {
        if(if_id != null) {
            String[] splitedInstruction = if_id.split(" ");
            String opcode = splitedInstruction[0];

            if (splitedInstruction.length > 1) {
                int oper1, oper2, oper3;
    
                if(opcode.equalsIgnoreCase("LW")) {
                    oper1 = registers[Integer.parseInt(splitedInstruction[1].substring(1))];
                    oper2 = Integer.parseInt(splitedInstruction[2].substring(1));
                    oper3 = Integer.parseInt(splitedInstruction[3]);
    
                    id_ex = new Instruction(opcode, oper1, oper2, oper3);
                } else if(opcode.equalsIgnoreCase("SW")) {
                    oper1 = registers[Integer.parseInt(splitedInstruction[1].substring(1))];
                    oper2 = Integer.parseInt(splitedInstruction[2].substring(1));
                    oper3 = Integer.parseInt(splitedInstruction[3]);
    
                    id_ex = new Instruction(opcode, oper1, oper2, oper3);
                } else if(opcode.equalsIgnoreCase("ADD")) {
                    oper1 = Integer.parseInt(splitedInstruction[1].substring(1));
                    oper2 = registers[Integer.parseInt(splitedInstruction[2].substring(1))];
                    oper3 = registers[Integer.parseInt(splitedInstruction[3].substring(1))];
    
                    id_ex = new Instruction(opcode, oper1, oper2, oper3);
                } else if(opcode.equalsIgnoreCase("SUB")) {
                    oper1 = Integer.parseInt(splitedInstruction[1].substring(1));
                    oper2 = registers[Integer.parseInt(splitedInstruction[2].substring(1))];
                    oper3 = registers[Integer.parseInt(splitedInstruction[3].substring(1))];
    
                    id_ex = new Instruction(opcode, oper1, oper2, oper3);
                } else if(opcode.equalsIgnoreCase("BEQ")) {
                    if(prediction.equals(Prediction.G_SHARE)) {
                        predictedTaken = predictBranch(programCounter - 1); // TODO it decrease the PC by one to be the correct PC of that instruction 

                        programCounter = (predictedTaken) ? Integer.parseInt(splitedInstruction[3]) : programCounter; // TODO review
                    }

                    oper1 = registers[Integer.parseInt(splitedInstruction[1].substring(1))];
                    oper2 = registers[Integer.parseInt(splitedInstruction[2].substring(1))];
                    oper3 = Integer.parseInt(splitedInstruction[3]);
    
                    id_ex = new Instruction(opcode, oper1, oper2, oper3);
                } 
            } else {
                id_ex = new Instruction(opcode, 0, 0, 0);
            }
    
            if_id = null;
        }
    }

    private void instructionFetch() {
        if_id = instructionMemory.get(programCounter++);
        totalFetchedInstructions++;
    }

    public void printRegisters() {
        System.out.println("Registers:");
        for (int i = 0; i < registers.length; i++) {
            System.out.println("R" + i + ": " + registers[i]);
        }
        System.out.println();
    }

    public void printMemory() {
        System.out.println("Memory:");

        for(int i = 0; i < dataMemory.length; i++) {
            System.out.println("Memory[" + i + "]: " + dataMemory[i]);
        } 
        System.out.println();
    }

    public void info() {
        System.out.println("Total of fetched instructions: " + totalFetchedInstructions);
        System.out.println("Total of invalid instructions: " + totalInvalidInstructions);
        System.out.println("Percentage of correct predictions: " + ((double) numCorrectedPredictions / (double) totalFetchedInstructions) * 100);
        System.out.println("Percentage of invalid intructions: " + ((double) totalInvalidInstructions / (double) totalFetchedInstructions) * 100);
    }

    public static void main(String[] args) {
        RISCProcessor processor = new RISCProcessor(Prediction.G_SHARE);
        processor.run();
        processor.printRegisters();
        processor.printMemory();
        processor.info();
    }
}