# language: pt
Funcionalidade: Acessar serviços de assistência
  Como um PCD, quero poder acessar os serviços que estão disponíveis para o meu tipo de deficiência, para que eu consiga aproveitar o evento da SBPC.

    Esquema do Cenário Listar serviços de acordo com meu tipo de deficiência
      Quando PCD com a deficiência de "<tipo_de_deficiência>" mandar qualquer mensagem
      Entao bot vai enviará <opccoes_de_servico> de acordo com o <tipo_de_deficiência> do pcd

      Exemplos:
        | tipo_de_deficiência | opccoes_de_servico                                                                         |
        | Surdez              | informações em Libras,atividade com interpretação em Libras                                |
        | Mobilidade Reduzida | ajuda na mobilidade,transporte para deslocamento no evento                                 |
        | Deficiência Física  | ajuda na mobilidade,ajuda com alimentação e higiene,transporte para deslocamento no evento |
        | Cegueira            | ajuda na mobilidade,programação com audiodescrição                                         |
        | Neurodivergente     | suporte para pessoas neurodivergentes                                                      |
        | Surdocegueira       | guia-intérprete                                                                            |

  Esquema do Cenário Direcionar para o atendente(monitor ou membro da comissão)
      Dado bot enviou a mensagem de opção de serviço
        E existe atendente disponível para o "<servico_desejado>"
      Quando PCD envia a mensagem "<servico_desejado>"
      Entao bot vai direcionar para o <tipo_de_atendente> disponível no momento

      Exemplos:
        | servico_desejado       | tipo_de_atendente  |
        | Informações em Libras  | monitor            |
        | Intérprete de Libras   | membro da comissão |
        | Assistência mobilidade | monitor            |
        | carro                  | membro da comissão |
        | Higiene e Nutrição     | membro da comissão |
        | Audiodescrição         | membro da comissão |
        | Apoio Neurodivergente  | monitor            |
        | Guia-intérprete        | membro da comissão |

  Esquema do Cenário: PCD entra na fila de espera
    Dado bot enviou a mensagem "<opção de serviço>"
      E não tem atendente disponível
    Quando PCD envia mensagem "<servico_desejado>"
    Entao bot enviará mensagem "No momento não há atendentes disponíveis. Por favor, aguarde na fila de espera e retornaremos assim que possível."
    Exemplos:
      | servico_desejado       |                    |
      | Informações em Libras  | monitor            |
      | Intérprete de Libras   | membro da comissão |
      | Assistência mobilidade | monitor            |
      | carro                  | membro da comissão |
      | Higiene e Nutrição     | membro da comissão |
      | Audiodescrição         | membro da comissão |
      | Apoio Neurodivergente  | monitor            |
      | Guia-intérprete        | membro da comissão |


  Cenário: Bot direciona mensagem do PCD para o atendente correto
    Dado Atendente aceitou o atendimento do PCD
    Quando pcd envia qualquer mensagem para o bot
    Então o Atendente deve receber a mensagem do PCD contendo o nome do PCD
    E nemhum outro Atendente ou PCD deve receber a mesma mensagem

  Cenário: Bot direciona mensagem do atendente para o PCD correto
    Dado que o Atendente aceitou o atendimento do PCD
    Quando atendente envia uma mensagem para o bot
    Então PCD deve receber a mensagem do Atendente contendo nome do atendente
    E nemhum outro PCD ou Atendente deve receber a mesma mensagem

  Cenario: Avisar que o atendimento foi encerrado
    Dado atendente estava em atendimento com um pcd
    Quando atendente encerrou o atendimento
    Entao pcd receberá mensagem "Atendimento Encerrado"

  Esquema do Cenário: Atendete quando entra no estado disponível é direcionado para o pcd
    Dado pcd que solicitou <tipo_de_servico> estava na fila de espera
    Quando atendente do <tipo_de_atendente> muda para o estado disponível
    Entao atendente do <tipo_de_atendente> será direcionado para o pcd

    Exemplos:
      | tipo_de_servico       | tipo_de_atendente    |
      | Libras                | MONITOR              |
      | LibrasInterpreter     | COMMITTEE_MEMBER     |
      | Mobility              | MONITOR              |
      | AudioDescription      | COMMITTEE_MEMBER     |
      | NeurodivergentSupport | MONITOR              |
      | GuideInterpreter      | COMMITTEE_MEMBER     |
      | HygieneAndNutrition   | COMMITTEE_MEMBER     |
      | Car                   | COMMITTEE_MEMBER     |
