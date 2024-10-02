# RISC-Implementation

## Objetivos
Comprovar a melhora do desempenho do processador com a
utilização de um mecanismo de predição e reforçar os conceitos de
funcionamento do pipeline.

## Resultado esperado
Um índice em porcentagem da melhora do desempenho
(compara a execução do mesmo código com e sem mecanismo de predição).

## Primeira entrega
Um simulador com os 5 estágios do pipeline, que tenha as
seguintes funcionalidades:

- Leitura da memória de programa (arquivo texto com instruções para um vetor de
instruções);
- Simulador de pipeline com 5 estágios (busca, decodificação, execução, memória e
escrita do resultado);
- Banco de registradores com R0 a R31 (R0 fixo em zero);
- Utiliza uma política fixa para instruções de desvio condicional (não Tomado);
- Não trata hazards de dados (o código de testes não pode conter);
- Mecanismo de invalidação da instrução se a execução do desvio for incorreta.

## Instruções suportadas
ADD, SUB, BEQ, LW, SW, NOOP, HALT

## Banco de registradores
Unsigned int R[32]
