# language: pt
Funcionalidade: Acessar serviços de assistência
  Como um PCD, quero poder acessar os serviços que estão disponíveis para o meu tipo de deficiência, para que eu consiga aproveitar o evento da SBPC.

    Cenário de Fundo: pcd está cadastrado

    Esquema do Cenário Listar serviços de acordo com meu tipo de deficiência
      Quando O PCD com a deficiência de <tipo_de_deficiencia> mandar qualquer mensagem
      Entao O bot vai enviar uma lista de opções de acordo com a <opccoes_de_servico>

      Exemplos:
        | tipo_de_deficiência | opccoes_de_servico                                                                         |
        | Surdez              | Informações em Libras,atividade com interpretação em Libras                                |
        | Mobilidade Reduzida | ajuda na mobilidade,transporte para deslocamento no evento                                 |
        | Deficiência Física  | ajuda na mobilidade,ajuda com alimentação e higiene,transporte para deslocamento no evento |
        | Cegueira            | ajuda na mobilidade,programação com audiodescrição                                         |
        | Neurodivergente     | suporte para pessoas neurodivergentes                                                      |
        | Surdocegueira       | guia-intérprete                                                                            |

    Esquema do Cenário Direcionar para o atendente(monitor ou membro da comissão)
      Dado o bot enviou a mensagem de opção de serviço
      Quando o PCD selecionar o <servico_desejado>
      Entao bot vai direcionar para o <tipo_de_atendente> disponível no momento

      Exemplos:
        | servico_desejado          | tipo_de_atendente  |
        | Informações em Libras     | monitor            |
        | 1  Intérprete de Libras   | membro da comissão |
        | 2  Assistência mobilidade | monitor            |
        | 3  carro                  | membro da comissão |
        | 4 Higiene e Nutrição      | membro da comissão |
        | 5  Audiodescrição         | membro da comissão |
        | 6  Apoio Neurodivergente  | monitor            |
        | 7  Guia-intérprete        | membro da comissão |

  Cenário: Bot direciona mensagem do PCD para o atendente correto
    Dado Atendente aceitou o atendimento do PCD
    Quando o PCD envia uma mensagem para o bot
    Então o Atendente deve receber a mensagem do PCD
    E NENHUM outro Atendente ou PCD deve receber a mesma mensagem

  Cenário: Bot direciona mensagem do atendente para o PCD correto
    Dado que o Atendente aceitou o atendimento do PCD
    Quando o Atendente envia uma mensagem para o bot
    Então o PCD deve receber a mensagem do Atendente
    E NENHUM outro PCD ou Atendente deve receber a mesma mensagem
