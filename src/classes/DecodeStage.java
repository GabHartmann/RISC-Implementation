public class DecodeStage {
    int[] registers;

    public DecodeStage(int[] registers) {
        this.registers = registers;
    }

    public Instruction decode(String instruction) {
        String[] result = instruction.split(" ");
        String opcode = result[0];

        if (result.length > 1) {
            int oper1, oper2, oper3;

            if(opcode.equalsIgnoreCase("LW")) {
                oper1 = registers[Integer.parseInt(result[1].substring(1))];
                oper2 = Integer.parseInt(result[2].substring(1));
                oper3 = Integer.parseInt(result[3]);

                return new Instruction(opcode, oper1, oper2, oper3);
            } else if(opcode.equalsIgnoreCase("SW")) {
                oper1 = registers[Integer.parseInt(result[1].substring(1))];
                oper2 = Integer.parseInt(result[2].substring(1));
                oper3 = Integer.parseInt(result[3]);

                return new Instruction(opcode, oper1, oper2, oper3);
            } else if(opcode.equalsIgnoreCase("ADD")) {
                oper1 = Integer.parseInt(result[1].substring(1));
                oper2 = registers[Integer.parseInt(result[2].substring(1))];
                oper3 = registers[Integer.parseInt(result[3].substring(1))];

                return new Instruction(opcode, oper1, oper2, oper3);
            } else if(opcode.equalsIgnoreCase("SUB")) {
                oper1 = Integer.parseInt(result[1].substring(1));
                oper2 = registers[Integer.parseInt(result[2].substring(1))];
                oper3 = registers[Integer.parseInt(result[3].substring(1))];

                return new Instruction(opcode, oper1, oper2, oper3);
            } else {
                // BEQ Instruction
                // beq R2 R0 16
                oper1 = registers[Integer.parseInt(result[1].substring(1))];
                oper2 = registers[Integer.parseInt(result[2].substring(1))];
                oper3 = Integer.parseInt(result[3]);

                return new Instruction(opcode, oper1, oper2, oper3);
            }
        }

        return(new Instruction(opcode, 0, 0, 0));
    }
}
