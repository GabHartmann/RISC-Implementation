public class Instruction {
    private String opcode;
    private int oper1, oper2, oper3;
    private int temp1, temp2, temp3;
    private boolean isValid = true;

    public Instruction(String opcode, int oper1, int oper2, int oper3) {
        this.opcode = opcode;
        this.oper1 = oper1;
        this.oper2 = oper2;
        this.oper3 = oper3;
        this.temp1 = 0;
        this.temp2 = 0;
        this.temp3 = 0;
    }

    public Instruction(String opcode, int oper1, int oper2, int oper3, int temp1, int temp2, int temp3) {
        this.opcode = opcode;
        this.oper1 = oper1;
        this.oper2 = oper2;
        this.oper3 = oper3;
        this.temp1 = temp1;
        this.temp2 = temp2;
        this.temp3 = temp3;
    }

    @Override
    public String toString() {
        return "Instruction [opcode=" + opcode + ", oper1=" + oper1 + ", oper2=" + oper2 + ", oper3=" + oper3
                + ", temp1=" + temp1 + ", temp2=" + temp2 + ", temp3=" + temp3 + ", isValid=" + isValid + "]";
    }

    public String getOpcode() {
        return opcode;
    }

    public int getOper1() {
        return oper1;
    }

    public int getOper2() {
        return oper2;
    }

    public int getOper3() {
        return oper3;
    }

    public int getTemp1() {
        return temp1;
    }

    public int getTemp2() {
        return temp2;
    }

    public int getTemp3() {
        return temp3;
    }

    public boolean isValid() {
        return isValid;
    }
}
