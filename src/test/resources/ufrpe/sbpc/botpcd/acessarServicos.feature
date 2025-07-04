# language: pt
Funcionalidade: Acessar serviços de assistência
  Como um PCD, quero poder acessar os serviços que estão disponíveis para o meu tipo de deficiência, para que eu consiga aproveitar o evento da SBPC.

  Esquema do Cenário Listar serviços de acordo com meu tipo de deficiência
    Quando PCD "<adjetivo_da_deficiencia>" mandar qualquer mensagem
    Entao "<adjetivo_da_deficiencia>" PCD receberá mensagem de opções de serviço "<opções_de_serviço>"

    Exemplos:
      | adjetivo_da_deficiencia | opções_de_serviço                                           |
      | um pessoa surda         | informações em Libras,atividade com interpretação em Libras |
      | mobilidade reduzida     | ajuda na mobilidade,transporte para deslocamento no evento  |
      | deficiente físico       | ajuda na mobilidade,transporte para deslocamento no evento  |
      | uma pessoa cega         | ajuda na mobilidade,programação com audiodescrição          |
      | neurodivergente         | suporte para pessoas neurodivergentes                       |

  Esquema do Cenário: Direcionar para o atendente(monitor ou membro da comissão)
    Dado que "<adjetivo_da_deficiencia>" PCD recebeu mensagem de opções de serviço
    E atendente que se chama "<nome_do_atendente>" está disponível para o "<servico_desejado>"
    E atendente que se chama "<nome_do_atendente>" enviou uma mensagem nas ultimas 24 horas para o bot
    Quando "<adjetivo_da_deficiencia>" PCD envia a mensagem "<numero_servico_desejado>"
    Entao "<adjetivo_da_deficiencia>" PCD receberá mensagem "O <tipo_de_atendente> <nome_do_atendente> irá realizar seu atendimento."

    Exemplos:
      | adjetivo_da_deficiencia | nome_do_atendente | servico_desejado                       | numero_servico_desejado | tipo_de_atendente  |
      | um pessoa surda         | João Comissão     | atividade com interpretação em Libras  | 1                       | membro da comissão |
      | um pessoa surda         | Ana Monitor       | informações em Libras                  | 2                       | monitor            |
      | mobilidade reduzida     | Pedro Monitor     | ajuda na mobilidade                    | 1                       | monitor            |
      | mobilidade reduzida     | Carla Comissão    | transporte para deslocamento no evento | 2                       | membro da comissão |
      | deficiente físico       | Pedro Monitor     | ajuda na mobilidade                    | 2                       | monitor            |
      | deficiente físico       | Carla Comissão    | transporte para deslocamento no evento | 3                       | membro da comissão |
      | uma pessoa cega         | Pedro Monitor     | ajuda na mobilidade                    | 1                       | monitor            |
      | uma pessoa cega         | Lucas Comissão    | programação com audiodescrição         | 2                       | membro da comissão |
      | neurodivergente         | Fábio Monitor     | suporte para pessoas neurodivergentes  | 1                       | monitor            |


  Regra: PCD precisa receber mensagem informando que ele está na fila de espera
    Esquema do Cenário: PCD entra na fila de espera ao solicitar um serviço
      Dado que "<adjetivo_da_deficiencia>" PCD recebeu mensagem de opções de serviço
      E que nenhum atendente para o "<servico_desejado>" está disponível
      Quando "<adjetivo_da_deficiencia>" PCD envia a mensagem "<numero_servico_desejado>"
      Entao "<adjetivo_da_deficiencia>" PCD receberá mensagem "No momento não há atendentes disponíveis. Por favor, aguarde na fila de espera e retornaremos assim que possível."

      Exemplos:
        | adjetivo_da_deficiencia | servico_desejado                       | numero_servico_desejado |
        | um pessoa surda         | atividade com interpretação em Libras  | 1                       |
        | um pessoa surda         | informações em Libras                  | 2                       |
        | mobilidade reduzida     | ajuda na mobilidade                    | 1                       |
        | mobilidade reduzida     | transporte para deslocamento no evento | 2                       |
        | deficiente físico       | ajuda na mobilidade                    | 2                       |
        | deficiente físico       | transporte para deslocamento no evento | 3                       |
        | uma pessoa cega         | ajuda na mobilidade                    | 1                       |
        | uma pessoa cega         | programação com audiodescrição         | 2                       |
        | neurodivergente         | suporte para pessoas neurodivergentes  | 1                       |


      Cenário: PCD está na fila de espera e manda mensagem novamente
        Dado PCD possuia serviço requisitado que ainda não foi iniciado
        Quando PCD mandar qualquer mensagem
        Então PCD receberá mensagem "No momento não há atendentes disponíveis. Por favor, aguarde na fila de espera e retornaremos assim que possível."

   Regra: Bot envia mensagem apenas para o usuário que mandaram mensagem nas últimas 24 horas

     Cenário de Fundo:
       Dado que o atendente "Carlos" de telefone "558100000001" enviou mensagem nas últimas 24 horas
       E que o PCD "João" de telefone "558100000002" enviou mensagem nas últimas 24 horas
       E que o atendente "Ana" de telefone "558100000003" enviou mensagem nas últimas 24 horas
       E que o PCD "Maria" de telefone "558100000004" enviou mensagem nas últimas 24 horas

     Cenário: Bot direciona mensagem do PCD para o atendente correto
       Dado que o atendente "Carlos" de telefone "558100000001" está em atendimento com o PCD "João" de telefone "558100000002"
       E que o atendente "Ana" de telefone "558100000003" está disponível, mas não em atendimento
       Quando o PCD "João" de telefone "558100000002" envia a mensagem "Onde fica o banheiro mais próximo?"
       Então o atendente "Carlos" de telefone "558100000001" deve receber a mensagem "Onde fica o banheiro mais próximo?" do PCD "João"
       E o atendente "Ana" de telefone "558100000003" não deve receber nenhuma nova mensagem do pcd "João"

     Cenário: Bot direciona mensagem do atendente para o PCD correto em um atendimento ativo
       Dado que o atendente "Carlos" de telefone "558100000001" está em atendimento com o PCD "João" de telefone "558100000002"
       E que o PCD "Maria" de telefone "558100000004" não está em atendimento com "Carlos"
       Quando o atendente "Carlos" de telefone "558100000001" envia a mensagem "Estou a caminho para ajudar."
       Então o PCD "João" de telefone "558100000002" deve receber a mensagem "Estou a caminho para ajudar." do atendente "Carlos"
       E o PCD "Maria" de telefone "558100000004" não deve receber nenhuma nova mensagem de "Carlos"

     Cenário: Atendente encerra o atendimento e PCD é notificado
       Dado que o atendente "Carlos" de telefone "558100000001" está em atendimento com o PCD "João" de telefone "558100000002"
       Quando o atendente "Carlos" de telefone "558100000001" encerra o atendimento
       Então o PCD "João" de telefone "558100000002" deve receber a mensagem "Atendimento encerrado" do bot

  Regra: Um atendente fica disponível é redirecionado para um pcd na fila de espera
    Esquema do Cenário: Atendente disponível é direcionado para PCD na fila de espera
     Dado que "<adjetivo_da_deficiencia>" PCD de número "<numero_pcd>" solicitou o serviço "<servico_desejado>" e está na fila de espera
     E que o atendente "<nome_do_atendente>" de "<numero_atendente>" do tipo "<tipo_de_atendente>" que presta serviço "<servico_desejado>" estava indisponível
     Quando o atendente "<nome_do_atendente>" de "<numero_atendente>" fica disponível
     Então "<adjetivo_da_deficiencia>" PCD de número "<numero_pcd>" receberá mensagem "O <tipo_de_atendente> <nome_do_atendente> irá realizar seu atendimento."

     Exemplos:
       | adjetivo_da_deficiencia | numero_pcd   | servico_desejado                       | nome_do_atendente | tipo_de_atendente  | numero_atendente |
       | um pessoa surda         | 558100000021 | informações em Libras                  | Sara Monitor      | monitor            | 558100000031     |
       | um pessoa surda         | 558100000022 | atividade com interpretação em Libras  | João Comissão     | membro da comissão | 558100000002     |
       | mobilidade reduzida     | 558100000023 | ajuda na mobilidade                    | Joana Monitor     | monitor            | 558100000033     |
       | mobilidade reduzida     | 558100000024 | transporte para deslocamento no evento | Carla Comissão    | membro da comissão | 558100000005     |
       | deficiente físico       | 558100000025 | ajuda na mobilidade                    | Pedro Monitor     | monitor            | 558100000006     |
       | deficiente físico       | 558100000027 | transporte para deslocamento no evento | Carla Comissão    | membro da comissão | 558100000008     |
       | uma pessoa cega         | 558100000028 | ajuda na mobilidade                    | Pedro Monitor     | monitor            | 558100000009     |
       | uma pessoa cega         | 558100000029 | programação com audiodescrição         | Lucas Comissão    | membro da comissão | 558100000010     |
       | neurodivergente         | 558100000030 | suporte para pessoas neurodivergentes  | Fábio Monitor     | monitor            | 558100000011     |