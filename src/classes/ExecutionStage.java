public class ExecutionStage {
    public int execute(Instruction instruction) {
        int result = 0;

        switch (instruction.getOpcode().toUpperCase()) {
            case "ADD": 
                result = instruction.getOper2() + instruction.getOper3();
                break;
            case "SUB":
                result = instruction.getOper2() - instruction.getOper3();
                break;
            case "BEQ": 
               result = (instruction.getOper1() == instruction.getOper2()) ? instruction.getOper3() : 0;
               break;
            case "LW":
                int address = instruction.getOper1() + instruction.getOper2();
                result = address;
                break;
            case "SW ":
                address = instruction.getOper1() + instruction.getOper2();
                result = address;
                break;
            default:
                break;  
        }
        return result;
    }
}
