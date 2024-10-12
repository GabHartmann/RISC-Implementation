import java.io.File;

public class RISCProcessor {
    FetchStage fetchStage;
    DecodeStage decodeStage;
    ExecutionStage executionStage;
    MemoryStage memoryStage;
    WriteBackStage writeBackStage;
    InstructionMemory instructionMemory;
    int[] registers; 

    public RISCProcessor() {
        this.instructionMemory = new InstructionMemory(new File("src/input.txt"));
        registers = new int[32];

        this.fetchStage = new FetchStage(instructionMemory);
        this.decodeStage = new DecodeStage(registers);
        this.executionStage = new ExecutionStage();
        this.memoryStage = new MemoryStage();
        this.writeBackStage = new WriteBackStage(registers);
    }

    public void run() {
        while (true) {
            // Instruction instruction = instructionMemory.getInstruction(programCounter++);
            // executeInstruction(instruction);

            String fetchedInstruction = fetchStage.fetch();
            Instruction decodedInstruction = decodeStage.decode(fetchedInstruction);
            int result = executionStage.execute(decodedInstruction);
            result = memoryStage.access(decodedInstruction);
            writeBackStage.write(decodedInstruction, result);
        }
    }

    // private void writeBack() {
        
    // }

    // Decodifica e executa uma instrução
    // private void executeInstruction(Instruction instruction) {
    //     // Formatando a instrução para extrair opcode e registradores/endereços
    //     switch (opcode) {
    //         case "ADD":
    //             registers[reg1] = registers[reg2] + registers[reg3];
    //             System.out.println("ADD: R" + reg1 + " = R" + reg2 + " + R" + reg3);

    //             break;
    //         case "SUB":
    //             registers[reg1] = registers[reg2] - registers[reg3];
    //             System.out.println("SUB: R" + reg1 + " = R" + reg2 + " - R" + reg3);
    //             break;
    //         case "BEQ":
    //             if (registers[reg1] == registers[reg2]) {
    //                 programCounter = programCounter + reg3;
    //                 System.out.println("BEQ: Branching to " + programCounter);
    //             }
    //             break;
    //         case "LW":
    //             registers[offset] = reg1;
    //             System.out.println("LW: Carregado valor da memória para R" + offset);
    //             break;
    //         case "SW":
    //             registers[offset] = reg1;
    //             System.out.println("SW: Valor de R" + offset + " armazenado na memória");
    //             break;
    //         case "NOOP":
    //             System.out.println("NOOP: Nenhuma operação realizada.");
    //             break;
    //         case "HALT":
    //             running = false;
    //             System.out.println("HALT: Execução interrompida.");
    //             break;
    //         default:
    //             System.out.println("Instrução desconhecida.");
    //             break;
    //     }
    // }

    public void printRegisters() {
        System.out.println("Registradores:");
        for (int i = 0; i < registers.length; i++) {
            System.out.println("R" + i + ": " + registers[i]);
        }
    }

    // Mostrar o estado da memória
    // public void printMemory(int start, int end) {
    //     System.out.println("Memória:");
    //     for (int i = start; i <= end; i++) {
    //         System.out.println("Memória[" + i + "]: " + memory[i]);
    //     }
    // }

    public static void main(String[] args) {
        RISCProcessor processor = new RISCProcessor();
        processor.run();
        processor.printRegisters();
    }
}