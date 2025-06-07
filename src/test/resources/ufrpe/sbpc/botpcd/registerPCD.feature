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
      Então bot registrará o usuário com deficiencia "<tipo_deficiencia>"
      E usuário receberá mensagem "Qual o seu nome?"

      Exemplos:
        | numero_da_deficiencia | tipo_deficiencia                                     |
        | 1                     | Deficiência visual                                   |
        | 2                     | Deficiência auditiva/surdez                          |
        | 3                     | Surdocegueira                                        |
        | 4                     | Transtorno do Espectro Autista/Neurodivergente       |
        | 5                     | Deficiência física                                   |
        | 6                     | Não tenho deficiência, mas tenho mobilidade reduzida |

  Esquema do Cenario: O usuário responde com o seu nome
    Dado usuário recebeu mensagem "Qual o seu nome?"
    E usuário possui deficiência cadastrada de "<tipo_de_deficiência>"
    Quando usuário envia mensagem "João Victor"
    Entao usuário receberá mensagem "Cadastro realizado."
      E bot salvará o nome do usuário "João Victor"
      E bot vai enviará "<opccoes_de_servico>" de acordo com o "<tipo_de_deficiência>" do pcd

    Exemplos:
      | tipo_de_deficiência | opccoes_de_servico                                                                         |
      | surdez              | informações em Libras,atividade com interpretação em Libras                                |
      | mobilidade reduzida | ajuda na mobilidade,transporte para deslocamento no evento                                 |
      | deficiência física  | ajuda na mobilidade,ajuda com alimentação e higiene,transporte para deslocamento no evento |
      | cegueira            | ajuda na mobilidade,programação com audiodescrição                                         |
      | neurodivergente     | suporte para pessoas neurodivergentes                                                      |
      | surdocegueira       | guia-intérprete                                                                            |
