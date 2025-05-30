# language: pt
Funcionalidade: Cadastro do PCD
  Como um PCD, quero poder me cadastrar para acessar os serviços da SBPC com mais facilidade.

  Cenario: O Usuário não cadastrado manda qualquer mensagem
    Dado Um Usuário não cadastrado
    Quando Usuário mandar qualquer mensagem para o botpcd
    Entao O bot envia a mensagem Olá, qual sua deficiência: E bot envia uma lista de botões com as seguintes deficiências: Deficiência visual, Deficiência auditiva/surdez, Surdocegueira, TEA/Neurodivergente, Deficiência física, Mobilidade reduzida, Não preciso de suporte.

  Regra: Apenas Usuarios com deficiência podem ser cadastrados no sistema
    Cenario de Fundo:
      um usuário que recebeu a pergunta Você possui algum tipo de deficiencia? E usuário não cadastrado

    Cenario: O usuário responde que não precisa de suporte
      Quando o usuário clicar no botão não preciso de suporte
      Então O bot envia a mensagem Agradecemos o contato! Este canal é exclusivo para atendimento de pessoas com deficiência ou mobilidade reduzida que participarão do evento. Desejamos a você uma excelente participação na 77ª Reunião Anual da SBPC.

    Cenario: O usuário responde que tem deficiência
      Quando o usuário clicar no botão de algum tipo de deficiência que seria: Deficiência visual, Deficiência auditiva/surdez, Surdocegueira, TEA/Neurodivergente, Deficiência física, Mobilidade reduzida, Não preciso de suporte.
      Então o bot vai registrar o tipo de deficiência do usuário E bot vai mandar a mensagem “Qual o seu nome?”

    Cenario: O usuário responde com o seu nome
      Dado Usuario recebe a mensagem do bot Qual o seu nome?
      Quando Usuário mandar qualquer mensagem para o botpcd
      Entao o bot precisa Salvar o nome do usuario
