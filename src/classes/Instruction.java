public class Instruction {
    String opcode;
    int rs, rt, rd, immediate;

    public Instruction(String opcode, int rs, int rt, int rd) {
        this.opcode = opcode;
        this.rs = rs;
        this.rt = rt;
        this.rd = rd;
    }

    public Instruction(String opcode, int rs, int rt, int rd, int immediate) {
        this.opcode = opcode;
        this.rs = rs;
        this.rt = rt;
        this.rd = rd;
        this.immediate = immediate;
    }

}
