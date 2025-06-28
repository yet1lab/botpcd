# language: pt
Funcionalidade: Cadastro do PCD
  Como um PCD, quero poder me cadastrar para acessar os serviços da SBPC com mais facilidade.

  Cenario: Usuário não cadastrado manda qualquer mensagem
    Dado usuário não cadastrado
    Quando usuário envia mensagem "Oi"
    Entao usuário receberá mensagem
      """
Olá, qual sua deficiência?
- Digite 1 para Deficiência visual
- Digite 2 para Deficiência auditiva/surdez
- Digite 3 para Surdocegueira
- Digite 4 para Transtorno do Espectro Autista/Neurodivergente
- Digite 5 para Deficiência física
- Digite 6 para Não tenho deficiência, mas tenho mobilidade reduzida
- Digite 7 para Não preciso de suporte.
"""

  Regra: Apenas Usuarios com deficiência podem ser cadastrados no sistema
    Cenário de Fundo: Dado usuário não cadastrado
      E usuário recebeu mensagem
    """
Olá, qual sua deficiência?
- Digite 1 para Deficiência visual
- Digite 2 para Deficiência auditiva/surdez
- Digite 3 para Surdocegueira
- Digite 4 para Transtorno do Espectro Autista/Neurodivergente
- Digite 5 para Deficiência física
- Digite 6 para Não tenho deficiência, mas tenho mobilidade reduzida
- Digite 7 para Não preciso de suporte.
"""

    Cenario: O usuário responde que não precisa de suporte
      Quando usuário envia mensagem "7"
      Então usuário receberá mensagem "Agradecemos o contato! Este canal é exclusivo para atendimento de pessoas com deficiência ou mobilidade reduzida que participarão do evento. Desejamos a você uma excelente participação na 77ª Reunião Anual da SBPC."

    Cenario:
      Quando usuário envia mensagem "10"
      Entao usuário receberá mensagem
      """
Olá, qual sua deficiência?
- Digite 1 para Deficiência visual
- Digite 2 para Deficiência auditiva/surdez
- Digite 3 para Surdocegueira
- Digite 4 para Transtorno do Espectro Autista/Neurodivergente
- Digite 5 para Deficiência física
- Digite 6 para Não tenho deficiência, mas tenho mobilidade reduzida
- Digite 7 para Não preciso de suporte.
"""
    Esquema do Cenario: O usuário responde que tem deficiência
      Dado usuário recebeu mensagem
    """
Olá, qual sua deficiência?
- Digite 1 para Deficiência visual
- Digite 2 para Deficiência auditiva/surdez
- Digite 3 para Surdocegueira
- Digite 4 para Transtorno do Espectro Autista/Neurodivergente
- Digite 5 para Deficiência física
- Digite 6 para Não tenho deficiência, mas tenho mobilidade reduzida
- Digite 7 para Não preciso de suporte.
"""
      Quando usuário envia mensagem "<numero_da_deficiencia>"
      Então bot registrará que usuário tem ou possui "<adjetivo_da_deficiencia>"
      E usuário receberá mensagem "Qual o seu nome?"

      Exemplos:
        | numero_da_deficiencia | adjetivo_da_deficiencia |
        | 1                     | uma pessoa cega         |
        | 2                     | um pessoa surda         |
        | 3                     | uma pessoa surdocega    |
        | 4                     | neurodivergente         |
        | 5                     | deficiente físico       |
        | 6                     | mobilidade reduzida     |

    Esquema do Cenario: O usuário responde com o seu nome
    Dado usuário recebeu mensagem "Qual o seu nome?"
    E usuário possui deficiência cadastrada de "<adjetivo_da_deficiencia>"
    Quando usuário envia mensagem "João Victor"
    Entao A penúltima mensagem recebida pelo usuário será "Cadastro realizado."
      E "<adjetivo_da_deficiencia>" PCD receberá mensagem de opções de serviço "<opções_de_serviço>"
      E bot salvará o nome do usuário "João Victor"

    Exemplos:
      | adjetivo_da_deficiencia | opções_de_serviço                                                                         |
      | um pessoa surda         | informações em Libras,atividade com interpretação em Libras                                |
      | mobilidade reduzida     | ajuda na mobilidade,transporte para deslocamento no evento                                 |
      | deficiente físico       | ajuda na mobilidade,ajuda com alimentação e higiene,transporte para deslocamento no evento |
      | uma pessoa cega         | ajuda na mobilidade,programação com audiodescrição                                         |
      | neurodivergente         | suporte para pessoas neurodivergentes                                                      |
      | uma pessoa surdocega    | guia-intérprete                                                                            |
