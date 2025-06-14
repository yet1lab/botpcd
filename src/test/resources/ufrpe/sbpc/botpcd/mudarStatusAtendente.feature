# language: pt
Funcionalidade: Mudar status do Atendente (Monitor ou Membro da Comissão)
  Como um Atendente, eu quero mudar meu status através de uma interação com o bot do WhatsApp, para que minha disponibilidade seja sempre atualizada.

  Esquema do Cenário: Atendente solicita mudança de status
    Dado que sou um "<tipo_de_atendente>" com status inicial "<status_atual>"
    Quando o "<tipo_de_atendente>" envia a mensagem "bot pcd"
    Então o "<tipo_de_atendente>" receberá a mensagem
      """
      *BotPCD:*
      <mensagem_status_options>
      """

    Exemplos:
      | tipo_de_atendente  | status_atual | mensagem_status_options                                                                                                                                                                |
      | Monitor            | AVAILABLE    | Você está *Disponível* no momento\n- Digite 1 para Ficar Indisponível\n- Escreva "cancelar" para sair do menu                                                                          |
      | Monitor            | UNAVAILABLE  | Você está *Indisponível* no momento\n- Digite 1 para Ficar Disponível\n- Escreva "cancelar" para sair do menu                                                                          |
      | Monitor            | BUSY         | Você está *em atendimento*\n- Digite 1 para Encerrar atendimento e Ficar Disponível\n- Digite 2 para Encerrar atendimento e Ficar Indisponível\n- Escreva "cancelar" para sair do menu |
      | Membro da Comissão | AVAILABLE    | Você está *Disponível* no momento\n- Digite 1 para Ficar Indisponível\n- Escreva "cancelar" para sair do menu                                                                          |
      | Membro da Comissão | UNAVAILABLE  | Você está *Indisponível* no momento\n- Digite 1 para Ficar Disponível\n- Escreva "cancelar" para sair do menu                                                                          |
      | Membro da Comissão | BUSY         | Você está *em atendimento*\n- Digite 1 para Encerrar atendimento e Ficar Disponível\n- Digite 2 para Encerrar atendimento e Ficar Indisponível\n- Escreva "cancelar" para sair do menu |

  Esquema do Cenário: Atendente confirma ou cancela mudança de status
    Dado que sou um "<tipo_de_atendente>" com status inicial "<status_antigo>"
    E o "<tipo_de_atendente>" recebeu a mensagem de opções para status "<status_antigo>"
    Quando o "<tipo_de_atendente>" envia a mensagem <resposta_usuario>
    Então o status do "<tipo_de_atendente>" deve ser "<status_novo_esperado>"
    E o "<tipo_de_atendente>" receberá a mensagem
      """
      <mensagem_confirmacao_completa>
      """

    Exemplos:
      | tipo_de_atendente  | status_antigo | resposta_usuario | status_novo_esperado | mensagem_confirmacao_completa                                                   |
      | Monitor            | AVAILABLE     | "1"              | UNAVAILABLE          | *BotPCD:*\n Seu status foi atualizado para Indisponível.                        |
      | Monitor            | AVAILABLE     | "cancelar"       | AVAILABLE            | *BotPCD:*\n Você continua Disponível.                                           |
      | Monitor            | AVAILABLE     | "X"              | AVAILABLE            | *BotPCD:*\n Opção inválida. Seu status permanece Disponível.                    |
      | Monitor            | UNAVAILABLE   | "1"              | AVAILABLE            | *BotPCD:*\n Seu status foi atualizado para Disponível.                          |
      | Monitor            | UNAVAILABLE   | "cancelar"       | UNAVAILABLE          | *BotPCD:*\n Você continua Indisponível.                                         |
      | Monitor            | BUSY          | "1"              | AVAILABLE            | *BotPCD:*\n Atendimento encerrado. Seu status foi atualizado para Disponível.   |
      | Monitor            | BUSY          | "2"              | UNAVAILABLE          | *BotPCD:*\n Atendimento encerrado. Seu status foi atualizado para Indisponível. |
      | Monitor            | BUSY          | "cancelar"       | BUSY                 | *BotPCD:*\n Você continua em atendimento.                                       |
      | Membro da Comissão | AVAILABLE     | "1"              | UNAVAILABLE          | *BotPCD:*\n Seu status foi atualizado para Indisponível.                        |
      | Membro da Comissão | UNAVAILABLE   | "1"              | AVAILABLE            | *BotPCD:*\n Seu status foi atualizado para Disponível.                          |
      | Membro da Comissão | BUSY          | "1"              | AVAILABLE            | *BotPCD:*\n Atendimento encerrado. Seu status foi atualizado para Disponível.   |
      | Membro da Comissão | BUSY          | "2"              | UNAVAILABLE          | *BotPCD:*\n Atendimento encerrado. Seu status foi atualizado para Indisponível. |