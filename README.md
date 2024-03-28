# Sistema de Notificação de Eventos em Tempo Real

O sistema permite que usuários se inscrevam para receber notificações sobre diferentes tipos de eventos. Os eventos são publicados em um tópico do Apache Kafka a partir de um microsserviço e os usuários inscritos receberão as notificações em tempo real a partir de outro microsserviço que consome esse tópico e envia as notificações por email a cada um deles.

## Funcionalidades

1. **Registro de Usuários:** Os usuários podem se registrar no sistema fornecendo como informações básicas seu nome e email.
   
2. **Gerenciamento de Eventos:** Os administradores podem adicionar novos tipos de eventos ao sistema, especificando o nome e a descrição do evento.

3. **Inscrição em Eventos:** Os usuários podem se inscrever para receber notificações sobre eventos específicos, escolhendo entre os tipos de eventos disponíveis.

4. **Publicação de Eventos:** O sistema permite que os produtores publiquem eventos em um tópico do Apache Kafka. Os eventos contém informações relevantes, como o tipo de evento e os detalhes específicos.

5. **Envio de Notificações:** Quando um evento for publicado, o sistema enviará notificações em tempo real para todos os usuários inscritos no tipo de evento correspondente.

## Tecnologias
- Java
- Spring Boot
- Apache Kafka
- Banco de dados (PostgreSQL para prod e H2 para testes)
- Docker
