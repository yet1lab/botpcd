# language: pt
Funcionalidade: Mudar status do Atendente(Monitor ou membro da comissão)
  Como um Atendente(Monitor ou membro da comissão), Eu quero mudar meu status através de uma interação com bot do whatsapp, Para que minha disponibilidade seja sempre atualizada. Os possíveis status são:
  Disponível quando ele iniciar o dia e também quando finalizar um atendimento.
  Ocupado durante um atendimento
  Indisponível quando encerrar o dia ou quando for ao banheiro, por exemplo.


  Esquema do Cenário: Solicitar mudança de status
    Dado <tipo_de_atendente> estava com status <status_atual>
    Quando <tipo_de_atendente> envia mensagem "bot pcd"
    Então usuário receberá mensagem "Olá, <tipo_de_atendente>! No momento, seu status está como <status_atual>. Para mudar para algum desses status <novo_status>, basta clicar no botão correspondente abaixo"

    Exemplos:
      | tipo_de_atendente  | status_atual | novo_status             |
      | monitor            | disponível   | indisponível            |
      | monitor            | indisponível | disponível              |
      | monitor            | ocupado      | disponível,indisponível |
      | membro da comissão | disponível   | indisponível            |
      | membro da comissão | indisponível | indisponível            |
      | membro da comissão | ocupado      | disponível,indisponível |

  Esquema do Cenário: Confirmar mudança de status
    Dado <tipo_de_atendente> está no <antigo_status> recebeu uma solicitação de mudança de status
    Quando <tipo_de_atendente> envia mensagem "<numero_novo_status>"
    Então o status do <tipo_de_atendente> deve ser atualizado para <novo_status>
    E usuário receberá mensagem "Mudança realizada com sucesso agora seu novo status é <novo_status>"

    Exemplos:
      | tipo_de_atendente  | antigo_status | novo_status  | numero_novo_status |
      | monitor            | disponível    | indisponível | 1                  |
      | monitor            | ocupado       | indisponível | 1                  |
      | monitor            | ocupado       | disponível   | 2                  |
      | monitor            | indisponível  | disponível   | 1                  |
      | membro da comissão | disponível    | indisponível | 1                  |
      | membro da comissão | ocupado       | indisponível | 1                  |
      | membro da comissão | ocupado       | disponível   | 2                  |
      | membro da comissão | indisponível  | disponível   | 1                  |
