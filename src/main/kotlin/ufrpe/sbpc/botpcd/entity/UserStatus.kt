package ufrpe.sbpc.botpcd.entity

/* 
(DISPONIVEL) ele pode receber um atendimento
(OCUPADO) ele esta em atendimento, mas quando finalizar o codigo trocara para disponivel
(INDISPONIVEL) ele foi ao banheiro / esta ocupado. Nao vai receber chamado ate que altere seu status manualmente para available
*/
enum class UserStatus(val text: String, val statusChangeMessage: String) {
    AVAILABLE(
        "Disponível",
        "Olá, você está Indisponível no momento. Digite 1 se Deseja ficar Disponível para receber atendimentos?\n1 - Ficar Disponível"
    ),
    BUSY(
        "Ocupado",
        "Olá, você está em atendimento. Deseja encerrá-lo e continuar Disponível ou deseja ficar Indisponível?\n1 - Encerrar Atendimento\n2 - Ficar Indisponível"
    ),
    UNAVAILABLE(
        "Indisponível",
        "Olá, você está Disponível no momento. Deseja ficar Indisponível para não receber atendimentos?\n1 - Ficar Indisponível"
    )
}