---

# Autorizador de Transações com Cartão de Crédito

Autor: **Rafael Oliveira Silva**  
Repositório GitHub: [svcautorizadorcartaocredito](https://github.com/rafaelos-git/svcautorizadorcartaocredito)

## Descrição

Este projeto implementa um autorizador de transações com cartão de crédito. O autorizador processa transações com base em categorias de saldo (FOOD, MEAL, CASH) e contém fallback para casos onde o saldo da categoria principal não é suficiente. A arquitetura utilizada segue o padrão hexagonal (Ports & Adapters) para manter o domínio desacoplado de detalhes externos, como banco de dados e controladores HTTP.

## Tecnologias

- Linguagem: **Java (JVM)**
- Build Tool: **Gradle**
- Banco de Dados: **MongoDB**
- Contêinerização: **Docker**
- Arquitetura: **Hexagonal Architecture**
- Frameworks: **Spring Boot, Spring Data MongoDB**
- Cache e Lock Distribuído: **Redis**

## Estrutura do Projeto

O projeto segue a arquitetura hexagonal, dividindo as responsabilidades em camadas distintas para isolar o domínio de tecnologias externas.

- **adapters**  
  - **in**: Controladores que recebem as requisições HTTP (Controller).
  - **out**: Contém os adapters que fazem a comunicação com o mundo externo, como o banco de dados e a implementação de distribuição de locks (Repository, Adapters).

- **application**  
  - **core**
    - **domain**: Entidades e regras de negócio.
    - **usecase**: Implementação dos casos de uso, contendo a lógica central de autorização das transações.
  - **ports**: Interfaces que conectam as camadas de adaptação (in/out) com o domínio, permitindo que a lógica de negócio não dependa das implementações concretas externas.

- **config**: Configurações do sistema, como beans do Spring e configurações de dependências.

### Arquitetura Hexagonal

A arquitetura hexagonal foi utilizada para manter a aplicação flexível, desacoplando a lógica de negócio dos detalhes técnicos. Esta arquitetura define que o domínio (casos de uso e entidades) deve estar no centro da aplicação, com as interfaces (ports) ligando o domínio aos adapters, que fazem a comunicação com tecnologias externas (como banco de dados e API REST). O objetivo é tornar a aplicação fácil de testar e mais resistente a mudanças tecnológicas.

## Funcionalidades Implementadas

### L1. Autorizador Simples

- Recebe a transação, mapeia o MCC para uma categoria de saldo (FOOD, MEAL ou CASH) e aprova ou rejeita a transação com base no saldo disponível.
  
### L2. Autorizador com Fallback

- Caso o saldo da categoria principal não seja suficiente, verifica se há saldo disponível na categoria de **CASH** para debitar o valor.

### L3. Dependente do Comerciante

- Corrige o MCC baseado no nome do comerciante, permitindo maior precisão na categorização da transação.

### L4. Discussão sobre Transações Simultâneas e Lock Distribuído

Para garantir que apenas uma transação por conta seja processada de cada vez, foi implementado um mecanismo de **lock distribuído** utilizando o Redis. Este lock é obtido antes de processar cada transação, garantindo exclusividade no processamento por conta. Se o lock não puder ser obtido dentro de 100 ms, a transação falha e retorna um código de erro.

Este mecanismo é especialmente importante em ambientes de alta concorrência, como serviços online e operações com cartões de crédito, onde transações simultâneas podem ocorrer para a mesma conta. O lock distribuído impede que duas transações sejam processadas ao mesmo tempo para uma mesma conta, garantindo a integridade dos dados e a correta dedução de saldo.

## Exemplo de Inserção de Dados

Para inicializar uma conta com saldos nas categorias de FOOD, MEAL e CASH, insira o seguinte registro no MongoDB:

```bash
db.accounts.insertOne({ "accountId": "1234", "balanceFood": 1000.00, "balanceMeal": 1000.00, "balanceCash": 1000.00 });
```

## Endpoints

### Processar Autorização Simples

**POST /api/v1/transactions/simple-authorizations**

Request Body:

```json
{
  "account": "123",
  "totalAmount": 100.00,
  "mcc": "5811",
  "merchant": "PADARIA DO ZE               SAO PAULO BR"
}
```

Response (aprovado):

```json
{
  "code": "00"
}
```

### Processar Autorização com Fallback

**POST /api/v1/transactions/fallback-authorizations**

Request Body:

```json
{
  "account": "123",
  "totalAmount": 150.00,
  "mcc": "5811",
  "merchant": "PADARIA DO ZE               SAO PAULO BR"
}
```

Response (saldo insuficiente, fallback para CASH):

```json
{
  "code": "00"
}
```

## Como Rodar o Projeto com Docker

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/rafaelos-git/svcautorizadorcartaocredito.git
   cd svcautorizadorcartaocredito
   ```

2. **Suba o ambiente Docker:**
   O projeto já contém um arquivo `docker-compose.yml` para subir o banco de dados MongoDB e o Redis.

   ```bash
   cd docker-local
   docker-compose up
   ```

3. **Build da aplicação:**
   Certifique-se de que o Gradle está configurado corretamente no projeto e execute o seguinte comando para compilar e rodar a aplicação:

   ```bash
   ./gradlew build
   ./gradlew bootRun
   ```

4. **Acesse a aplicação:**
   A aplicação estará disponível em `http://localhost:8081`.

## Testes

A aplicação possui testes unitários para cobrir os principais cenários de autorização. Para rodar os testes, utilize o comando:

```bash
./gradlew test
```
