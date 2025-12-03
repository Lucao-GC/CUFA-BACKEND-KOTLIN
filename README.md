🚀 Guia de Deploy (Implantação)
Este guia detalha o processo de preparação e implantação do backend CUFA-BACKEND-KOTLIN, um projeto desenvolvido em Kotlin com o framework Spring Boot e utilizando SQL Server como banco de dados.

⚙️ Pré-requisitos
Para realizar o deploy da aplicação, você precisará ter o seguinte ambiente configurado no servidor de destino:

Java Development Kit (JDK): Versão 17 ou superior.

Acesso ao Banco de Dados: Credenciais e acesso ao servidor SQL Server onde o banco de dados da aplicação será hospedado.

Ferramenta de Build: O projeto utiliza Gradle (incluso, não precisa instalar).

Git: Para clonar o repositório.

🔧 Configuração de Ambiente
Antes de realizar o build e a execução, as configurações do banco de dados e outras variáveis de ambiente devem ser definidas.

1. Clonar o Repositório
No servidor de destino ou na máquina que fará o build do artefato:

Bash

git clone https://github.com/Karpos-SPTech/CUFA-BACKEND-KOTLIN.git
cd CUFA-BACKEND-KOTLIN
2. Configurar Variáveis de Ambiente
O Spring Boot lê as configurações do banco de dados e da aplicação a partir do arquivo src/main/resources/application.properties por padrão.

No entanto, para o ambiente de produção, a melhor prática é usar variáveis de ambiente ou sobrescrever essas propriedades no comando de execução para evitar expor credenciais no código-fonte.

Arquivo de Configuração (Exemplo de application.properties):

Properties

# Configurações do Servidor
server.port=8080

# Configurações do Banco de Dados SQL Server
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.hibernate.ddl-auto=update # Use "validate" em produção para mais segurança!
spring.jpa.show-sql=false
Definição das Variáveis (Método Recomendado):

As variáveis $DB_URL, $DB_USERNAME, e $DB_PASSWORD devem ser exportadas no shell antes de executar o .jar.

Exemplo no Linux/macOS:

Bash

export DB_URL="jdbc:sqlserver://[SEU_SERVIDOR]:1433;databaseName=[SEU_DB]"
export DB_USERNAME="[SEU_USUARIO]"
export DB_PASSWORD="[SUA_SENHA_SEGURA]"
Substitua os valores entre colchetes ([]) pelas suas credenciais reais.

📦 Build da Aplicação (Gerando o JAR)
O Spring Boot utiliza o Gradle para empacotar a aplicação em um arquivo .jar executável, que contém todas as dependências necessárias.

Na pasta raiz do projeto (CUFA-BACKEND-KOTLIN), execute o comando de build do Gradle:

Bash

./gradlew bootJar
Em sistemas Windows, use gradlew bootJar.

Ao finalizar, o arquivo .jar (o artefato de deploy) será gerado no diretório build/libs/ com um nome similar a CUFA-BACKEND-KOTLIN-*.jar.

▶️ Execução da Aplicação
Após gerar o .jar e configurar as variáveis de ambiente, a aplicação está pronta para ser iniciada.

1. Navegar para a Pasta de Build
Bash

cd build/libs/
2. Executar o JAR
Utilize o comando java -jar para iniciar o Spring Boot. É fundamental que as variáveis de ambiente necessárias (como as do banco de dados) já estejam definidas na sua sessão.

Bash

java -jar CUFA-BACKEND-KOTLIN-*.jar
✅ Execução com Sobrescrita de Propriedades (Alternativa)

Você também pode passar as propriedades diretamente no comando de execução, substituindo o uso do export:

Bash

java -jar CUFA-BACKEND-KOTLIN-*.jar \
    --server.port=8080 \
    --spring.datasource.url="jdbc:sqlserver://[SEU_SERVIDOR]:1433;databaseName=[SEU_DB]" \
    --spring.datasource.username="[SEU_USUARIO]" \
    --spring.datasource.password="[SUA_SENHA_SEGURA]"
Observação: O uso de variáveis de ambiente (export) é geralmente preferido para manter as credenciais fora dos logs de comando.

🛡️ Execução em Produção (Serviço/Daemon)
Para um ambiente de produção real, o ideal é configurar a aplicação para rodar como um serviço (daemon) do sistema operacional (como systemd no Linux). Isso garante que o aplicativo reinicie automaticamente em caso de falha ou após o reboot do servidor.

O Spring Boot Gradle Plugin pode criar um jar "totalmente executável" para facilitar a criação de serviços systemd. Para mais detalhes, consulte a documentação oficial do Spring Boot sobre implantação.
