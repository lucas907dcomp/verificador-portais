# Verificador de Portais de Notícias

Sistema para verificação de portais de notícias usando DNS e hash para garantir a autenticidade das fontes.

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- Swagger/OpenAPI
- dnsjava

## Funcionalidades

- Registro de portais de notícias
- Geração de hash único para cada portal
- Verificação via registro DNS
- Verificação de URLs suspeitas

## Como Executar

1. Clone o repositório
2. Execute o projeto:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Acesse a documentação Swagger:
   ```
   http://localhost:8080/swagger-ui.html
   ```
4. Acesse o console H2:
   ```
   http://localhost:8080/h2-console
   ```

## Endpoints

- `POST /api/portals/register`: Registra um novo portal
- `GET /api/portals/verify`: Verifica se um portal está configurado
- `GET /api/portals/check`: Verifica se uma URL é confiável

## Configuração do DNS

Para verificar um portal, adicione um registro TXT no DNS com o hash fornecido pelo sistema.

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request