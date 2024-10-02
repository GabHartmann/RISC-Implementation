import java.util.HashMap;
import java.util.Map;

public class RISCProcessor {
    private Register[] registers;
    private InstructionData instructionData;
    private InstructionMemory instructionMemory;
    private ProgramCounter programCounter;
    private boolean running; // Controle de execução do processador

    public RISCProcessor(int memorySize, int registerCount) {
        this.registers = new int[registerCount];
        this.memory = new int[memorySize];
        this.pc = 0;
        this.running = true;
    }

    public void run() {
        while (running) {
            int instruction = memory[pc++];
            executeInstruction(instruction);
        }
    }

    // Decodifica e executa uma instrução
    private void executeInstruction(int instruction) {
        // Formatando a instrução para extrair opcode e registradores/endereços
        int opcode = (instruction >> 26) & 0x3F;
        int reg1 = (instruction >> 21) & 0x1F;
        int reg2 = (instruction >> 16) & 0x1F;
        int reg3 = instruction & 0x1F;
        int immediate = instruction & 0xFFFF;

        switch (opcode) {
            case 0: // ADD
                registers[reg1] = registers[reg2] + registers[reg3];
                System.out.println("ADD: R" + reg1 + " = R" + reg2 + " + R" + reg3);
                break;
            case 1: // SUB
                registers[reg1] = registers[reg2] - registers[reg3];
                System.out.println("SUB: R" + reg1 + " = R" + reg2 + " - R" + reg3);
                break;
            case 2: // BEQ (Branch if Equal)
                if (registers[reg1] == registers[reg2]) {
                    pc = pc + immediate; // Pular para o endereço relativo
                    System.out.println("BEQ: Branching to " + pc);
                }
                break;
            case 3: // LW (Load Word)
                registers[reg1] = memory[registers[reg2] + immediate];
                System.out.println("LW: Carregado valor da memória para R" + reg1);
                break;
            case 4: // SW (Store Word)
                memory[registers[reg2] + immediate] = registers[reg1];
                System.out.println("SW: Valor de R" + reg1 + " armazenado na memória");
                break;
            case 5: // NOOP
                System.out.println("NOOP: Nenhuma operação realizada.");
                break;
            case 6: // HALT
                running = false;
                System.out.println("HALT: Execução interrompida.");
                break;
            default:
                System.out.println("Instrução desconhecida.");
                break;
        }
    }

    // Carregar o programa na memória
    public void loadProgram(int[] program) {
        System.arraycopy(program, 0, memory, 0, program.length);
    }

    // Mostrar o estado dos registradores
    public void printRegisters() {
        System.out.println("Registradores:");
        for (int i = 0; i < registers.length; i++) {
            System.out.println("R" + i + ": " + registers[i]);
        }
    }

    // Mostrar o estado da memória
    public void printMemory(int start, int end) {
        System.out.println("Memória:");
        for (int i = start; i <= end; i++) {
            System.out.println("Memória[" + i + "]: " + memory[i]);
        }
    }

    public static void main(String[] args) {
        // Exemplo de programa
        int[] program = {
                0x00221820, // ADD R3 = R1 + R2
                0x00632022, // SUB R4 = R3 - R1
                0x10840002, // BEQ R4, R2 (salta 2 instruções se R4 == R2)
                0x8C850000, // LW R5, 0(R4)
                0xAC860001, // SW R6, 1(R4)
                0x00000000, // NOOP
                0xFC000000 // HALT
        };

        RISCProcessor processor = new RISCProcessor(128, 32); // Memória com 128 palavras, 32 registradores
        processor.loadProgram(program); // Carrega o programa na memória
        processor.run(); // Executa o programa
        processor.printRegisters(); // Mostra o estado final dos registradores
    }
}