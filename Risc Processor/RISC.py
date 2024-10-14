mem_programa = []
mem_dados = [0] * 1024  # Inicializa a memória de dados
registradores = [0] * 32
instrucao_decod = ""
instrucao_exec = []

pc = 0

def wb(reg, content):
    global instrucao_wb
    registradores[reg] = content
    print(f"Registrador R{reg} atualizado para {content}")

def mem(reg, offset):
    global instrucao_mem
    global instrucao_exec
    mem_dados[offset] = instrucao_exec[3]
    # Lê da memória usando o offset
    content = mem_dados[offset]  # Acessa a memória com o offset
    wb(reg, content)

def exe():
    global instrucao_exec
    global pc

    if instrucao_exec[0] == "lw":
        # Calcula o endereço usando o offset
        offset = int(instrucao_exec[3]) + registradores[int(instrucao_exec[2])]
        mem(int(instrucao_exec[1]), offset)  # Lê da memória para o registrador
    elif instrucao_exec[0] == "add":
        ula = int(registradores[int(instrucao_exec[2])]) + int(registradores[int(instrucao_exec[3])])
        wb(int(instrucao_exec[1]), ula)
    elif instrucao_exec[0] == "sub":
        ula = registradores[int(instrucao_exec[2])] - registradores[int(instrucao_exec[3])]
        wb(int(instrucao_exec[1]), ula)
    elif instrucao_exec[0] == "beq":
        if registradores[int(instrucao_exec[1])] == registradores[int(instrucao_exec[2])]:
            pc = int(instrucao_exec[3]) - 1  # Salta para o endereço especificado
    elif instrucao_exec[0] == "noop":
        pass
    elif instrucao_exec[0] == "halt":
        print("Execução interrompida.")
        exit()  # Interrompe a execução do programa

def dec():
    global instrucao_decod
    global instrucao_exec
    instrucao_exec = separar_operadores(instrucao_decod)
    print("Instrução DEC:", instrucao_exec)

def separar_operadores(instrucao):
    partes = instrucao.strip().split()
    return partes  # Retorna a lista de partes

def busca():
    global pc
    global instrucao_decod
    if pc < len(mem_programa):
        instrucao_decod = mem_programa[pc]
        pc += 1
        print("Instrução buscada:", instrucao_decod)
        print("PC:", pc)

def print_status():
    print('Banco de Registradores:', registradores)

def pipeline():
    global mem_programa
    with open('programa.txt', 'r') as file:
        mem_programa = [line.strip() for line in file]

    while True:
        input("Pressione para próximo ciclo:\n")
        busca()  # A busca deve vir antes da decodificação
        dec()
        exe()
        print_status()

# Inicia o pipeline
pipeline()
