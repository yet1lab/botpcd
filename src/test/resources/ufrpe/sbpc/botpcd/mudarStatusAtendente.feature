# language: pt
Funcionalidade: Mudar status do Atendente (Monitor ou Membro da Comissão)
  Como um Atendente, eu quero mudar meu status através de uma interação com o bot do WhatsApp,
  para que minha disponibilidade seja sempre atualizada.

  Esquema do Cenário: Atendente solicita mudança de status
    Dado que sou um <tipo_de_atendente> com número <numero_atendente> e status inicial <status_atual>
    Quando o "<tipo_de_atendente>" com número <numero_atendente> envia a mensagem "bot pcd"
    Então o "<tipo_de_atendente>" com número <numero_atendente> receberá a mensagem
      """
      *BotPCD:*
      <mensagem_status_options>
      """

    Exemplos:
      | tipo_de_atendente  | numero_atendente | status_atual | mensagem_status_options                                                                                                                                        |
      | Monitor            | 558100000001     | AVAILABLE    | Você está *Disponível* no momento\n1 - Ficar Indisponível\n- Escreva "cancelar" para sair do menu                                                              |
      | Monitor            | 558100000002     | UNAVAILABLE  | Você está *Indisponível* no momento\n1 - Ficar Disponível\n- Escreva "cancelar" para sair do menu                                                              |
      | Monitor            | 558100000003     | BUSY         | Você está *em atendimento*\n1 - Encerrar atendimento e Ficar Disponível\n2 - Encerrar atendimento e Ficar Indisponível\n- Escreva "cancelar" para sair do menu |
      | Membro da Comissão | 558100000004     | AVAILABLE    | Você está *Disponível* no momento\n1 - Ficar Indisponível\n- Escreva "cancelar" para sair do menu                                                              |
      | Membro da Comissão | 558100000005     | UNAVAILABLE  | Você está *Indisponível* no momento\n1 - Ficar Disponível\n- Escreva "cancelar" para sair do menu                                                              |
      | Membro da Comissão | 558100000006     | BUSY         | Você está *em atendimento*\n1 - Encerrar atendimento e Ficar Disponível\n2 - Encerrar atendimento e Ficar Indisponível\n- Escreva "cancelar" para sair do menu |

  Esquema do Cenário: Atendente confirma ou cancela mudança de status
    Dado que sou um "<tipo_de_atendente>" com número <numero_atendente> e status inicial "<status_antigo>"
    E o "<tipo_de_atendente>" com número <numero_atendente> recebeu a mensagem de opções para status "<status_antigo>"
    Quando o "<tipo_de_atendente>" com número <numero_atendente> envia a mensagem "<resposta_usuario>"
    Então o status do "<tipo_de_atendente>" com número <numero_atendente> deve ser "<status_novo_esperado>"
    E o "<tipo_de_atendente>" com número <numero_atendente> receberá a mensagem
      """
      *BotPCD:*
      <mensagem_confirmacao>
      """

    Exemplos:
      | tipo_de_atendente  | numero_atendente | status_antigo | resposta_usuario | status_novo_esperado | mensagem_confirmacao                                                |
      | Monitor            | 558100000001     | AVAILABLE     | "1"              | UNAVAILABLE          | Seu status foi atualizado para Indisponível.                        |
      | Monitor            | 558100000001     | AVAILABLE     | "cancelar"       | AVAILABLE            | Você continua Disponível.                                           |
      | Monitor            | 558100000001     | AVAILABLE     | "X"              | AVAILABLE            | Opção inválida. Seu status permanece Disponível.                    |
      | Monitor            | 558100000002     | UNAVAILABLE   | "1"              | AVAILABLE            | Seu status foi atualizado para Disponível.                          |
      | Monitor            | 558100000002     | UNAVAILABLE   | "cancelar"       | UNAVAILABLE          | Você continua Indisponível.                                         |
      | Monitor            | 558100000003     | BUSY          | "1"              | AVAILABLE            | Atendimento encerrado. Seu status foi atualizado para Disponível.   |
      | Monitor            | 558100000003     | BUSY          | "2"              | UNAVAILABLE          | Atendimento encerrado. Seu status foi atualizado para Indisponível. |
      | Monitor            | 558100000003     | BUSY          | "cancelar"       | BUSY                 | Você continua em atendimento.                                       |
      | Membro da Comissão | 558100000004     | AVAILABLE     | "1"              | UNAVAILABLE          | Seu status foi atualizado para Indisponível.                        |
      | Membro da Comissão | 558100000005     | UNAVAILABLE   | "1"              | AVAILABLE            | Seu status foi atualizado para Disponível.                          |
      | Membro da Comissão | 558100000006     | BUSY          | "1"              | AVAILABLE            | Atendimento encerrado. Seu status foi atualizado para Disponível.   |
      | Membro da Comissão | 558100000006     | BUSY          | "2"              | UNAVAILABLE          | Atendimento encerrado. Seu status foi atualizado para Indisponível. |