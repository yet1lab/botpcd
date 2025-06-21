# language: pt
Funcionalidade: Acessar serviços de assistência
  Como um PCD, quero poder acessar os serviços que estão disponíveis para o meu tipo de deficiência, para que eu consiga aproveitar o evento da SBPC.

  Esquema do Cenário Listar serviços de acordo com meu tipo de deficiência
    Quando PCD "<adjetivo_da_deficiencia>" mandar qualquer mensagem
    Entao "<adjetivo_da_deficiencia>" PCD receberá mensagem de opcções de serviço "<opcções_de_serviço>"

    Exemplos:
      | adjetivo_da_deficiencia | opcções_de_serviço                                                                         |
      | um pessoa surda         | informações em Libras,atividade com interpretação em Libras                                |
      | mobilidade reduzida     | ajuda na mobilidade,transporte para deslocamento no evento                                 |
      | deficiente físico       | ajuda na mobilidade,ajuda com alimentação e higiene,transporte para deslocamento no evento |
      | uma pessoa cega         | ajuda na mobilidade,programação com audiodescrição                                         |
      | neurodivergente         | suporte para pessoas neurodivergentes                                                      |
      | uma pessoa surdocega    | guia-intérprete                                                                            |

  Esquema do Cenário: Direcionar para o atendente(monitor ou membro da comissão)
    Dado que "<adjetivo_da_deficiencia>" PCD recebeu mensagem de opcções de serviço
    E atendente que se chama "<nome_do_atendente>" está disponível para o "<servico_desejado>"
    E atendente que se chama "<nome_do_atendente>" enviou uma mensagem nas ultimas 24 horas para o bot
    Quando "<adjetivo_da_deficiencia>" PCD envia a mensagem "<numero_servico_desejado>"
    Entao "<adjetivo_da_deficiencia>" PCD receberá mensagem "O <tipo_de_atendente> <nome_do_atendente> irá realizar seu atendimento."

    Exemplos:
      | adjetivo_da_deficiencia | nome_do_atendente | servico_desejado                       | numero_servico_desejado | tipo_de_atendente  |
      | um pessoa surda         | Ana Monitor       | informações em Libras                  | 1                       | monitor            |
      | um pessoa surda         | João Comissão     | atividade com interpretação em Libras  | 2                       | membro da comissão |
      | mobilidade reduzida     | Pedro Monitor     | ajuda na mobilidade                    | 3                       | monitor            |
      | deficiente físico       | Carla Comissão    | transporte para deslocamento no evento | 1                       | membro da comissão |
      | deficiente físico       | Bia Comissão      | ajuda com alimentação e higiene        | 2                       | membro da comissão |
      | uma pessoa cega         | Lucas Comissão    | programação com audiodescrição         | 1                       | membro da comissão |
      | neurodivergente         | Fábio Monitor     | suporte para pessoas neurodivergentes  | 1                       | monitor            |
      | uma pessoa surdocega    | Maria Comissão    | guia-intérprete                        | 1                       | membro da comissão |

  Regra: PCD precisa receber mensagem informando que ele está na fila de espera
      Cenário de Fundo: Dado que PCD recebeu a mensagem "<opção de serviço>"
        E não que tem atendente disponível

      Esquema do Cenário: PCD entra na fila de espera
        Quando PCD envia mensagem "<servico_desejado>"
        Entao PCD receberá mensagem "No momento não há atendentes disponíveis. Por favor, aguarde na fila de espera e retornaremos assim que possível."
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

      Cenário: PCD está na fila de espera e manda mensagem novamente
        Dado PCD possuia serviço requisitado que ainda não foi iniciado
        Quando PCD mandar qualquer mensagem
        Então PCD receberá mensagem "No momento não há atendentes disponíveis. Por favor, aguarde na fila de espera e retornaremos assim que possível."


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
