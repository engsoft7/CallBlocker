# CallBlocker 🍏🚫

CallBlocker é um aplicativo Android premium e minimalista focado em proteger você contra chamadas indesejadas e spams, combinando a robustez do sistema operacional Android com o design limpo e moderno inspirado na estética da Apple (iOS).

## ✨ Principais Funcionalidades

- **Design Premium "Apple-Style"**: Interface focada no minimalismo, cores sólidas (Preto Puro e Branco Puro) e botões arredondados, proporcionando a melhor experiência de usuário.
- **Modo Claro e Escuro Dinâmico**: O aplicativo adapta instantaneamente suas cores conforme o tema do seu celular (Light/Dark Mode).
- **Proteção Ativa e Silenciosa**: Intercepta chamadas em segundo plano sem que o seu celular toque usando a API moderna do Android `CallScreeningService`.
- **Filtros de Bloqueio**:
  - **Bloquear Desconhecidos**: Apenas contatos salvos na sua agenda podem te ligar.
  - **Bloquear Tudo**: Recusa qualquer chamada automaticamente.
  - **Lista Negra Personalizada**: Adicione números específicos que sempre serão bloqueados, independente do modo atual.
- **Banco de Dados Profissional**: Todo o histórico e as configurações são salvos localmente e de forma segura utilizando **Room Database**, garantindo rapidez e zero travamentos.
- **Tela de Chamada Integrada**: Inclui uma tela de chamadas ("In-Call") que substitui o sistema padrão do Android com temporizador, opções de Mudo/Viva-Voz e interface sofisticada.

## 🚀 Como Funciona

Para que o aplicativo possa bloquear chamadas ativamente e exibir a nova tela de ligações, ele deve ser configurado como o **Aplicativo de Telefone / Discador Padrão** nas configurações do seu Android.

## 🛠️ Tecnologias Utilizadas

- **Kotlin** (Linguagem Principal)
- **Jetpack Compose** (Para construção 100% da UI)
- **Room Database / KSP** (Persistência de Dados Profissional)
- **Material Icons Extended** (Ícones Padronizados)
- **CallScreeningService / InCallService** (Telecom APIs)

## 📄 Licença

Este projeto é distribuído sob a licença **MIT**. Você é livre para usá-lo, modificá-lo e distribuí-lo. Veja o arquivo `LICENSE` para mais detalhes.
