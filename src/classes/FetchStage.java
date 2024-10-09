public class FetchStage {
    InstructionMemory instructionMemory;
    int programCounter;

    public FetchStage(InstructionMemory instructionMemory) {
        this.programCounter = 0;
        this.instructionMemory = instructionMemory;
    }

    public String fetch() {
        if(programCounter < instructionMemory.instructions.size()) {
            return instructionMemory.getInstruction(programCounter++);
        }
        return null;
    }
}
