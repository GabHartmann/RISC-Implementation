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
    HashMap<Integer, Integer> branchAddressesHistory;
    HashMap<Integer, Integer> phtTable;
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

        if(prediction.equals(Prediction.PHT)) {
            branchAddressesHistory = new HashMap<>();
            phtTable = new HashMap<>();
        }

        programCounter = 1;
        instructionMemory = new ArrayList<>();
        dataMemory = new int[32];
        registers = new int[32];
        alu = new LinkedList<>();

        loadProgram(PROGRAM_PATH);
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
                // add R2 R1 R2
                registers[mem_wb.getOper1()] = alu.remove();
            } else if(opcode.equalsIgnoreCase("SUB") && mem_wb.isValid()) {
                // sub R2 R1 R2
                registers[mem_wb.getOper1()] = alu.remove();
            } else if(opcode.equalsIgnoreCase("LW") && mem_wb.isValid()) {
                //lw R0 R1 -1
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
                // lw R0 R1 -1   (offset, address, value)
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
                // add R2 R1 R2
                alu.add(id_ex.getOper2() + id_ex.getOper3());
            } else if(opcode.equalsIgnoreCase("SUB")) {
                // sub R2 R1 R2
                alu.add(id_ex.getOper2() - id_ex.getOper3());
            } else if(opcode.equalsIgnoreCase("BEQ")) {
                // beq R0 R0 5
                if(id_ex.getOper1() == id_ex.getOper2()) {
                    if(prediction.equals(Prediction.PHT)) {
                        int xor = programCounter ^ branchAddressesHistory.get(programCounter);
                    }

                    programCounter = id_ex.getOper3();

                    mem_wb.setValid(false);

                    if_id = null;
                    id_ex.setValid(false);
                }
            } else if (opcode.equalsIgnoreCase("LW")) {
                // lw R0 R1 -1
                int offset = id_ex.getOper1();

                alu.add(offset + id_ex.getOper2());
            } else if(opcode.equalsIgnoreCase("SW")) {
                // TODO SW
                // sw R0 R1

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
                    // add R3 R1 R2
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
                    if(prediction.equals(Prediction.PHT)) {
                        branchAddressesHistory.put(programCounter, 0);
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
    }

    public void printRegisters() {
        System.out.println("Registers:");
        for (int i = 0; i < registers.length; i++) {
            System.out.println("R" + i + ": " + registers[i]);
        }
    }

    public void printMemory() {
        System.out.println("Memory:");

        for(int i = 0; i < dataMemory.length; i++) {
            System.out.println("Memory[" + i + "]: " + dataMemory[i]);
        } 
    }

    public static void main(String[] args) {
        RISCProcessor processor = new RISCProcessor(Prediction.PHT);
        processor.run();
        processor.printRegisters();
        processor.printMemory();
    }
}