public class WriteBackStage {
    int[] registers;

    public WriteBackStage(int[] registers) {
        this.registers = registers;
    }

    public void write(Instruction instruction, int result) {
        if(instruction.getOpcode().equalsIgnoreCase("ADD") || instruction.getOpcode().equals("SUB")) {
            registers[instruction.getOper3()] = result;
        }

        if(instruction.getOpcode().equalsIgnoreCase("LW")) {
            registers[instruction.getOper1() + instruction.getOper2()] = instruction.getOper3();
        }
    }   
}
