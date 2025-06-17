# language: pt
Funcionalidade: Acessar Serviços de Assistência
  Como uma Pessoa com Deficiência (PCD), eu quero solicitar um serviço de assistência
  e ser conectado a um atendente disponível ou informado sobre a fila de espera.

  Contexto:
    Dado um bot configurado com o número "15556557522"

  Esquema do Cenário: PCD solicita serviço e é direcionado para um atendente disponível
    Dado um PCD cadastrado com o número "<numero_pcd>", nome "<nome_pcd>" e deficiência "<tipo_deficiencia>"
    E um "<tipo_atendente>" disponível com nome "<nome_atendente>", número "<numero_atendente>" e tipo de assistência "<tipo_assistencia_monitor>" se aplicável
    E o PCD com número "<numero_pcd>" recebeu a mensagem com as opções de serviço para sua deficiência
    Quando o PCD com número "<numero_pcd>" envia a mensagem escolhendo a opção de serviço "<opcao_servico>" que corresponde ao "<servico_escolhido>"
    Então o PCD com número "<numero_pcd>" recebe uma mensagem informando que o atendente "<nome_atendente>" irá realizar o atendimento
    E o Atendente com número "<numero_atendente>" recebe uma mensagem informando que irá atender o PCD "<nome_pcd>"
    E um registro de atendimento é criado para o PCD com número "<numero_pcd>" e o Atendente com número "<numero_atendente>" com o serviço "<servico_escolhido>" e status iniciado
    E o status do Atendente com número "<numero_atendente>" muda para OCUPADO

    Exemplos:
      | numero_pcd | nome_pcd | tipo_deficiencia | tipo_atendente | nome_atendente | numero_atendente | tipo_assistencia_monitor | opcao_servico | servico_escolhido |
      | 5581988880001 | Ana Silva | deficiente físico | Monitor | Carlos Monitor | 5581977770001 | MOBILITY_MONITOR  | 1 | Mobility |
      | 5581988880002 | Bruno Costa | um pessoa surda | Membro da Comissão | Sofia Comissão | 5581977770002  |   | 1 | Libras |

  Esquema do Cenário: PCD solicita serviço e é colocado na fila de espera
    Dado um PCD cadastrado com o número "<numero_pcd>", nome "<nome_pcd>" e deficiência "<tipo_deficiencia>"
    E não há "<tipo_atendente>" disponível para o serviço "<servico_escolhido>"
    E o PCD com número "<numero_pcd>" recebeu a mensagem com as opções de serviço para sua deficiência
    Quando o PCD com número "<numero_pcd>" envia a mensagem escolhendo a opção de serviço "<opcao_servico>" que corresponde ao "<servico_escolhido>"
    Então o PCD com número "<numero_pcd>" recebe uma mensagem informando que foi colocado na fila de espera
    E um registro de atendimento é criado para o PCD com número "<numero_pcd>" com o serviço "<servico_escolhido>" e status solicitado aguardando atendente

    Exemplos:
      | numero_pcd | nome_pcd | tipo_deficiencia | tipo_atendente | servico_escolhido | opcao_servico |
      | 5581988880003 | Clara Luz | deficiente físico | Monitor | Mobility | 1 |
      | 5581988880004 | Davi Melo | uma pessoa cega | Membro da Comissão | AudioDescription | 1 |