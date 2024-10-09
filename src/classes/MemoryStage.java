public class MemoryStage {
    int[] dataMemory;

    public MemoryStage() {
        dataMemory = new int[32];
    }
                        
    public int access(Instruction instruction) {
        if(instruction.getOpcode().equalsIgnoreCase("LW")) {
            // lw R0 R1 -1   (offset, address, value)
            return dataMemory[instruction.getOper3()];
        } else if(instruction.getOpcode().equalsIgnoreCase("SW")) {
            dataMemory[instruction.getOper3()] = instruction.getOper3();
        }
        return 0;
    }
}
