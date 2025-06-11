# uma-a3-bradesco

O projeto uma-a3-bradesco se trata de uma simulação bancária, onde é possível criar uma conta, seja pessoa física ou pessoa jurídica, criar uma chave PIX para cada conta, efetuar transferências bancárias utilizando agência e número de conta ou PIX.

## Funcionalidades

- **Login**: Tela inicial para autenticação.
- **Cadastrar Conta (PF/PJ)**: Permite criar contas de pessoa física ou jurídica.
- **Listar Transferências Realizadas**: Exibe o histórico de transferências.
- **Transferência via PIX**: Realiza transferências utilizando chaves PIX.
- **Transferência via Agência e Número de Conta**: Realiza transferências utilizando dados bancários tradicionais.
- **Denunciar Transação**: Permite denunciar transações suspeitas.

## Premissa

Cada conta inicia com uma pontuação de confiança de 60. Cada denúncia recebida reduz a pontuação em 5 pontos. Contas com pontuação igual ou abaixo de 50 exibem um aviso de segurança ao cliente ao tentar realizar uma transferência bancária.

## Tecnologias Utilizadas

- **Java**: Linguagem principal do projeto.
- **MySQL**: Banco de dados utilizado.
- **IntelliJ IDEA**: IDE recomendada para desenvolvimento.
- **Maven**: Gerenciador de dependências e build.

## Requisitos

- **Java**: Versão 17 ou superior.
- **MySQL**: Versão 8.0 ou superior.
- **Maven**: Configurado no ambiente.

## Configuração Inicial

### 1. Clonar o Repositório

Clone o repositório para sua máquina local:

```bash
git clone <URL_DO_REPOSITORIO>
```

### 2. Configurar o Banco de Dados

1. Instale o MySQL e configure um banco de dados.
2. Crie um banco de dados chamado `uma_a3_bradesco`.
3. Execute o script de inicialização localizado em `src/resources/init.sql` para criar as tabelas e inserir dados iniciais:

```bash
mysql -u <USUARIO> -p uma_a3_bradesco < src/resources/init.sql
```

### 3. Configurar o Projeto

Abra o projeto em sua IDE preferida (IntelliJ IDEA ou VS Code).

#### IntelliJ IDEA

1. Importe o projeto como um projeto Maven.
2. Configure o SDK para utilizar o Java 17.
3. Execute o comando Maven para compilar o projeto:

```bash
mvn clean install
```

#### VS Code

1. Certifique-se de ter as extensões para Java instaladas.
2. Configure o ambiente para utilizar o Java 17.
3. Compile o projeto utilizando o Maven:

```bash
mvn clean install
```

### 4. Executar o Projeto

#### IntelliJ IDEA

1. Localize a classe `Main` em `src/main/java/com/banco/Main.java`.
2. Execute a classe `Main` diretamente pela IDE.

#### VS Code

1. Compile o projeto utilizando o Maven.
2. Execute o comando:

```bash
java -cp target/classes com.banco.Main
```

## Estrutura do Projeto

- **src/main/java/com/banco**: Código fonte principal.
  - **model**: Contém as classes de modelo, como `Conta`, `ContaPF`, `ContaPJ`.
  - **service**: Contém as classes de serviço, como `ContaService`, `PixService`.
  - **ui**: Contém as classes de interface gráfica.
  - **util**: Contém utilitários, como `DatabaseConnection`.
- **src/resources**: Contém recursos como o script de inicialização do banco de dados.
- **target**: Diretório gerado após o build.

## Observações

- Certifique-se de que o MySQL está em execução antes de iniciar o projeto.
- Todas as funcionalidades foram testadas manualmente e estão funcionando corretamente.

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou enviar pull requests.

## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.