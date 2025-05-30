# language: pt
Funcionalidade: Mudar status do Atendente(Monitor ou membro da comissão)
  Como um Atendente(Monitor ou membro da comissão), Eu quero mudar meu status através de uma interação com bot do whatsapp, Para que minha disponibilidade seja sempre atualizada. Os possíveis status são:
  *Disponível quando ele iniciar o dia e também quando finalizar um atendimento.
  *Ocupado durante um atendimento
  *Indisponível quando encerrar o dia ou quando for ao banheiro, por exemplo.


  Esquema do Cenário: Solicitar mudança de status
    Dado que um <tipo_de_atendente> está com status <status_atual>
    Quando inicia a interação para mudar o status
    Então o bot envia a mensagem “Olá, <tipo_de_atendente>! No momento, seu status está como <status_atual>. Para mudar para algum desses status <novo_status>, basta clicar no botão correspondente abaixo.” E o bot envia a lista de botões dos possíveis novos status.
    Exemplos:
      | tipo_de_atendente  | status_atual | novo_status             |
      | monitor            | disponível   | indisponível            |
      | monitor            | indisponível | disponível              |
      | monitor            | ocupado      | disponível,indisponível |
      | membro da comissão | disponível   | indisponível            |
      | membro da comissão | indisponível | indisponível            |
      | membro da comissão | ocupado      | disponível,indisponível |

  Esquema do Cenário: Confirmar mudança de status
    Dado que o <tipo_de_atendente> está no <antigo_status> recebeu uma solicitação para mudar para o status <novo_status>
    Quando o <tipo_de_atendente> confirma a mudança
    Então o status do <tipo_de_atendente> deve ser atualizado para <novo_status> E o bot deve enviar a mensagem “Mudança realizada com sucesso agora seu novo status é <novo_status>”.

    Exemplos:
      | tipo_de_atendente  | antigo_status | novo_status  |
      | monitor            | disponível    | indisponível |
      | monitor            | ocupado       | indisponível |
      | monitor            | ocupado       | disponível   |
      | monitor            | indisponível  | disponível   |
      | membro da comissão | disponível    | indisponível |
      | membro da comissão | ocupado       | indisponível |
      | membro da comissão | ocupado       | disponível   |
      | membro da comissão | indisponível  | disponível   |
