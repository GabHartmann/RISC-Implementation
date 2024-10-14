program_memory = []
data_memory = [0] * 1024
registers = [0] * 32


IF_ID = {}
ID_EX = {}
EX_MEM = {}
MEM_WB = {}

program_counter = 0
halt_flag = False


def initialize_data_memory():

    data_memory[-1] = -1
    data_memory[10] = 10
    data_memory[1] = 1

def write_back():
    if MEM_WB:
        destination_register = MEM_WB.get("destination_register")
        write_value = MEM_WB.get("write_value")
        if destination_register is not None:
            registers[destination_register] = write_value
            print(f"Write Back: R{destination_register} atualizado para {write_value}")


def memory_access():
    if EX_MEM:
        operation = EX_MEM.get("operation")
        destination_register = EX_MEM.get("destination_register")
        memory_address = EX_MEM.get("memory_address")
        
        if operation == "lw":
            read_value = data_memory[memory_address]
            MEM_WB["destination_register"] = destination_register
            MEM_WB["write_value"] = read_value
            print(f"Memory Access: lw de {memory_address} para R{destination_register}, valor lido: {read_value}")

        elif operation == "sw":
            data_memory[memory_address] = registers[destination_register]
            print(f"Memory Access: sw de R{destination_register} para {memory_address}, valor escrito: {registers[destination_register]}")

        else:
            MEM_WB["destination_register"] = destination_register
            MEM_WB["write_value"] = EX_MEM.get("execution_result")


def execute_instruction():
    global program_counter
    if ID_EX:
        operation = ID_EX.get("operation")
        source_register_1 = ID_EX.get("source_register_1")
        source_register_2 = ID_EX.get("source_register_2")
        destination_register = ID_EX.get("destination_register")
        print(f"Execute: {operation} R{source_register_1}, R{source_register_2} -> R{destination_register}")

        if operation == "add":
            result = registers[source_register_1] + registers[source_register_2]
            EX_MEM["operation"] = "add"
            EX_MEM["destination_register"] = destination_register
            EX_MEM["execution_result"] = result
            print(f"Resultado de add: {result}")

        elif operation == "sub":
            result = registers[source_register_1] - registers[source_register_2]
            EX_MEM["operation"] = "sub"
            EX_MEM["destination_register"] = destination_register
            EX_MEM["execution_result"] = result
            print(f"Resultado de sub: {result}")

        elif operation == "lw":
            memory_address = registers[source_register_1] + source_register_2
            EX_MEM["operation"] = "lw"
            EX_MEM["destination_register"] = destination_register
            EX_MEM["memory_address"] = memory_address
            print(f"lw: Carregando de endereço {memory_address}")

        elif operation == "sw":
            memory_address = registers[source_register_1] + source_register_2
            EX_MEM["operation"] = "sw"
            EX_MEM["destination_register"] = destination_register
            EX_MEM["memory_address"] = memory_address
            print(f"sw: Armazenando em endereço {memory_address}")

        elif operation == "beq":
            if registers[destination_register] == registers[source_register_1]:
                program_counter = int(source_register_2) - 1
                print(f"beq: Salto para PC {program_counter} devido à igualdade.")
        elif operation == "noop":
            print("noop: Nenhuma operação.")
            pass
        elif operation == "halt":
            print("Execução interrompida.")
            global halt_flag
            halt_flag = True

def decode_instruction():
    if IF_ID:
        fetched_instruction = IF_ID.get("fetched_instruction")
        if fetched_instruction:
            decoded_instruction = parse_instruction(fetched_instruction)
            operation = decoded_instruction[0]
            ID_EX["operation"] = operation
            print(f"Decode: {fetched_instruction} -> {decoded_instruction}")

            if operation in ["add", "sub", "beq"]:
                ID_EX["destination_register"] = int(decoded_instruction[1])
                ID_EX["source_register_1"] = int(decoded_instruction[2])
                ID_EX["source_register_2"] = int(decoded_instruction[3])
            elif operation in ["lw", "sw"]:
                ID_EX["destination_register"] = int(decoded_instruction[1])
                ID_EX["source_register_1"] = int(decoded_instruction[2])
                ID_EX["source_register_2"] = int(decoded_instruction[3])
            elif operation == "noop" or operation == "halt":
                ID_EX["operation"] = operation

def fetch_instruction():
    global program_counter
    if program_counter < len(program_memory):
        fetched_instruction = program_memory[program_counter]
        IF_ID["fetched_instruction"] = fetched_instruction
        print(f"Fetch: Buscando instrução: {fetched_instruction}")
        program_counter += 1
    
def parse_instruction(instruction):
    return instruction.strip().split()

def print_register_status():
    print('Banco de Registradores:', registers)

def pipeline():
    global program_memory, halt_flag
    with open('Risc Processor\programa.txt', 'r') as file:
        program_memory = [line.strip() for line in file]

    initialize_data_memory()

    while not halt_flag:
        print("PC:", program_counter)
        input("Pressione Enter para próximo ciclo:\n")
        write_back()
        memory_access()
        execute_instruction()
        decode_instruction()
        fetch_instruction() 
        print_register_status()

pipeline()
