🚀 Guia de Deploy — CUFA-BACKEND-KOTLIN

Este guia descreve o processo completo de preparação, build e implantação do CUFA-BACKEND-KOTLIN, desenvolvido em Kotlin + Spring Boot, utilizando SQL Server como banco de dados.

⚙️ Pré-requisitos

Antes de iniciar o deploy, certifique-se de ter o seguinte ambiente configurado:

Java JDK 17+

Acesso ao Banco SQL Server

URL de conexão

Usuário e senha

Git (para clonar o repositório)

Gradle (não precisa instalar — wrapper incluso)

🔧 Configuração de Ambiente
1. Clonar o Repositório
git clone https://github.com/Karpos-SPTech/CUFA-BACKEND-KOTLIN.git
cd CUFA-BACKEND-KOTLIN

2. Configurar Variáveis de Ambiente

O Spring Boot lê o arquivo padrão:

src/main/resources/application.properties


Mas para produção, o recomendado é NÃO deixar credenciais no código — use variáveis de ambiente.

📄 Exemplo de application.properties
# Configurações do Servidor
server.port=8080

# Configurações do Banco (SQL Server)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update   # Em produção, prefira "validate"
spring.jpa.show-sql=false

🌱 Definir Variáveis de Ambiente (Recomendado)
Linux / macOS
export DB_URL="jdbc:sqlserver://SEU_SERVIDOR:1433;databaseName=SEU_DB"
export DB_USERNAME="SEU_USUARIO"
export DB_PASSWORD="SUA_SENHA_SEGURA"


Substitua os valores conforme seu ambiente real.

📦 Build da Aplicação (Gerar o JAR)

O Gradle empacota tudo em um .jar executável.

Na raiz do projeto, execute:

./gradlew bootJar


No Windows:
gradlew bootJar

O artefato final estará em:

build/libs/CUFA-BACKEND-KOTLIN-*.jar

▶️ Executando a Aplicação
1. Navegue até o diretório do build:
cd build/libs/

2. Execute o JAR:
java -jar CUFA-BACKEND-KOTLIN-*.jar


⚠️ Certifique-se de que as variáveis de ambiente já estão definidas.

✅ Execução com Sobrescrita de Propriedades (Alternativa)

Você também pode sobrescrever os valores diretamente na execução:

java -jar CUFA-BACKEND-KOTLIN-*.jar \
    --server.port=8080 \
    --spring.datasource.url="jdbc:sqlserver://SEU_SERVIDOR:1433;databaseName=SEU_DB" \
    --spring.datasource.username="SEU_USUARIO" \
    --spring.datasource.password="SUA_SENHA_SEGURA"


Importante:
Evite este método em produção — credenciais podem aparecer em logs.

🛡️ Rodando em Produção (Serviço / Daemon)

Para ambientes produtivos, o ideal é rodar o backend como um serviço systemd, garantindo:

Reinício automático após falhas

Inicialização junto com o servidor

Logs centralizados

O Spring Boot permite criar JARs "executáveis" que facilitam essa configuração.

Documentação recomendada:
https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html
