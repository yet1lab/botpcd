Feature: Mudar status do Atendente(Monitor ou membro da comissão)
Como um Atendente(Monitor ou membro da comissão), Eu quero mudar meu status através de uma interação com bot do whatsapp, Para que minha disponibilidade seja sempre atualizada. Os possíveis status são:
*Disponível quando ele iniciar o dia e também quando finalizar um atendimento.
*Ocupado durante um atendimento
*Indisponível quando encerrar o dia ou quando for ao banheiro, por exemplo.


Esquema do Cenário: Solicitar mudança de status
Dado que um <tipo_de_atendente> está com status <status_atual>
Quando inicia a interação para mudar o status
Então o sistema deve apresentar uma <mensagem_de_troca_de_status>

Exemplos:
| tipo_de_atendente | status_atual |mensagem_de_troca_de_status|
| monitor           | disponível   | Olá, você está Disponível no momento. Deseja ficar Indisponível para não receber atendimentos?     	 |
| monitor 		  | indisponível | Olá, você está Indisponível no momento. Deseja ficar Disponível para receber atendimentos?          |
| monitor		  | ocupado      | Olá, você está em atendimento. Deseja encerrá-lo e continuar Disnpoível ou deseja ficar Indisponível?|
| membro da comissão| disponível   | Olá, você está Disponível no momento. Deseja ficar Indisponível para não receber atendimentos?      	 |
| membro da comissão| indisponível | Olá, você está Indisponível no momento. Deseja ficar Disponível para receber atendimentos?          |
| membro da commissão | ocupado      | Olá, você está em atendimento. Deseja encerrá-lo e continuar Disnpoível ou deseja ficar Indisponível?|

Esquema do Cenário: Confirmar mudança de status
Dado que o <tipo_de_atendente> está no <antigo_status> recebeu uma solicitação para mudar para o status <novo_status>
Quando o <tipo_de_atendente> confirma a mudança
Então o status do <tipo_de_atendente> deve ser atualizado para <novo_status> E o sistema deve notificar sobre a mudança bem-sucedida

Exemplos:
| tipo_de_atendente | antigo_status | novo_status |
| monitor           | disponível    | indisponível |
| monitor           | ocupado       | indisponível  |
| monitor | ocupado       | disponível   |
| monitor | indisponível  | disponível   |
| membro da comissão | disponível    | indisponível |
| membro da comissão | ocupado       | indisponível  |
| membro da comissão | ocupado       | disponível   |
| membro da comissão | indisponível  | disponível   |
