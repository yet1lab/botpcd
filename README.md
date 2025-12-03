♿ Bot de Acessibilidade - 77ª SBPC (UFRPE)

📖 Sobre o Projeto

Este projeto consiste no desenvolvimento de um Chatbot via WhatsApp voltado para prover acessibilidade e suporte durante a 77ª Reunião Anual da Sociedade Brasileira para o Progresso da Ciência (SBPC), realizada na UFRPE.

🎯 Contexto e Impacto

O objetivo principal é conectar participantes com deficiência (PCD) aos monitores de apoio e membros da comissão de acessibilidade. A solução resolve gargalos logísticos críticos:

Privacidade: Elimina a necessidade de expor números de telefone pessoais de monitores e participantes.

Escalabilidade: Automatiza a triagem e o direcionamento de solicitações, suportando picos de demanda previstos para o evento.

Acessibilidade: Utiliza a interface nativa do WhatsApp, aproveitando recursos de acessibilidade (leitores de tela, comando de voz) já familiares aos usuários.

🚀 Tecnologias Utilizadas

Linguagem: Kotlin

Framework: Spring Boot

Banco de Dados: PostgreSQL (Google Cloud SQL em produção)

Infraestrutura:

Docker para containerização.

Google Cloud Platform (GCP): Compute Engine (App) e Cloud SQL (Banco).

Nginx + Certbot: Proxy reverso e SSL.

GitHub Actions: CI/CD Pipeline.

API Externa: WhatsApp Business Cloud API.

Testes:

JUnit 5: Testes unitários.

Cucumber: Testes de aceitação baseados em comportamento (BDD).

⚙️ Pré-requisitos

Para rodar o projeto localmente, você precisará ter instalado:

JDK 17 ou superior.

Docker

Conta de Desenvolvedor na Meta (Facebook) com uma aplicação WhatsApp configurada.

🔧 Configuração e Execução

1. Clonar o Repositório

git clone [https://github.com/seu-usuario/bot-pcd-sbpc.git](https://github.com/seu-usuario/bot-pcd-sbpc.git)
cd bot-pcd-sbpc


2. Variáveis de Ambiente

Crie um arquivo .env na raiz do projeto ou configure as variáveis no seu ambiente (ou application.properties para dev local):

# Banco de Dados
DB_URL=jdbc:postgresql://localhost:5432/botpcd
DB_USERNAME=postgres
DB_PASSWORD=sua_senha

# WhatsApp API
WHATSAPP_TOKEN=seu_token_de_acesso_meta
WHATSAPP_PHONE_ID=seu_phone_number_id
WHATSAPP_VERIFY_TOKEN=seu_token_de_verificacao_webhook



A aplicação estará disponível em http://localhost:8080.

3. Rodando Manualmente (Gradle)

Caso prefira rodar sem Docker:

# Linux/Mac
./gradlew bootRun

# Windows
.\gradlew.bat bootRun


🧪 Executando os Testes

O projeto segue rigorosos padrões de qualidade, utilizando BDD (Behavior Driven Development). A pipeline de CI/CD está configurada para bloquear builds caso os testes falhem.

Rodar todos os testes

Para executar a suíte completa (Unitários + Integração + Cucumber):

./gradlew test


Estrutura dos Testes BDD

Os cenários de teste estão descritos em arquivos .feature na pasta src/test/resources. Exemplos de funcionalidades cobertas:

registerPCD.feature: Fluxo de cadastro de pessoas com deficiência.

acessarServicos.feature: Solicitação de apoio e serviços.

mudarStatusAtendente.feature: Monitor altera status (Disponível/Indisponível).

Exemplo de cenário Gherkin:

Funcionalidade: Cadastro de PCD
  Cenario: Usuario envia mensagem inicial e recebe lista de deficiencias
    Dado que o usuario envia "Ola"
    Entao o bot deve responder com a lista de deficiencias disponiveis

🤝 Contribuição

Faça um Fork do projeto.

Crie uma Branch para sua Feature (git checkout -b feature/MinhaFeature).

Commit suas mudanças (git commit -m 'Adiciona funcionalidade X').

Push para a Branch (git push origin feature/MinhaFeature).

Abra um Pull Request.

Nota: Certifique-se de que todos os testes estão passando antes de abrir o PR.

📝 Licença

Este projeto está sob a licença Apache 2.

## Como rodar o projeto (debian)

```sh
> apt install openjdk-17
> wget https\://services.gradle.org/distributions/gradle-8.13-bin.zip

```
