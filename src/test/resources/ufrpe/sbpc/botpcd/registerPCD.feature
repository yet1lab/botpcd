# language: pt
Funcionalidade: Cadastro do PCD
  Como um PCD, quero poder me cadastrar para acessar os serviços da SBPC com mais facilidade.

  Cenario: Usuário não cadastrado manda qualquer mensagem
    Dado usuário não cadastrado
    Quando usuário envia mensagem "Oi"
    Entao bot envia mensagem
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
    Cenário de Fundo: usuário recebeu mensagem
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
      E usuário não cadastrado

    Cenario: O usuário responde que não precisa de suporte
      Quando usuário envia mensagem "7"
      Então bot envia mensagem "Agradecemos o contato! Este canal é exclusivo para atendimento de pessoas com deficiência ou mobilidade reduzida que participarão do evento. Desejamos a você uma excelente participação na 77ª Reunião Anual da SBPC."

    Cenario:
      Quando usuário envia mensagem "10"
      Entao bot envia mensagem
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
      Quando usuário envia mensagem "<numero_da_deficiencia>"
      Então bot registrar o usuário com "<tipo_deficiencia>"
      E bot envia mensagem "Qual o seu nome?"

      Exemplos:
        | numero_da_deficiencia | tipo_deficiencia                                      |
        | 1                     | Deficiência visual                                    |
        | 2                     | Deficiência auditiva/surdez                             |
        | 3                     | Surdocegueira                                         |
        | 4                     | Transtorno do Espectro Autista/Neurodivergente          |
        | 5                     | Deficiência física                                    |
        | 6                     | Não tenho deficiência, mas tenho mobilidade reduzida   |
    Cenario: O usuário responde com o seu nome
    Dado usuário recebeu mensagem "Qual o seu nome?"
    Quando usuário envia mensagem "João Victor"
    Entao bot envia a mensagem "Cadastro realizado" E bot salva o nome do usuário
