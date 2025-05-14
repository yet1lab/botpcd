package ufrpe.sbpc.botpcd.entity

/* 
(DISPONIVEL) ele pode receber um atendimento
(OCUPADO) ele esta em atendimento, mas quando finalizar o codigo trocara para disponivel
(INDISPONIVEL) ele foi ao banheiro / esta ocupado. Nao vai receber chamado ate que altere seu status manualmente para available
*/
enum class UserStatus {
    AVAILABLE("Disponível"), 
    BUSY("Ocupado"), 
    UNAVAILABLE("Indisponível") 
}