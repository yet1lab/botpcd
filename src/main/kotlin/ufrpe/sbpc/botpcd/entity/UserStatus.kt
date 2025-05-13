package ufrpe.sbpc.botpcd.entity

enum class UserStatus {
    AVAILABLE, /* (DISPONIVEL) ele pode receber um atendimento */
    BUSY, /* (OCUPADO) ele esta em atendimento, mas quando finalizar o codigo trocara para disponivel */ 
    UNAVAILABLE /* (INDISPONIVEL) ele foi ao banheiro / esta ocupado. Nao vai receber chamado ate que altere seu status manualmente para available*/
}