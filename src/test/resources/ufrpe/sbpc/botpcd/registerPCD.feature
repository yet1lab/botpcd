# language: pt
Funcionalidade: Cadastro do PCD
  Como um PCD, quero poder me cadastrar para acessar os serviços da SBPC com mais facilidade.

  Cenario: Usuário não cadastrado manda qualquer mensagem
    Dado usuário não cadastrado
    Quando usuário mandar qualquer mensagem para o botpcd
    Entao bot envia mensagem "Olá, qual sua deficiência?\n- Digite 1 para Deficiência visual\n- Digite 2 para Deficiência auditiva/surdez\n- Digite 3 para Surdocegueira\n- Digite 4 para Transtorno do Espectro Autista/Neurodivergente\n- Digite 5 para Deficiência física\n- Digite 6 para Não tenho deficiência, mas tenho mobilidade reduzida\n- Digite 7 para Não preciso de suporte."

  Regra: Apenas Usuarios com deficiência podem ser cadastrados no sistema
    Cenário de Fundo: usuário que recebeu a mensagem "com os tipos de deficiencias" E usuário não cadastrado

    Cenario: O usuário responde que não precisa de suporte
      Quando O usuário envia a mensagem "7"
      Então bot envia a mensagem "Agradecemos o contato! Este canal é exclusivo para atendimento de pessoas com deficiência ou mobilidade reduzida que participarão do evento. Desejamos a você uma excelente participação na 77ª Reunião Anual da SBPC."

    Cenario:
      Quando o usuário envia mensagem "10"
      Entao bot envia mensagem "com os tipos de deficiencias"

    Esquema do Cenario: O usuário responde que tem deficiência
      Quando usuário envia a mensagem "<numero_da_deficiencia>"
      Então o bot vai registrar o tipo de deficiência do usuário E bot vai envia a mensagem "Qual o seu nome?"
      Exemplos:
        | numero_da_deficiencia |
        | 1                     |
        | 2                     |
        | 3                     |
        | 4                     |
        | 5                     |
        | 6                     |
    Cenario: O usuário responde com o seu nome
      Dado usuário recebeu a mensagem do bot "Qual o seu nome?"
      Quando usuário mandar qualquer mensagem para o botpcd
      Entao bot envia a mensagem "Cadastro realizado" E bot salva o nome do usuário
